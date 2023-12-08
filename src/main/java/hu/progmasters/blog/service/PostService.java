package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.domain.Post;
import hu.progmasters.blog.dto.google.TextToSpeechReq;
import hu.progmasters.blog.dto.google.TranslateReq;
import hu.progmasters.blog.dto.post.*;
import hu.progmasters.blog.domain.PostCategory;
import hu.progmasters.blog.domain.PostTag;
import hu.progmasters.blog.exception.DateEarlierThanTheCurrentOneException;
import hu.progmasters.blog.exception.NotFoundPostException;
import hu.progmasters.blog.repository.AccountRepository;
import hu.progmasters.blog.repository.PostRepository;
import hu.progmasters.blog.service.google.CloudStorageService;
import hu.progmasters.blog.service.google.SpeechRecognitionService;
import hu.progmasters.blog.service.google.TextToSpeechService;
import hu.progmasters.blog.service.google.TranslateApiService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PostService {
    private final AccountRepository accountRepository;

    private PostRepository postRepository;
    private CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;
    private final TranslateApiService translateApiService;
    private final TextToSpeechService textToSpeechService;
    private final CloudStorageService cloudStorageService;
    private final SpeechRecognitionService speechRecognitionService;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Post createPost(CreatePostReq createPostReq) {
        CreatePostReq moderatedPost = contentModerator(createPostReq);
        Post newPost = modelMapper.map(moderatedPost, Post.class);

        PostCategory categoryForThePost = categoryService.getCategoryByName(createPostReq.getCategory());
        newPost.setCategory(categoryForThePost);
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setAccount(findAccountById(createPostReq.getAccountId()));

        postRepository.save(newPost);
        return newPost;
    }

    public String uploadImage(Long id, PostImage postImage) {
        List<String> urls = cloudinaryService.uploadImages(postImage.getImages());
        Post postToUploadImages = findPostById(id);
        postToUploadImages.setImageUrls(urls);
        return urls.toString();
    }

    public void createPostScheduled(CreatePostReq createPostReq, int hour, int minute) {
        LocalTime desiredTime = LocalTime.of(hour, minute);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledTime = LocalDateTime.of(now.toLocalDate(), desiredTime);

        if (now.isAfter(scheduledTime)) {
            throw new DateEarlierThanTheCurrentOneException();
        }

        Post newPost = createPost(createPostReq);
        newPost.setScheduled(true);
        newPost.setDeleted(true);
        postRepository.save(newPost);

        long delay = ChronoUnit.SECONDS.between(now, scheduledTime);

        Runnable task = () -> {
            Post sheduledPost = findPostById(newPost.getId());
            if (sheduledPost.isScheduled()) {
                sheduledPost.setCreatedAt(LocalDateTime.now());
                sheduledPost.setDeleted(false);
                postRepository.save(sheduledPost);
                log.info("Scheduled post created ", LocalDateTime.now());
            } else {
                postRepository.delete(sheduledPost);
            }
        };
        scheduler.schedule(task, delay, TimeUnit.SECONDS);
    }

    public void editingScheduledPost(Long id, CreatePostReq createPostReq) {
        Post editedPost = findPostById(id);
        modelMapper.map(createPostReq, editedPost);
        postRepository.save(editedPost);
    }

    public void deleteScheduledPost(Long id) {
        Post deletedPost = findPostById(id);
        deletedPost.setDeleted(true);
        deletedPost.setScheduled(false);
    }

    public List<ListPostsRes> getPostListItems() {
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream()
                .filter(p -> !p.isDeleted())
                .map(this::postToPostListItem)
                .collect(Collectors.toList());
    }

    private ListPostsRes postToPostListItem(Post post) {
        ListPostsRes listPostsRes = modelMapper.map(post, ListPostsRes.class);
        listPostsRes.setPostBodyShortened(shorter(post.getPostBody()));
        listPostsRes.setCategoryName(post.getCategory().getCategoryName());
        for (PostTag postTag : post.getPostTags()) {
            listPostsRes.getTagsName().add(postTag.getTagsName());
        }
        listPostsRes.setNumberOfComments(post.getComments().size());
        return listPostsRes;
    }

    private String shorter(String postBody) {
        return Stream.of(postBody)
                .map(string -> string.substring(0, Math.min(200, string.length())))
                .map(string -> string.substring(0, string.contains(" ") && postBody.length() > 205 ? string.lastIndexOf(" ") : string.length()))
                .map(string -> string.equals(postBody) ? string : string.concat("..."))
                .collect(Collectors.joining());
    }

    public List<Map<String, Object>> executeQuery(Long id) {
        Post post = findPostById(id);
        List<Map<String, Object>> queryResults = new ArrayList<>();
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("postBody", post.getPostBody());
        commentData.put("title", post.getTitle());
        queryResults.add(commentData);
        return queryResults;
    }

    public List<Map<String, Object>> execute(String title) {
        List<Map<String, Object>> queryResults = new ArrayList<>();
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("Vásárlási bizonylat", title);
        commentData.put("title", "Köszönjük, hogy köszi :)");
        queryResults.add(commentData);
        return queryResults;
    }

    public GetPostRes getPostDetailsById(Long id) {
        Post post = findPostById(id);
        PostCategory category = post.getCategory();
        GetPostRes getPostRes = modelMapper.map(post, GetPostRes.class);
        getPostRes.setCategoryName(category.getCategoryName());
        return getPostRes;
    }

    public Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(NotFoundPostException::new);
    }

    public void deletePost(Long id) {
        Post deletedPost = findPostById(id);
        deletedPost.setDeleted(true);
    }

    public void restorationPost(Long id) {
        Post restorationPost = findPostById(id);
        restorationPost.setDeleted(false);
    }

    public GetPostRes editPost(Long id, CreatePostReq createPostReq) {
        Post editPost = findPostById(id);
        modelMapper.map(createPostReq, editPost);
        return modelMapper.map(editPost, GetPostRes.class);
    }

    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(IllegalArgumentException::new);
    }

    public List<ListPostsRes> searchPostsByTitle(String title) {
        return postRepository.searchPostByTitleContainsIgnoreCaseOrderByIdDesc(title)
                .stream()
                .map(post -> {
                    ListPostsRes listPostsRes = modelMapper.map(post, ListPostsRes.class);
                    listPostsRes.setPostBodyShortened(shorter(post.getPostBody()));
                    listPostsRes.setCategoryName(post.getCategory().getCategoryName());
                    return listPostsRes;
                })
                .collect(Collectors.toList());
    }


    private CreatePostReq contentModerator(CreatePostReq createPostReq) {
        String title = createPostReq.getTitle();
        String body = createPostReq.getPostBody();

        createPostReq.setPostBody(stringReplacer(body));
        createPostReq.setTitle(stringReplacer(title));

        return createPostReq;
    }

    private String stringReplacer(String input) {
        List<String> prohibitedWords = Arrays.asList("fuck", "shit", "badword");
        List<String> replacementWords = Arrays.asList("duck", "shoot", "goodword");

        List<Pattern> patterns = prohibitedWords.stream()
                .map(word -> Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());

        for (int i = 0; i < prohibitedWords.size(); i++) {
            input = patterns.get(i).matcher(input).replaceAll(replacementWords.get(i));
        }

        return input;
    }

    public void likePost(Long id) {
        Post postToLike = findPostById(id);
        postToLike.setLikes(postToLike.getLikes() + 1);
    }

    public PostTranslatedRes translatePost(Long postId, String targetLanguage) throws IOException {
        Post post = findPostById(postId);
        TranslateReq postTitleToTranslate = new TranslateReq(post.getTitle(), targetLanguage);
        TranslateReq postBodyToTranslate = new TranslateReq(post.getPostBody(), targetLanguage);
        String translatedTitle = translateApiService.translate(postTitleToTranslate).getData().getTranslations().get(0).getTranslatedText();
        String translatedBody = translateApiService.translate(postBodyToTranslate).getData().getTranslations().get(0).getTranslatedText();
        return new PostTranslatedRes(translatedTitle, translatedBody);
    }

    public byte[] synthesizePost(Long postId) throws IOException {
        Post post = findPostById(postId);
        String contentToTranslate = post.getTitle().concat(" ").concat(post.getPostBody());

        TextToSpeechReq req = new TextToSpeechReq();
        TextToSpeechReq.SynthesisInput input = new TextToSpeechReq.SynthesisInput();
        input.setText(contentToTranslate);
        req.setInput(input);

        TextToSpeechReq.VoiceSelectionParams voice = new TextToSpeechReq.VoiceSelectionParams();
        voice.setLanguageCode("en-US");
        voice.setName("en-US-Wavenet-D");
        req.setVoice(voice);

        TextToSpeechReq.AudioConfig audioConfig = new TextToSpeechReq.AudioConfig();
        audioConfig.setAudioEncoding("MP3");
        audioConfig.setEffectsProfileId(List.of(new String[]{"handset-class-device"}));
        audioConfig.setPitch(0.0);
        audioConfig.setSpeakingRate(1.0);
        audioConfig.setVolumeGainDb(0.0);
        audioConfig.setSampleRateHertz(24000);
        req.setAudioConfig(audioConfig);

        return textToSpeechService.synthesizeText(req);
    }
    public String processAudioFile(MultipartFile file) throws Exception {
        String destinationBlobName = file.getOriginalFilename();
        String gcsUri = cloudStorageService.uploadFile(file.getInputStream(), destinationBlobName);
        return speechRecognitionService.asyncRecognizeGcsMp3(gcsUri);
    }
    public List<ListPostsRes> searchPostsByVoice(MultipartFile file) throws Exception {
        String title = speechRecognitionService.streamingMicRecognize(file.getInputStream());
        return searchPostsByTitle(title);
    }



}
