package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.dto.account.DocumentInsertReq;
import hu.progmasters.blog.dto.paypal.CompleteOrder;
import hu.progmasters.blog.dto.billingo.PremiumDocumentInsertReq;
import hu.progmasters.blog.dto.billingo.PremiumReceiptItemInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@Transactional
public class BillingoService {

    @Value("${billingo.api.key}")
    private String apiKey;

    private  WebClient webClient = WebClient.builder()
            .baseUrl("https://api.billingo.hu/v3")
            .build();

    public String createDocument(DocumentInsertReq requestDto) {

        return webClient.post()
                .uri("/documents/receipt")
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void createReceipt(CompleteOrder order, Account account) {

        PremiumReceiptItemInfo receiptDto = new PremiumReceiptItemInfo();
        PremiumDocumentInsertReq requestDto = new PremiumDocumentInsertReq();
        requestDto.getItems().add(receiptDto);
        requestDto.setName(account.getUsername());
        requestDto.getEmails().add(account.getEmail());
        requestDto.setVendor_id(order.getPayId());

         webClient.post()
                .uri("/documents/receipt")
                .header("X-API-KEY", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
