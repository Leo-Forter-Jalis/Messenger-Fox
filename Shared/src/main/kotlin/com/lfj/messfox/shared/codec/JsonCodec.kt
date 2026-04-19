package com.lfj.messfox.shared.codec

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.lfj.messfox.protocol.Protocol
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.InvalidParameterException

class JsonCodec : ByteToMessageCodec<Protocol>() {
    companion object{
        private val mapper: ObjectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModules(JavaTimeModule()).registerKotlinModule()
        private val LENGTH_FIELD_SIZE: Int = 4
        private val MAX_LENGTH: Int = 1024 * 1024
        private val logger: Logger = LoggerFactory.getLogger(JsonCodec::class.java)
    }
    override fun encode(ctx: ChannelHandlerContext?, msg: Protocol, out: ByteBuf) {
        logger.trace("Encoding message...")
        val bytes: ByteArray = mapper.writeValueAsBytes(msg)
        require(bytes.size <= MAX_LENGTH){ "Message too large $MAX_LENGTH bytes" }
        out.writeInt(bytes.size)
        out.writeBytes(bytes)
        logger.trace("Sent response")
    }

    public override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf, out: MutableList<Any>) {
        if (`in`.readableBytes() < LENGTH_FIELD_SIZE) {
            return
        }
        `in`.markReaderIndex()
        val length: Int = `in`.readInt()
        if(length !in 1..MAX_LENGTH) throw InvalidParameterException("Frame length to invalid value: $length > $MAX_LENGTH")
        if (`in`.readableBytes() < length) {
            `in`.resetReaderIndex()
            return
        }
        val bytes: ByteArray = ByteArray(length)
        `in`.readBytes(bytes)
        try {
            val message: Protocol = mapper.readValue(bytes)
            out.add(message)
        } catch (e: Exception) {
            ctx?.fireExceptionCaught(e)
        }
    }

}