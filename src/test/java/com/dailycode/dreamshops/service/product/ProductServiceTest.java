package com.dailycode.dreamshops.service.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dailycode.dreamshops.exception.ProductNotFoundException;
import com.dailycode.dreamshops.model.Category;
import com.dailycode.dreamshops.model.Product;
import com.dailycode.dreamshops.repository.CategoryRepository;
import com.dailycode.dreamshops.repository.ProductRepository;
import com.dailycode.dreamshops.request.AddProductRequest;
import com.dailycode.dreamshops.request.ProductUpdateRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ProductService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class ProductServiceTest {
    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;


    @Test
    void testAddProduct() {
        // Arrange
        BigDecimal price = new BigDecimal("2.3");
        Product product = new Product("Name", "Brand", price, 1, "The characteristics of someone or something",
                new Category("Name"));

        when(productRepository.save(Mockito.<Product>any())).thenReturn(product);
        when(categoryRepository.findByName(Mockito.<String>any())).thenReturn(new Category("Name"));
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(new Category("Name"));

        AddProductRequest request = new AddProductRequest();
        request.setBrand("Brand");
        request.setCategory(new Category("Name"));
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setInventory(1);
        request.setName("Name");
        request.setPrice(new BigDecimal("2.3"));

        // Act
        Product actualAddProductResult = productService.addProduct(request);

        // Assert
        verify(categoryRepository).findByName(eq("Name"));
        verify(productRepository).save(isA(Product.class));
        assertEquals(0, request.getCategory().getId());
        assertSame(product, actualAddProductResult);
    }

    @Test
    void testAddProduct2() {
        // Arrange
        when(categoryRepository.findByName(Mockito.<String>any()))
                .thenThrow(new ProductNotFoundException("An error occurred"));

        AddProductRequest request = new AddProductRequest();
        request.setBrand("Brand");
        request.setCategory(new Category("Name"));
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setInventory(1);
        request.setName("Name");
        request.setPrice(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.addProduct(request));
        verify(categoryRepository).findByName(eq("Name"));
    }


    @Test
    void testAddProduct3() {
        // Arrange
        BigDecimal price = new BigDecimal("2.3");
        Product product = new Product("Name", "Brand", price, 1, "The characteristics of someone or something",
                new Category("Name"));

        when(productRepository.save(Mockito.<Product>any())).thenReturn(product);
        when(categoryRepository.findByName(Mockito.<String>any())).thenReturn(null);
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(new Category("Name"));

        AddProductRequest request = new AddProductRequest();
        request.setBrand("Brand");
        request.setCategory(new Category("Name"));
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setInventory(1);
        request.setName("Name");
        request.setPrice(new BigDecimal("2.3"));

        // Act
        Product actualAddProductResult = productService.addProduct(request);

        // Assert
        verify(categoryRepository).findByName(eq("Name"));
        verify(categoryRepository).save(isA(Category.class));
        verify(productRepository).save(isA(Product.class));
        assertEquals(0, request.getCategory().getId());
        assertSame(product, actualAddProductResult);
    }


    @Test
    void testGetProductById() {
        // Arrange
        BigDecimal price = new BigDecimal("2.3");
        Product product = new Product("Name", "Brand", price, 1, "The characteristics of someone or something",
                new Category("Name"));

        Optional<Product> ofResult = Optional.of(product);
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        Product actualProductById = productService.getProductById(1L);

        // Assert
        verify(productRepository).findById(eq(1L));
        assertSame(product, actualProductById);
    }


    @Test
    void testGetProductById2() {
        // Arrange
        Optional<Product> emptyResult = Optional.empty();
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository).findById(eq(1L));
    }


    @Test
    void testGetProductById3() {
        // Arrange
        when(productRepository.findById(Mockito.<Long>any())).thenThrow(new ProductNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository).findById(eq(1L));
    }


    @Test
    void testDeleteProductById() {
        // Arrange
        doNothing().when(productRepository).delete(Mockito.<Product>any());
        BigDecimal price = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        productService.deleteProductById(1L);

        // Assert that nothing has changed
        verify(productRepository).delete(isA(Product.class));
        verify(productRepository).findById(eq(1L));
    }


    @Test
    void testDeleteProductById2() {
        // Arrange
        Optional<Product> emptyResult = Optional.empty();
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(1L));
        verify(productRepository).findById(eq(1L));
    }


    @Test
    void testUpdateProduct() {
        // Arrange
        BigDecimal price = new BigDecimal("2.3");
        Product product = new Product("Name", "Brand", price, 1, "The characteristics of someone or something",
                new Category("Name"));

        when(productRepository.save(Mockito.<Product>any())).thenReturn(product);
        BigDecimal price2 = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price2, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(categoryRepository.findByName(Mockito.<String>any())).thenReturn(new Category("Name"));

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setBrand("Brand");
        request.setCategory(new Category("Name"));
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setInventory(1);
        request.setName("Name");
        request.setPrice(new BigDecimal("2.3"));

        // Act
        Product actualUpdateProductResult = productService.updateProduct(1L, request);

        // Assert
        verify(categoryRepository).findByName(eq("Name"));
        verify(productRepository).findById(eq(1L));
        verify(productRepository).save(isA(Product.class));
        assertSame(product, actualUpdateProductResult);
    }


    @Test
    void testUpdateProduct2() {
        // Arrange
        BigDecimal price = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(categoryRepository.findByName(Mockito.<String>any()))
                .thenThrow(new ProductNotFoundException("An error occurred"));

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setBrand("Brand");
        request.setCategory(new Category("Name"));
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setInventory(1);
        request.setName("Name");
        request.setPrice(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, request));
        verify(categoryRepository).findByName(eq("Name"));
        verify(productRepository).findById(eq(1L));
    }


    @Test
    void testUpdateProduct3() {
        // Arrange
        when(productRepository.save(Mockito.<Product>any())).thenReturn(null);
        BigDecimal price = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(categoryRepository.findByName(Mockito.<String>any())).thenReturn(new Category("Name"));

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setBrand("Brand");
        request.setCategory(new Category("Name"));
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setInventory(1);
        request.setName("Name");
        request.setPrice(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, request));
        verify(categoryRepository).findByName(eq("Name"));
        verify(productRepository).findById(eq(1L));
        verify(productRepository).save(isA(Product.class));
    }


    @Test
    void testUpdateProduct4() {
        // Arrange
        Optional<Product> emptyResult = Optional.empty();
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setBrand("Brand");
        request.setCategory(new Category("Name"));
        request.setDescription("The characteristics of someone or something");
        request.setId(1L);
        request.setInventory(1);
        request.setName("Name");
        request.setPrice(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, request));
        verify(productRepository).findById(eq(1L));
    }


    @Test
    void testGetAllProducts() {
        // Arrange
        ArrayList<Product> productList = new ArrayList<>();
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> actualAllProducts = productService.getAllProducts();

        // Assert
        verify(productRepository).findAll();
        assertTrue(actualAllProducts.isEmpty());
        assertSame(productList, actualAllProducts);
    }


    @Test
    void testGetAllProducts2() {
        // Arrange
        when(productRepository.findAll()).thenThrow(new ProductNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getAllProducts());
        verify(productRepository).findAll();
    }


    @Test
    void testGetProductsByCategory() {
        // Arrange
        ArrayList<Product> productList = new ArrayList<>();
        when(productRepository.findByCategoryName(Mockito.<String>any())).thenReturn(productList);

        // Act
        List<Product> actualProductsByCategory = productService.getProductsByCategory("Category");

        // Assert
        verify(productRepository).findByCategoryName(eq("Category"));
        assertTrue(actualProductsByCategory.isEmpty());
        assertSame(productList, actualProductsByCategory);
    }


    @Test
    void testGetProductsByCategory2() {
        // Arrange
        when(productRepository.findByCategoryName(Mockito.<String>any()))
                .thenThrow(new ProductNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductsByCategory("Category"));
        verify(productRepository).findByCategoryName(eq("Category"));
    }


    @Test
    void testGetProductsByBrand() {
        // Arrange
        ArrayList<Product> productList = new ArrayList<>();
        when(productRepository.findByBrand(Mockito.<String>any())).thenReturn(productList);

        // Act
        List<Product> actualProductsByBrand = productService.getProductsByBrand("Brand");

        // Assert
        verify(productRepository).findByBrand(eq("Brand"));
        assertTrue(actualProductsByBrand.isEmpty());
        assertSame(productList, actualProductsByBrand);
    }


    @Test
    void testGetProductsByBrand2() {
        // Arrange
        when(productRepository.findByBrand(Mockito.<String>any()))
                .thenThrow(new ProductNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductsByBrand("Brand"));
        verify(productRepository).findByBrand(eq("Brand"));
    }


    @Test
    void testGetProductsByCategoryAndBrand() {
        // Arrange
        ArrayList<Product> productList = new ArrayList<>();
        when(productRepository.findProductsByCategoryNameAndBrand(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(productList);

        // Act
        List<Product> actualProductsByCategoryAndBrand = productService.getProductsByCategoryAndBrand("Category", "Brand");

        // Assert
        verify(productRepository).findProductsByCategoryNameAndBrand(eq("Category"), eq("Brand"));
        assertTrue(actualProductsByCategoryAndBrand.isEmpty());
        assertSame(productList, actualProductsByCategoryAndBrand);
    }


    @Test
    void testGetProductsByCategoryAndBrand2() {
        // Arrange
        when(productRepository.findProductsByCategoryNameAndBrand(Mockito.<String>any(), Mockito.<String>any()))
                .thenThrow(new ProductNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductsByCategoryAndBrand("Category", "Brand"));
        verify(productRepository).findProductsByCategoryNameAndBrand(eq("Category"), eq("Brand"));
    }


    @Test
    void testGetProductByName() {
        // Arrange
        ArrayList<Product> productList = new ArrayList<>();
        when(productRepository.findProductByName(Mockito.<String>any())).thenReturn(productList);

        // Act
        List<Product> actualProductByName = productService.getProductByName("Name");

        // Assert
        verify(productRepository).findProductByName(eq("Name"));
        assertTrue(actualProductByName.isEmpty());
        assertSame(productList, actualProductByName);
    }


    @Test
    void testGetProductByName2() {
        // Arrange
        when(productRepository.findProductByName(Mockito.<String>any()))
                .thenThrow(new ProductNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductByName("Name"));
        verify(productRepository).findProductByName(eq("Name"));
    }


    @Test
    void testGetProductByBrandAndName() {
        // Arrange
        ArrayList<Product> productList = new ArrayList<>();
        when(productRepository.findProductByBrandAndName(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn(productList);

        // Act
        List<Product> actualProductByBrandAndName = productService.getProductByBrandAndName("Brand", "Name");

        // Assert
        verify(productRepository).findProductByBrandAndName(eq("Brand"), eq("Name"));
        assertTrue(actualProductByBrandAndName.isEmpty());
        assertSame(productList, actualProductByBrandAndName);
    }


    @Test
    void testGetProductByBrandAndName2() {
        // Arrange
        when(productRepository.findProductByBrandAndName(Mockito.<String>any(), Mockito.<String>any()))
                .thenThrow(new ProductNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductByBrandAndName("Brand", "Name"));
        verify(productRepository).findProductByBrandAndName(eq("Brand"), eq("Name"));
    }


    @Test
    void testCountProductsByBrandAndName() {
        // Arrange
        when(productRepository.countProductsByBrandAndName(Mockito.<String>any(), Mockito.<String>any())).thenReturn(3L);

        // Act
        Long actualCountProductsByBrandAndNameResult = productService.countProductsByBrandAndName("Brand", "Name");

        // Assert
        verify(productRepository).countProductsByBrandAndName(eq("Brand"), eq("Name"));
        assertEquals(3L, actualCountProductsByBrandAndNameResult.longValue());
    }


    @Test
    void testCountProductsByBrandAndName2() {
        // Arrange
        when(productRepository.countProductsByBrandAndName(Mockito.<String>any(), Mockito.<String>any()))
                .thenThrow(new ProductNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ProductNotFoundException.class, () -> productService.countProductsByBrandAndName("Brand", "Name"));
        verify(productRepository).countProductsByBrandAndName(eq("Brand"), eq("Name"));
    }
}
