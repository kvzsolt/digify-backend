package hu.progmasters.blog.controller;

import com.itextpdf.text.DocumentException;
import hu.progmasters.blog.domain.enums.PremiumPrice;
import hu.progmasters.blog.service.EmailService;
import hu.progmasters.blog.service.PaypalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping(value = "/api/paypal")
@CrossOrigin(origins = "https://api-m.sandbox.paypal.com/v2/checkout/orders")
@PreAuthorize("hasRole('ROLE_USER')")
@AllArgsConstructor
public class PaypalController {

    private PaypalService paypalService;
    private EmailService emailService;

    @PostMapping(value = "/createToken")
    public String createToken() {
        log.info("Http request, POST /api/paypal/init Paypal request send");
        return paypalService.createToken();
    }

    @PostMapping(value = "/createOrder/premium/{accountId}")
    public String createPayorder(@PathVariable("accountId") Long id) {
        String currency = "HUF";
        BigDecimal amount = BigDecimal.valueOf(PremiumPrice.PREMIUM_PRICE.price);
        log.info("Http request, POST /api/paypal/init Paypal request send");
        return paypalService.createOrder(amount, currency, id);
    }

    @PostMapping(value = "/init/{accountId}")
    public String createPayment(@RequestParam("currency") String currency,
                                @RequestParam("amount") BigDecimal amount,
                                @PathVariable("accountId") Long id) {
        log.info("Http request, POST /api/paypal/init Paypal request send");
        return paypalService.createOrder(amount, currency, id);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @PostMapping(value = "/capture")
    public RedirectView completePayment(@RequestParam("token") String token,
                                        @RequestParam("accountId") Long id,
                                        @RequestParam("amount") BigDecimal amount) throws DocumentException, IOException {
        log.info("Http request, POST /api/paypal/capture Paypal payd confirmed");
        return paypalService.capture(token, id, amount);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/payment-success/{accountId}/{amount}")
    public ModelAndView handlePaymentSuccess(@RequestParam("token") String token,
                                             @PathVariable("accountId") Long id,
                                             @PathVariable("amount") BigDecimal amount) {
        log.info("Http request, GET /api/paypal/payment-success Paypal payment successful");
        return paypalService.paymentSucces(token, id, amount);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/payment-cancel")
    public String handlePaymentCancel() {
        String htmlContent = "<html><body><h1>Payment cancel!</h1></body></html>";
        emailService.sendEmail("blogprogmasters@gmail.com", "Payment ", "Payment canceled");
        log.info("Http request, GET /api/paypal/payment-cancel Paypal payment cancel");
        return htmlContent;
    }

}