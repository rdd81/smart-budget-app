package com.smartbudget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbudget.dto.CategorySuggestion;
import com.smartbudget.dto.CategorySuggestionRequest;
import com.smartbudget.entity.TransactionType;
import com.smartbudget.service.CategorizationService;
import com.smartbudget.service.JwtService;
import com.smartbudget.config.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategorizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategorizationService categorizationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Test
    void suggestCategory_ShouldReturnSuggestion() throws Exception {
        UUID categoryId = UUID.randomUUID();
        Mockito.when(categorizationService.suggestCategory(any(), any(), eq(TransactionType.EXPENSE), any()))
                .thenReturn(new CategorySuggestion(categoryId, "Food", 0.9));

        CategorySuggestionRequest request = new CategorySuggestionRequest();
        request.setDescription("Starbucks latte");
        request.setAmount(new BigDecimal("5.25"));
        request.setTransactionType(TransactionType.EXPENSE);

        mockMvc.perform(post("/api/categorization/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(categoryId.toString()))
                .andExpect(jsonPath("$.categoryName").value("Food"))
                .andExpect(jsonPath("$.confidence").value(0.9));
    }

    @Test
    void suggestCategory_NoSuggestion_ShouldReturnNullFields() throws Exception {
        Mockito.when(categorizationService.suggestCategory(any(), any(), eq(TransactionType.EXPENSE), any()))
                .thenReturn(null);

        CategorySuggestionRequest request = new CategorySuggestionRequest();
        request.setDescription("Unknown vendor");
        request.setAmount(new BigDecimal("12.34"));
        request.setTransactionType(TransactionType.EXPENSE);

        mockMvc.perform(post("/api/categorization/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryId").value(nullValue()))
                .andExpect(jsonPath("$.categoryName").value(nullValue()))
                .andExpect(jsonPath("$.confidence").value(0.0));
    }

    @Test
    void suggestCategory_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        CategorySuggestionRequest request = new CategorySuggestionRequest();
        request.setDescription(""); // invalid blank
        request.setTransactionType(null);

        mockMvc.perform(post("/api/categorization/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
