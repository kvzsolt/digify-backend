package hu.progmasters.blog.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateReq {

    @NotBlank(message = "Category name must not be empty")
    private String categoryName;
}
