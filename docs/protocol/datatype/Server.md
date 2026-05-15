## Пакет [`com.lfj.messenger.dto.datatype.server`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/server)

# [`ChatDTO`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/server/ChatDTO.java) - record класс
## Поля:
| Тип поля | Имя Поля  | Nullable | Цель                                          |
|----------|-----------|----------|-----------------------------------------------|
| UUID     | chatId    | NotNull  | Хранит основной идентификатор чата            |
| ChatType | chatType  | NotNull  | Хранит тип чата. Private или Group            |
| String   | chatTag   | Nullable | Дополнительный идентификатор, для поиска чата |
| String   | chatName  | NotNull  | Имя данного чата                              |
| Instant  | instant   | NotNull  | Время создание чата на сервере                |

## Методы:
> # Автоматическая генерация по имени поля. Record

# [`ChatMemberDTO`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/server/ChatMemberDTO.java) - record класс

---

---
## Вложенные классы:
### [`Member`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/server/ChatMemberDTO.java) - record класс
### Поля:
| Тип поля | Имя поля | Nullable | Цель                              |
|----------|----------|----------|-----------------------------------|
| UserDTO  | user     | NotNull  | Данные о пользователе в чате      |
| Instant  | joinAt   | NotNull  | Время когда человек присоединился |

### Методы:
> # Автоматическая генерация по имени поля. Record
---

---
## Поля:
| Тип поля                 | Имя поля | Nullable | Цель                         |
|--------------------------|----------|----------|------------------------------|
| UUID                     | chatId   | NotNull  | Индентификатор чата          |
| Map`<Role, Set<Member>>` | members  | NotNull  | Карта пользователей по ролям |

## Методы:
> # Автоматическая генерация по имени поля. Record

# [`MessageDTO`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/server/MessageDTO.java) - record класс

## Поле
| Тип поля    | Имя поля    | Nullable | Цель                                            |
|-------------|-------------|----------|-------------------------------------------------|
| UUID        | messageId   | NotNull  | Идентификатор сообщения                         |
| UUID        | chatId      | NotNull  | Идентификатор чата в который отправленное в чат |
| UserDTO     | sender      | NotNull  | Отправитель сообщения                           |
| MessageType | messageType | NotNull  | Тип отправленного сообщения                     |
| String      | content     | NotNull  | Содержание сообщения                            |
| Instant     | instant     | NotNull  | Время когда сервер его фактически обработал     |

## Метод:
> # Автоматическая генерация по имени поля. Record

# [`UserDTO`](../../../Protocol/src/main/java/com/lfj/messenger/dto/datatype/server/UserDTO.java) - record классы

## Поле:
| Тип поля | Имя поля    | Nullable | Цель                                    |
|----------|-------------|----------|-----------------------------------------|
| UUID     | userId      | NotNull  | Уникальный идентификатор пользователя   |
| String   | displayName | NotNull  | Отображаемое имя                        |
| String   | userName    | NotNull  | Уникальный идентификатор внутри системы |
| Instant  | createAt    | NotNull  | Время создания аккаунта на сервере      |

## Метод:
> # Автоматическая генерация по имени поля. Record
