package com.sogonsogon.neighclova.dto.request.news;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsMessageRequestDto {
    private String role;
    private String content;
}
