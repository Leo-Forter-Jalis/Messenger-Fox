package com.lfj.messfox.protocol.type

enum class MessageType{
    TEXT,
    AUDIO,
    FILE
    // ...
}
enum class ChatType{
    PRIVATE,
    GROUP
}

enum class Role{
    OWNER,
    ADMIN,
    MEMBER // ?
}