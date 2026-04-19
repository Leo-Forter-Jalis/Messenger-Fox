package com.lfj.messfox.server.registry

import com.lfj.messfox.server.ifPresentOrElseWithResult
import io.netty.channel.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ConnectionRegistry {
    data class Connection(val channel: Channel, private var userId: UUID? = null){
        fun setUserId(userId: UUID){
            if(this.userId == null) this.userId = userId
        }
        fun userId() = Optional.ofNullable(userId)
    }
    private val connections: MutableList<Connection> = mutableListOf();
    private val logger: Logger = LoggerFactory.getLogger(ConnectionRegistry::class.java)

    fun add(connection: Connection){
        logger.info("Add new connection > {}", connection)
        connections.add(connection)
    }
    fun removeForId(userId: UUID){
        logger.info("Remove connection for id > {}", userId)
        connections.removeIf{ connection ->
            return@removeIf connection.userId().ifPresentOrElseWithResult(
                action = {return@ifPresentOrElseWithResult it == userId}, emptyAction = {return@ifPresentOrElseWithResult false}
            )
        }
    }
    fun removeForChannel(channel: Channel){
        logger.info("Remove connection for channel > {}", channel)
        connections.removeIf{ connection ->
            connection.channel == channel
        }
    }
    fun contains(userId: UUID) : Boolean{
        var isExists: Boolean = false
        connections.forEach {
            it.userId().get()?.let {
                if(it == userId) isExists = true
            }
        }
        return isExists
    }
    fun getConnectionForId(userId: UUID) : Connection{
        logger.info("Getting connection for userId >> {}", userId)
        return connections.first { connection ->
            connection.userId().ifPresentOrElseWithResult(action = { it == userId }, emptyAction = { false })
        }
    }
    fun getConnectionForChannel(channel: Channel) : Connection{
        logger.info("Getting connection for channel >> {}", channel)
        return connections.first{ connection ->
            connection.channel == channel
        }
    }
    fun getConnectionsChannel() : List<Channel>{
        logger.info("Getting connections list")
        return connections.map { connection ->
            connection.channel
        }
    }
    fun setUserIdInConnection(channel: Channel, userId: UUID){
        logger.info("Set userId({}) in Connection for channel({})", userId, channel)
        connections.forEach { connection ->
            if(connection.channel == channel) connection.setUserId(userId)
        }
    }
}