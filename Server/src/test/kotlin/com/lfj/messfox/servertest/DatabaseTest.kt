package com.lfj.messfox.servertest

import com.lfj.messfox.protocol.Response
import com.lfj.messfox.protocol.datatype.User
import com.lfj.messfox.protocol.request.*
import com.lfj.messfox.protocol.response.*
import com.lfj.messfox.protocol.type.MessageType
import com.lfj.messfox.server.dao.ChatDataTable.H2ChatDAO
import com.lfj.messfox.server.dao.ChatMemberDataTable.H2ChatMemberDao
import com.lfj.messfox.server.dao.MessageDataTable.H2MessageDAO
import com.lfj.messfox.server.dao.UserDataTable.H2UserDAO
import com.lfj.messfox.server.service.ChatService
import com.lfj.messfox.server.service.DataBase
import com.lfj.messfox.server.service.MessageService
import com.lfj.messfox.server.service.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant.now
import java.util.UUID.randomUUID
import javax.sql.DataSource

class ServiceTest {
    val dataSource: DataSource = DataBase.H2TestDB.dataSource()
    val userDao = H2UserDAO(dataSource)
    val chatDao = H2ChatDAO(dataSource)
    val messageDao = H2MessageDAO(dataSource)
    val chatMemberDao = H2ChatMemberDao(dataSource)
    @Test
    fun userDaoTest(){
        val userService: UserService = UserService(userDao)
        val request: RegisterRequest = RegisterRequest(randomUUID(), "Example", "example@mail.com", "12345", now())
        val request1: AuthRequest = AuthRequest(randomUUID(), "example@mail.com", "12345", now())
        val response: Response = userService.authorizationUser(request1)
        val response1: Response = userService.registrationUser(request)
        val user: User? = if(response1 is RegisterResponse) response1.user else null
        val response2: Response = userService.registrationUser(request)
        val response3: Response = userService.authorizationUser(request1)
        assert(response is ErrorResponse, { "response is not ErrorResponse >> ${response.type}" })
        assert(response1 is RegisterResponse, { "response1 is not RegisterResponse >> ${response1.type}" })
        assert(response2 is ErrorResponse, { "response2 is not ErrorResponse >> ${response2.type}" })
        assert(response3 is AuthResponse, { "response3 is not AuthResponse >> ${response3.type}" })
        user?.let{
            println("User state test")
            val request4: FindUserByIdRequest = FindUserByIdRequest(randomUUID(), user.userId, now())
            val request5: FindUserByUsernameRequest = FindUserByUsernameRequest(randomUUID(), "test", now())
            val request6: FindUserByDisplayNameRequest = FindUserByDisplayNameRequest(
                randomUUID(), user.displayName,
                now()
            )
            val response4: Response = userService.findUserById(request4)
            val response5: Response = userService.findUserById(request4)
            val response6: Response = userService.findUserByUsername(request5)
            val response7: Response = userService.findUserByDisplayName(request6)
            assert(response4 is FindUserByIdResponse, { "response4 is not FindUserByIdResponse >> ${response4.type}" })
            assert(!(response5 is ErrorResponse), { "response5 is ErrorResponse >> ${response5.type}" })
            assert(response6 is FindUserByUsernameResponse, { "response6 is not FindUserByUsernameResponse >> ${response6.type}" })
            assert(response7 is FindUserByDisplayNameResponse, { "response7 is not FindUserByDisplayNameResponse >> ${response7.type}" })
        }
    }
    @Test
    fun chatDaoTest(){
        val userService: UserService = UserService(userDao)
        val chatService: ChatService = ChatService(chatDao, chatMemberDao)
        val response1: Response = userService.registrationUser(RegisterRequest(randomUUID(), "Example", "example1@mail.com", "12345", now()))
        val response2: Response = userService.registrationUser(RegisterRequest(randomUUID(), "Exampli", "example2@mail.com", "12345", now()))
        if(response1 is RegisterResponse && response2 is RegisterResponse){
            println(response1.user)
            println(response2.user)
            val request: CreatePrivateChatRequest = CreatePrivateChatRequest(randomUUID(), response2.user.userId, now())
            val request1: CreateGroupChatRequest = CreateGroupChatRequest(randomUUID(), "TEST", "Meow", listOf(response2.user.userId), now())
            val request2: CreateGroupChatRequest = CreateGroupChatRequest(randomUUID(), "TEST", null, listOf(response2.user.userId), now())
            val response3: Response = chatService.createGroupChat(request1, response1.user.userId)
            val response4: Response = chatService.createGroupChat(request2, response1.user.userId)
            val response: Response = chatService.createPrivateChat(request, response1.user.userId)
            println(response)
            println(response3)
            println(response4)
        }
    }
    @Test
    fun messageDaoTest(){
        val userService = UserService(userDao)
        val chatService = ChatService(chatDao, chatMemberDao)
        val messageService = MessageService(messageDao, userDao)
        val responseUser1 = userService.registrationUser(RegisterRequest(randomUUID(), "K","example3@mail.com", "12345", now()))
        val responseUser2 = userService.registrationUser(RegisterRequest(randomUUID(), "F","example4@mail.com", "12345", now()))
        if(responseUser1 is RegisterResponse && responseUser2 is RegisterResponse){
            val user1 = responseUser1.user
            val user2 = responseUser2.user
            val responseChat = chatService.createPrivateChat(CreatePrivateChatRequest(randomUUID(), user2.userId, now()), user1.userId)
            if(responseChat !is CreatePrivateChatResponse){
                println(responseChat)
                return
            }
            val chat = responseChat.chat
            val messageResponse = messageService.sendMessage(SendMessageRequest(randomUUID(), chat.chatId, MessageType.TEXT, "TEST TEST TEST TEST TEST TEST TEST", now()), user1.userId)
            if(messageResponse !is ReceiveMessageResponse){
                println(messageResponse)
                return
            }
            println(messageResponse.message)
        }
    }
    fun userTest(user: User, user2: User){
        assertEquals(user.userId, user2.userId, "userId mismatch")
        assertEquals(user.userName, user2.userName, "userName mismatch")
        assertEquals(user.displayName, user2.displayName, "displayName mismatch")
        assertEquals(user.createdAt, user2.createdAt)
    }
}
