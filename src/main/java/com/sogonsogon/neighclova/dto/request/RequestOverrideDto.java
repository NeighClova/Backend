package com.sogonsogon.neighclova.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestOverrideDto {
    private Map<String, Object> operations;
}

