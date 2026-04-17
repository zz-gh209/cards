package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferRequestDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtAuthenticationFilter;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private EncryptionUtil encryptionUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Card testCard;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("user");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setStatus(CardStatus.ACTIVE);
        testCard.setEncryptedNumber("enc123");
        when(userService.findByUsername(anyString())).thenReturn(testUser);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getMyCards_success() throws Exception {
        Page<Card> page = new PageImpl<>(List.of(testCard), PageRequest.of(0, 10), 1);
        when(cardService.getUserCards(any(User.class), eq(null), any(PageRequest.class))).thenReturn(page);
        when(encryptionUtil.maskCardNumber("enc123")).thenReturn("**** **** **** 1234");

        mockMvc.perform(get("/api/user/cards")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].encryptedNumber").value("**** **** **** 1234"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void transfer_success() throws Exception {
        TransferRequestDto dto = new TransferRequestDto();
        dto.setFromCardId(1L);
        dto.setToCardId(2L);
        dto.setAmount(BigDecimal.valueOf(100));

        doNothing().when(cardService).transferBetweenOwnCards(any(), any());

        mockMvc.perform(post("/api/user/transfer")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(Message.TRANSFER_COMPLETED));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void getBalance_success() throws Exception {
        when(cardService.getBalance(eq(1L), any(User.class))).thenReturn(BigDecimal.valueOf(500));
        when(userService.findByUsername("user")).thenReturn(testUser);

        mockMvc.perform(get("/api/user/cards/1/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(500));
    }
}
