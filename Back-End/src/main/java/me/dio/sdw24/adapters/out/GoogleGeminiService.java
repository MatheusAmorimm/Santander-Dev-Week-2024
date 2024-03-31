package me.dio.sdw24.adapters.out;

import feign.FeignException;
import feign.RequestInterceptor;
import me.dio.sdw24.domain.ports.GenerativeAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@ConditionalOnProperty(name = "generative-ai.provider", havingValue = "GEMINI", matchIfMissing = true)
@FeignClient(name = "geminiApi", url = "${gemini.base-url}", configuration = GoogleGeminiService.Config.class)
public interface GoogleGeminiService extends GenerativeAiApi {

    @PostMapping("v1beta/models/gemini-pro:generateContent")
    GoogleGeminiResponse textOnlyInput(GoogleGeminiRequest request);

    @Override
    default String generateContent(String objective, String context){

        String prompt = """
                %s
                %s
                """.formatted(objective, context);

        GoogleGeminiRequest request = new GoogleGeminiRequest(
                List.of(new Content(List.of(new Part(prompt))))
        );

        try {
            GoogleGeminiResponse response = textOnlyInput(request);

            return response.candidates().getFirst().content().parts().getFirst().text();
        } catch (FeignException httErrors){
            return "Sorry buddy, an error occurred in the Google API communication";
        } catch (Exception unexpectedError){
            return "Ops, the return from the google gemini api does not contain the expected data";
        }
    }

    record GoogleGeminiRequest(List<Content> contents) {}
    record Content( List<Part> parts ) { }
    record Part(String text){}
    record GoogleGeminiResponse(List<Candidate> candidates) {}
    record Candidate(Content content){}


    class Config {
        @Bean
        public RequestInterceptor apikeyRequestInterceptor(@Value("${gemini.api-key}") String apiKey){
            return requestTemplate -> requestTemplate.query("key", apiKey);
        }
    }
}
