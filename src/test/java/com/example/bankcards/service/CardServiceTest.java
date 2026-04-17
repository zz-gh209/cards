package com.example.bankcards.service;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private EncryptionUtil encryptionUtil;

    @InjectMocks
    private CardService cardService;

    private User user;
    private Card fromCard;
    private Card toCard;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setBalance(BigDecimal.valueOf(1000));
        fromCard.setUser(user);

        toCard = new Card();
        toCard.setId(2L);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setBalance(BigDecimal.valueOf(500));
        toCard.setUser(user);
    }

    @Test
    void transferBetweenOwnCards_success() {
        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(200));

        when(cardRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(toCard));

        cardService.transferBetweenOwnCards(request, user);

        assertEquals(BigDecimal.valueOf(800), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), toCard.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transferBetweenOwnCards_insufficientFunds() {
        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(2000));

        when(cardRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndUser(2L, user)).thenReturn(Optional.of(toCard));

        assertThrows(RuntimeException.class, () -> cardService.transferBetweenOwnCards(request, user));
    }

    @Test
    void blockCard_userOwnCard_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));

        cardService.blockCard(1L, user, false);

        assertEquals(CardStatus.BLOCKED, fromCard.getStatus());
        verify(cardRepository).save(fromCard);
    }

    @Test
    void blockCard_adminAnyCard_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));

        cardService.blockCard(1L, null, true);

        assertEquals(CardStatus.BLOCKED, fromCard.getStatus());
        verify(cardRepository).save(fromCard);
    }

    @Test
    void blockCard_userNotOwnCard_throwsException() {
        User otherUser = new User();
        otherUser.setId(2L);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));

        assertThrows(RuntimeException.class, () -> cardService.blockCard(1L, otherUser, false));
    }

    @Test
    void createCard_duplicateNumber_throwsException() {
        String cardNumber = "1234567890123456";
        String encrypted = "encrypted";
        when(encryptionUtil.encrypt(cardNumber)).thenReturn(encrypted);
        when(cardRepository.findByEncryptedNumber(encrypted)).thenReturn(Optional.of(new Card()));

        assertThrows(RuntimeException.class, () -> cardService.createCard(cardNumber, "Owner",
                LocalDate.now().plusYears(3), BigDecimal.valueOf(100), user));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void getUserCards_withStatusFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> cardPage = new PageImpl<>(List.of(fromCard));
        when(cardRepository.findByUserAndStatus(eq(user), eq(CardStatus.ACTIVE), eq(pageable)))
                .thenReturn(cardPage);

        Page<Card> result = cardService.getUserCards(user, CardStatus.ACTIVE, pageable);
        assertEquals(1, result.getTotalElements());
    }
}
