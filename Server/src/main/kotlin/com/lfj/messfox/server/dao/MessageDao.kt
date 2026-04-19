package com.lfj.messfox.server.dao

import com.lfj.messenger.uuid7.UUIDv7
import com.lfj.messfox.exceptions.MessFoxException
import com.lfj.messfox.protocol.type.MessageType
import com.lfj.messfox.server.ifPresentOrElseWithResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import javax.sql.DataSource
import kotlin.reflect.KClass

interface MessageDataTable{
    fun insertMessage(chatId: UUID, senderId: UUID, messageType: MessageType, content: String) : Result<MessageBillet>
    fun deleteMessage(messageId: UUID) : Boolean
    fun findMessageForId(messageId: UUID, chatId: UUID) : Result<MessageBillet>
    fun findMessagesForSenderId(senderId: UUID, chatId: UUID) : List<MessageBillet>
    fun getLastMessage(chatId: UUID) : Result<MessageBillet>

    interface CompanionsSQLRequest{
        val INSERT_MESSAGE: String
        val DELETE_MESSAGE: String
        val FIND_MESSAGE_FOR_ID: String
        val FIND_MESSAGES_FOR_SENDER_ID: String
        val GET_LAST_MESSAGE: String
        val GET_LATEST_MESSAGES: String
        val GET_ORDER_MESSAGES: String
    }

    fun mapToMessageBillet(rs: ResultSet) : MessageBillet{
        return MessageBillet(rs.getObject("message_id", UUID::class.java), rs.getObject("chat_id", UUID::class.java), rs.getObject("sender_id", UUID::class.java), MessageType.valueOf(rs.getString("message_type")), rs.getString("content"), rs.getTimestamp("created_at").toInstant())
    }

    open class AbstractMessageDAO <T : MessageDataTable> (open val dataSource: DataSource, val sqlRequest: CompanionsSQLRequest, clazz: KClass<T>) : MessageDataTable{
        private val logger: Logger = LoggerFactory.getLogger(clazz.java)
        override fun insertMessage(chatId: UUID, senderId: UUID, messageType: MessageType, content: String): Result<MessageBillet> {
            return try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.INSERT_MESSAGE).use { stmt ->
                        val messageId: UUID = UUIDv7.next()
                        UUIDv7.getTimestamp(messageId).ifPresentOrElseWithResult(action = {
                            stmt.setObject(1, messageId)
                            stmt.setObject(2, chatId)
                            stmt.setObject(3, senderId)
                            stmt.setString(4, messageType.name)
                            stmt.setString(5, content)
                            stmt.setTimestamp(6, Timestamp.from(it))
                            stmt.executeUpdate()
                            Result.success(MessageBillet(messageId, chatId, senderId, messageType, content, it))
                        }, emptyAction = { Result.failure(IllegalStateException("")) })
                    }
                }
            }catch (e: SQLException){
                logger.error("MessageDao error", e)
                Result.failure(e)
            }
        }

        override fun deleteMessage(messageId: UUID) : Boolean {
            try { // Добавить проверку идентификатора отправителя
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.DELETE_MESSAGE).use { stmt ->
                        stmt.setObject(1, messageId)
                        stmt.executeUpdate()
                        return true
                    }
                }
            }catch (e: SQLException){
                logger.error("MessageDAO Error", e)
                return false
            }
        }

        override fun findMessageForId(messageId: UUID, chatId: UUID): Result<MessageBillet> {
            return try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.FIND_MESSAGE_FOR_ID).use { stmt ->
                        stmt.setObject(1, chatId)
                        stmt.setObject(2, messageId)
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) {
                                logger.debug("Message find by id SUCCESS")
                                Result.success(mapToMessageBillet(rs))
                            } else {
                                logger.debug("Message find by id FAILED")
                                Result.failure(IllegalStateException(""))
                            }
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("MessageDAO error", e)
                Result.failure(e)
            }
        }

        override fun findMessagesForSenderId(senderId: UUID, chatId: UUID): List<MessageBillet> {
            return try{
                val mutableList: MutableList<MessageBillet> = mutableListOf()
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.FIND_MESSAGES_FOR_SENDER_ID).use { stmt ->
                        stmt.setObject(1, chatId)
                        stmt.setObject(2, senderId)
                        stmt.executeQuery().use{ rs ->
                            while (rs.next()){
                                mutableList.add(mapToMessageBillet(rs))
                            }
                        }
                        mutableList.toList()
                    }
                }
            }catch (e: SQLException){
                logger.error("MessageDAO error", e)
                listOf()
            }
        }
        override fun getLastMessage(chatId: UUID) : Result<MessageBillet> {
            return try{
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.GET_LAST_MESSAGE).use { stmt ->
                        stmt.setObject(1, chatId)
                        stmt.executeQuery().use { rs ->
                            if(rs.next()) Result.success(mapToMessageBillet(rs))
                            else Result.failure(MessFoxException("Message not found!"))
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("MessageDAO error", e)
                Result.failure(MessFoxException("Message not found!", e))
            }
        }
    }

    class H2MessageDAO(override val dataSource: DataSource) : AbstractMessageDAO<H2MessageDAO>(dataSource, h2SQLRequest, H2MessageDAO::class){
        private companion object h2SQLRequest : CompanionsSQLRequest{
            override val INSERT_MESSAGE = """
                INSERT INTO messages_table(message_id, chat_id, sender_id, message_type, content, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
            override val DELETE_MESSAGE = """
                DELETE FROM messages_table
                WHERE message_id = ? AND user_id OR ? 
            """.trimIndent() // ???
            override val FIND_MESSAGE_FOR_ID = """
                SELECT message_id, chat_id, sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ? AND message_id = ?
            """.trimIndent()
            override val FIND_MESSAGES_FOR_SENDER_ID = """
                SELECT message_id, chat_id, sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ? AND sender_id = ?
                LIMIT 19
            """.trimIndent()
            override val GET_LAST_MESSAGE: String = """
                SELECT message_id, chat_id, sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ?
                ORDER BY message_id DESC
                LIMIT 1 
            """.trimIndent()
            override val GET_LATEST_MESSAGES: String = """
                SELECT message_id, chat_id, sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ?
                ORDER BY message_id DESC
                LIMIT 50 
            """.trimIndent()
            override val GET_ORDER_MESSAGES: String = """
                SELECT message_id, chat_id. sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ? AND message_id < ?
                LIMIT 50 
            """.trimIndent()
        }
    }
    class PostgresMessageDAO(override val dataSource: DataSource) : AbstractMessageDAO<PostgresMessageDAO>(dataSource, postgreSQLRequest, PostgresMessageDAO::class){
        private companion object postgreSQLRequest : CompanionsSQLRequest{
            override val INSERT_MESSAGE = """
                INSERT INTO messages_table(message_id, chat_id, sender_id, message_type, content, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(message_id) DO NOTHING
            """.trimIndent()
            override val DELETE_MESSAGE = """
                DELETE FROM messages_table
                WHERE message_id = ?
            """.trimIndent()
            override val FIND_MESSAGE_FOR_ID = """
                SELECT message_id, chat_id, sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ? AND message_id = ?
            """.trimIndent()
            override val FIND_MESSAGES_FOR_SENDER_ID = """
                SELECT message_id, chat_id, sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ? AND sender_id = ?
                LIMIT 19
            """.trimIndent()
            override val GET_LAST_MESSAGE: String = """
                SELECT message_id, chat_id, sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ?
                ORDER BY message_id DESC
                LIMIT 1 
            """.trimIndent()
            override val GET_LATEST_MESSAGES: String = """
                SELECT message_id, chat_id, sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ?
                ORDER BY message_id DESC
                LIMIT 50 
            """.trimIndent()
            override val GET_ORDER_MESSAGES: String = """
                SELECT message_id, chat_id. sender_id, message_type, content, created_at
                FROM messages_table
                WHERE chat_id = ? AND message_id < ?
                LIMIT 50 
            """.trimIndent()
        }
    }
    data class MessageBillet(val messageId: UUID, val chatId: UUID, val senderId: UUID, val messageType: MessageType, val content: String, val createdAt: Instant)
}