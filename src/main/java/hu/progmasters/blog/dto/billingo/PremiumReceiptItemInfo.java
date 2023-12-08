package hu.progmasters.blog.dto.billingo;

import hu.progmasters.blog.domain.enums.PremiumPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumReceiptItemInfo {

//    private Integer product_id;
    private String name = "Digify Premium tagság";
    private Double unit_price = (double) PremiumPrice.PREMIUM_PRICE.price;
    private String vat = "27%";
}
