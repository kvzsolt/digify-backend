package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.PostCategory;
import hu.progmasters.blog.dto.category.CategoryCreateReq;
import hu.progmasters.blog.dto.category.CategoryListItemReq;
import hu.progmasters.blog.exception.category.CategoryNotExistsException;
import hu.progmasters.blog.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CategoryService {

    private CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public void addCategory(CategoryCreateReq categoryCreateReq) {
        PostCategory newCategory = modelMapper.map(categoryCreateReq, PostCategory.class);
        categoryRepository.save(newCategory);
    }

    public List<CategoryListItemReq> getCategoryListItems() {
        List<PostCategory> categoriesFromDb = categoryRepository.findAll();

        return categoriesFromDb.stream()
                .map(postCategory -> new CategoryListItemReq(postCategory.getId(), postCategory.getCategoryName()))
                .collect(Collectors.toList());

    }

    public PostCategory getCategoryByName(String category) {
        Optional<PostCategory> categoryForThePost = categoryRepository.findByCategoryName(category);

        return categoryForThePost.orElseThrow(CategoryNotExistsException::new);
    }
}
