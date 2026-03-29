package com.lfj.messfox.server.dao

import com.lfj.messfox.protocol.datatype.ChatMember
import com.lfj.messfox.protocol.type.Role
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import javax.sql.DataSource
import kotlin.reflect.KClass

interface ChatMemberDataTable {
    fun addMemberForChat(chatId: UUID, userId: UUID, role: Role) : Result<ChatMember>
    fun addMembersForChat(chatId: UUID, userIds: List<UUID>, role: Role) : List<ChatMember>
    fun deleteMemberForChat(chatId: UUID, userId: UUID)
    fun selectUserByChatId(chatId: UUID) : List<ChatMember>
    fun selectChatsIdForUserId(userId: UUID) : List<UUID>

    interface CompanionsSQLRequest{
        val ADD_MEMBER_FOR_CHAT: String
        val REMOVE_MEMBER_FOR_CHAT: String
        val SELECT_MEMBERS_FOR_CHAT: String
        val SELECT_CHATS_FOR_USER: String
    }
    open class AbstractChatMembersDao <T : ChatMemberDataTable> (open val dataSource: DataSource, val sqlRequest: CompanionsSQLRequest, clazz: KClass<T>) : ChatMemberDataTable{
        private val logger: Logger = LoggerFactory.getLogger(clazz.java)
        override fun addMemberForChat(chatId: UUID, userId: UUID, role: Role) : Result<ChatMember> {
            return try{
                dataSource.connection.use{ conn ->
                    conn.prepareStatement(sqlRequest.ADD_MEMBER_FOR_CHAT).use { stmt ->
                        val instant: Instant = Instant.now()
                        stmt.setObject(1, chatId)
                        stmt.setObject(2, userId)
                        stmt.setString(3, role.name)
                        stmt.setTimestamp(4, Timestamp.from(instant))
                        stmt.executeUpdate()
                        Result.success(ChatMember(userId, role, instant))
                    }
                }
            }catch (e: SQLException){
                logger.error("Error ChatMembersDao", e)
                Result.failure(e)
            }
        }

        override fun addMembersForChat(chatId: UUID, userIds: List<UUID>, role: Role): List<ChatMember> {
            val mutableList: MutableList<ChatMember> = mutableListOf()
            return try{
                dataSource.connection.use{ conn ->
                    conn.prepareStatement(sqlRequest.ADD_MEMBER_FOR_CHAT).use{ stmt ->
                        userIds.forEach {
                            val instant: Instant = Instant.now()
                            stmt.setObject(1, chatId)
                            stmt.setObject(2, it)
                            stmt.setString(3, role.name)
                            stmt.setTimestamp(4, Timestamp.from(instant))
                            stmt.executeUpdate()
                            mutableList.add(ChatMember(it, role, instant))
                        }
                        mutableList.toList()
                    }
                }
            }catch (e: SQLException){
                logger.error("Error ChatMembersDao", e)
                listOf()
            }
        }
        override fun deleteMemberForChat(chatId: UUID, userId: UUID) {
            try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.REMOVE_MEMBER_FOR_CHAT).use { stmt ->
                        stmt.setObject(1, chatId)
                        stmt.setObject(2, userId)
                        stmt.executeUpdate()
                    }
                }
            }catch (e: SQLException){
                logger.error("Error ChatMemberDao", e)
            }
        }
        override fun selectUserByChatId(chatId: UUID) : List<ChatMember> {
            val mutableList: MutableList<ChatMember> = mutableListOf()
            try { dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.SELECT_MEMBERS_FOR_CHAT).use{ stmt ->
                        stmt.setObject(1, chatId)
                        stmt.executeQuery().use{ rs ->
                            while(rs.next()) mutableList.add(ChatMember(rs.getObject("user_id", UUID::class.java), Role.valueOf(rs.getString("role")), rs.getTimestamp("joined_at").toInstant()))
                        }
                        return mutableList.toList()
                    }
                }
            }catch (e: SQLException){
                logger.error("Error ChatMemberDao", e)
                return listOf()
            }
        }
        override fun selectChatsIdForUserId(userId: UUID) : List<UUID> {
            val mutableList: MutableList<UUID> = mutableListOf()
            try{ dataSource.connection.use{ conn ->
                    conn.prepareStatement(sqlRequest.SELECT_CHATS_FOR_USER).use{ stmt ->
                        stmt.setObject(1, userId)
                        stmt.executeQuery().use{ rs ->
                            while(rs.next()) mutableList.add(rs.getObject("chatId", UUID::class.java))
                        }
                        return mutableList
                    }
                }
            }catch (e: SQLException){
                logger.error("Error ChatMemberDao", e)
                return listOf()
            }
        }
    }
    class H2ChatMemberDao(override val dataSource: DataSource) : AbstractChatMembersDao<H2ChatMemberDao>(dataSource, h2SQLRequest, H2ChatMemberDao::class) {
        private companion object h2SQLRequest : CompanionsSQLRequest {
            override val ADD_MEMBER_FOR_CHAT = """
                INSERT INTO chat_members_table(chat_id, user_id, role, joined_at) 
                VALUES (?, ?, ?, ?)
            """.trimIndent()
            override val REMOVE_MEMBER_FOR_CHAT = """
                DELETE FROM chat_members_table
                WHERE chat_id = ? AND user_id = ?
            """.trimIndent()
            override val SELECT_MEMBERS_FOR_CHAT = """
                SELECT user_id FROM chat_members_table
                WHERE chat_id = ?
            """.trimIndent()
            override val SELECT_CHATS_FOR_USER = """
                SELECT chat_id FROM chat_members_table
                WHERE user_id = ?
            """.trimIndent()
        }
    }
}