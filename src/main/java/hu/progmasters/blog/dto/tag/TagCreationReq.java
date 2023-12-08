package hu.progmasters.blog.dto.tag;


import hu.progmasters.blog.validator.imagevalidator.NotEmptyList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagCreationReq {

    @NotBlank(message = "Tag cannot be empty")
    private String tagsName;

}
