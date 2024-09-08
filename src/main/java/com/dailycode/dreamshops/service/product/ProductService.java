package com.dailycode.dreamshops.service.product;

import com.dailycode.dreamshops.exception.ProductNotFoundException;
import com.dailycode.dreamshops.model.Product;
import com.dailycode.dreamshops.repository.ProductRepository;

import java.util.List;

public class ProductService implements IProductService{
    private ProductRepository productRepository;
    @Override
    public Product addProduct(Product product) {
        return null;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresentOrElse(productRepository::delete,()->{throw new ProductNotFoundException("Product not found");});
    }

    @Override
    public void updateProduct(Long productId, Product product) {

    }

    @Override
    public List<Product> getAllProducts() {
        return null;
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return null;
    }

    @Override
    public List<Product> getProductsByBrand(String brandId) {
        return null;
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return null;
    }

    @Override
    public List<Product> getProductByName(String name) {
        return null;
    }

    @Override
    public List<Product> getProductByBrandAndName(String brand, String name) {
        return null;
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return null;
    }
}
