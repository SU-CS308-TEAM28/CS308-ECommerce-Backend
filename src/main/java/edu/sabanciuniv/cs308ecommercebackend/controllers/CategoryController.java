package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Category;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product/category")
public class CategoryController
{

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/list")
    public TeknocsResponse<List<Category>> getAllCategories()
    {
        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Categories returned successfully.",
                categoryService.getAllCategories()
        );
    }

    @PostMapping("/add")
    public TeknocsResponse<Category> addCategory(@RequestBody Category category)
    {
        return new TeknocsResponse<>(
                HttpStatus.CREATED,
                "Category %s successfully created.".formatted(category.getLabel()),
                categoryService.createNewCategory(category)
        );
    }

    @PutMapping("/{id}")
    public TeknocsResponse<Category> updateCategory(@RequestBody Category category, @PathVariable String id)
    {
        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Category %s successfully updated.".formatted(category.getLabel()),
                categoryService.updateCategory(id, category)
        );
    }

    @DeleteMapping("/{id}")
    public TeknocsResponse<?> removeCategory(@PathVariable String id)
    {
        Category category = null;
        try
        {
            category = categoryService.removeCategory(id);
        }
        catch (Exception e)
        {
            return new TeknocsResponse<>(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage(),
                    null
            );
        }

        return new TeknocsResponse<>(
                HttpStatus.OK,
                "Category %s removed successfully.".formatted(category.getLabel()),
                null
        );
    }

}
