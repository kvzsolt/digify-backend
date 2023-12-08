package hu.progmasters.blog.controller;

import com.itextpdf.text.DocumentException;
import hu.progmasters.blog.config.PdfUtilsConfig;
import hu.progmasters.blog.dto.google.AsyncRecognizeRes;
import hu.progmasters.blog.dto.post.*;
import hu.progmasters.blog.service.EmailService;
import hu.progmasters.blog.service.ImageUploadService;
import hu.progmasters.blog.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@Slf4j
public class PostController {

    private PostService postService;
    private EmailService emailService;
    private ImageUploadService imageUploadService;


    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @PostMapping
    public ResponseEntity createPost(@Valid @RequestBody CreatePostReq createPostReq) throws DocumentException, IOException {
        postService.createPost(createPostReq);
        log.info("Http request, POST /api/posts/ Post created");
//        emailService.sendEmail("blogprogmasters@gmail.com", "Post created", "Http request, POST /api/posts/ Post created");
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @PostMapping("/scheduled/{hour},{minute}")
    public ResponseEntity createPostScheduled(@Valid @RequestBody CreatePostReq createPostReq, @PathVariable("hour") int hour, @PathVariable("minute") int minute) {
        postService.createPostScheduled(createPostReq, hour, minute);
        log.info("Http request, POST /api/posts/scheduled/{hour},{minute} " + hour + ":" + minute + " Scheduled post scheduled");
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @customPermissionEvaluator.isOwnPost(authentication, #id))")
    @PutMapping("/scheduled/{postId}")
    public ResponseEntity editScheduledPost(@PathVariable("postId") Long id,
                                            @Valid @RequestBody CreatePostReq createPostReq) {
        postService.editingScheduledPost(id, createPostReq);
        log.info("Http request, PUT /api/posts/scheduled/{postId}" + id + " Scheduled post edited");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @customPermissionEvaluator.isOwnPost(authentication, #id))")
    @PutMapping("/scheduled/deleted/{postId}")
    public ResponseEntity deleteSheduledPost(@PathVariable("postId") Long id) {
        postService.deleteScheduledPost(id);
        log.info("Http request, PUT /api/posts/scheduled/deleted/{postId}" + id + " Scheduled post deleted");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping
    public ResponseEntity<List<ListPostsRes>> getPostList() {
        log.info("Http request, GET /api/posts/  All post listed");
        return new ResponseEntity<>(postService.getPostListItems(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/{id}")
    public ResponseEntity<GetPostRes> getPost(@PathVariable("id") Long id) {
        GetPostRes getPostRes = postService.getPostDetailsById(id);
        log.info("Http request, GET /api/posts/{postId}" + id + " Post find by id");
        return new ResponseEntity<>(getPostRes, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @customPermissionEvaluator.isOwnPost(authentication, #id))")
    @PutMapping("/delete/{postId}")
    public ResponseEntity deletePost(@PathVariable("postId") Long id) {
        postService.deletePost(id);
        log.info("Http request, PUT /api/posts/delete/{postId}" + id + " Post deleted");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/restore/{postId}")
    public ResponseEntity restorationPost(@PathVariable("postId") Long id) {
        postService.restorationPost(id);
        log.info("Http request, PUT /api/posts/restore/{postId}" + id + " Deleted post restored");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_AUTHOR') and @customPermissionEvaluator.isOwnPost(authentication, #id))")
    @PutMapping("/edit/{postId}")
    public ResponseEntity<GetPostRes> editPost(@PathVariable("postId") Long id,
                                               @Valid @RequestBody CreatePostReq createPostReq) {
        GetPostRes edited = postService.editPost(id, createPostReq);
        log.info("Http request, PUT /api/posts/edit/{postId} body: " + createPostReq.toString() + " post edited id: " + id);
        return new ResponseEntity<>(edited, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER') and @customUserDetailsService.isUserPremium(authentication.name) or hasRole('ROLE_AUTHOR')")
    @GetMapping("/pdf/{postId}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable("postId") Long id) throws IOException, DocumentException {
        List<Map<String, Object>> queryResults = postService.executeQuery(id);
        ByteArrayOutputStream pdfStream = PdfUtilsConfig.generatePdfStream(queryResults);
        log.info("Http request, GET /api/posts/pdf/{postId}, pdf created to post id: " + id);
        return new ResponseEntity<>(pdfStream.toByteArray(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_USER') and @customUserDetailsService.isUserPremium(authentication.name) or hasRole('ROLE_AUTHOR')")
    @GetMapping("{postId}/translate")
    public ResponseEntity<PostTranslatedRes> translatePost(@PathVariable("postId") Long id, @RequestParam String target) throws IOException {
        PostTranslatedRes response = postService.translatePost(id, target);
        log.info("Http request, GET /api/posts/{postId}/translate?target={target}, Post translated");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("{postId}/synthesize")
    public ResponseEntity<byte[]> synthesizeSpeech(@PathVariable("postId") Long id) throws IOException {
        byte[] audioContent = postService.synthesizePost(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        log.info("Http request, POST /api/posts/{postId}/synthesize, Post synthesize");
        return new ResponseEntity<>(audioContent, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/search")
    public ResponseEntity<List<ListPostsRes>> searchByTitle(@RequestParam String title) {
        List<ListPostsRes> posts = postService.searchPostsByTitle(title);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @PutMapping("/uploadImages/{postId}")
    public ResponseEntity<String> uploadImages(@PathVariable("postId") Long id,
                                               @ModelAttribute @Valid PostImage postImage) throws IOException {
        log.info("Http request, PUT /api/posts/uploadImages/{postId}, image(s) uploaded to post id: " + id);
        if (!imageUploadService.uploadImage(postImage)) {
            emailService.sendEmail("blogprogmasters@gmail.com", "Potential Explicit Content Upload, POST ID: " + id,
                    "An attempted upload of explicit content was blocked by the system. Requesting admin review to ensure the uploaded  post (id: " + id + ") itself doesn't contain any explicit material!");
            return new ResponseEntity<>("One of the selected images contains explicit content and cannot be uploaded.", HttpStatus.BAD_REQUEST);
        } else {
            String result = postService.uploadImage(id, postImage);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/like/{postId}")
    public ResponseEntity like(@PathVariable("postId") Long id) {
        postService.likePost(id);
        log.info("Http request, PUT /api/posts/like/{postId}" + id + " Post liked");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @PostMapping("/audio/upload")
    public ResponseEntity<AsyncRecognizeRes> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        AsyncRecognizeRes res = new AsyncRecognizeRes(postService.processAudioFile(file));
        log.info("Http request, POST /api/posts/audio/upload");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @PostMapping("audio/stream")
    public ResponseEntity<List<ListPostsRes>> searchByVoice(@RequestParam MultipartFile file) throws Exception {
        List<ListPostsRes> posts = postService.searchPostsByVoice(file);
        log.info("Http request, POST /api/posts/audio/stream");
        return new ResponseEntity<>(posts, HttpStatus.CREATED);
    }


}

