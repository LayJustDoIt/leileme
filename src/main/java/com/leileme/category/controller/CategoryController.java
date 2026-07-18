package com.leileme.category.controller;

import com.leileme.category.service.CategoryService;
import com.leileme.category.vo.CategoryVO;
import com.leileme.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) { this.categoryService = categoryService; }

    @GetMapping
    public ApiResponse<List<CategoryVO>> list() {
        return ApiResponse.success(categoryService.listEnabled());
    }
}
