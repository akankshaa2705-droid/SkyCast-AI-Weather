package com.example.aiweatherapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NewsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Put your NEW NewsAPI key here
    private final String apiKey = "bd38cee1182d4966b0c5451fcf150e3a";

    public String getTopNews() {

        try {

            String url =
                    "https://newsapi.org/v2/everything"
                            + "?q=weather"
                            + "&language=en"
                            + "&sortBy=publishedAt"
                            + "&pageSize=100"
                            + "&apiKey=" + apiKey;

            String response =
                    restTemplate.getForObject(url, String.class);

            JsonNode root =
                    objectMapper.readTree(response);

            if (!"ok".equals(root.path("status").asText())) {
                return "{\"status\":\"error\",\"message\":\"Unable to load news.\"}";
            }

            JsonNode articles = root.path("articles");

            List<ScoredArticle> candidates = new ArrayList<>();
            Set<String> usedTitles = new HashSet<>();

            for (JsonNode article : articles) {

                String title =
                        article.path("title").asText("").trim();

                String description =
                        article.path("description").asText("").trim();

                String source =
                        article.path("source").path("name").asText("").trim();

                String titleLower = title.toLowerCase();
                String descriptionLower = description.toLowerCase();
                String sourceLower = source.toLowerCase();

                if (title.isEmpty()) {
                    continue;
                }

                // ------------------------------------------------
                // REMOVE DUPLICATE STORIES
                // ------------------------------------------------

                if (!usedTitles.add(titleLower)) {
                    continue;
                }

                // ------------------------------------------------
                // HARD BLOCK UNRELATED TOPICS
                // ------------------------------------------------

                String[] blockedTopics = {

                        // Sports
                        "football",
                        "soccer",
                        "basketball",
                        "cricket",
                        "tennis",
                        "baseball",
                        "golf",
                        "rugby",
                        "f1",
                        "formula 1",
                        "formula one",
                        "grand prix",
                        "nascar",
                        "nhl",
                        "nfl",
                        "nba",
                        "mlb",
                        "championship",
                        "match",
                        "game preview",
                        "game report",

                        // Cars / motorsport
                        "car review",
                        "car launch",
                        "automobile",
                        "vehicle review",
                        "motorcycle",
                        "motorbike",
                        "audi",
                        "bmw",
                        "mercedes",
                        "tesla",

                        // Entertainment
                        "movie",
                        "film",
                        "celebrity",
                        "actor",
                        "actress",
                        "netflix",
                        "music",
                        "singer",
                        "album",
                        "tv show",

                        // Technology
                        "iphone",
                        "smartphone",
                        "laptop",
                        "gaming",
                        "playstation",
                        "xbox",
                        "software update",

                        // Shopping / lifestyle
                        "shopping",
                        "discount",
                        "coupon",
                        "sale",
                        "deal",
                        "product review",
                        "fashion",
                        "clothing",
                        "sweatshirt",
                        "dress",
                        "makeup",
                        "hotel",
                        "resort",
                        "restaurant",
                        "recipe",

                        // Random lifestyle
                        "refund",
                        "room upgrade",
                        "moth-infested",
                        "viral post",
                        "social media trend"
                };

                boolean blocked = false;

                for (String word : blockedTopics) {

                    if (titleLower.contains(word)) {
                        blocked = true;
                        break;
                    }
                }

                if (blocked) {
                    continue;
                }

                // ------------------------------------------------
                // MAJOR WEATHER EVENTS
                // ------------------------------------------------

                String[] majorEvents = {

                        "storm",
                        "severe storm",
                        "tropical storm",

                        "flood",
                        "flooding",
                        "flash flood",
                        "flash flooding",

                        "cyclone",
                        "hurricane",
                        "typhoon",
                        "tornado",

                        "thunderstorm",
                        "lightning",

                        "heavy rain",
                        "heavy rainfall",
                        "extreme rainfall",
                        "record rainfall",

                        "snowstorm",
                        "blizzard",
                        "heavy snowfall",

                        "heatwave",
                        "heat wave",
                        "extreme heat",

                        "cold wave",
                        "cold snap",
                        "extreme cold",

                        "drought",

                        "extreme weather",
                        "severe weather",

                        "weather warning",
                        "weather alert",
                        "weather emergency",

                        "wind warning",
                        "strong winds",
                        "damaging winds",

                        "record temperature",
                        "temperature record",
                        "record heat",
                        "record cold",
                        "record high",
                        "record low",

                        "hottest",
                        "coldest",
                        "highest temperature",
                        "lowest temperature",

                        "longest rainy",
                        "rainiest",
                        "snowiest",

                        "monsoon",
                        "weather disaster"
                };

                int score = 0;
                boolean majorWeatherEvent = false;

                for (String word : majorEvents) {

                    if (titleLower.contains(word)) {
                        score += 12;
                        majorWeatherEvent = true;
                    }

                    if (descriptionLower.contains(word)) {
                        score += 4;
                        majorWeatherEvent = true;
                    }
                }

                // ------------------------------------------------
                // WEATHER EVENT WORDS
                // ------------------------------------------------

                String[] weatherWords = {

                        "weather",
                        "rain",
                        "rainfall",
                        "rainy",
                        "snow",
                        "snowfall",
                        "wind",
                        "winds",
                        "temperature",
                        "temperatures",
                        "monsoon",
                        "meteorological",
                        "climate"
                };

                int weatherWordCount = 0;

                for (String word : weatherWords) {

                    if (titleLower.contains(word)) {
                        score += 4;
                        weatherWordCount++;
                    }

                    if (descriptionLower.contains(word)) {
                        score += 1;
                    }
                }

                // ------------------------------------------------
                // WEATHER IMPACT
                // ------------------------------------------------

                String[] impactWords = {

                        "stranded",
                        "rescued",
                        "evacuated",
                        "evacuation",
                        "displaced",

                        "damage",
                        "damaged",
                        "destroyed",

                        "disrupted",
                        "disruption",

                        "power outage",
                        "power outages",

                        "roads closed",
                        "road closures",

                        "schools closed",
                        "airport closed",
                        "flights cancelled",
                        "travel disruption",

                        "warning issued",
                        "authorities warn",
                        "officials warn",

                        "emergency response",
                        "disaster response"
                };

                for (String word : impactWords) {

                    if (titleLower.contains(word)) {
                        score += 7;
                    }

                    if (descriptionLower.contains(word)) {
                        score += 2;
                    }
                }

                // ------------------------------------------------
                // RECORD / EXTREME CONDITIONS
                // ------------------------------------------------

                String[] recordWords = {

                        "record",
                        "historic",
                        "unprecedented",
                        "extreme",
                        "worst",
                        "highest",
                        "lowest",
                        "longest",
                        "shortest"
                };

                for (String word : recordWords) {

                    if (titleLower.contains(word)) {
                        score += 5;
                    }
                }

                // ------------------------------------------------
                // ROUTINE FORECAST FILTER
                // ------------------------------------------------

                boolean routineForecast =

                        titleLower.contains("weather today")
                                || titleLower.contains("weather tomorrow")
                                || titleLower.contains("weather forecast")
                                || titleLower.contains("weather update")
                                || titleLower.contains("forecast for")
                                || titleLower.contains("daily forecast")
                                || titleLower.contains("weekly forecast")
                                || titleLower.contains("this week's weather")
                                || titleLower.contains("temperatures today");

                if (routineForecast && !majorWeatherEvent) {
                    continue;
                }

                // ------------------------------------------------
                // MUST ACTUALLY BE ABOUT WEATHER
                // ------------------------------------------------

                boolean genuineWeatherStory =
                        majorWeatherEvent
                                || weatherWordCount >= 2;

                if (!genuineWeatherStory) {
                    continue;
                }

                // If it only says "weather" once, reject it.
                if (!majorWeatherEvent && weatherWordCount < 2) {
                    continue;
                }

                // ------------------------------------------------
                // QUALITY BONUS
                // ------------------------------------------------

                if (!description.isEmpty()) {
                    score += 2;
                }

                if (!article.path("url").asText("").isEmpty()) {
                    score += 2;
                }

                if (!article.path("urlToImage").asText("").isEmpty()) {
                    score += 3;
                }

                if (!source.isEmpty()) {
                    score += 1;
                }

                // ------------------------------------------------
                // FINAL QUALITY THRESHOLD
                // ------------------------------------------------

                if (score >= 14) {

                    candidates.add(
                            new ScoredArticle(
                                    score,
                                    article
                            )
                    );
                }
            }

            // ------------------------------------------------
            // SORT BY WEATHER IMPORTANCE
            // ------------------------------------------------

            candidates.sort(
                    Comparator.comparingInt(
                            ScoredArticle::score
                    ).reversed()
            );

            ArrayNode filteredArticles =
                    objectMapper.createArrayNode();

            int limit =
                    Math.min(10, candidates.size());

            for (int i = 0; i < limit; i++) {

                filteredArticles.add(
                        candidates.get(i).article()
                );
            }

            // ------------------------------------------------
            // RESPONSE
            // ------------------------------------------------

            var result =
                    objectMapper.createObjectNode();

            result.put(
                    "status",
                    "ok"
            );

            result.put(
                    "totalResults",
                    filteredArticles.size()
            );

            result.set(
                    "articles",
                    filteredArticles
            );

            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(result);

        } catch (Exception e) {

            e.printStackTrace();

            return "{\"status\":\"error\",\"message\":\"Unable to load news.\"}";
        }
    }

    private record ScoredArticle(
            int score,
            JsonNode article
    ) {
    }
}