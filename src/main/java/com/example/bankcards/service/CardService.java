package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final EncryptionUtil encryptionUtil;

    public CardService(CardRepository cardRepository, EncryptionUtil encryptionUtil) {
        this.cardRepository = cardRepository;
        this.encryptionUtil = encryptionUtil;
    }

    // Создание карты (ADMIN)
    public Card createCard(String cardNumber, String owner, LocalDate expiryDate, BigDecimal balance, User user) {
        String encryptedNumber = encryptionUtil.encrypt(cardNumber);
        if (cardRepository.findByEncryptedNumber(encryptedNumber).isPresent()) {
            throw new RuntimeException(Message.CARD_ALREADY_EXISTS);
        }
        Card card = new Card();
        card.setEncryptedNumber(encryptedNumber);
        card.setOwner(owner);
        card.setExpiryDate(expiryDate);
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(balance);
        card.setUser(user);
        return cardRepository.save(card);
    }

    // Просмотр всех карт (ADMIN) с пагинацией
    public Page<Card> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    // Просмотр своих карт (USER) с пагинацией и фильтром по статусу
    public Page<Card> getUserCards(User user, CardStatus status, Pageable pageable) {
        if (status != null) {
            return cardRepository.findByUserAndStatus(user, status, pageable);
        } else {
            return cardRepository.findByUser(user, pageable);
        }
    }

    // Блокировка карты (ADMIN может любую, USER только свою)
    @Transactional
    public void blockCard(Long cardId, User requester, boolean isAdmin) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException(Message.CARD_NOT_FOUND));
        if (!isAdmin && !card.getUser().getId().equals(requester.getId())) {
            throw new RuntimeException(Message.CAN_BLOCK_OWN);
        }
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new RuntimeException(Message.CANT_BLOCK_EXPIRED);
        }
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    // Активация карты (только ADMIN)
    @Transactional
    public void activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException(Message.CARD_NOT_FOUND));
        if (card.getStatus() == CardStatus.ACTIVE) {
            return;
        }
        if (card.getStatus() == CardStatus.EXPIRED) {
            throw new RuntimeException(Message.CANT_ACTIVATE_EXPIRED);
        }

        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    // Удаление карты (ADMIN)
    @Transactional
    public void deleteCard(Long cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new RuntimeException(Message.CARD_NOT_FOUND);
        }
        cardRepository.deleteById(cardId);
    }

    // Просмотр баланса карты (только свои)
    public BigDecimal getBalance(Long cardId, User requester) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException(Message.CARD_NOT_FOUND));
        if (!card.getUser().getId().equals(requester.getId())) {
            throw new RuntimeException(Message.ACCESS_DENIED);
        }
        return card.getBalance();
    }

    // Перевод между своими картами
    @Transactional
    public void transferBetweenOwnCards(TransferRequestDto request, User user) {
        Card fromCard = cardRepository.findByIdAndUser(request.getFromCardId(), user)
                .orElseThrow(() -> new RuntimeException(Message.SOURCE_CARD_NOT_FOUND));
        Card toCard = cardRepository.findByIdAndUser(request.getToCardId(), user)
                .orElseThrow(() -> new RuntimeException(Message.DESTINATION_CARD_NOT_FOUND));

        if (fromCard.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException(Message.SOURCE_CARD_IS_NOT_ACTIVE);
        }
        if (toCard.getStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException(Message.DESTINATION_CARD_IS_NOT_ACTIVE);
        }
        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException(Message.INSUFFICIENT_FUNDS);
        }
        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }
}
