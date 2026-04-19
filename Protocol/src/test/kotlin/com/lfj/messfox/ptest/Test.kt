package com.lfj.messfox.ptest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.lfj.messfox.protocol.Protocol
import com.lfj.messfox.protocol.datatype.Chat
import com.lfj.messfox.protocol.datatype.ChatMember
import com.lfj.messfox.protocol.datatype.Message
import com.lfj.messfox.protocol.datatype.User
import com.lfj.messfox.protocol.request.*
import com.lfj.messfox.protocol.response.*
import com.lfj.messfox.protocol.type.ChatType
import com.lfj.messfox.protocol.type.MessageType
import com.lfj.messfox.protocol.type.Role
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.Instant.now
import java.util.UUID.randomUUID
import kotlin.test.assertEquals

class TestTest{
    @Test
    fun test() = runTest {
        val mapper: ObjectMapper = ObjectMapper().registerModules(JavaTimeModule()).registerKotlinModule()

        val user = User(randomUUID(), "Example", "example", now())
        val chat = Chat(
            randomUUID(),
            ChatType.PRIVATE,
            chatTag = null,
            chatName = "F",
            listOf(
                ChatMember(
                    user.userId,
                    Role.MEMBER,
                    now()
                )
            ),
            now())
        val message = Message(randomUUID(),
            chat.chatId,
            user,
            MessageType.TEXT,
            "Hello World",
            now())

        val protocolList: List<Protocol> = listOf(
            AuthRequest(randomUUID(), "example@mail.com", "12345", now()),
            AuthResponse(randomUUID(), user, now()),
            RegisterRequest(randomUUID(), "Example", "example@mail.com", "12345", now()),
            RegisterResponse(randomUUID(), user, now()),
            HeartbeatRequest(randomUUID(), now()),
            HeartbeatsResponse(randomUUID(), now()),
            SendMessageRequest(randomUUID(), chat.chatId, MessageType.TEXT, "TEST", now()),
            ReceiveMessageResponse(randomUUID(), Message(randomUUID(), chat.chatId, user, MessageType.TEXT, "TEST", now()), now()),
            ErrorResponse(randomUUID(), Exception("Test"),now()),
            GetLastMessageRequest(randomUUID(), chat.chatId, now()),
            GetLatestMessagesRequest(randomUUID(), chat.chatId,  now()),
            GetMessagesAfterRequest(randomUUID(), chat.chatId, randomUUID(), now()),
            GetLastMessageResponse(randomUUID(), message, now()),
            GetLatestMessagesResponse(randomUUID(), chat.chatId, listOf(message), now()),
            GetMessagesAfterResponse(randomUUID(), chat.chatId, listOf(message), now()),
            FindUserByIdRequest(randomUUID(), user.userId, now()),
            FindUserByIdResponse(randomUUID(), user, now()),
            FindUserByUsernameRequest(randomUUID(), "meow", now()),
            FindUserByUsernameResponse(randomUUID(), listOf(), now()),
            FindUserByDisplayNameRequest(randomUUID(), user.displayName, now()),
            FindUserByDisplayNameResponse(randomUUID(), listOf(user), now()),
            CreateGroupChatRequest(randomUUID(), chat.chatName!!, chat.chatTag, listOf(randomUUID()),now()),
            CreateGroupChatResponse(randomUUID(), chat, now()),
            CreatePrivateChatRequest(randomUUID(), user.userId, now()),
            FindChatForTagResponse(randomUUID(), listOf(chat), now()),
            FindChatForNameRequest(randomUUID(), chat.chatName, now()),
            FindChatForNameResponse(randomUUID(), listOf(chat), now()),
            ChatMembersInChatRequest(randomUUID(), chat.chatId, now()),
            ChatMembersInChatResponse(randomUUID(), listOf(), now()),
            ChatsForUserRequest(randomUUID(), now()),
            ChatsForUserIdResponse(randomUUID(), listOf(), now()),
            AddChatMemberRequest(randomUUID(), randomUUID(), listOf(), now()),
            AddMemberChatResponse(randomUUID(), listOf(), now()),
            RemoveChatMemberRequest(randomUUID(), chat.chatId, user.userId, now()),
            RemoveChatMemberResponse(randomUUID(), chat.chatId, user.userId, now())
        )

        val strings: List<String> = protocolList.map{ mapper.writeValueAsString(it) }
        println("Serialization protocol visible:")
        strings.forEachIndexed { index, protocol -> println("$index - $protocol") }
        val protocolListAfter: List<Protocol> = strings.map { mapper.readValue(it) }
        println("Deserialization protocol visible:")
        protocolListAfter.forEachIndexed { index, protocol -> println("$index - $protocol") }

        protocolList.zip(protocolListAfter).forEachIndexed { index, (original, restored) ->
            assertEquals(original::class,
                restored::class,
                "Class mismatch at index $index: expected ${original::class.simpleName}, got ${restored::class.simpleName}")
            assertEquals(original.type, restored.type, "Type mismatch")
            assertEquals(original.requestId, restored.requestId, "RequestId mismatch")
            assertEquals(original.instant, restored.instant, "Instant mismatch")
        }
    }
}