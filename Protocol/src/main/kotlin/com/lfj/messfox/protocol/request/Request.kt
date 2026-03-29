package com.lfj.messfox.protocol.request

import com.lfj.messfox.protocol.Request
import com.lfj.messfox.protocol.type.MessageType
import java.time.Instant
import java.util.*

data class AuthRequest (override val requestId: UUID,
                        val email: String,
                        val password: String,
                        override val instant: Instant) // Отправить запрос на авторизацию
    : Request(requestId, type="AUTH_REQUEST", instant)
data class RegisterRequest(override val requestId: UUID,
                           val displayName: String,
                           val email: String,
                           val password: String,
                           override val instant: Instant) // Отправить запрос на регистрацию
    : Request(requestId, type="REGISTER_REQUEST",instant)
data class SendMessageRequest(override val requestId: UUID,
                              val chatId: UUID,
                              val messageType: MessageType,
                              val content: String,
                              override val instant: Instant) // Отправить сообщение в чат
    : Request(requestId, type = "SEND_MESSAGE_REQUEST", instant)
data class GetLastMessageRequest(override val requestId: UUID,
                                 val chatId: UUID,
                                 override val instant: Instant) // Получение последнего сообщения
    : Request(requestId, type = "GET_LAST_MESSAGE_REQUEST", instant)
data class GetMessagesAfterRequest(override val requestId: UUID,
                                      val chatId: UUID,
                                      val messageId: UUID,
                                      val limit: Int,
                                      override val instant: Instant) // Получение сообщений по идентификатору сообщения
    : Request(requestId, type = "GET_MESSAGES_AFTER_REQUEST", instant)
data class GetLatestMessagesRequest(override val requestId: UUID,
                             val chatId: UUID,
                             val limit: Int,
                             override val instant: Instant) // Запрос на получение новых сообщений, при открытии чата
    : Request(requestId, type = "GET_LATEST_MESSAGES_REQUEST", instant)
data class HeartbeatRequest(override val requestId: UUID,
                            override val instant: Instant) // Сердцебиение
    : Request(requestId, type = "HEARTBEAT_REQUEST", instant)
data class CreateGroupChatRequest(override val requestId: UUID,
                             val chatName: String,
                             val chatTag: String?,
                             val invitedUser: List<UUID>?,
                             val ownerId: UUID,
                             override val instant: Instant)
    : Request(requestId, type = "CREATE_GROUP_CHAT_REQUEST", instant)
data class CreatePrivateChatRequest(override val requestId: UUID,
                                    val twoUsers: List<UUID>,
                                    override val instant: Instant)
    : Request(requestId, "CREATE_PRIVATE_CHAT_REQUEST", instant)
data class DeleteChatRequest(override val requestId: UUID,
                             val chatId: UUID,
                             override val instant: Instant)
    : Request(requestId, type = "DELETE_CHAT_REQUEST", instant)
data class AddChatMemberRequest(override val requestId: UUID,
                                val chatId: UUID,
                                val invitedUser: List<UUID>,
                                override val instant: Instant)
    : Request(requestId, type = "ADD_CHAT_MEMBER_REQUEST", instant)
data class RemoveChatMemberRequest(override val requestId: UUID,
                                   val chatId: UUID,
                                   val userId: UUID,
                                   override val instant: Instant)
    : Request(requestId, type = "REMOVE_CHAT_MEMBER_REQUEST", instant)
data class ChatMembersInChatRequest(override val requestId: UUID,
                                    val chatId: UUID,
                                    override val instant: Instant)
    : Request(requestId, type = "CHAT_MEMBERS_IN_CHAT_REQUEST", instant)
data class ChatsForUserRequest(override val requestId: UUID,
                               override val instant: Instant)
    : Request(requestId, type = "CHATS_FOR_USER_REQUEST", instant) // Временный запрос
data class FindChatForIdRequest(override val requestId: UUID,
                                val chatId: UUID,
                                override val instant: Instant)
    : Request(requestId, type = "FIND_CHAT_FOR_ID_REQUEST", instant)
data class FindChatForTagRequest(override val requestId: UUID,
                                 val chatTag: String,
                                 override val instant: Instant)
    : Request(requestId, type = "FIND_CHAT_FOR_TAG_REQUEST", instant)
data class FindChatForNameRequest(override val requestId: UUID,
                                  val chatName: String,
                                  override val instant: Instant)
    : Request(requestId, type = "FIND_CHAT_FOR_NAME_REQUEST", instant)
data class FindUserByIdRequest(override val requestId: UUID,
                               val userId: UUID,
                               override val instant: Instant)
    : Request(requestId, type = "FIND_USER_BY_ID_REQUEST", instant)
data class FindUserByUsernameRequest(override val requestId: UUID,
                              val userName: String,
                              override val instant: Instant)
    : Request(requestId, type = "FIND_USER_BY_USERNAME_REQUEST", instant)
data class FindUserByDisplayNameRequest(override val requestId: UUID,
                                        val displayName: String,
                                        override val instant: Instant)
    : Request(requestId, type = "FIND_USER_BY_DISPLAY_NAME_REQUEST", instant)