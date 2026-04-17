# Bank Card Management System

Backend-приложение для управления банковскими картами на Spring Boot.

## Репозиторий проекта

https://github.com/zz-gh209/card.git

------------------------------------------------------------------------

## Возможности

### Пользователь (USER)

-   Просмотр своих карт (с пагинацией и фильтрацией по статусу)
-   Просмотр баланса карты
-   Запрос на блокировку карты
-   Переводы между своими картами

### Администратор (ADMIN)

-   Создание карт
-   Блокировка / активация карт
-   Удаление карт
-   Просмотр всех карт
-   Управление пользователями

------------------------------------------------------------------------

## Архитектура

controller → service → repository → database

------------------------------------------------------------------------

## Безопасность

-   JWT аутентификация
-   Роли: USER, ADMIN
-   BCrypt хэширование паролей
-   Маскирование номера карты

------------------------------------------------------------------------

## Конфигурация приложения

Файл: `src/main/resources/application.yml`

### Основные настройки:

``` yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/bank_db
    username: bank_user
    password: bank_user

jwt:
  secret: your_secret_key_here
  expiration: 86400000
```

### Что менять при проблемах:

Проблемы с БД: - Запусти docker-compose: docker-compose up -d - Проверь
порт 5433 - При необходимости поменяй на 5432 и в docker-compose.yml

Проблемы с JWT: - Секрет минимум 32 символа

Проблемы с портом: - Измени server.port

Проблемы со Swagger: - http://localhost:8080/swagger-ui.html -
http://localhost:8080/swagger-ui/index.html

------------------------------------------------------------------------

## API

Auth: POST /api/auth/register\
POST /api/auth/login

User: GET /api/user/cards\
POST /api/user/cards/{id}/block-request\
GET /api/user/cards/{id}/balance\
POST /api/user/transfer

Admin: POST /api/admin/cards\
GET /api/admin/cards\
PUT /api/admin/cards/{id}/block\
PUT /api/admin/cards/{id}/activate\
DELETE /api/admin/cards/{id}\
GET /api/admin/users

------------------------------------------------------------------------

## Запуск

docker-compose up -d\
mvn clean install\
mvn spring-boot:run

Swagger: http://localhost:8080/swagger-ui.html

------------------------------------------------------------------------

## Тесты

mvn test

------------------------------------------------------------------------

## Технологии

Java 17, Spring Boot, Security, JPA, PostgreSQL, Liquibase, JWT, Docker
