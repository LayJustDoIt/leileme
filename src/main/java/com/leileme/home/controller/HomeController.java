package com.leileme.home.controller;

import com.leileme.common.response.ApiResponse;
import com.leileme.home.service.HomeService;
import com.leileme.home.vo.HomeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) { this.homeService = homeService; }

    @GetMapping("/home")
    public ApiResponse<HomeVO> home() {
        return ApiResponse.success(homeService.getHome());
    }
}
