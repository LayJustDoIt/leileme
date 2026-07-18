package com.leileme.user.controller;

import com.leileme.common.auth.UserContext;
import com.leileme.common.exception.UnauthorizedException;
import com.leileme.common.response.ApiResponse;
import com.leileme.common.vo.PageResult;
import com.leileme.content.vo.ContentCardVO;
import com.leileme.history.service.HistoryService;
import com.leileme.history.vo.HistoryItemVO;
import com.leileme.user.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {
    private final FavoriteService favoriteService;
    private final HistoryService historyService;

    public MeController(FavoriteService favoriteService, HistoryService historyService) {
        this.favoriteService = favoriteService;
        this.historyService = historyService;
    }

    @GetMapping("/favorites")
    public ApiResponse<PageResult<ContentCardVO>> favorites(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        return ApiResponse.success(favoriteService.listFavorites(userId, page, size));
    }

    @GetMapping("/history")
    public ApiResponse<PageResult<HistoryItemVO>> history(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "20") long size) {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        return ApiResponse.success(historyService.listHistory(userId, page, size));
    }

    @DeleteMapping("/history")
    public ApiResponse<Void> clearHistory() {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        historyService.clearAll(userId);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/history/{contentId}")
    public ApiResponse<Void> deleteHistoryItem(@PathVariable Long contentId) {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        historyService.deleteOne(userId, contentId);
        return ApiResponse.success(null);
    }
}
