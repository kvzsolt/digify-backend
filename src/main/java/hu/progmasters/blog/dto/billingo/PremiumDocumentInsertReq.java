package hu.progmasters.blog.dto.billingo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PremiumDocumentInsertReq {
    private String vendor_id;
    private Integer partner_id =0;
    private String name;
    private List<String> emails = new ArrayList<>();
    private Integer block_id = 220598;
    private String type = "receipt";
    private String payment_method = "paypal";
    private String currency = "HUF";
    private Double conversion_rate = 1.0;
    private Boolean electronic = true;
    private List<PremiumReceiptItemInfo> items = new ArrayList<>();
}
