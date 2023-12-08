package hu.progmasters.blog.dto.google;

import lombok.Data;

import java.util.List;

@Data
public class TextToSpeechReq {

    private SynthesisInput input;
    private VoiceSelectionParams voice;
    private AudioConfig audioConfig;

    @Data
    public static class SynthesisInput {
        private String text;
    }

    @Data
    public static class VoiceSelectionParams {
        private String languageCode;
        private String name;
    }

    @Data
    public static class AudioConfig {
        private String audioEncoding;
        private List<String> effectsProfileId;
        private Double pitch;
        private Double speakingRate;
        private Double volumeGainDb;
        private Integer sampleRateHertz;
    }
}

