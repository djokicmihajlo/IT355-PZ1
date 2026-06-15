package com.it355pz.freelance.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.it355pz.freelance.model.FreelanceJob;
import com.it355pz.freelance.model.Skill;
import com.it355pz.freelance.model.User;
import com.it355pz.freelance.service.FreelanceJobService;
import com.it355pz.freelance.service.ProposalDraftService;
import com.it355pz.freelance.service.UserService;
import com.it355pz.freelance.service.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GeminiProposalDraftService implements ProposalDraftService {

    private static final String API_KEY_NAME = "GEMINI_API_KEY";
    private static final String MODEL_NAME = "GEMINI_MODEL";
    private static final int MAX_OUTPUT_TOKENS = 450;

    private final FreelanceJobService freelanceJobService;
    private final UserService userService;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String configuredApiKey;
    private final String configuredModel;
    private final String baseUrl;
    private final Path envFile;

    public GeminiProposalDraftService(FreelanceJobService freelanceJobService, UserService userService,
                                      RestClient.Builder restClientBuilder, ObjectMapper objectMapper,
                                      @Value("${app.gemini.api-key:}") String configuredApiKey,
                                      @Value("${app.gemini.model:gemini-3.5-flash}") String configuredModel,
                                      @Value("${app.gemini.base-url:https://generativelanguage.googleapis.com/v1beta}") String baseUrl,
                                      @Value("${app.gemini.env-file:.env}") String envFile) {
        this.freelanceJobService = freelanceJobService;
        this.userService = userService;
        this.restClient = restClientBuilder.build();
        this.objectMapper = objectMapper;
        this.configuredApiKey = configuredApiKey;
        this.configuredModel = configuredModel;
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.envFile = Path.of(envFile);
    }

    @Override
    public String generateDraft(Long jobId, Long freelancerId) {
        if (freelancerId == null) {
            throw new ValidationException("Izaberi freelancera pre generisanja drafta.");
        }

        FreelanceJob job = freelanceJobService.getById(jobId);
        User freelancer = userService.getById(freelancerId);
        if (!freelancer.isFreelancer()) {
            throw new ValidationException("Draft moze da se generise samo za freelancer korisnika.");
        }

        String apiKey = resolveValue(API_KEY_NAME, configuredApiKey)
                .filter(this::isRealApiKey)
                .orElseThrow(() -> new ValidationException(
                        "Gemini API kljuc nije podesen. Dodaj GEMINI_API_KEY u .env ili environment."));
        String model = resolveValue(MODEL_NAME, configuredModel)
                .filter(value -> !value.isBlank())
                .orElse("gemini-3.5-flash");

        String responseBody = callGemini(apiKey, model, buildPrompt(job, freelancer));
        return extractText(responseBody);
    }

    private String callGemini(String apiKey, String model, String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                        "temperature", 0.65,
                        "maxOutputTokens", MAX_OUTPUT_TOKENS
                )
        );

        try {
            return restClient.post()
                    .uri(baseUrl + "/models/" + model + ":generateContent")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-goog-api-key", apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException ex) {
            throw new ValidationException("Gemini draft trenutno nije dostupan. Pokusaj ponovo kasnije.");
        }
    }

    private String extractText(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText("");

            if (text.isBlank()) {
                throw new ValidationException("Gemini nije vratio draft tekst.");
            }

            return text.trim();
        } catch (JsonProcessingException ex) {
            throw new ValidationException("Gemini odgovor nije u ocekivanom formatu.");
        }
    }

    private String buildPrompt(FreelanceJob job, User freelancer) {
        String skills = job.getRequiredSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.joining(", "));

        return """
                Napisi profesionalan proposal tekst na srpskom latinicom za freelance platformu.
                Stil treba da bude konkretan, kratak i u prvom licu.
                Ne izmisljaj iskustvo koje nije dato. Ne koristi markdown.
                Maksimalna duzina je 150 reci.

                Posao:
                Naziv: %s
                Opis: %s
                Kategorija: %s
                Budzet: %s
                Potrebne vestine: %s

                Freelancer:
                Ime: %s
                Profil: %s
                """.formatted(
                job.getTitle(),
                job.getDescription(),
                job.getCategory().getName(),
                job.getBudget(),
                skills,
                freelancer.getFullName(),
                freelancer.getProfileSummary()
        );
    }

    private Optional<String> resolveValue(String name, String configuredValue) {
        String environmentValue = System.getenv(name);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return Optional.of(environmentValue.trim());
        }

        Optional<String> envFileValue = readEnvValue(name);
        if (envFileValue.isPresent()) {
            return envFileValue;
        }

        if (configuredValue != null && !configuredValue.isBlank()) {
            return Optional.of(configuredValue.trim());
        }

        return Optional.empty();
    }

    private Optional<String> readEnvValue(String name) {
        if (!Files.exists(envFile)) {
            return Optional.empty();
        }

        try {
            return Files.readAllLines(envFile).stream()
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("#"))
                    .filter(line -> line.startsWith(name + "="))
                    .map(line -> line.substring((name + "=").length()).trim())
                    .findFirst();
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    private boolean isRealApiKey(String apiKey) {
        return !apiKey.isBlank() && !apiKey.contains("YOUR_GEMINI_API_KEY");
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "https://generativelanguage.googleapis.com/v1beta";
        }

        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
