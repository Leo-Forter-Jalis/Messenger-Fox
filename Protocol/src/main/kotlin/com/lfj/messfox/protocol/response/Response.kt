package com.lfj.messfox.protocol.response

import com.lfj.messfox.protocol.Response
import com.lfj.messfox.protocol.datatype.Chat
import com.lfj.messfox.protocol.datatype.ChatMember
import com.lfj.messfox.protocol.datatype.Message
import com.lfj.messfox.protocol.datatype.User
import java.time.Instant
import java.util.UUID

data class AuthResponse(override val requestId: UUID, // User
                        val user: User,
                        override val instant: Instant) // Ответ авторизации
    : Response(requestId, type = "AUTH_RESPONSE", instant)
data class RegisterResponse(override val requestId: UUID, // User
                            val user: User,
                            override val instant: Instant) // Ответ регистрации
    : Response(requestId, type = "REGISTER_RESPONSE", instant)
data class SetUsernameResponse(override val requestId: UUID,
                               val user: User,
                               override val instant: Instant)
    : Response(requestId, type = "SET_USERNAME_RESPONSE", instant)
data class ErrorResponse(override val requestId: UUID,
                         val exception: Throwable,
                         override val instant: Instant) // Возвращение ошибки
    : Response(requestId, type = "ERROR_RESPONSE", instant)
data class ReceiveMessageResponse(override val requestId: UUID,
                                  val message: Message,
                                  override val instant: Instant) // Возвращаем сообщение участникам
    : Response(requestId, type = "RECEIVE_MESSAGE_RESPONSE", instant)
data class CreateGroupChatResponse(override val requestId: UUID,
                              val chat: Chat,
                              override val instant: Instant)
    : Response(requestId, type = "CREATE_GROUP_CHAT_RESPONSE", instant)
data class CreatePrivateChatResponse(override val requestId: UUID,
                                     val chat: Chat,
                                     override val instant: Instant)
    : Response(requestId, type = "CREATE_PRIVATE_CHAT_RESPONSE", instant)
data class DeleteChatResponse(override val requestId: UUID,
                              override val instant: Instant)
    : Response(requestId, type = "DELETE_CHAT_RESPONSE", instant)
data class AddMemberChatResponse(override val requestId: UUID, // Add members in chat
                                 val members: List<ChatMember>,
                                 override val instant: Instant)
    : Response(requestId, type = "ADD_MEMBER_CHAT_RESPONSE", instant)
data class RemoveChatMemberResponse(override val requestId: UUID,
                                   val chatId: UUID,
                                   val userId: UUID,
                                   override val instant: Instant)
    : Response(requestId, type = "REMOVE_CHAT_MEMBER_RESPONSE", instant)
data class ChatMembersInChatResponse(override val requestId: UUID,
                                       val members: List<ChatMember>,
                                       override val instant: Instant)
    : Response(requestId, type = "CHAT_MEMBERS_IN_CHAT_RESPONSE", instant)
data class ChatsForUserIdResponse(override val requestId: UUID,
                                  val chats: List<Chat>,
                                  override val instant: Instant)
    : Response(requestId, type = "CHATS_FOR_USER_ID_RESPONSE", instant)
data class FindChatForIdResponse(override val requestId: UUID,
                                 val chat: Chat,
                                 override val instant: Instant)
    : Response(requestId, type = "FIND_CHAT_FOR_ID_RESPONSE", instant)
data class FindChatForTagResponse(override val requestId: UUID,
                                  val chats: List<Chat>,
                                  override val instant: Instant)
    : Response(requestId, type = "FIND_CHAT_FOR_TAG_RESPONSE", instant)
data class FindChatForNameResponse(override val requestId: UUID,
                                   val chats: List<Chat>,
                                   override val instant: Instant)
    : Response(requestId, type = "FIND_CHAT_FOR_NAME_RESPONSE", instant)
data class HeartbeatsResponse(override val requestId: UUID, // Возвращает информацию о подключении
                      override val instant: Instant)
    : Response(requestId, type = "HEARTBEAT_RESPONSE", instant)
data class GetMessageForId(override val requestId: UUID,
                           val message: Message,
                           override val instant: Instant)
    : Response(requestId, type = "GET_MESSAGE_FOR_ID", instant)
data class GetMessageForSenderId(override val requestId: UUID,
                                 val messages: List<Message>,
                                 override val instant: Instant)
    : Response(requestId, type = "GET_MESSAGE_FOR_SENDER_ID", instant)
data class GetLastMessageResponse(override val requestId: UUID, // Получение последнего сообщение
                                  val lastMessage: Message,
                                  override val instant: Instant)
    : Response(requestId, type = "GET_LAST_MESSAGE_RESPONSE", instant)
data class GetLatestMessagesResponse(override val requestId: UUID, // Получение последних сообщений
                                      val chatId: UUID,
                                      val messages: List<Message>,
                                      override val instant: Instant)
    : Response(requestId, type = "GET_LATEST_MESSAGES_RESPONSE", instant)
data class GetMessagesAfterResponse(override val requestId: UUID, // Получение сообщений после определенного
                                    val chatId: UUID,
                                    val messages: List<Message>,
                                    override val instant: Instant)
    : Response(requestId, type = "GET_MESSAGES_AFTER_RESPONSE", instant)
data class FindUserByIdResponse(override val requestId: UUID,
                                val user: User,
                                override val instant: Instant)
    : Response(requestId, type = "FIND_USER_BY_ID_RESPONSE", instant)
data class FindUserByUsernameResponse(override val requestId: UUID,
                                      val users: List<User>,
                                      override val instant: Instant)
    : Response(requestId, type = "FIND_USER_BY_USERNAME_RESPONSE", instant)
data class FindUserByDisplayNameResponse(override val requestId: UUID,
                                      val users: List<User>,
                                      override val instant: Instant)
    : Response(requestId, type = "FIND_USER_BY_DISPLAY_NAME_RESPONSE", instant)