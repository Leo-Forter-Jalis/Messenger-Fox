package com.lfj.messfox.protocol.datatype

import com.lfj.messfox.protocol.type.ChatType
import com.lfj.messfox.protocol.type.MessageType
import com.lfj.messfox.protocol.type.Role
import java.time.Instant
import java.util.*

data class User(val userId: UUID,
                val displayName: String,
                var userName: String?, // optional parameter
                val createdAt: Instant)

data class Message(val messageId: UUID,
                   val chatId: UUID,
                   val sender: User,
                   val messageType: MessageType,
                   val content: String,
                   val sentAt: Instant)

data class Chat(
    val chatId: UUID,
    val chatType: ChatType,
    var chatTag: String?, // optional parameter
    val chatName: String?,
    val members: List<ChatMember>?, // optional parameter
    val createdAt: Instant)

data class ChatMember(
    val userId: UUID,
    val role: Role,
    val joinedAt: Instant
)