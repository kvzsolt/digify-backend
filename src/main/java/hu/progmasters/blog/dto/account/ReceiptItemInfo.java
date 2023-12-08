package hu.progmasters.blog.dto.account;

import lombok.Data;

@Data
public class ReceiptItemInfo {

//    private Integer product_id;
    private String name;
    private Double unit_price;
    private String vat;
}
