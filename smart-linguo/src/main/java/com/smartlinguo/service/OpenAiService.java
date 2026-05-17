package com.smartlinguo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputItem;
import com.smartlinguo.dto.request.CreateTranslationRequest;
import com.smartlinguo.dto.response.TranslationResult;
import com.smartlinguo.entity.Translation;
import com.smartlinguo.entity.UsageQuota;
import com.smartlinguo.enums.SupportedLanguage;
import com.smartlinguo.repository.TranslationRepository;
import com.smartlinguo.repository.UsageQuotaRepository;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OpenAiService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final OpenAIClient client = OpenAIOkHttpClient.fromEnv();
    private final TranslationRepository translationRepository;
    private final UsageQuotaRepository usageQuotaRepository;

    public OpenAiService(TranslationRepository translationRepository, UsageQuotaRepository usageQuotaRepository) {
        this.translationRepository = translationRepository;
        this.usageQuotaRepository = usageQuotaRepository;
    }
    public record TranslatableItem(
        SupportedLanguage sourceLang,
        SupportedLanguage targetLang,
        List<String> texts
    ) {}

    public Uni<List<TranslationResult>> createTranslation(CreateTranslationRequest request, UUID apiKeyId, String keycloakUserId) {

        List<TranslatableItem> items = preformatData(request);
        String systemPrompt = createSystemPrompt();

        List<Uni<Response>> calls = items.stream()
                .map(item -> callOpenAiUni(systemPrompt, item))
                .toList();

        return Uni.combine().all().unis(calls)
                .with(responses -> {
                    List<TranslationResult> translations = new ArrayList<>();
                    long totalTokenUsed = 0;

                    for (int i = 0; i < responses.size(); i++) {
                        Response response = (Response) responses.get(i);

                        long totalTokens = extractTotalTokens(response);
                        totalTokenUsed += totalTokens;
                        String json = extractOutputText(response);
                        TranslationResult translation = parseJSON(json, totalTokens);
                        translations.add(translation);
                    }

                    UsageQuota quota = usageQuotaRepository.findByKeycloakUserId(keycloakUserId);
                    quota.tokensRemaining -= totalTokenUsed;
                    usageQuotaRepository.persist(quota);

                    List<Translation> entities = toEntities(translations, request, apiKeyId);
                    translationRepository.insertBatch(entities);

                    return translations;
                });
    }

    private Uni<Response> callOpenAiUni(String systemPrompt, TranslatableItem item) {
        return Uni.createFrom().item(() -> {
                    try {
                        String userInput = MAPPER.writeValueAsString(item);

                        ResponseCreateParams params = ResponseCreateParams.builder()
                                .model("gpt-5.5")
                                .instructions(systemPrompt)
                                .input(userInput)
                                .build();

                        return client.responses().create(params);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to serialize TranslatableItem", e);
                    }
                })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
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
            You receive a JSON object with this structure:
            {
                "sourceLang": "<ISO 639-1 code, lowercase>",
                "targetLang": "<ISO 639-1 code, lowercase>",
                "texts": ["<string, may contain HTML>", ...]
            }

            ## YOUR TASK
            1. Translate EACH element of the "texts" array from "sourceLang" to "targetLang"
            2. Preserve HTML exactly (tags, attributes, href values, structure)
            3. Translate only visible text content — never translate tag names or attributes
            4. Return exactly one translated JSON object

            ## RULES
            - Do NOT modify keys: sourceLang, targetLang, texts
            - Language codes are ISO 639-1, lowercase (e.g. "fr", "en", "es")
            - The "texts" array must keep the SAME order and SAME size as input
            - Preserve all whitespace and formatting
            - Return exactly one JSON object `{...}`
            - Do NOT add explanations, markdown, or code fences

                        ## STRICT OUTPUT CONTRACT
                        - Output MUST be valid JSON parseable by a strict JSON parser
                        - Output MUST start with `{` and end with `}`
                        - Output MUST contain only this object, with no prefix/suffix text
                        - Output MUST NOT be an array
                        - Output MUST NOT contain trailing commas
                        - Output MUST use double quotes for all keys and string values
                        - Output MUST follow exactly this shape:
                            {
                                "sourceLang": "<string>",
                                "targetLang": "<string>",
                                "texts": ["<string>", "..."]
                            }

            ## EXAMPLE
            Input:
                {
                    "sourceLang": "en",
                    "targetLang": "fr",
                    "texts": [
                        "<p>Hello world</p>",
                        "Welcome <strong>user</strong>",
                        "Forgot password?"
                    ]
                }

            Output:
                {
                    "sourceLang": "en",
                    "targetLang": "fr",
                    "texts": [
                        "<p>Bonjour le monde</p>",
                        "Bienvenue <strong>utilisateur</strong>",
                        "Mot de passe oublié ?"
                    ]
                }
            """;
    }

    private TranslationResult parseJSON(String json, long totalTokens) {
        try {
            TranslationResult parsed = MAPPER.readValue(json, TranslationResult.class);
            return new TranslationResult(
                parsed.sourceLang(),
                parsed.targetLang(),
                parsed.texts(),
                totalTokens
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON from OpenAI", e);
        }
    }

    private String extractOutputText(Response response) {
        String text = response.output().stream()
            .filter(ResponseOutputItem::isMessage)
            .map(ResponseOutputItem::asMessage)
            .flatMap(message -> message.content().stream())
            .filter(content -> content.isOutputText())
            .map(content -> content.asOutputText().text())
            .filter(Objects::nonNull)
            .collect(java.util.stream.Collectors.joining("\n"));

        if (text.isBlank()) {
            throw new RuntimeException("No output_text found in OpenAI response output items");
        }

        return text;
    }

    private long extractTotalTokens(Response response) {
        long totalTokens = response.usage().stream()
            .map(usage -> usage.totalTokens())
            .filter(Objects::nonNull)
            .map(Number::longValue)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No usage.total_tokens found in OpenAI response"));

        if (totalTokens <= 0) {
            throw new RuntimeException("OpenAI usage.total_tokens must be > 0");
        }

        return totalTokens;
    }

    private List<Translation> toEntities(List<TranslationResult> translations, CreateTranslationRequest request, UUID apiKey) {
        List<Translation> entities = new ArrayList<>();

        for (TranslationResult translation : translations) {
            UUID uuid = UUID.randomUUID();
            List<String> translatedTexts = translation.texts();

            for (int index = 0; index < translatedTexts.size(); index++) {
                Translation t = new Translation();
                t.translationId  = uuid;
                t.sourceLang     = translation.sourceLang().getCode();
                t.targetLang     = translation.targetLang().getCode();
                t.sourceText     = request.texts().get(index);
                t.translatedText = translatedTexts.get(index);
                t.tokensUsed     = translation.totalTokens();
                t.index          = index;
                t.apiKeyId       = apiKey;
                entities.add(t);
            }
        }

        return entities;
    }
}