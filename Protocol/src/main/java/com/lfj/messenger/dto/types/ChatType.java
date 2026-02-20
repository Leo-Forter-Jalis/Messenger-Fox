package com.lfj.messenger.dto.types;

public enum ChatType {
    PRIVATE,
    GROUP;
    public static ChatType nameToChatType(String type){
        switch(type.toLowerCase()){
            case "private" -> {
                return ChatType.PRIVATE;
            }
            case "group" ->{
                return ChatType.GROUP;
            }
            default -> throw new IllegalArgumentException("Unknown chat_type");
        }
    }
}
