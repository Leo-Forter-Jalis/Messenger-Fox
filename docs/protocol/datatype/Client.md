## Пакет [`com.lfj.messenger.dto.datatype.client`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/client)

# [`Chat`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/client/Chat.java) - record класс
##  Задача класса создать первоначальное представление о чате. С дальнейшей передачей к серверу.
## Поля:
| Тип поля     | Имя поля | Nullable? | Цель                                                  |
|--------------|----------|-----------|-------------------------------------------------------|
| ChatType     | chatType | NotNull   | Четко указать тип чата(PRIVATE или GROUP)             |
| String       | chatName | NotNull   | Хранит имя чата для его отображения в GUI             |                                          
| String       | chatTag  | Nullable  | Доп. уникальный идентификатор чата, удобен для Client |                                          
| List`<UUID>` | members  | NotNull   | Список пользователей, которых нужно добавить в чат    |

## Методы:
> # Автоматическая генерация по имени поля. Record

# [`Message`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/client/Message.java) - record класс

## Задача класса сформировать первое представление сообщения.

## Поля:
| Тип поля    | Имя поля    | Nullable? | Цель                                                     |
|-------------|-------------|-----------|----------------------------------------------------------|
| UUID        | chatId      | NotNull   | Идентификатор чата, определяет кому отправить сообщение  |
| UserDTO     | sender      | NotNull   | Удалить...                                               |
| MessageType | messageType | NotNull   | Тип сообщение, для понимания, как обрабатывать сообщение |
| String      | content     | NotNull   | Содержание сообщение, отправляемого пользователем        |

## Методы:
> # Автоматическая генерация по имени поля. Record

# [`User`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/client/User.java) - record класс
## Задача определить необходимые данные для регистрации/авторизации пользователя.

## Поля:
| Тип поля | Имя поля    | Nullable | Цель                                                          |
|----------|-------------|----------|---------------------------------------------------------------|
| String   | displayName | NotNull  | Хранить отображаемое имя для регистрации                      |
| String   | email       | NotNull  | Хранить эл. почту для регистрации/авторизации                 |
| String   | password    | NotNull  | Хранит пароль к аккаунту или для только создаваемого аккаунта |

## Методы:
> # Автоматическая генерация по имени поля. Record
