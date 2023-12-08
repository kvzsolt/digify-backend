package hu.progmasters.blog.controller;


import hu.progmasters.blog.dto.category.CategoryCreateReq;
import hu.progmasters.blog.dto.category.CategoryListItemReq;
import hu.progmasters.blog.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@AllArgsConstructor
@Slf4j
public class CategoryController {

    private CategoryService categoryService;
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity createCategory(@Valid @RequestBody CategoryCreateReq categoryCreateReq) {
        categoryService.addCategory(categoryCreateReq);
        log.info("Http request, POST /api/categories Category added: " + categoryCreateReq.getCategoryName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping
    public ResponseEntity<List<CategoryListItemReq>> getCategories() {
        log.info("Http request, GET /api/categories Category list all");
        return new ResponseEntity<>(categoryService.getCategoryListItems(), HttpStatus.OK);
    }
}
