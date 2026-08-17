package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.client.InternationalizationClient;
import com.example.service.SomeService;

@SpringBootTest
class DemoApplicationTests {

    @MockBean
    private SomeService someService;

    @MockBean
    private InternationalizationClient internationalizationClient;

    @Test
    void contextLoads() {
    }
}
