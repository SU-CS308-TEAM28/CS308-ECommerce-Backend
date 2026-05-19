package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Category;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import edu.sabanciuniv.cs308ecommercebackend.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
