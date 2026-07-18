package com.leileme.content.controller;

import com.leileme.common.response.ApiResponse;
import com.leileme.content.service.ContentService;
import com.leileme.content.vo.ContentDetailVO;
import com.leileme.content.vo.RandomTipVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) { this.contentService = contentService; }

    @GetMapping("/contents/{id}")
    public ApiResponse<ContentDetailVO> detail(@PathVariable Long id,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) String sessionId) {
        return ApiResponse.success(contentService.getDetail(id, userId, sessionId));
    }

    @GetMapping("/random-tip")
    public ApiResponse<RandomTipVO> randomTip() {
        return ApiResponse.success(contentService.randomTip());
    }
}
