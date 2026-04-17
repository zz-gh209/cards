package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.Message;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CardService cardService;
    private final UserService userService;

    public AdminController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    @PostMapping("/cards")
    public ResponseEntity<Card> createCard(@Valid @RequestBody CardCreateDto dto) {
        User user = userService.findById(dto.getUserId());
        Card card = cardService.createCard(dto.getCardNumber(), dto.getOwner(),
                dto.getExpiryDate(), dto.getBalance(), user);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<Card>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(pageable));
    }

    @PutMapping("/cards/{id}/block")
    public ResponseEntity<?> blockCard(@PathVariable Long id) {
        cardService.blockCard(id, null, true);
        return ResponseEntity.ok(Message.CARD_BLOCKED);
    }

    @PutMapping("/cards/{id}/activate")
    public ResponseEntity<?> activateCard(@PathVariable Long id) {
        cardService.activateCard(id);
        return ResponseEntity.ok(Message.CARD_ACTIVATED);
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.ok(Message.CARD_DELETED);
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }
}
