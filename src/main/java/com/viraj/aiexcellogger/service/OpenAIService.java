package com.viraj.aiexcellogger.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIService {

    @Value("${nvidia.nim.api.key}")
    private String apiKey;

    @Value("${nvidia.nim.url}")
    private String url;

    @Value("${nvidia.nim.model}")
    private String model;

    private final ObjectMapper mapper = new ObjectMapper();

    public String getStructuredJson(String input) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String prompt =
                    "You are a work log assistant. Convert the given work summary into STRICT VALID JSON.\n\n" +
                            "RULES:\n" +
                            "- Return ONLY valid JSON, no explanation, no markdown, no backticks\n" +
                            "- 'tasks' field: use the bullet character • (Unicode U+2022) for each task, separated by \\n. " +
                            "IMPORTANT: use only • character, not *, -, or ?\n" +
                            "- 'description' field: for EACH task write 2-3 sentences explaining WHAT was done, " +
                            "HOW it was done, and WHY. Number each task like 1) 2) 3). " +
                            "DO NOT use placeholder text.\n" +
                            "- All string values must be in double quotes\n\n" +
                            "Return this exact format:\n" +
                            "{\n" +
                            "  \"date\": \"\",\n" +
                            "  \"projectName\": \"guess from context or General Work\",\n" +
                            "  \"tasks\": \"• task1\\n• task2\",\n" +
                            "  \"description\": \"1) Explanation.\\n\\n2) Explanation.\",\n" +
                            "  \"hours\": 8,\n" +
                            "  \"nextAction\": \"what to do next\",\n" +
                            "  \"inProgress\": true,\n" +
                            "  \"dueDate\": \"\",\n" +
                            "  \"reviewedBy\": \"\",\n" +
                            "  \"remark\": \"\"\n" +
                            "}\n\n" +
                            "Input: " + input;

            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("max_tokens", 1024);
            requestBody.put("temperature", 0.3);

            ArrayNode messages = mapper.createArrayNode();
            ObjectNode message = mapper.createObjectNode();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.set("messages", messages);

            String body = mapper.writeValueAsString(requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            System.out.println("NVIDIA RAW RESPONSE: " + response.getBody());

            return response.getBody();

        } catch (Exception e) {
            System.err.println("NVIDIA API ERROR: " + e.getMessage());
            throw new RuntimeException("NVIDIA API call failed: " + e.getMessage());
        }
    }
}