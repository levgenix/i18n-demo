package com.example.demo.dto;

import com.example.model.LocalizedRequest;

public class InputBody implements LocalizedRequest {
    @Override
    public String requestLang() {
        return "ru";
    }
}
