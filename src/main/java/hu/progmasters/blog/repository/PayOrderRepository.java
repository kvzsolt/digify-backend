package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.PayOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayOrderRepository extends JpaRepository<PayOrder,Long> {
}
