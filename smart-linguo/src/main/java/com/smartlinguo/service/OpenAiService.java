package com.smartlinguo.service;

import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.smartlinguo.dto.CreateTranslationRequest;
import com.smartlinguo.enums.SupportedLanguage;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OpenAiService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public record TranslatableItem(
        SupportedLanguage sourceLang,
        SupportedLanguage targetLang,
        List<String> texts
    ) {}

    public void createTranslation(CreateTranslationRequest request) {
        List<TranslatableItem> items = preformatData(request);
        String systemPrompt = createSystemPrompt();
        try {
            String userInput = MAPPER.writeValueAsString(items);
            callOpenAi(systemPrompt, userInput);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize translation items", e);
        }
    }

    private void callOpenAi(String systemPrompt, String userInput) {
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();
        ResponseCreateParams params = ResponseCreateParams.builder()
                .model("gpt-5.5")
                .instructions(systemPrompt)
                .input(userInput)
                .build();
        Response response = client.responses().create(params);
        System.out.println(response);
    }

    private List<TranslatableItem> preformatData(CreateTranslationRequest request) {
        return request.targetLangs().stream()
            .map(targetLang -> new TranslatableItem(
                request.sourceLanguage(),
                targetLang,
                request.texts()
            ))
            .toList();
    }

    private String createSystemPrompt() {
        return """
            You are a professional translation API. Your sole function is to translate text.

            ## INPUT FORMAT
            You receive a JSON array where each object has this structure:
            {
                "sourceLang": "<ISO 639-1 code, lowercase>",
                "targetLang": "<ISO 639-1 code, lowercase>",
                "texts": ["<string, may contain HTML>", ...]
            }

            ## YOUR TASK
            For each object in the array:
            1. Translate EACH element of the "texts" array from "sourceLang" to "targetLang"
            2. Preserve HTML exactly (tags, attributes, href values, structure)
            3. Translate only visible text content — never translate tag names or attributes
            4. Return a translated version of the full array

            ## RULES
            - Do NOT modify keys: sourceLang, targetLang, texts
            - Language codes are ISO 639-1, lowercase (e.g. "fr", "en", "es")
            - The "texts" array must keep the SAME order and SAME size as input
            - Preserve all whitespace and formatting
            - Return a JSON array `[...]`, never a bare object `{...}`
            - Do NOT add explanations, markdown, or code fences

            ## EXAMPLE
            Input:
            [
                {
                    "sourceLang": "en",
                    "targetLang": "fr",
                    "texts": [
                        "<p>Hello world</p>",
                        "Welcome <strong>user</strong>",
                        "Forgot password?"
                    ]
                }
            ]

            Output:
            [
                {
                    "sourceLang": "en",
                    "targetLang": "fr",
                    "texts": [
                        "<p>Bonjour le monde</p>",
                        "Bienvenue <strong>utilisateur</strong>",
                        "Mot de passe oublié ?"
                    ]
                }
            ]
            """;
    }
}