package com.lfj.messfox.server.handler

import com.lfj.messfox.exceptions.MessFoxException
import com.lfj.messfox.protocol.Request
import com.lfj.messfox.protocol.request.*
import com.lfj.messfox.protocol.response.AuthResponse
import com.lfj.messfox.protocol.response.ErrorResponse
import com.lfj.messfox.protocol.response.ReceiveMessageResponse
import com.lfj.messfox.protocol.response.RegisterResponse
import com.lfj.messfox.server.registry.ConnectionRegistry
import com.lfj.messfox.server.service.ChatService
import com.lfj.messfox.server.service.MessageService
import com.lfj.messfox.server.service.UserService
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.AttributeKey
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*

class ServerHandler(val userService: UserService, val chatService: ChatService, val messageService: MessageService, val connectionRegistry: ConnectionRegistry) : ChannelInboundHandlerAdapter() {
    private val logger: Logger = LoggerFactory.getLogger(ServerHandler::class.java)
    companion object Attributes{
        val userId: AttributeKey<UUID> = AttributeKey.valueOf("thisUser")
    }
    private var requestResponseHandler: RequestResponseHandler? = null
    override fun channelActive(ctx: ChannelHandlerContext) {
        val scopeNetty = CoroutineScope(
            ctx.channel().eventLoop().asCoroutineDispatcher() +
                    SupervisorJob() +
                    CoroutineExceptionHandler { _, throwable ->
                        logger.error("Netty coroutine error", throwable)
                    }
        )
        this.requestResponseHandler = RequestResponseHandler(scopeNetty, Attributes)
        logger.trace("New client connect >> {}", ctx.channel().remoteAddress())
        connectionRegistry.add(ConnectionRegistry.Connection(ctx.channel()))
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if(msg !is Request) return
        when(msg){
            is RegisterRequest -> requestResponseHandler?.registrationHandle(msg, userService, connectionRegistry, ctx)
            is AuthRequest -> requestResponseHandler?.authRequestHandle(msg, userService, connectionRegistry, ctx)
            is CreateGroupChatRequest -> requestResponseHandler?.createGroupRequestHandle(msg, chatService, ctx)
            is SendMessageRequest -> requestResponseHandler?.sendMessageRequestHandle(msg, chatService, messageService, connectionRegistry, ctx)
            is SetUsernameRequest -> requestResponseHandler?.setUsernameRequestHandle(msg, userService, ctx)
            is FindUserByUsernameRequest -> requestResponseHandler?.findUserByUsernameRequestHandle(msg, userService, ctx)
            is CreatePrivateChatRequest -> requestResponseHandler?.createPrivateChatRequestHandle(msg, chatService, connectionRegistry, ctx)
            is GetLastMessageRequest -> requestResponseHandler?.getLastMessage(msg, messageService, ctx)
        } // Разрастание данного блока. Решить или оставить?
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        val userId = ctx.channel().attr(userId).get()
        println(userId)
        if(userId != null) {
            logger.debug("Remove connection for id.")
            connectionRegistry.removeForId(userId)
        }else{
            logger.debug("Remove connection for channel")
            connectionRegistry.removeForChannel(ctx.channel())
        }
        logger.trace("Client {} disconnect", ctx.channel().remoteAddress())
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        logger.error("Handler error", cause)
        ctx.disconnect()
        ctx.close()
    }
}


private class RequestResponseHandler(private val scopeNetty: CoroutineScope, private val attributes: ServerHandler.Attributes){
    private val logger: Logger = LoggerFactory.getLogger(RequestResponseHandler::class.java)
    private fun metrics(timestamp1: Long, timestamp2: Long){
        logger.debug("Task compete >> {} ms", timestamp2 - timestamp1)
    }

    fun registrationHandle(request: RegisterRequest, userService: UserService, connectionRegistry: ConnectionRegistry, ctx: ChannelHandlerContext){
        logger.debug("Registration operation handle")
        scopeNetty.launch {
            val timestamp = System.currentTimeMillis()
            val response = withContext(Dispatchers.Default) {
                return@withContext userService.registrationUser(request)
            }
            if(response is RegisterResponse) {
                logger.info("User registration. Write id for registry.")
                ctx.channel().attr(attributes.userId).set(response.user.userId)
                connectionRegistry.setUserIdInConnection(ctx.channel(), response.user.userId)
            }
            ctx.writeAndFlush(response)
            val timestamp2 = System.currentTimeMillis()
            metrics(timestamp, timestamp2)
        }
    }

    fun authRequestHandle(request: AuthRequest, userService: UserService, connectionRegistry: ConnectionRegistry, ctx: ChannelHandlerContext){
        logger.info("Authorization operation handle")
        scopeNetty.launch {
            val timestamp1 = System.currentTimeMillis()
            val response = withContext(Dispatchers.Default) {
                return@withContext userService.authorizationUser(request)
            }
            if(response is AuthResponse){
                logger.info("User authorization. Write id for registry.")
                ctx.channel().attr(attributes.userId).set(response.user.userId)
                connectionRegistry.setUserIdInConnection(ctx.channel(), response.user.userId)
            }
            ctx.writeAndFlush(response)
            val timestamp2 = System.currentTimeMillis()
            metrics(timestamp1, timestamp2)
        }
    }
    fun createGroupRequestHandle(request: CreateGroupChatRequest, chatService: ChatService, ctx: ChannelHandlerContext){
        logger.info("Create group operation handle")
        scopeNetty.launch {
            val timestamp1 = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO) {
                val userId: UUID? = ctx.channel().attr(attributes.userId).get()
                if (userId != null) return@withContext chatService.createGroupChat(request, userId)
                else return@withContext ErrorResponse(
                    request.requestId,
                    MessFoxException("Failed create chat >> you not reg/auth"),
                    Instant.now()
                )
            }
            logger.debug("Writing...")
            ctx.writeAndFlush(response)
            val timestamp2 = System.currentTimeMillis()
            metrics(timestamp1, timestamp2)
        }
    }
    fun sendMessageRequestHandle(request: SendMessageRequest, chatService: ChatService, messageService: MessageService, connectionRegistry: ConnectionRegistry, ctx: ChannelHandlerContext){
        logger.info("Send message operation handle")
        scopeNetty.launch {
            val timestamp1 = System.currentTimeMillis()
            val userId: UUID? = ctx.channel().attr(attributes.userId).get()
            val response = withContext(Dispatchers.IO) {
                if (userId != null) return@withContext messageService.sendMessage(request, userId)
                else return@withContext ErrorResponse(
                    request.requestId,
                    MessFoxException("Failed send message >> you not reg/auth"),
                    Instant.now()
                )
            }
            logger.debug("Writing...")
            ctx.writeAndFlush(response)
            if(response is ReceiveMessageResponse){
                val list = withContext(Dispatchers.IO){
                    chatService.selectUsersForChatId(ChatMembersInChatRequest(UUID.randomUUID(), request.chatId, Instant.now()))
                }
                list.forEach {
                    if(it.userId != userId){
                        val channel: Channel? = if(connectionRegistry.contains(it.userId)) connectionRegistry.getConnectionForId(it.userId).channel else null
                        channel?.writeAndFlush(response)
                    }
                }
            }
            val timestamp2 = System.currentTimeMillis()
            metrics(timestamp1, timestamp2)
        }
    }
    fun setUsernameRequestHandle(request: SetUsernameRequest, userService: UserService, ctx: ChannelHandlerContext){
        logger.info("Set username operation...")
        scopeNetty.launch {
            val timestamp1 = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO){
                val userId: UUID? = ctx.channel().attr(attributes.userId).get()
                if(userId != null) return@withContext userService.setUserName(request, userId)
                else return@withContext ErrorResponse(request.requestId, MessFoxException("Error set userName operation. No authorization user"), Instant.now())
            }
            ctx.writeAndFlush(response)
            val timestamp2 = System.currentTimeMillis()
            metrics(timestamp1, timestamp2)
        }
    }
    fun findUserByUsernameRequestHandle(request: FindUserByUsernameRequest, userService: UserService, ctx: ChannelHandlerContext){
        logger.info("Find user by username operation...")
        scopeNetty.launch {
            val timestamp1 = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO){
                userService.findUserByUsername(request)
            }
            ctx.writeAndFlush(response)
            val timestamp2 = System.currentTimeMillis()
            metrics(timestamp1, timestamp2)
        }
    }
    fun createPrivateChatRequestHandle(request: CreatePrivateChatRequest, chatService: ChatService, connectionRegistry: ConnectionRegistry, ctx: ChannelHandlerContext){
        logger.info("Create private chat operation...")
        scopeNetty.launch {
            val timestamp1 = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO){
                val userId: UUID? = ctx.channel().attr(attributes.userId).get()
                if(userId != null)chatService.createPrivateChat(request, userId)
                else ErrorResponse(request.requestId, MessFoxException("Error create private chat. UserId is empty"), Instant.now())
            }
            val interlocutorChannel: Channel? = if(connectionRegistry.contains(request.interlocutor)) connectionRegistry.getConnectionForId(request.interlocutor).channel else null
            ctx.writeAndFlush(response)
            interlocutorChannel?.writeAndFlush(response)
            val timestamp2 = System.currentTimeMillis()
            metrics(timestamp1, timestamp2)
        }
    }
    fun getLastMessage(request: GetLastMessageRequest, messageService: MessageService, ctx: ChannelHandlerContext){
        logger.info("Get last message for chat ${request.chatId}")
        scopeNetty.launch {
            val timestamp1 = System.currentTimeMillis()
            val response = withContext(Dispatchers.IO){
                messageService.getLastMessage(request)
            }
            ctx.writeAndFlush(response)
            val timestamp2 = System.currentTimeMillis()
            metrics(timestamp1, timestamp2)
        }
    }
}
