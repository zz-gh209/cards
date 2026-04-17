package com.example.bankcards.util;

import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    // Простейшее шифрование для тестового задания.
    // сдвиг символов. 
    public String encrypt(String data) {
        if (data == null) return null;
        StringBuilder encrypted = new StringBuilder();
        for (char c : data.toCharArray()) {
            encrypted.append((char) (c + 1));
        }
        return encrypted.toString();
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null) return null;
        StringBuilder decrypted = new StringBuilder();
        for (char c : encryptedData.toCharArray()) {
            decrypted.append((char) (c - 1));
        }
        return decrypted.toString();
    }

    // Маскирование номера карты: **** **** **** 1234
    public String maskCardNumber(String encryptedNumber) {
        String decrypted = decrypt(encryptedNumber);
        if (decrypted == null || decrypted.length() < 4) return "**** **** **** ****";
        String last4 = decrypted.substring(decrypted.length() - 4);
        return "**** **** **** " + last4;
    }
}