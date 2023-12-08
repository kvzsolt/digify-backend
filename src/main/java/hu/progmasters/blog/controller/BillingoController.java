package hu.progmasters.blog.controller;

import hu.progmasters.blog.dto.account.DocumentInsertReq;
import hu.progmasters.blog.service.BillingoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/billingo")
@Slf4j
@AllArgsConstructor
public class BillingoController {

    private final BillingoService billingoService;

    @PostMapping("/create-document")
    public ResponseEntity<String> createDocument(@RequestBody DocumentInsertReq requestDto) {
        log.info("Http request, POST /api/public/billingo Billingo receipt created");
        return new ResponseEntity<>(billingoService.createDocument(requestDto), HttpStatus.OK);
    }
}
