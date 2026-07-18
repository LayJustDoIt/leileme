package com.leileme.search.controller;

import com.leileme.common.response.ApiResponse;
import com.leileme.search.dto.SearchClickRequest;
import com.leileme.search.service.SearchService;
import com.leileme.search.vo.SearchResultVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) { this.searchService = searchService; }

    @GetMapping
    public ApiResponse<SearchResultVO> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String sessionId) {
        return ApiResponse.success(searchService.search(keyword, contentType, categoryId, page, size, userId, sessionId));
    }

    @PostMapping("/click")
    public ApiResponse<Void> click(@Valid @RequestBody SearchClickRequest request,
                                   @RequestParam(required = false) Long userId) {
        searchService.recordClick(request.searchRequestId(), request.contentId(), request.position(),
                userId, request.sessionId());
        return ApiResponse.success(null);
    }
}
