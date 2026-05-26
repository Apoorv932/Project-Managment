package com.personalfinance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class FinanceApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullFinanceWorkflowWorksWithSessionAuthentication() throws Exception {
        MockHttpSession session = registerAndLogin("phase4-user@example.com");

        mockMvc.perform(get("/api/categories").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[?(@.name == 'Salary' && @.type == 'INCOME')]").exists())
                .andExpect(jsonPath("$.categories[?(@.name == 'Food' && @.type == 'EXPENSE')]").exists());

        mockMvc.perform(post("/api/categories")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Freelance",
                                  "type": "INCOME"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Freelance"))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.isCustom").value(true));

        MvcResult incomeResult = mockMvc.perform(post("/api/transactions")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 50000.00,
                                  "date": "2024-01-15",
                                  "category": "Salary",
                                  "description": "January Salary"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("Salary"))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andReturn();

        long incomeId = readId(incomeResult);

        mockMvc.perform(post("/api/transactions")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 1500.00,
                                  "date": "2024-01-16",
                                  "category": "Food",
                                  "description": "Groceries"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("EXPENSE"));

        mockMvc.perform(get("/api/transactions")
                        .session(session)
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .param("category", "Salary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions.length()").value(1))
                .andExpect(jsonPath("$.transactions[0].id").value((int) incomeId));

        mockMvc.perform(put("/api/transactions/" + incomeId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 60000.00,
                                  "description": "Updated January Salary"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(60000.00))
                .andExpect(jsonPath("$.date").value("2024-01-15"));

        mockMvc.perform(post("/api/goals")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "goalName": "Emergency Fund",
                                  "targetAmount": 5000.00,
                                  "targetDate": "2027-01-01",
                                  "startDate": "2024-01-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"))
                .andExpect(jsonPath("$.currentProgress").value(58500.00))
                .andExpect(jsonPath("$.remainingAmount").value(0));

        mockMvc.perform(get("/api/reports/monthly/2024/1").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(1))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.totalIncome.Salary").value(60000.00))
                .andExpect(jsonPath("$.totalExpenses.Food").value(1500.00))
                .andExpect(jsonPath("$.netSavings").value(58500.00));

        mockMvc.perform(get("/api/reports/yearly/2024").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").doesNotExist())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.netSavings").value(58500.00));

        mockMvc.perform(delete("/api/categories/Food").session(session))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/categories/Freelance").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    @Test
    void rejectsUnauthenticatedRequestsValidationErrorsAndCrossUserAccess() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "bad-email",
                                  "password": "short",
                                  "fullName": "",
                                  "phoneNumber": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));

        MockHttpSession ownerSession = registerAndLogin("owner@example.com");
        MockHttpSession otherSession = registerAndLogin("other@example.com");

        MvcResult transactionResult = mockMvc.perform(post("/api/transactions")
                        .session(ownerSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 1000.00,
                                  "date": "2024-02-01",
                                  "category": "Salary",
                                  "description": "Private income"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        long transactionId = readId(transactionResult);

        mockMvc.perform(put("/api/transactions/" + transactionId)
                        .session(otherSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 2000.00
                                }
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/transactions")
                        .session(ownerSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount": 100.00,
                                  "date": "2099-01-01",
                                  "category": "Food",
                                  "description": "Future transaction"
                                }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handlesConflictsGoalManagementInvalidLoginAndLogout() throws Exception {
        MockHttpSession session = registerAndLogin("management@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "management@example.com",
                                  "password": "password123",
                                  "fullName": "Duplicate User",
                                  "phoneNumber": "+1234567890"
                                }
                                """))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "management@example.com",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/categories")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Salary",
                                  "type": "INCOME"
                                }
                                """))
                .andExpect(status().isConflict());

        MvcResult goalResult = mockMvc.perform(post("/api/goals")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "goalName": "Vacation",
                                  "targetAmount": 10000.00,
                                  "targetDate": "2027-06-01",
                                  "startDate": "2024-01-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.remainingAmount").value(10000.00))
                .andReturn();

        long goalId = readId(goalResult);

        mockMvc.perform(get("/api/goals").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals.length()").value(1));

        mockMvc.perform(get("/api/goals/" + goalId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalName").value("Vacation"));

        mockMvc.perform(put("/api/goals/" + goalId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "targetAmount": 12000.00,
                                  "targetDate": "2027-07-01"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetAmount").value(12000.00))
                .andExpect(jsonPath("$.targetDate").value("2027-07-01"));

        mockMvc.perform(delete("/api/goals/" + goalId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));

        mockMvc.perform(get("/api/goals/" + goalId).session(session))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));

        mockMvc.perform(get("/api/goals").session(session))
                .andExpect(status().isUnauthorized());
    }

    private MockHttpSession registerAndLogin(String username) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "password123",
                                  "fullName": "Test User",
                                  "phoneNumber": "+1234567890"
                                }
                                """.formatted(username)))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "password123"
                                }
                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andReturn();

        HttpSession session = loginResult.getRequest().getSession(false);
        return (MockHttpSession) session;
    }

    private long readId(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
