package com.example.aiweatherapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final HttpClient client =
            HttpClient.newHttpClient();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public String askAI(
            String question,
            String weatherData
    ) {

        String prompt = """
                User question:
                %s

                Current weather data from the weather application:
                %s

                Use the weather data above when answering.

                Important:
                - Do not invent weather information.
                - Current weather conditions are not the same as a future forecast.
                - If the user asks whether it will rain later today and there is no
                  rainfall forecast or probability in the data, clearly explain that
                  the current data cannot confirm future rainfall.
                - Mention the city and relevant weather information when useful.
                - Keep the answer short, friendly and easy to understand.
                """.formatted(
                question,
                weatherData
        );

        String response =
                callGemini(
                        "gemini-3.8-flash",
                        prompt
                );

        if (response != null) {
            return response;
        }

        response =
                callGemini(
                        "gemini-3.5-flash-lite",
                        prompt
                );

        if (response != null) {
            return response;
        }

        return "SkyCast AI is temporarily unavailable. Please try again.";
    }

    private String callGemini(
            String model,
            String question
    ) {

        try {

            String safeQuestion =
                    question
                            .replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n");

            String systemInstruction = """
                    You are SkyCast AI, a friendly weather assistant.

                    Answer using the weather data supplied by the application.

                    Never invent temperature, humidity, wind, pressure,
                    rainfall or forecast information.

                    Keep responses short, natural and useful.
                    """;

            String safeSystemInstruction =
                    systemInstruction
                            .replace("\\", "\\\\")
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n");

            String json = """
                    {
                      "system_instruction": {
                        "parts": [
                          {
                            "text": "%s"
                          }
                        ]
                      },
                      "contents": [
                        {
                          "parts": [
                            {
                              "text": "%s"
                            }
                          ]
                        }
                      ]
                    }
                    """.formatted(
                    safeSystemInstruction,
                    safeQuestion
            );

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/"
                            + model
                            + ":generateContent";

            for (int attempt = 0; attempt < 3; attempt++) {

                HttpRequest request =
                        HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .header(
                                        "Content-Type",
                                        "application/json"
                                )
                                .header(
                                        "x-goog-api-key",
                                        apiKey
                                )
                                .POST(
                                        HttpRequest.BodyPublishers
                                                .ofString(json)
                                )
                                .build();

                HttpResponse<String> response =
                        client.send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

                System.out.println(
                        "Gemini " + model +
                                " response: " +
                                response.statusCode()
                );

                if (response.statusCode() == 200) {

                    return extractText(
                            response.body()
                    );
                }

                if (response.statusCode() == 503) {

                    long delay =
                            1000L * (1L << attempt);

                    System.out.println(
                            "Gemini temporarily unavailable. " +
                                    "Retrying in " +
                                    delay +
                                    " ms..."
                    );

                    Thread.sleep(delay);

                    continue;
                }

                System.out.println(
                        "Gemini error: " +
                                response.body()
                );

                return null;
            }

        } catch (Exception e) {

            System.out.println(
                    "Gemini connection error: " +
                            e.getMessage()
            );
        }

        return null;
    }

    private String extractText(
            String jsonResponse
    ) {

        try {

            JsonNode root =
                    objectMapper.readTree(
                            jsonResponse
                    );

            JsonNode textNode =
                    root.path("candidates")
                            .path(0)
                            .path("content")
                            .path("parts")
                            .path(0)
                            .path("text");

            if (
                    !textNode.isMissingNode()
                            && !textNode.isNull()
            ) {

                return textNode.asText();
            }

        } catch (Exception e) {

            System.out.println(
                    "Unable to read Gemini response: "
                            + e.getMessage()
            );
        }

        return "I received a response, but I couldn't read it properly.";
    }
}