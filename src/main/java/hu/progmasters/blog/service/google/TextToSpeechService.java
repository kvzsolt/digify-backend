package hu.progmasters.blog.service.google;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import hu.progmasters.blog.dto.google.TextToSpeechReq;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TextToSpeechService {

    private final GoogleAuthService googleAuthService;

    public TextToSpeechService(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    public byte[] synthesizeText(TextToSpeechReq req) throws IOException  {
        GoogleCredentials credentials = googleAuthService.authGoogle();


        TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create(settings)) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(req.getInput().getText()).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(req.getVoice().getLanguageCode())
                    .setName(req.getVoice().getName())
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.valueOf(req.getAudioConfig().getAudioEncoding().toUpperCase()))
                    .setSpeakingRate(req.getAudioConfig().getSpeakingRate())
                    .setPitch(req.getAudioConfig().getPitch())
                    .setVolumeGainDb(req.getAudioConfig().getVolumeGainDb())
                    .setSampleRateHertz(req.getAudioConfig().getSampleRateHertz())
                    .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
            ByteString audioContents = response.getAudioContent();
            return audioContents.toByteArray();
        }
    }
}