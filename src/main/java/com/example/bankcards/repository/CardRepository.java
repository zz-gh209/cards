package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    // Поиск всех карт пользователя с пагинацией
    Page<Card> findByUser(User user, Pageable pageable);

    // Поиск карт пользователя с фильтрацией по статусу
    Page<Card> findByUserAndStatus(User user, CardStatus status, Pageable pageable);

    // Поиск карты по ID и пользователю для проверки принадлежности
    Optional<Card> findByIdAndUser(Long id, User user);

    // Для администратора: все карты с пагинацией и фильтрацией по статусу
    @Override
    Page<Card> findAll(Pageable pageable);

    Page<Card> findByStatus(CardStatus status, Pageable pageable);

    // Поиск карты по зашифрованному номеру (уникальность)
    Optional<Card> findByEncryptedNumber(String encryptedNumber);

    // Проверка, есть ли у пользователя карта с таким ID
    boolean existsByIdAndUser(Long id, User user);
}
