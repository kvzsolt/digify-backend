package hu.progmasters.blog.dto.google;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateRes {
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private List<Translation> translations;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Translation {
        private String translatedText;
        private String detectedSourceLanguage;
    }
}
