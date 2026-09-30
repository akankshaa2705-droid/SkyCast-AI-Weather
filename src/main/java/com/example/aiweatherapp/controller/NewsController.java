package com.example.aiweatherapp.controller;

import com.example.aiweatherapp.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/api/news")
    public String getNews() {
        return newsService.getTopNews();
    }
}