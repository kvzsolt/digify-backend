package hu.progmasters.blog.dto.account;

import hu.progmasters.blog.dto.billingo.PremiumReceiptItemInfo;
import lombok.Data;

import java.util.List;

@Data
public class DocumentInsertReq {
    private String vendor_id;
    private Integer partner_id;
    private String name;
    private List<String> emails;
    private String type;
    private String payment_method;
    private String currency;
    private Double conversion_rate;
    private Boolean electronic;
    private List<PremiumReceiptItemInfo> items;
}
