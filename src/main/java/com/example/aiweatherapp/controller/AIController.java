package com.example.aiweatherapp.controller;

import com.example.aiweatherapp.service.AIService;
import com.example.aiweatherapp.service.WeatherService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AIController {

    private final AIService aiService;
    private final WeatherService weatherService;

    public AIController(
            AIService aiService,
            WeatherService weatherService
    ) {
        this.aiService = aiService;
        this.weatherService = weatherService;
    }

    @GetMapping("/chat")
    public String chat(
            @RequestParam String question,
            @RequestParam String city
    ) {

        String weatherData =
                weatherService.getWeather(city);

        return aiService.askAI(
                question,
                weatherData
        );
    }
}