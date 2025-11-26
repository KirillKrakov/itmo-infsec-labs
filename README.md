# Краков Кирилл Константинович. Группа P3431. Поток 1.22
## Лабораторная работа №1 - Разработка защищенного REST API с интеграцией в CI/CD 

## Описание проекта

REST API для управления устройствами пользователя с соблюдением базовых мер информационной безопасности:
- **аутентификация**: JWT (HS256), пароли с BCrypt
- **авторизация**: доступ к API только с `127.0.0.1/::1`, неизвестные пути запрещены
- **ввод/валидация**: `jakarta.validation` для DTO
- **персистентность**: Spring Data JPA (подготовленные выражения → защита от SQLi)
- **HTTP-защита**: безопасные заголовки, строгий CORS, stateless-сессии, CSRF отключён для REST

## API

- **Базовый URL**: `http://localhost:8081`
- **Ограничение по IP**: доступ к `/api/v1/auth/**` и `/api/v1/notes/**` только с `127.0.0.1` и `::1` (иначе `403 Forbidden`).
- **Аутентификация**: передавайте токен в заголовке `Authorization: Bearer <JWT>`.

### Аутентификация

- **POST /api/v1/auth/register** — регистрация
  - Тело (JSON):
    ```json
    {
      "username": "Kirill Krakov",
      "password": "kirill1234",
      "email": "krakov-k@mail.ru"
    }
    ```
  - Ответ (200):
    ```json
    { "token": "<JWT>", "username": "Kirill Krakov" }
    ```

- **POST /api/v1/auth/login** — вход
  - Тело (JSON):
    ```json
    { "username": "Kirill Krakov", "password": "kirill1234" }
    ```
  - Ответ (200):
    ```json
    { "token": "<JWT>", "username": "Kirill Krakov" }
    ```

Пример вызовов (curl):
```bash
# Register
curl -s -X POST http://localhost:8081/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"Kirill Krakov","password":"kirill1234","email":"krakov-k@mail.ru"}'

# Login
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"Kirill Krakov","password":"kirill1234"}' | jq -r .token)
```

### Заметки пользователя (требуется JWT)

- **POST /api/v1/notes** — создать заметку
  - Тело (JSON):
    ```json
    {
      "noteTitle": "information Security Lab #1",
      "noteContent": "You need to do the #1 information security labwork (development of a secure REST API with CI/CD integration) by 20:00 on 11/26/2025"
    }
    ```
  - Ответ (200): `UserNoteResponseDTO`

- **GET /api/v1/notes** — список заметок текущего пользователя
  - Ответ (200): массив `UserNoteResponseDTO`, который состоит из `id`,`noteTitle`,`noteContent`,`lastModifiedAt`

- **PUT /api/v1/notes/{id}** — обновить заметку по id
  - Тело (JSON): поля как для создания (также обновляется поле lastModifiedAt)
  - Ответ (200): `UserNoteResponseDTO`

- **DELETE /api/v1/notes/{id}** — удалить заметку по id
  - Ответ: `204 No Content`

Примеры вызовов (curl):
```bash
# List
curl -s http://localhost:8081/api/v1/notes \
  -H "Authorization: Bearer $TOKEN"

# Create
curl -s -X POST http://localhost:8081/api/v1/notes \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"noteTitle": "information Security Lab #1","noteContent": "You need to do the #1 information security labwork (development of a secure REST API with CI/CD integration) by 20:00 on 11/26/2025"}'

# Update
curl -s -X PUT http://localhost:8081/api/v1/notes/1 \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"noteTitle": "information Security Lab #1 (new deadline time)","noteContent": "You need to do the #1 information security labwork (development of a secure REST API with CI/CD integration) by 20:30 on 11/26/2025"}'

# Delete
curl -s -X DELETE http://localhost:8081/api/v1/notes/1 \
  -H "Authorization: Bearer $TOKEN"
```

Валидация входных данных (основные ограничения):
- `username`: 2–40 символов; `password`: минимум 8 символов; `email`: корректный адрес, все поля обязательны
- `noteTitle`: ≤50; `noteContent`: ≤1000, все поля обязательны


## Реализованные меры защиты

### Аутентификация и авторизация
- **JWT (HS256)**: генерация и валидация токенов (подпись секретным ключом, срок жизни из `JWT_EXPIRATION`).
- **Безопасный секрет**: берётся из `JWT_SECRET`; при слабом/пустом значении генерируется криптографически стойкий ключ на запуск (для разработки).
- **Stateless-сессии**: хранение контекста только в JWT, без серверной сессии.
- **Пароли**: хэшируются `BCryptPasswordEncoder`.
- **Доступ только с localhost**: `/api/v1/auth/**` и `/api/v1/notes/**` разрешены только с `127.0.0.1`/`::1`.
- **Запрет всего прочего**: `anyRequest().denyAll()`.

### Защита от SQL Injection (SQLi)
- **Spring Data JPA**: используются репозитории и методы с параметрами (`findByUsername`, `findByIdAndUsername`, `existsByNoteTitle` и др.),
  что транслируется в подготовленные выражения Hibernate — без конкатенации SQL-строк.
- **Typed DTO + валидация**: ограничивает типы/диапазоны и исключает произвольные SQL-фрагменты во входных данных.

### Защита от XSS и связанных рисков
- **Контент только JSON** на стороне сервера; нет рендеринга HTML.
- **Безопасные заголовки**: `X-Content-Type-Options=nosniff`, `X-Frame-Options=DENY`, `Referrer-Policy=no-referrer`.
- **Валидация входа**: длины и формат полей предотвращают экстремальные значения.

### CSRF и CORS
- **CSRF отключён** для stateless REST с токенами в `Authorization`-заголовке.
- **CORS ограничён**: разрешены только источники `http://localhost:*`, методы `GET, POST, PUT, DELETE`, произвольные заголовки; `allowCredentials=true`.


## SAST/SCA отчёты (CI/CD)

- **SAST (SpotBugs)** — отчёт и скриншот:
  - Скриншот: 
  - Отчёты: 

- **SCA (Snyk)** — отчёт и скриншот:
  - Скриншот: 
  - Отчёты: 

Скриншоты соответствуют выполненным пайплайнам в разделе GitHub Actions репозитория.


## Postman коллекция
- Коллекция:
- Окружение:
 - Скриншоты (Postman):
   - Логин: 
   - Авторизованный POST /api/v1/notes: 
   - Авторизованное DELETE /api/v1/notes/{id}:
   - Неавторизованный GET /api/v1/notes:
   - Неавторизованное DELETE /api/v1/devices/{id}: 

