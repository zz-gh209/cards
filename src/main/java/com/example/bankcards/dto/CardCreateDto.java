package com.example.bankcards.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CardCreateDto {

    @NotBlank(message = "Номер карты обязателен")
    private String cardNumber;

    @NotBlank(message = "Имя владельца обязательно")
    private String owner;

    @NotNull(message = "Срок действия обязателен")
    private LocalDate expiryDate;

    @NotNull(message = "Баланс обязателен")
    @PositiveOrZero(message = "Баланс должен быть >= 0")
    private BigDecimal balance;

    @NotNull(message = "Идентификатор пользователя обязателен")
    private Long userId;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
