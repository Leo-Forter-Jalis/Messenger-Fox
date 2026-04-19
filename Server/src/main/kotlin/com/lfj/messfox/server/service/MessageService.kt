package com.lfj.messfox.server.service

import com.lfj.messfox.protocol.Response
import com.lfj.messfox.protocol.datatype.Message
import com.lfj.messfox.protocol.request.GetLastMessageRequest
import com.lfj.messfox.protocol.request.SendMessageRequest
import com.lfj.messfox.protocol.response.ErrorResponse
import com.lfj.messfox.protocol.response.GetLastMessageResponse
import com.lfj.messfox.protocol.response.ReceiveMessageResponse
import com.lfj.messfox.server.dao.MessageDataTable
import com.lfj.messfox.server.dao.UserDataTable
import java.time.Instant
import java.util.*

class MessageService(private val messageDao: MessageDataTable, private val userDao: UserDataTable) {
    fun sendMessage(request: SendMessageRequest, senderId: UUID) : Response {
        return messageDao.insertMessage(request.chatId, senderId, request.messageType, request.content).fold(onSuccess = { messageB ->
            userDao.findUserById(senderId).fold(onSuccess = { user ->
                ReceiveMessageResponse(request.requestId, Message(messageB.messageId, messageB.chatId, user, messageB.messageType, messageB.content, messageB.createdAt), Instant.now())
            }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) })
        }, onFailure = { exception -> ErrorResponse(request.requestId, exception, Instant.now()) })
    }
    fun deleteMessage(messageId: UUID) : Boolean {
        return messageDao.deleteMessage(messageId)
    }
    fun getLastMessage(request: GetLastMessageRequest) : Response {
        return messageDao.getLastMessage(request.chatId).fold(onSuccess = { billet ->
            userDao.findUserById(billet.senderId).fold(onSuccess = { user ->
                GetLastMessageResponse(request.requestId, Message(billet.messageId, billet.chatId, user, billet.messageType, billet.content, billet.createdAt), Instant.now())
            }, onFailure = { exception ->  ErrorResponse(request.requestId, exception, Instant.now()) })
        }, onFailure = { exception ->  ErrorResponse(request.requestId, exception, Instant.now()) })
    }
}