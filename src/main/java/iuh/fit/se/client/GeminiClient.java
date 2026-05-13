package iuh.fit.se.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Component
public class GeminiClient {

    @Value("${google.ai.api-key}")
    private String apiKey;

    @Value("${google.ai.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String layLoiKhuyen(String prompt) {
        try {
            // Chuẩn bị Header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Cấu hình Body theo format của Google Gemini
            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> parts = Map.of("parts", List.of(textPart));
            Map<String, Object> contents = Map.of("contents", List.of(parts));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contents, headers);

            // Gọi API
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl + "?key=" + apiKey, entity, Map.class);

            // Bóc tách JSON trả về: candidates[0].content.parts[0].text
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List candidates = (List) response.getBody().get("candidates");
                Map firstCandidate = (Map) candidates.get(0);
                Map content = (Map) firstCandidate.get("content");
                List resParts = (List) content.get("parts");
                Map firstPart = (Map) resParts.get(0);
                return (String) firstPart.get("text");
            }
        } catch (Exception e) {
            return "Lỗi khi gọi AI: " + e.getMessage();
        }
        return "Không nhận được phản hồi từ AI.";
    }
}