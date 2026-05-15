# Задача модуля
- ### Установить правила взаимодействия сервера и клиента

# Пакеты модуля:
- ### `com.lfj.messenger.dto` - **Основной пакет**
- ### `com.lfj.messenger.dto.datatype` - **Пакет типов данных**
- ### `com.lfj.messenger.dto.datatype.client` - **Пакет типов для клиента**
- ### `com.lfj.messenger.dto.datatype.server` - **Пакет типов для сервера**
- ### `com.lfj.messenger.dto.request` - **Пакет запросов**
- ### `com.lfj.messenger.dto.response` - **Пакет ответов**
- ### `com.lfj.messenger.dto.types` - **Пакет типов**

# Классы
## `com.lfj.messenger.dto.datatype.client`:
- ## Перечисленные классы нужны для промежуточного вида данных
- ### [`Chat`](datatype/Client.md) - **Промежуточное представление чата**
- ### [`Message`](datatype/Client.md) - **Промежуточная представление сообщения**
- ### [`User`](datatype/Client.md) - **Промежуточная представления пользователя**

## `com.lfj.messenger.dto.datatype.server`:
- ## Формируются из клиентских промежуточных данных
- ### [`ChatDTO`](datatype/Server.md) - **Полное представление чата (идентификатор, тип, участники, время создание)**
- ### [`ChatMemberDTO`](datatype/Server.md) - **Преставление списка участников**
- ### [`MessageDTO`](datatype/Server.md) - **Полное представление сообщения(идентификатор, тип, контент, время создания)**
- ### [`UserDTO`](datatype/Server.md) - **Полное представление пользователя**

## `com.lfj.messenger.dto.request`:
- ### [`Request`](request/Request.md) - **Интерфейс для всех запросов**
- ### [`AuthRequest`](request/Request.md) - **Запрос авторизации к серверу. Содержит email и пароль**
- ### [`ChatsRequest`](request/Request.md) - **Запрос получения данных чата**
- ### [`CreateChatRequest`](request/Request.md) - **Запрос на создание чата**
- ### [`GetMessageRequest`](request/Request.md) - **Запрос получения сообщений чата**
- ### [`HeartbeatRequest`](request/Request.md) - **Спец. запрос для поддержания соединения**
- ### [`LastMessageRequest`](request/Request.md) - **Запрос получения последнего сообщения чата**
- ### [`MessageRequest`](request/Request.md) - **Запрос отправки сообщения в определенный чат**
- ### [`RegisterRequest`](request/Request.md) - **Запрос регистрации нового аккаунта**

## `com.lfj.messenger.dto.response`:
- ### [`Response`](response/Response.md) - **Интерфейс для всех ответов**
- ### [`AuthResponse`](response/Response.md) - **Ответ сервера на авторизацию(только при успешной и возвращает пользователя)**
- ### [`ChatsReponse`](response/Response.md) - **Ответ возвращает чат**
- ### [`CreatedChatResponse`](response/Response.md) - **Ответ сервера возвращает ChatDTO**
- ### [`ErrorResponse`](response/Response.md) - **Ответ возвращает ошибку выполнения запроса**
- ### [`GetMessageResponse`](response/Response.md) - **Ответ возвращает список сообщений**
- ### [`HeartbeatResponse`](response/Response.md) - **Спец. ответ для поддержания соединения(не используется)**
- ### [`LastMessageResponse`](response/Response.md) - **Ответ возвращает последнее сообщение(MessageDTO)**
- ### [`MessageResponse`](response/Response.md) - **Ответ получателям сообщения**
- ### [`RegisterResponse`](response/Response.md) - **Ответ на регистрацию(только при успешной операции, возвращает пользователя)**

## `com.lfj.messenger.dto.types`:
- ### [`ChatType`](types/Types.md) - **Enum класс типов чата**
- ### [`MessageStatus`](types/Types.md) - **Enum класс статусов сообщений**
- ### [`MessageType`](types/Types.md) - **Enum класс типов сообщений**
- ### [`MessageTypeConstants`](types/Types.md) - **Класс типов запросов**
- ### [`Role`](types/Types.md) - **Enum класс ролей в чате**
- ### [`UserState`](types/Types.md) - **Enum класс состояний пользователя**

## `com.lfj.messenger.dto`:
- ### [`Message`](Message.md) - **Интерфейс. Родительский для Request&Response**