package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.PaypalToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaypalTokenRepository extends JpaRepository<PaypalToken,Long> {
}
