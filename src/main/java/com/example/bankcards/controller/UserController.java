package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final CardService cardService;
    private final UserService userService;          // добавлено поле
    private final EncryptionUtil encryptionUtil;

    public UserController(CardService cardService,
            UserService userService,
            EncryptionUtil encryptionUtil) {
        this.cardService = cardService;
        this.userService = userService;
        this.encryptionUtil = encryptionUtil;
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<Card>> getMyCards(
            @RequestParam(required = false) CardStatus status,
            Pageable pageable,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername());
        Page<Card> cards = cardService.getUserCards(user, status, pageable);
        cards.forEach(card -> {
            String masked = encryptionUtil.maskCardNumber(card.getEncryptedNumber());
            card.setEncryptedNumber(masked);
        });
        return ResponseEntity.ok(cards);
    }

    @PostMapping("/cards/{id}/block-request")
    public ResponseEntity<?> requestBlockCard(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        cardService.blockCard(id, user, false);
        return ResponseEntity.ok(Message.BLOCK_REQUEST_SENT);
    }

    @GetMapping("/cards/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        BigDecimal balance = cardService.getBalance(id, user);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        cardService.transferBetweenOwnCards(request, user);
        return ResponseEntity.ok(Message.TRANSFER_COMPLETED);
    }
}
