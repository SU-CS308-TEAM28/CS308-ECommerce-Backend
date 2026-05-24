package edu.sabanciuniv.cs308ecommercebackend.services;

import edu.sabanciuniv.cs308ecommercebackend.models.Category;
import edu.sabanciuniv.cs308ecommercebackend.models.Product;
import edu.sabanciuniv.cs308ecommercebackend.models.User;
import edu.sabanciuniv.cs308ecommercebackend.models.payloads.product.ProductAction;
import edu.sabanciuniv.cs308ecommercebackend.repositories.PagedProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.ProductRepository;
import edu.sabanciuniv.cs308ecommercebackend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductService
{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PagedProductRepository pagedProductRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    public Optional<Product> getProduct(String id)
    {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts()
    {
        return productRepository.findAll();
    }

    public Page<Product> getPagedProducts(int page, int size, String sort, String order)
    {
        return pagedProductRepository.findAll(PageRequest.of(
                page,
                size,
                order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
        ));
    }

    public Page<Product> getPagedProducts(int page, int size, String sort, String order, Category category, int minPrice, int maxPrice)
    {
        return pagedProductRepository.findAllByCategoryContainingIgnoreCaseAndPriceIsBetween(
                category.getAbbrv(),
                minPrice,
                maxPrice,
                PageRequest.of(
                        page,
                        size,
                        order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
                )
        );
    }

    public Page<Product> getPagedProducts(int page, int size, String sort, String order, String searchQuery, int minPrice, int maxPrice)
    {
        return pagedProductRepository.findAllByKeywordAndFilterByPriceRange(
                searchQuery,
                minPrice,
                maxPrice,
                PageRequest.of(
                        page,
                        size,
                        order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
                )
        );
    }

    public Product addProduct(ProductAction.Request request) throws Exception
    {
        categoryService.validateCategory(request.getCategory());

        if (request.getSubcategories() != null)
            for (String sub : request.getSubcategories())
                categoryService.validateSubCategory(sub);

        return productRepository.save(Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .activeDiscount(request.getActiveDiscount())
                .model(request.getModel())
                .serialNumber(request.getSerialNumber())
                .warrantyStatus(request.getWarrantyStatus())
                .distributorInformation(request.getDistributorInformation())
                .thumbnailUrl(request.getThumbnailUrl())
                .imageUrls(request.getImageUrls())
                .category(request.getCategory())
                .subcategories(request.getSubcategories())
                .stock(request.getStock())
                .ratings(Product.Ratings.builder().count(0).value(0.0).build())
                .extraProps(request.getExtraProps())
                .build());
    }

    public Product updateProduct(String id, ProductAction.Request request) throws Exception
    {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product with id '" + id + "' not found."));

        categoryService.validateCategory(request.getCategory());

        if (request.getSubcategories() != null)
            for (String sub : request.getSubcategories())
                categoryService.validateSubCategory(sub);

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setActiveDiscount(request.getActiveDiscount());
        existing.setModel(request.getModel());
        existing.setSerialNumber(request.getSerialNumber());
        existing.setWarrantyStatus(request.getWarrantyStatus());
        existing.setDistributorInformation(request.getDistributorInformation());
        existing.setThumbnailUrl(request.getThumbnailUrl());
        existing.setImageUrls(request.getImageUrls());
        existing.setCategory(request.getCategory());
        existing.setSubcategories(request.getSubcategories());
        existing.setStock(request.getStock());
        existing.setExtraProps(request.getExtraProps());

        return productRepository.save(existing);
    }

    public void removeProduct(String id) throws Exception
    {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product with id '" + id + "' not found."));

        productRepository.delete(existing);
    }

    public List<Product> bulkDiscount(List<String> productIds, double discount)
    {
        List<Product> products = productRepository.findAllById(productIds);
        products.forEach(p -> p.setActiveDiscount(discount));
        List<Product> saved = productRepository.saveAll(products);

        List<User> interestedUsers = userRepository.findAllByWishlistProductIdIn(productIds);
        for (User user : interestedUsers)
        {
            if (user.getUserData() == null || user.getUserData().getWishlist() == null)
                continue;

            Set<String> userWishlistIds = new java.util.HashSet<>();
            user.getUserData().getWishlist().forEach(w -> userWishlistIds.add(w.getProductId()));

            List<Product> userProducts = saved.stream()
                    .filter(p -> userWishlistIds.contains(p.getId()))
                    .toList();

            try
            {
                mailService.sendDiscountNotificationEmail(user.getEmail(), user.getName(), userProducts, discount);
            }
            catch (Exception ignored) {}
        }

        return saved;
    }

}
