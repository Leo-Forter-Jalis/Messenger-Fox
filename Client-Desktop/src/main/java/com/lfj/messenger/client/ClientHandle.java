package com.lfj.messenger.client;

import com.lfj.messenger.dto.Message;
import com.lfj.messenger.dto.datatype.UserDTO;
import com.lfj.messenger.dto.request.HeartbeatRequest;
import com.lfj.messenger.dto.response.*;
import com.lfj.messenger.dto.types.MessageTypeConstants;
import com.lfj.messenger.time.Time;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientHandle extends ChannelInboundHandlerAdapter {
    private static volatile UserDTO userDTO;
    private static volatile List<UserDTO> userDTOS;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj){
        if(!(obj instanceof Message message)) return;
        switch (message.type()){
            case MessageTypeConstants.AUTH_RESPONSE -> {
                System.out.println("Authorization success.");
                AuthResponse response = (AuthResponse) message;
                userDTO = response.user();
            }
            case MessageTypeConstants.REGISTER_RESPONSE -> {
                System.out.println("Register success");
                RegisterResponse response = (RegisterResponse) message;
                userDTO = response.user();
            }
            case MessageTypeConstants.MESSAGE_RESPONSE -> {
                System.out.println("Message received");
                MessageResponse response = (MessageResponse) message;
                if(userDTOS == null || userDTOS.isEmpty()) return;
                UserDTO sender = userDTOS.stream().filter(d -> d.userId().equals(response.getSenderId())).findFirst().orElse(null);
                if(sender == null) return;
                System.out.printf("%s\nОтправитель >> %s\nСообщение >> %s\nДата отправки >> %s\n%s\n", "-".repeat(40), sender.displayName(), response.message().content(), Time.getTime(response.instant()), "-".repeat(40));
            }
            case MessageTypeConstants.CHATS_RESPONSE -> {
                System.out.println("Chat_response");
                ChatsResponse response = (ChatsResponse) message;
                for(UserDTO user : ((ChatsResponse) message).users()) System.out.printf("%s - %s\n", user.userId(), user.displayName());
                userDTOS = response.users();
            }
            case MessageTypeConstants.ERROR_RESPONSE -> {
                ErrorResponse response = (ErrorResponse) message;
                System.out.printf("%s - %d\n", response.errorMessage(), response.errorCode());
            }
            default -> System.out.println("Invalid message type case.");
        }
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object o){
        if(o instanceof IdleStateEvent event){
            if(event.state() == IdleState.WRITER_IDLE){
                System.out.println("WRITE!");
                ctx.writeAndFlush(new HeartbeatRequest(UUID.randomUUID(), Time.nowInstant()));
            }
        }
    }
    public Optional<UserDTO> user(){
        return Optional.ofNullable(userDTO);
    }
    public Optional<List<UserDTO>> users() { return Optional.ofNullable(userDTOS); }
}
