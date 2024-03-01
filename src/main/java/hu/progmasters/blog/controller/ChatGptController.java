package hu.progmasters.blog.controller;

import hu.progmasters.blog.service.ChatGptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static hu.progmasters.blog.controller.constants.Endpoints.GPT_MAPPING;

@RestController
@RequestMapping(GPT_MAPPING)
@PreAuthorize("hasRole('ROLE_USER') and @customUserDetailsService.isUserPremium(authentication.name) or hasRole('ROLE_AUTHOR')")
@AllArgsConstructor
@Slf4j
public class ChatGptController {

    private final ChatGptService chatGptService;
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody String message) {
        log.info("Http request, POST /api/gpt/chat ChatGpt answer");
        return new ResponseEntity<>(chatGptService.responseContent(message), HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody String prompt) {
        log.info("Http request, POST /api/gpt/generate ChatGpt generate picture");
        return new ResponseEntity<>(chatGptService.generatePicture(prompt),HttpStatus.OK);
    }

    @PostMapping("/generate/{postId}")
    public ResponseEntity<String> generateByTitle(@PathVariable("postId") Long id) {
        log.info("Http request, POST /api/gpt/generate/postId" + id + "ChatGpt generate picture by post title");
        return new ResponseEntity<>(chatGptService.generatePictureByTitle(id),HttpStatus.OK);
    }

    @PostMapping("/moderate")
    public ResponseEntity<String> moderate() {
        log.info("Http request, POST /api/gpt/moderate ChatGpt analize content");
        return new ResponseEntity<>(chatGptService.getModerateResponse(),HttpStatus.OK);
    }

}
