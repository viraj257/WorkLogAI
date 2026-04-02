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

            // ✅ NVIDIA NIM response format
            JsonNode choices = root.get("choices");
            if (choices == null) {
                throw new RuntimeException("Invalid NVIDIA response: " + rawResponse);
            }
            String content = choices.get(0).get("message").get("content").asText();

            String json = extractJson(content);
            json = cleanJson(json);

            WorkLog log = mapper.readValue(json, WorkLog.class);

            log.setTaskSummary(ensureBullets(log.getTaskSummary()));
            log.setDescription(ensureBullets(log.getDescription()));
            log.setDate(java.time.LocalDate.now().toString());

            if (log.getHours() == 0) log.setHours(8);
            if (log.getProjectName() == null || log.getProjectName().isEmpty())
                log.setProjectName("General Work");
            if (log.getNextAction() == null || log.getNextAction().isEmpty())
                log.setNextAction("Continue work");

            excelService.writeToExcel(log);
            return "Saved to Excel ✅";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        throw new RuntimeException("Invalid JSON from AI: " + text);
    }

    private String cleanJson(String json) {
        return json.replaceAll("(?<=:)\\s*•", "\"•")
                .replaceAll("(?<=\\n)•", "•")
                .replaceAll("•\\s*", "• ")
                .replaceAll("\"\\s*,", "\",");
    }

    private String ensureBullets(String text) {
        if (text == null || text.trim().isEmpty()) return text;

        //  Replace any bullet-like characters with proper •
        text = text.replaceAll("[\\?\\*\\-–•]\\s*", "• ");

        // Already properly formatted
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
