package com.lfj.messfox.server.dao

import com.lfj.messenger.uuid7.UUIDv7
import com.lfj.messfox.protocol.type.ChatType
import com.lfj.messfox.server.ifPresentOrElseWithResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.JDBCType
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import javax.sql.DataSource
import kotlin.reflect.KClass

interface ChatDataTable{
    fun createGroupChat(chatName: String, chatTag: String?) : Result<ChatBillet>
    fun createPrivateChat() : Result<ChatBillet>
    fun deleteChat(chatId: UUID)
    fun findChatForId(chatId: UUID) : Result<ChatBillet>
    fun findChatForTag(chatTag: String) : Result<List<ChatBillet>>
    fun findChatForName(chatName: String) : Result<List<ChatBillet>>
    fun setTag(chatId: UUID, chatTag: String) : Result<ChatBillet>

    fun mapToChatBillet(rs: ResultSet) : ChatBillet{
        return ChatBillet(rs.getObject("chat_id",
            UUID::class.java),
            rs.getString("chat_name"),
            rs.getString("chat_tag"),
            ChatType.valueOf(rs.getString("chat_type")),
            rs.getTimestamp("created_at").toInstant())
    }

    interface CompanionsSQLRequest{
        val CREATE_CHAT: String
        val DELETE_CHAT: String
        val FIND_CHAT_FOR_ID: String
        val FIND_CHAT_FOR_TAG: String
        val FIND_CHAT_FOR_NAME: String
        val SET_CHAT_TAG: String
    }

    open class AbstractChatDAO <T : ChatDataTable> (open val dataSource: DataSource, val companionsSQLRequest: CompanionsSQLRequest, val clazz: KClass<T>) : ChatDataTable{
        private val logger: Logger = LoggerFactory.getLogger(clazz.java)

        override fun createGroupChat(chatName: String, chatTag: String?): Result<ChatBillet> {
            val start = System.currentTimeMillis()
            return try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(companionsSQLRequest.CREATE_CHAT).use { stmt ->
                        val chatId = UUIDv7.next()
                        UUIDv7.getTimestamp(chatId).ifPresentOrElseWithResult(action={ createdAt ->
                            stmt.setObject(1, chatId)
                            stmt.setString(2, ChatType.GROUP.name)
                            if (chatTag != null) stmt.setString(3, chatTag) else stmt.setNull(3, JDBCType.VARCHAR.ordinal)
                            stmt.setString(4, chatName)
                            stmt.setTimestamp(5, Timestamp.from(createdAt))
                            stmt.executeUpdate()
                            val end = System.currentTimeMillis()
                            logger.debug("Created chat took >> {}ms", end - start)
                            Result.success(ChatBillet(chatId, chatName, chatTag, ChatType.GROUP, createdAt))
                        }, emptyAction = { Result.failure(IllegalStateException("")) })
                    }
                }
            }catch (e: SQLException){
                logger.error("Chat dao error", e)
                Result.failure(e)
            }
        }

        override fun createPrivateChat(): Result<ChatBillet> {
            return dataSource.connection.use { conn ->
                conn.prepareStatement(companionsSQLRequest.CREATE_CHAT).use{ stmt ->
                    val chatId: UUID = UUIDv7.next()
                    UUIDv7.getTimestamp(chatId).ifPresentOrElseWithResult( action = {
                        stmt.setObject(1, chatId)
                        stmt.setString(2, ChatType.PRIVATE.name)
                        stmt.setObject(3, null)
                        stmt.setObject(4, null)
                        stmt.setTimestamp(5, Timestamp.from(it))
                        stmt.executeUpdate()
                        Result.success(ChatBillet(chatId, null, null, ChatType.PRIVATE, it))
                    }, emptyAction = { Result.failure(IllegalStateException("")) })
                }
            }
        }

        override fun deleteChat(chatId: UUID) {
            TODO("Not yet implemented")
        }

        override fun findChatForId(chatId: UUID): Result<ChatBillet> {
            return try{
                dataSource.connection.use { conn ->
                    conn.prepareStatement(companionsSQLRequest.FIND_CHAT_FOR_ID).use { stmt ->
                        stmt.setObject(1, chatId)
                        stmt.executeQuery().use{ rs ->
                            if(rs.next()){
                                logger.debug("Find chat {} success", chatId)
                                Result.success(mapToChatBillet(rs))
                            }else{
                                logger.warn("Find chat {} failed", chatId)
                                Result.failure(IllegalStateException("Chat is not fined"))
                            }
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("Failed find chat for chatId", e)
                Result.failure(e)
            }
        }

        override fun findChatForTag(chatTag: String): Result<List<ChatBillet>> {
            val mutableList: MutableList<ChatBillet> = mutableListOf()
            return try{
                dataSource.connection.use { conn ->
                    conn.prepareStatement(companionsSQLRequest.FIND_CHAT_FOR_TAG).use{ stmt ->
                        stmt.setString(1, chatTag)
                        stmt.executeQuery().use { rs ->
                            while (rs.next()){
                                mutableList.add(mapToChatBillet(rs))
                            }
                            logger.debug("Find is number chat {}", mutableList.size)
                            Result.success(mutableList.toList())
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("Failed find chat for chatTag", e)
                Result.failure(e)
            }
        }

        override fun findChatForName(chatName: String): Result<List<ChatBillet>> {
            val mutableList: MutableList<ChatBillet> = mutableListOf()
            return try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(companionsSQLRequest.FIND_CHAT_FOR_NAME).use{ stmt ->
                        stmt.setString(1, chatName)
                        stmt.executeQuery().use { rs ->
                            while (rs.next()){
                                mutableList.add(mapToChatBillet(rs))
                            }
                            logger.debug("Find is number chat {}", mutableList.size)
                            return Result.success(mutableList.toList())
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("Failed find chat for chatName", e)
                Result.failure(e)
            }
        }

        override fun setTag(chatId: UUID, chatTag: String): Result<ChatBillet> {
            TODO("Not yet implemented")
        }
    }

    class PostgresChatDAO(override val dataSource: DataSource) : AbstractChatDAO <PostgresChatDAO>(dataSource, postgreSqlRequst, PostgresChatDAO::class){
        private companion object postgreSqlRequst : CompanionsSQLRequest{
            override val CREATE_CHAT = """
                INSERT INTO chats_table (chat_id, chat_type, chat_tag, chat_name, created_at)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (chat_id) DO NOTHING
            """.trimIndent()
            override val DELETE_CHAT = """
                ...
            """.trimIndent()
            override val FIND_CHAT_FOR_ID = """
                SELECT chat_id, chat_type, chat_tag, chat_name, created_at
                FROM chats_table
                WHERE chat_id = ?
            """.trimIndent()
            override val FIND_CHAT_FOR_TAG = """
                SELECT chat_id, chat_type, chat_tag, chat_name, created_at
                FROM chats_table
                WHERE chat_tag LIKE ?
            """.trimIndent()
            override val FIND_CHAT_FOR_NAME = """
                SELECT chat_id, chat_type, chat_tag, chat_name, created_at
                FROM chats_table
                WHERE chat_name ILIKE ?
            """.trimIndent()
            override val SET_CHAT_TAG = """
                UPDATE chats_table
                SET chat_tag = ?
                WHERE chat_id = ?
            """.trimIndent()
        }
    }
    class H2ChatDAO(override val dataSource: DataSource) : AbstractChatDAO <H2ChatDAO>(dataSource, h2SqlRequest, H2ChatDAO::class){
        private companion object h2SqlRequest : CompanionsSQLRequest{
            override val CREATE_CHAT = """
                INSERT INTO chats_table (chat_id, chat_type, chat_tag, chat_name, created_at)
                VALUES (?, ?, ?, ?, ?)
            """.trimIndent()
            override val DELETE_CHAT = """
                ...
            """.trimIndent()
            override val FIND_CHAT_FOR_ID = """
                SELECT chat_id, chat_type, chat_tag, chat_name, created_at
                FROM chats_table
                WHERE chat_id = ?
            """.trimIndent()
            override val FIND_CHAT_FOR_TAG = """
                SELECT chat_id, chat_type, chat_tag, chat_name, created_at
                FROM chats_table
                WHERE chat_tag LIKE ?
            """.trimIndent()
            override val FIND_CHAT_FOR_NAME = """
                SELECT chat_id, chat_type, chat_tag, chat_name, created_at
                FROM chats_table
                WHERE chat_name ILIKE ?
            """.trimIndent()
            override val SET_CHAT_TAG = """
                UPDATE chats_table
                SET chat_tag = ?
                WHERE chat_id = ?
            """.trimIndent()
        }
    }
}

data class ChatBillet (val chatId: UUID,
                       val chatName: String?,
                       val chatTag: String?,
                       val chatType: ChatType,
                       val createdAt: Instant)
