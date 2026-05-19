package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Category;
import edu.sabanciuniv.cs308ecommercebackend.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService
{

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createNewCategory(Category category)
    {
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories()
    {
        return categoryRepository.findAll();
    }

    public Category getCategory(String abbrv)
    {
        return categoryRepository.findByAbbrvOrSubCategoriesAbbrv(abbrv, abbrv);
    }

    public Category getSubCategory(String abbrv)
    {
        return categoryRepository.findBySubCategoriesAbbrv(abbrv).getSubCategories().stream().filter(s -> s.getAbbrv().equals(abbrv)).findFirst().orElseThrow();
    }

}
