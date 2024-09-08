package com.dailycode.dreamshops.repository;

import com.dailycode.dreamshops.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
