package com.example.bankcards.util;

public final class Message {

    private Message() {}

    // Сообщения для пользователей и карт
    public static final String CARD_NOT_FOUND = "Карта не найдена";
    public static final String CARD_ALREADY_EXISTS = "Карта с таким номером уже существует";
    public static final String CARD_BLOCKED = "Карта блокирована";
    public static final String CARD_ACTIVATED = "Карта активирована";
    public static final String CARD_EXPIRED = "Карта просрочена";
    public static final String CARD_DELETED = "Карта удалена";
    public static final String CARD_NOT_ACTIVE = "Карта неактивна";
    public static final String CAN_BLOCK_OWN = "Вы можете заблокировать только свою карту";
    public static final String CANT_BLOCK_EXPIRED = "Нельзя заблокировать просроченую карту";
    public static final String CANT_ACTIVATE_EXPIRED = "Нельзя активировать просроченую карту";
    public static final String BLOCK_REQUEST_SENT = "Запрос на блокировку отправлен";

    // Сообщения для переводов
    public static final String INSUFFICIENT_FUNDS = "Недостаточно средств";
    public static final String SOURCE_CARD_NOT_FOUND = "Исходная карта не найдена или в доступе отказано";
    public static final String SOURCE_CARD_IS_NOT_ACTIVE = "Исходная карта неактивна";
    public static final String DESTINATION_CARD_NOT_FOUND = "Целевая карта не найдена илии в доступе отказано";
    public static final String DESTINATION_CARD_IS_NOT_ACTIVE = "Целевая карта неактивна";
    public static final String SAME_CARD_TRANSFER = "Перевод на ту же карту невозможен";
    public static final String TRANSFER_COMPLETED = "Перевод выполнен";

    // Сообщения для пользователей
    public static final String USER_SUCCESSFULLY_REGISTERED = "Пользователь успешно зарегистрирован";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String USERNAME_ALREADY_EXISTS = "Имя пользователя уже используется";
    public static final String ACCESS_DENIED = "В доступе отказано";

    // Сообщения для аутентификации
    public static final String INVALID_CREDENTIALS = "Неверное имя пользователя или пароль";
    public static final String TOKEN_EXPIRED = "Токен JWT просрочен";
    public static final String TOKEN_INVALID = "Неверный тоткен JWT";

    // Сообщения для общих ошибок
    public static final String INTERNAL_ERROR = "Внутренняя ошибка сервера";
    public static final String BAD_REQUEST = "Неверный запрос";

}