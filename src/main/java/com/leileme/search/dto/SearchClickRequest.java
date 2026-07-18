package com.leileme.search.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SearchClickRequest(
        @NotBlank String searchRequestId,
        @NotNull Long contentId,
        @Min(1) Integer position,
        String sessionId
) {}
