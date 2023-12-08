package hu.progmasters.blog.service.google;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.longrunning.OperationTimedPollAlgorithm;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.gax.retrying.TimedRetryAlgorithm;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.threeten.bp.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
@Slf4j
public class SpeechRecognitionService {

    private final GoogleAuthService googleAuthService;

    public SpeechRecognitionService(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;

    }

    /**
     * Performs microphone streaming speech recognition with a duration of 1 minute.
     */
    public String streamingMicRecognize(InputStream audioInputStream) throws Exception {
        StringBuffer transcription = new StringBuffer();
        CountDownLatch finishLatch = new CountDownLatch(1);
        GoogleCredentials credentials = googleAuthService.authGoogle();

        SpeechSettings speechSettings = SpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        try (SpeechClient client = SpeechClient.create(speechSettings)) {

            ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {
                ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                public void onStart(StreamController controller) {
                }

                public void onResponse(StreamingRecognizeResponse response) {
                    responses.add(response);
                }

                public void onComplete() {
                    for (StreamingRecognizeResponse response : responses) {
                        StreamingRecognitionResult result = response.getResultsList().get(0);
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                        transcription.append(alternative.getTranscript());
                    }
                    finishLatch.countDown();
                }

                public void onError(Throwable t) {
                    log.error(t.getMessage());

                }
            };

            ClientStream<StreamingRecognizeRequest> clientStream =
                    client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .setEnableAutomaticPunctuation(true)
                    .build();

            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build();

            clientStream.send(request);

            byte[] data = new byte[6400];
            int bytesRead;
            while ((bytesRead = audioInputStream.read(data)) != -1) {
                request = StreamingRecognizeRequest.newBuilder()
                        .setAudioContent(ByteString.copyFrom(data, 0, bytesRead))
                        .build();
                clientStream.send(request);
            }

            clientStream.closeSend();

            try {
                finishLatch.await();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
            return transcription.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return transcription.toString();
    }


    /**
         * Performs non-blocking speech recognition on remote MP3 file and returns the transcription.
         *
         * @param gcsUri the path to the remote MP3 audio file to transcribe.
         * @return The transcription of the audio file.
         */
    public String asyncRecognizeGcsMp3(String gcsUri) throws Exception {
        SpeechSettings.Builder speechSettings = SpeechSettings.newBuilder();
        TimedRetryAlgorithm timedRetryAlgorithm =
                OperationTimedPollAlgorithm.create(
                        RetrySettings.newBuilder()
                                .setInitialRetryDelay(Duration.ofMillis(500L))
                                .setRetryDelayMultiplier(1.5)
                                .setMaxRetryDelay(Duration.ofMillis(5000L))
                                .setInitialRpcTimeout(Duration.ZERO)
                                .setRpcTimeoutMultiplier(1.0)
                                .setMaxRpcTimeout(Duration.ZERO)
                                .setTotalTimeout(Duration.ofHours(24L))
                                .build());
        speechSettings.longRunningRecognizeOperationSettings().setPollingAlgorithm(timedRetryAlgorithm);

        StringBuilder transcription = new StringBuilder();

        GoogleCredentials credentials = googleAuthService.authGoogle();

        speechSettings.setCredentialsProvider(FixedCredentialsProvider.create(credentials));
        try (SpeechClient speech = SpeechClient.create(speechSettings.build())) {


            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.MP3)
                            .setLanguageCode("en-US")
                            .setSampleRateHertz(16000)
                            .setEnableAutomaticPunctuation(true)
                            .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speech.longRunningRecognizeAsync(config, audio);
            while (!response.isDone()) {
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();
            results.forEach(result ->
                    transcription.append(result.getAlternativesList().get(0).getTranscript()).append("\n"));
        }
        return transcription.toString();
    }

}
