package hu.progmasters.blog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.domain.PaypalToken;
import hu.progmasters.blog.dto.paypal.CompleteOrder;
import hu.progmasters.blog.repository.PaypalTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@Slf4j
public class PaypalService {

    @Value("${paypal.clientID}")
    private String clientID;

    @Value("${paypal.clientSecret}")
    private String clientSecret;

    @Autowired
    private BillingoService billingoService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PaypalTokenRepository paypalTokenRepository;


    public String createToken() {

        String tokenUrl = "https://api-m.sandbox.paypal.com/v1/oauth2/token";

        WebClient webClient = WebClient.builder()
                .baseUrl(tokenUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodeCredentials(clientID, clientSecret))
                .build();

        String response = webClient.post()
                .uri(uriBuilder -> uriBuilder.build())
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        saveToken(response);
        return response;
    }

    private void saveToken(String response) {
        String JsoResponse = response;
        String token;
        int expire;
        PaypalToken paypalToken = new PaypalToken();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(JsoResponse);

            token = jsonNode
                    .path("access_token")
                    .asText();
            expire = jsonNode
                    .path("expires_in")
                    .asInt();

            paypalToken.setId(1L);
            paypalToken.setToken(token);
            paypalToken.setExpiresIn(expire);
            paypalTokenRepository.save(paypalToken);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private static String encodeCredentials(String clientID, String clientSecret) {
        String credentials = clientID + ":" + clientSecret;
        return java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public String createOrder(BigDecimal amount, String currency, Long accountId) {

        String authorizationToken = "Bearer " + checkToken();

        String apiUrl = "https://api-m.sandbox.paypal.com/v2/checkout/orders";

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Prefer", "return=minimal")
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorizationToken)
                .build();
        String succes = "http://localhost:8080/api/paypal/payment-success/" + accountId + "/" + amount;

        String requestBody = "{ \"intent\": \"CAPTURE\"," +
                " \"purchase_units\": " +
                "[ { \"reference_id\": \"d9f80740-38f0-11e8-b467-0ed5f89f711n\", " +
                "\"payee\": { \"email_address\": \"sb-kqru115361356@business.example.com\"}," +
                "\"amount\": { \"currency_code\": \"" + currency + "\"," +
                " \"value\": \"" + amount + "\" } } ]," +
                " \"payment_source\": { \"paypal\": { \"experience_context\": { \"payment_method_preference\": \"UNRESTRICTED\"," +
                " \"landing_page\": \"LOGIN\"," +
                " \"user_action\": \"PAY_NOW\"," +
                " \"return_url\": \"" + succes + "\"," +
                " \"cancel_url\": \"http://localhost:8080/api/paypal/payment-cancel\" } } } }";

        return webClient.post()
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }

    private String checkToken() {
        createToken();
        PaypalToken paypalToken = paypalTokenRepository.getById(1L);
        if (!(paypalToken.getExpiresIn() > 1) ){
            createToken();
        }
        return paypalTokenRepository.getById(1L).getToken();
    }

    public RedirectView capture(String token, Long id, BigDecimal amount) throws DocumentException, IOException {

        String apiUrl = "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + token + "/capture";
        String authorizationToken = "Bearer " + checkToken();

        WebClient webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Prefer", "return=minimal")
                .defaultHeader(HttpHeaders.AUTHORIZATION, authorizationToken)
                .build();
        Account account = accountService.findAccountById(id);
        CompleteOrder order = new CompleteOrder("Successful Digify premium membership purchase", token);
        createOrder(order, id);
        emailService.sendEmailWithAttachment(order,account, amount);
        accountService.setPremium(account);
        billingoService.createReceipt(order, account);

        webClient.post()
                .retrieve()
                .bodyToMono(String.class)
                .block();

       RedirectView redirectView = new RedirectView("https://www.sandbox.paypal.com/checkoutnow?token=" + token);

        return redirectView;
    }

    private void createOrder(CompleteOrder order, Long id) {
        payOrderService.createPayOrder(order, id);
    }

    public ModelAndView paymentSucces(String token, Long id, BigDecimal amount) {
        ModelAndView modelAndView = new ModelAndView("createPay");
        modelAndView.addObject("token", token);
        modelAndView.addObject("accountId", id);
        modelAndView.addObject("amount", amount);
        return modelAndView;
    }
}