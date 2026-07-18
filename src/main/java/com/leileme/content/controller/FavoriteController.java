package com.leileme.content.controller;

import com.leileme.common.auth.UserContext;
import com.leileme.common.exception.UnauthorizedException;
import com.leileme.common.response.ApiResponse;
import com.leileme.user.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contents/{id}/favorite")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ApiResponse<Void> favorite(@PathVariable Long id) {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        favoriteService.favorite(userId, id);
        return ApiResponse.success(null);
    }

    @DeleteMapping
    public ApiResponse<Void> unfavorite(@PathVariable Long id) {
        Long userId = UserContext.get();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        favoriteService.unfavorite(userId, id);
        return ApiResponse.success(null);
    }
}
