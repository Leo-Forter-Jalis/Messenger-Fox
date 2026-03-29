package com.lfj.messfox.protocol

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.lfj.messfox.protocol.request.*
import com.lfj.messfox.protocol.response.*
import java.time.Instant
import java.util.*

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
    visible = true
)

@JsonSubTypes(
    JsonSubTypes.Type(value = AuthRequest::class, name = "AUTH_REQUEST"),
        JsonSubTypes.Type(value = RegisterRequest::class, name = "REGISTER_REQUEST"),
        JsonSubTypes.Type(value = SendMessageRequest::class, name = "SEND_MESSAGE_REQUEST"),
        JsonSubTypes.Type(value = GetLastMessageRequest::class, name = "GET_LAST_MESSAGE_REQUEST"),
        JsonSubTypes.Type(value = GetMessagesAfterRequest::class, name = "GET_MESSAGES_AFTER_REQUEST"),
        JsonSubTypes.Type(value = GetLatestMessagesRequest::class, name = "GET_LATEST_MESSAGES_REQUEST"),
        JsonSubTypes.Type(value = HeartbeatRequest::class, name = "HEARTBEAT_REQUEST"),
        JsonSubTypes.Type(value = AuthResponse::class, name= "AUTH_RESPONSE"),
        JsonSubTypes.Type(value = RegisterResponse::class, name = "REGISTER_RESPONSE"),
        JsonSubTypes.Type(value = ErrorResponse::class, name = "ERROR_RESPONSE"),
        JsonSubTypes.Type(value = ReceiveMessageResponse::class, name = "RECEIVE_MESSAGE_RESPONSE"),
        JsonSubTypes.Type(value = HeartbeatsResponse::class, name = "HEARTBEAT_RESPONSE"),
        JsonSubTypes.Type(value = GetLastMessageResponse::class, name = "GET_LAST_MESSAGE_RESPONSE"),
        JsonSubTypes.Type(value = GetLatestMessagesResponse::class, name = "GET_LATEST_MESSAGES_RESPONSE"),
        JsonSubTypes.Type(value = GetMessagesAfterResponse::class, name = "GET_MESSAGES_AFTER_RESPONSE"),
        JsonSubTypes.Type(value = FindUserByIdRequest::class, name= "FIND_USER_BY_ID_REQUEST"),
        JsonSubTypes.Type(value = FindUserByIdResponse::class, name = "FIND_USER_BY_ID_RESPONSE"),
        JsonSubTypes.Type(value = FindUserByUsernameRequest::class, name = "FIND_USER_BY_USERNAME_REQUEST"),
        JsonSubTypes.Type(value = FindUserByUsernameResponse::class, name = "FIND_USER_BY_USERNAME_RESPONSE"),
        JsonSubTypes.Type(value = FindUserByDisplayNameRequest::class, name = "FIND_USER_BY_DISPLAY_NAME_REQUEST"),
        JsonSubTypes.Type(value = FindUserByDisplayNameResponse::class, name = "FIND_USER_BY_DISPLAY_NAME_RESPONSE"),
        JsonSubTypes.Type(value = CreateGroupChatRequest::class, name = "CREATE_GROUP_CHAT_REQUEST"),
        JsonSubTypes.Type(value = CreateGroupChatResponse::class, name = "CREATE_GROUP_CHAT_RESPONSE"),
        JsonSubTypes.Type(value = CreatePrivateChatRequest::class, name = "CREATE_PRIVATE_CHAT_REQUEST"),
        JsonSubTypes.Type(value = CreatePrivateChatResponse::class, name = "CREATE_PRIVATE_CHAT_RESPONSE"),
        JsonSubTypes.Type(value = DeleteChatRequest::class, name = "DELETE_CHAT_REQUEST"),
        JsonSubTypes.Type(value = DeleteChatResponse::class, name = "DELETE_CHAT_RESPONSE"),
        JsonSubTypes.Type(value = FindChatForIdRequest::class, name = "FIND_CHAT_FOR_ID_REQUEST"),
        JsonSubTypes.Type(value = FindChatForIdResponse::class, name = "FIND_CHAT_FOR_ID_RESPONSE"),
        JsonSubTypes.Type(value = FindChatForTagRequest::class, name = "FIND_CHAT_FOR_TAG_REQUEST"),
        JsonSubTypes.Type(value = FindChatForTagResponse::class, name = "FIND_CHAT_FOR_TAG_RESPONSE"),
        JsonSubTypes.Type(value = FindChatForNameRequest::class, name = "FIND_CHAT_FOR_NAME_REQUEST"),
        JsonSubTypes.Type(value = FindChatForNameResponse::class, name = "FIND_CHAT_FOR_NAME_RESPONSE"),
        JsonSubTypes.Type(value = ChatMembersInChatRequest::class, name = "CHAT_MEMBERS_IN_CHAT_REQUEST"),
        JsonSubTypes.Type(value = ChatMembersInChatResponse::class, name = "CHAT_MEMBERS_IN_CHAT_RESPONSE"),
        JsonSubTypes.Type(value = AddChatMemberRequest::class, name = "ADD_CHAT_MEMBER_REQUEST"),
        JsonSubTypes.Type(value = AddMemberChatResponse::class, name = "ADD_MEMBER_CHAT_RESPONSE"),
        JsonSubTypes.Type(value = RemoveChatMemberRequest::class, name = "REMOVE_CHAT_MEMBER_REQUEST"),
        JsonSubTypes.Type(value = RemoveChatMemberResponse::class, name = "REMOVE_CHAT_MEMBER_RESPONSE"),
        JsonSubTypes.Type(value = ChatsForUserRequest::class, name = "CHATS_FOR_USER_REQUEST"),
        JsonSubTypes.Type(value = ChatsForUserIdResponse::class, name = "CHATS_FOR_USER_ID_REQUEST")
)
abstract class Protocol (open val requestId: UUID, open val type: String, open val instant: Instant)

abstract class Request(override val requestId: UUID, override val type: String, override val instant: Instant) : Protocol(requestId, type, instant)
abstract class Response(override val requestId: UUID, override val type: String , override val instant: Instant) : Protocol(requestId, type, instant)
