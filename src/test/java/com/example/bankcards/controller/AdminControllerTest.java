package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtUtil;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = true)
@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void adminAccess_forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/cards")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_success() throws Exception {
        CardCreateDto dto = new CardCreateDto();
        dto.setCardNumber("1234567890123456");
        dto.setOwner("Owner");
        dto.setExpiryDate(LocalDate.now().plusYears(2));
        dto.setBalance(BigDecimal.valueOf(1000));
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(user);
        when(cardService.createCard(anyString(), anyString(), any(), any(), any()))
                .thenReturn(new Card());

        mockMvc.perform(post("/api/admin/cards")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_success() throws Exception {
        doNothing().when(cardService).blockCard(eq(1L), isNull(), eq(true));

        mockMvc.perform(put("/api/admin/cards/1/block")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(Message.CARD_BLOCKED));
    }

    @Test
    @WithMockUser(roles = "USER") // Недостаточно прав
    void blockCard_forbidden() throws Exception {
        mockMvc.perform(put("/api/admin/cards/1/block")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
