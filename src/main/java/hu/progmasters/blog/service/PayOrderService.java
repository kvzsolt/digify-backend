package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.domain.PayOrder;
import hu.progmasters.blog.dto.paypal.CompleteOrder;
import hu.progmasters.blog.repository.PayOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@AllArgsConstructor
public class PayOrderService {

    private PayOrderRepository payOrderRepository;
    private AccountService accountService;

    public void createPayOrder(CompleteOrder order, Long id) {
        Account account = accountService.findAccountById(id);
        PayOrder payOrder = new PayOrder();
        payOrder.setPayId(order.getPayId());
        payOrder.setAccount(account);
        payOrder.setPaidAt(LocalDateTime.now());
        payOrderRepository.save(payOrder);
    }
}
