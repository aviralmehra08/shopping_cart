package com.dailycode.dreamshops.repository;

import com.dailycode.dreamshops.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByCategoryName(String category);

    List<Product> findByBrand(String brand);

    List<Product> findProductsByCategoryNameAndBrand(String category, String brand);

    List<Product> findProductByName(String name);

    List<Product> findProductByBrandAndName(String brand, String name);

    Long countProductsByBrandAndName(String brand, String name);
}
