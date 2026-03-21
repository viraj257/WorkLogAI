package com.viraj.aiexcellogger.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viraj.aiexcellogger.model.WorkLog;
import com.viraj.aiexcellogger.service.ExcelService;
import com.viraj.aiexcellogger.service.OpenAIService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WorkLogController {

    private final OpenAIService openAIService;
    private final ExcelService excelService;
    private final ObjectMapper mapper = new ObjectMapper();

    public WorkLogController(OpenAIService openAIService, ExcelService excelService) {
        this.openAIService = openAIService;
        this.excelService = excelService;
    }

    @PostMapping("/log")
    public String logWork(@RequestBody String input) {
        try {

            String rawResponse = openAIService.getStructuredJson(input);

            JsonNode root = mapper.readTree(rawResponse);

            // 🔥 Safe response extraction
            JsonNode responseNode = root.get("response");

            if (responseNode == null) {
                throw new RuntimeException("Invalid Ollama response: " + rawResponse);
            }

            String content = responseNode.asText();

            // Extract JSON
            String json = extractJson(content);

            // Clean JSON
            json = cleanJson(json);

            // Convert to object
            WorkLog log = mapper.readValue(json, WorkLog.class);

            // 🔥 Ensure bullet formatting
            log.setTaskSummary(ensureBullets(log.getTaskSummary()));
            log.setDescription(ensureBullets(log.getDescription()));

            // 🔥 Fix date
            log.setDate(java.time.LocalDate.now().toString());

            // 🔥 Fix hours
            if (log.getHours() == 0) {
                log.setHours(8);
            }

            // 🔥 Fix project name
            if (log.getProjectName() == null || log.getProjectName().isEmpty()) {
                log.setProjectName("General Work");
            }

            // 🔥 Fix next action
            if (log.getNextAction() == null || log.getNextAction().isEmpty()) {
                log.setNextAction("Continue work");
            }

            // Write to Excel
            excelService.writeToExcel(log);

            return "Saved to Excel ✅";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // 🔥 Extract JSON from messy AI response
    private String extractJson(String text) {

        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");

        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }

        throw new RuntimeException("Invalid JSON from AI: " + text);
    }

    // 🔥 Clean invalid JSON (fix bullets etc.)
    private String cleanJson(String json) {
        return json.replaceAll("(?<=:)\\s*•", "\"•")
                .replaceAll("(?<=\\n)•", "•")
                .replaceAll("•\\s*", "• ")
                .replaceAll("\"\\s*,", "\",");
    }

    // 🔥 Ensure bullet points formatting
    private String ensureBullets(String text) {
        if (text == null || text.trim().isEmpty()) return text;

        // Already bullet formatted
        if (text.contains("•")) return text;

        String[] parts = text.split("[,.]");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                result.append("• ").append(part.trim()).append("\n");
            }
        }

        return result.toString().trim();
    }
}