package com.email.Email_Writer.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.*;


import java.util.Map;


@Service
public class EmailGeneratorService {
@Value("${gemni.api.url}")
    private String gemniApiURL;
    @Value("${gemni.api.key}")
    private String gemniApiKey;

    private final WebClient webClient;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest) {
//        build the promt
        String prompt = buildPrompt(emailRequest);
//        craft the request
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

//        do request and get the response
        String response = webClient.post()
                .uri(gemniApiURL + gemniApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();





//        return resposne
        return ExtractResponseContent(response);



    }

    private String ExtractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
           JsonNode root = mapper.readTree(response);
           return root.path("candidates")
                   .get(0)
                   .path("content")
                   .path("parts")
                   .get(0)
                   .path("text").asText();
        }
        catch (Exception e) {
//            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a proffessional email reply for the following email content . Please dont generate a subject line ");
        if((emailRequest.getTone()!=null )&& (!emailRequest.getTone().isEmpty())) {
            prompt.append("use a").append(emailRequest.getTone() ).append(" tone.");

        }
        prompt.append("\nOriginal Email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();


//        return null;

    }
}
