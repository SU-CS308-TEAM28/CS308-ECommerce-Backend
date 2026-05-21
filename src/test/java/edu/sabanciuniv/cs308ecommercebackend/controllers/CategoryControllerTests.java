package edu.sabanciuniv.cs308ecommercebackend.controllers;

import edu.sabanciuniv.cs308ecommercebackend.models.Category;
import edu.sabanciuniv.cs308ecommercebackend.services.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTests
{

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void doesGetAllCategoriesReturnCategoryList()
    {
        List<Category> categories = List.of(
                buildCategory("COMPUTERS", "Computers", true),
                buildCategory("PHONES", "Phones", true)
        );

        when(categoryService.getAllCategories()).thenReturn(categories);

        var response = categoryController.getAllCategories();
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Categories returned successfully.", body.get("message"));
        assertEquals(categories, body.get("data"));

        verify(categoryService).getAllCategories();
    }

    @Test
    void doesAddCategoryReturnCreatedCategory()
    {
        Category category = buildCategory("COMPUTERS", "Computers", true);

        when(categoryService.createNewCategory(category)).thenReturn(category);

        var response = categoryController.addCategory(category);
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Category Computers successfully created.", body.get("message"));
        assertSame(category, body.get("data"));

        verify(categoryService).createNewCategory(category);
    }

    @Test
    void doesUpdateCategoryReturnUpdatedCategory()
    {
        Category category = buildCategory("PHONES", "Phones", true);

        when(categoryService.updateCategory("category-1", category)).thenReturn(category);

        var response = categoryController.updateCategory(category, "category-1");
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Category Phones successfully updated.", body.get("message"));
        assertSame(category, body.get("data"));

        verify(categoryService).updateCategory("category-1", category);
    }

    @Test
    void doesRemoveCategoryReturnSuccessWhenServiceRemoves()
    {
        Category category = buildCategory("HOME", "Home", false);

        try
        {
            when(categoryService.removeCategory("category-2")).thenReturn(category);
        }
        catch (Exception e)
        {
            assert false;
        }

        var response = categoryController.removeCategory("category-2");
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Category Home removed successfully.", body.get("message"));
    }

    @Test
    void doesRemoveCategoryReturnBadRequestWhenServiceThrows()
    {
        try
        {
            when(categoryService.removeCategory("category-3"))
                    .thenThrow(new Exception("Primitive categories of the application cannot be removed."));
        }
        catch (Exception e)
        {
            assert false;
        }

        var response = categoryController.removeCategory("category-3");
        Map<String, Object> body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals("Primitive categories of the application cannot be removed.", body.get("message"));
    }

    private Category buildCategory(String abbrv, String label, boolean isPrimitive)
    {
        return Category.builder()
                .abbrv(abbrv)
                .label(label)
                .isPrimitive(isPrimitive)
                .build();
    }

}
