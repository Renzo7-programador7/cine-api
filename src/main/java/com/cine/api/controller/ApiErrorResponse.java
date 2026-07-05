package com.cine.api.controller;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiErrorResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

}
