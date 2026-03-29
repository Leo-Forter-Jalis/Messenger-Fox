package com.lfj.messfox.server.service

import com.lfj.messfox.protocol.Response
import com.lfj.messfox.protocol.datatype.Chat
import com.lfj.messfox.protocol.datatype.ChatMember
import com.lfj.messfox.protocol.request.*
import com.lfj.messfox.protocol.response.*
import com.lfj.messfox.protocol.type.Role
import com.lfj.messfox.server.dao.ChatDataTable
import com.lfj.messfox.server.dao.ChatMemberDataTable
import java.time.Instant

class ChatService(private val chatDAO: ChatDataTable, private val chatMemberDao: ChatMemberDataTable){
    fun createGroupChat(request: CreateGroupChatRequest) : Response{
        return chatDAO.createGroupChat(request.chatName, request.chatTag).fold(onSuccess = {chat ->
            val chatMembers: MutableList<ChatMember> = mutableListOf()
            chatMemberDao.addMemberForChat(chat.chatId, request.ownerId, Role.OWNER).fold(onSuccess = { owner ->
                chatMembers.add(owner)
                request.invitedUser?.run{
                    chatMembers.addAll(chatMemberDao.addMembersForChat(chat.chatId, this, Role.MEMBER))
                }
            }, onFailure = {  })
            CreateGroupChatResponse(request.requestId, Chat(chat.chatId, chat.chatType, chat.chatTag, chat.chatName, chatMembers.toList(), chat.createdAt), Instant.now())
        }, onFailure = {exception -> ErrorResponse(request.requestId, exception, Instant.now())})
    }
    fun createPrivateChat(request: CreatePrivateChatRequest) : Response{
        return chatDAO.createPrivateChat().fold(onSuccess = { chatB ->
            val list = chatMemberDao.addMembersForChat(chatB.chatId, request.twoUsers, Role.MEMBER)
            CreatePrivateChatResponse(request.requestId, Chat(chatB.chatId, chatB.chatType, null, null, list, chatB.createdAt), Instant.now())
        }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) })
    }
    fun deleteChat(request: DeleteChatRequest) : Response{
        TODO("This function not realization")
    }
    fun findChatForChatId(request: FindChatForIdRequest) : Response{
        return chatDAO.findChatForId(request.chatId).fold(onSuccess = { chat ->
            FindChatForIdResponse(request.requestId, Chat(chat.chatId, chat.chatType, chat.chatTag, chat.chatName, null, chat.createdAt), Instant.now())
        }, onFailure ={ exception ->  ErrorResponse(request.requestId, exception, Instant.now()) })
    }
    fun findChatForTag(request: FindChatForTagRequest) : Response{
        return chatDAO.findChatForTag(request.chatTag).fold(onSuccess = { chats ->
            FindChatForTagResponse(request.requestId, chats.map{ chatBillet -> Chat(chatBillet.chatId, chatBillet.chatType, chatBillet.chatTag, chatBillet.chatName, null, chatBillet.createdAt) }, Instant.now())
        }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) })
    }
    fun findChatForName(request: FindChatForNameRequest) : Response{
        return chatDAO.findChatForName(request.chatName).fold(onSuccess = { chats ->
            FindChatForNameResponse(request.requestId, chats.map{ chatBillet -> Chat(chatBillet.chatId, chatBillet.chatType, chatBillet.chatTag, chatBillet.chatName, null, chatBillet.createdAt) }, Instant.now())
        }, onFailure = { exception ->  ErrorResponse(request.requestId, exception, Instant.now())})
    }
    fun addMemberForChat(request: AddChatMemberRequest) : Response{
        val result: MutableList<ChatMember> = mutableListOf()
        request.invitedUser.forEach { chatMemberDao.addMemberForChat(request.chatId, it, Role.MEMBER).fold(onSuccess = { member ->
            result.add(member)
        }, onFailure = { }) }
        return AddMemberChatResponse(request.requestId, result.toList(), Instant.now())
    }
}