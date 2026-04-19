package com.lfj.messfox.server.dao

import com.lfj.messenger.bcrypt.PasswordUtil
import com.lfj.messenger.bcrypt.PasswordUtil.validPassword
import com.lfj.messenger.uuid7.UUIDv7
import com.lfj.messfox.protocol.datatype.User
import com.lfj.messfox.server.exc.AuthorizationFailedException
import com.lfj.messfox.server.exc.RegistrationFailedException
import com.lfj.messfox.server.exc.UserAlreadyExistsException
import com.lfj.messfox.server.ifPresentOrElseWithResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp.from
import java.util.*
import javax.sql.DataSource
import kotlin.reflect.KClass

interface UserDataTable {

    fun registerUser(displayName: String, email: String, password: String): Result<User>
    fun authorizationUser(email: String, password: String): Result<User>
    fun findUserById(userId: UUID) : Result<User>
    fun findUserByUserName(userName: String) : Result<List<User>>
    fun findUserByDisplayName(displayName: String) : Result<List<User>>
    fun setUserName(userName: String, userId: UUID) : Boolean // Пока без реализации

    fun mapResultSetToUser(rs: ResultSet): User {
        return User(
            rs.getObject(
                "user_id",
                UUID::class.java
            ),
            rs.getString("display_name"),
            rs.getString("user_name"),
            rs.getTimestamp("created_at").toInstant()
        )
    }

    interface CompanionsSQLRequest{
        val INSERT_USER: String
        val AUTHORIZATION_USER: String
        val FIND_USER_BY_ID: String
        val FIND_USER_BY_USER_NAME: String
        val FIND_USER_BY_DISPLAY_NAME: String
        val SET_USER_NAME: String
    }

    open class AbstractUserDao <T : UserDataTable>(open val dataSource: DataSource, val sqlRequest: CompanionsSQLRequest, clazz: KClass<T>) : UserDataTable{
        private val logger: Logger = LoggerFactory.getLogger(clazz.java)

        override fun registerUser(displayName: String, email: String, password: String): Result<User> {
            return try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.INSERT_USER).use { stmt ->
                        val userId: UUID = UUIDv7.next()
                        val passwordHash: String = PasswordUtil.hashPassword(password)
                        val rs: Result<User> = UUIDv7.getTimestamp(userId).ifPresentOrElseWithResult({ createdAt ->
                            stmt.setObject(1, userId)
                            stmt.setString(2, displayName)
                            stmt.setObject(3, null)
                            stmt.setString(4, email)
                            stmt.setString(5, passwordHash)
                            stmt.setTimestamp(6, from(createdAt))
                            stmt.executeUpdate()
                            Result.success(User(userId, displayName, null, createdAt))
                        } ,{Result.failure(RegistrationFailedException("Registration failed: Failed get 'createdAt' timestamp"))})
                        return rs
                    }
                }
            }catch (e: SQLException){
                logger.error("Failed registrations", e)
                Result.failure(RegistrationFailedException("Registration failed", e))
            }
        }

        override fun authorizationUser(email: String, password: String): Result<User> {
            return try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.AUTHORIZATION_USER).use { stmt ->
                        stmt.setString(1, email)
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) {
                                if (validPassword(password, rs.getString("password"))) {
                                    logger.info("Authorization success")
                                    Result.success(mapResultSetToUser(rs))
                                } else {
                                    logger.warn("Authorization failed: Invalid password")
                                    Result.failure(AuthorizationFailedException("Authorization failed: invalid password"))
                                }
                            } else {
                                logger.warn("Failed authorization: user not found")
                                Result.failure(AuthorizationFailedException(
                                    "Failed authorization: user not found",
                                    UserAlreadyExistsException("User not found")
                                ))
                            }
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("Failed authorization", e)
                Result.failure(e)
            }
        }

        override fun findUserById(userId: UUID): Result<User> {
            return  try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.FIND_USER_BY_ID).use { stmt ->
                        stmt.setObject(1, userId)
                        stmt.executeQuery().use { rs ->
                            if (rs.next()) {
                                logger.info("User $userId is found")
                                Result.success(mapResultSetToUser(rs))
                            } else {
                                logger.warn("User $userId is not found.")
                                Result.failure(UserAlreadyExistsException("Failed find operation: user not found"))
                            }
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("Failed find user by userId", e)
                Result.failure(e)
            }
        }

        override fun findUserByUserName(userName: String): Result<List<User>> {
            val mutableList: MutableList<User> = mutableListOf()
            return try {
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.FIND_USER_BY_USER_NAME).use { stmt ->
                        stmt.setString(1, userName)
                        stmt.executeQuery().use { rs ->
                            while (rs.next()) {
                                mutableList.add(mapResultSetToUser(rs))
                            }
                            Result.success(mutableList.toList())
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("Failed find user by userName", e)
                Result.failure(e)
            }
        }

        override fun findUserByDisplayName(displayName: String): Result<List<User>> {
            val mutableList: MutableList<User> = mutableListOf()
            return try{
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.FIND_USER_BY_DISPLAY_NAME).use { stmt ->
                        stmt.setString(1, displayName)
                        stmt.executeQuery().use { rs ->
                            while(rs.next()){
                                mutableList.add(mapResultSetToUser(rs))
                            }
                            Result.success(mutableList.toList())
                        }
                    }
                }
            }catch (e: SQLException){
                logger.error("Failed find by displayName", e)
                Result.failure(e)
            }
        }

        override fun setUserName(userName: String, userId: UUID): Boolean {
            return try{
                dataSource.connection.use { conn ->
                    conn.prepareStatement(sqlRequest.SET_USER_NAME).use { stmt ->
                        stmt.setString(1, userName)
                        stmt.setObject(2, userId)
                        val update = stmt.executeUpdate()
                        logger.debug("Update >> {}", update)
                        update == 1
                    }
                }
            }catch (e: SQLException){
                logger.error("UserDao error", e)
                false
            }
        }
    }

    // PostgreSQL database for production
    class PostgresUserDAO(override val dataSource: DataSource) : UserDataTable.AbstractUserDao<PostgresUserDAO>(dataSource, postgreSqlRequest, PostgresUserDAO::class) {
        private companion object postgreSqlRequest : UserDataTable.CompanionsSQLRequest {
            override val INSERT_USER: String = """
                INSERT INTO users_table(user_id, display_name, user_name, email, password, created_at) 
                VALUES (?, ?, ?, ?, ?, ?) 
                ON CONFLICT (email) DO NOTHING
             """.trimIndent()
            override val AUTHORIZATION_USER: String = """
                SELECT user_id, display_name, user_name, email, password, created_at 
                FROM users_table 
                WHERE email = ?
            """.trimIndent()
            override val FIND_USER_BY_ID = """
                SELECT user_id, display_name, user_name, created_at
                FROM users_table
                WHERE user_id = ?
            """.trimIndent()
            override val FIND_USER_BY_USER_NAME: String = """
                SELECT user_id, display_name, user_name, created_at 
                FROM users_table 
                WHERE user_name LIKE ?
                LIMIT 10
            """.trimIndent()
            override val FIND_USER_BY_DISPLAY_NAME: String = """
                SELECT user_id, display_name, user_name, created_at
                FROM users_table
                WHERE display_name ILIKE ?
                LIMIT 10
            """.trimIndent()
            override val SET_USER_NAME: String = """
                UPDATE users_table
                SET user_name = ?
                WHERE user_id = ?
            """.trimIndent()
        }
    }
    // H2 Database for testing
    class H2UserDAO(override val dataSource: DataSource) : AbstractUserDao<H2UserDAO>(dataSource, h2SqlRequest, H2UserDAO::class) {
        private companion object h2SqlRequest : UserDataTable.CompanionsSQLRequest {
            override val INSERT_USER: String = """
                INSERT INTO users_table(user_id, display_name, user_name, email, password, created_at) 
                VALUES (?, ?, ?, ?, ?, ?)
             """.trimIndent()
            override val AUTHORIZATION_USER: String = """
                SELECT user_id, display_name, user_name, email, password, created_at 
                FROM users_table 
                WHERE email = ?
            """.trimIndent()
            override val FIND_USER_BY_ID = """
                SELECT user_id, display_name, user_name, created_at
                FROM users_table
                WHERE user_id = ?
            """.trimIndent()
            override val FIND_USER_BY_USER_NAME: String = """
                SELECT user_id, display_name, user_name, created_at 
                FROM users_table 
                WHERE user_name LIKE ?
                LIMIT 10
            """.trimIndent()
            override val FIND_USER_BY_DISPLAY_NAME: String = """
                SELECT user_id, display_name, user_name, created_at
                FROM users_table
                WHERE display_name ILIKE ?
                LIMIT 10
            """.trimIndent()
            override val SET_USER_NAME: String = """
                UPDATE users_table
                SET user_name = ?
                WHERE user_id = ?
            """.trimIndent()
        }
    }

}