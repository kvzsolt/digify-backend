package hu.progmasters.blog.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEditReq {

    private String realName;
    private LocalDate dateOfBirth;
    private String aboutMe;
    private String profileImageUrl;
}
