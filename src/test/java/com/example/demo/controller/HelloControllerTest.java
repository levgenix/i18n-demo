package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.service.SomeService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SomeService someService;

    @Test
    void postHello_returnsSuccess() throws Exception {
        mockMvc.perform(post("/api/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
