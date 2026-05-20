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

    public Category updateCategory(String categoryId, Category category)
    {
        // TODO Check if any product has the subcategory if up for removal.
        category.setId(categoryId);
        return categoryRepository.save(category);
    }

    public Category removeCategory(String categoryId) throws Exception
    {
        Category toRemove = categoryRepository.findById(categoryId).orElseThrow();

        // TODO Check if any product has the category.

        if (toRemove.getIsPrimitive())
            throw new Exception("Primitive categories of the application cannot be removed.");

        categoryRepository.delete(toRemove);

        return toRemove;
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

    public void validateCategory(String abbrv) throws Exception
    {
        if (categoryRepository.findByAbbrvOrSubCategoriesAbbrv(abbrv, abbrv) == null)
            throw new Exception("Category '" + abbrv + "' does not exist.");
    }

    public void validateSubCategory(String abbrv) throws Exception
    {
        if (categoryRepository.findBySubCategoriesAbbrv(abbrv) == null)
            throw new Exception("Subcategory '" + abbrv + "' does not exist.");
    }

}
