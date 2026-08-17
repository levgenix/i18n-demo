package com.example.service.impl;

import org.springframework.stereotype.Service;

import com.example.component.impl.SomeComponent;
import com.example.service.InternationalizationService;
import com.example.service.SomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SomeServiceImpl implements SomeService {

    private final SomeComponent someComponent;

    private final InternationalizationService internationalizationService;

    public void next(String serviceId) {
        log.info("next invoked for serviceId={}", serviceId);

        someComponent.initialize();

        internationalizationService.init(serviceId);

    }

}
