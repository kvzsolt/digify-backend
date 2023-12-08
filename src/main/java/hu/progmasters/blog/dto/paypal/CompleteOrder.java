package hu.progmasters.blog.dto.paypal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrder {
    private String status;
    private String payId;

    public CompleteOrder(String status) {
        this.status = status;
    }
}
