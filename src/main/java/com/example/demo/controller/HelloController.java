package com.example.demo.controller;

import com.example.demo.dto.HelloResponse;
import com.example.demo.dto.InputBody;
import com.example.service.SomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HelloController {

    private final SomeService someService;

    @PostMapping("/api/hello")
    public HelloResponse postHello(@RequestBody InputBody body) {
        someService.next("nextId");

        return HelloResponse.builder()
                .message("Hello")
                .status("SUCCESS")
                .build();
    }
}
