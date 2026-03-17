package utils;

import okhttp3.*;
import org.json.*;

public class AiAnalyzer {

    private static final String API_KEY = System.getenv("GROQ_API_KEY");
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final OkHttpClient client = new OkHttpClient();

    public static String analyzeFailure(String testName, String errorMessage, String stackTrace) {

        String prompt = """
                You are a Selenium test automation expert.
                Analyze this test failure and provide:
                1. Root cause (1-2 sentences)
                2. Likely fix (bullet points)
                3. Prevention tip
                
                Test Name: %s
                Error: %s
                Stack Trace: %s
                """.formatted(testName, errorMessage, stackTrace.substring(0, Math.min(800, stackTrace.length())));

        try {
            String requestBody = new JSONObject()
                    .put("model", "llama-3.3-70b-versatile")
                    .put("messages", new JSONArray()
                            .put(new JSONObject()
                                    .put("role", "user")
                                    .put("content", prompt)))
                    .put("max_tokens", 500)
                    .toString();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String json = response.body().string();
                return new JSONObject(json)
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            }
        } catch (Exception e) {
            return "AI analysis unavailable: " + e.getMessage();
        }
    }

}
