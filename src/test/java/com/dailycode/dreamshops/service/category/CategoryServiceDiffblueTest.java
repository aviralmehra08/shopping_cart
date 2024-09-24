package com.dailycode.dreamshops.service.category;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dailycode.dreamshops.exception.AlreadyExistedException;
import com.dailycode.dreamshops.exception.ResourceNotFoundException;
import com.dailycode.dreamshops.model.Category;
import com.dailycode.dreamshops.repository.CategoryRepository;

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

@ContextConfiguration(classes = {CategoryService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class CategoryServiceDiffblueTest {
    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;


    @Test
    void testGetCategoryById() {
        // Arrange
        Category category = new Category("Name");
        Optional<Category> ofResult = Optional.of(category);
        when(categoryRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        Category actualCategoryById = categoryService.getCategoryById(1L);

        // Assert
        verify(categoryRepository).findById(eq(1L));
        assertSame(category, actualCategoryById);
    }


    @Test
    void testGetCategoryById2() {
        // Arrange
        Optional<Category> emptyResult = Optional.empty();
        when(categoryRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
        verify(categoryRepository).findById(eq(1L));
    }


    @Test
    void testGetCategoryById3() {
        // Arrange
        when(categoryRepository.findById(Mockito.<Long>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
        verify(categoryRepository).findById(eq(1L));
    }


    @Test
    void testGetCategoryByName() {
        // Arrange
        Category category = new Category("Name");
        when(categoryRepository.findByName(Mockito.<String>any())).thenReturn(category);

        // Act
        Category actualCategoryByName = categoryService.getCategoryByName("Name");

        // Assert
        verify(categoryRepository).findByName(eq("Name"));
        assertSame(category, actualCategoryByName);
    }


    @Test
    void testGetCategoryByName2() {
        // Arrange
        when(categoryRepository.findByName(Mockito.<String>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryByName("Name"));
        verify(categoryRepository).findByName(eq("Name"));
    }


    @Test
    void testGetAllCategories() {
        // Arrange
        ArrayList<Category> categoryList = new ArrayList<>();
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // Act
        List<Category> actualAllCategories = categoryService.getAllCategories();

        // Assert
        verify(categoryRepository).findAll();
        assertTrue(actualAllCategories.isEmpty());
        assertSame(categoryList, actualAllCategories);
    }


    @Test
    void testGetAllCategories2() {
        // Arrange
        when(categoryRepository.findAll()).thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getAllCategories());
        verify(categoryRepository).findAll();
    }


    @Test
    void testAddCategory() {
        // Arrange
        when(categoryRepository.existsByName(Mockito.<String>any())).thenReturn(true);

        // Act and Assert
        assertThrows(AlreadyExistedException.class, () -> categoryService.addCategory(new Category("Name")));
        verify(categoryRepository).existsByName(eq("Name"));
    }


    @Test
    void testAddCategory2() {
        // Arrange
        when(categoryRepository.existsByName(Mockito.<String>any())).thenReturn(false);
        Category category = new Category("Name");
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(category);

        // Act
        Category actualAddCategoryResult = categoryService.addCategory(new Category("Name"));

        // Assert
        verify(categoryRepository).existsByName(eq("Name"));
        verify(categoryRepository).save(isA(Category.class));
        assertSame(category, actualAddCategoryResult);
    }


    @Test
    void testAddCategory3() {
        // Arrange
        when(categoryRepository.existsByName(Mockito.<String>any()))
                .thenThrow(new AlreadyExistedException("An error occurred"));

        // Act and Assert
        assertThrows(AlreadyExistedException.class, () -> categoryService.addCategory(new Category("Name")));
        verify(categoryRepository).existsByName(eq("Name"));
    }


    @Test
    void testUpdateCategory() {
        // Arrange
        Category category = new Category("Name");
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(category);
        Optional<Category> ofResult = Optional.of(new Category("Name"));
        when(categoryRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        Category actualUpdateCategoryResult = categoryService.updateCategory(new Category("Name"), 1L);

        // Assert
        verify(categoryRepository).findById(eq(1L));
        verify(categoryRepository).save(isA(Category.class));
        assertSame(category, actualUpdateCategoryResult);
    }


    @Test
    void testUpdateCategory2() {
        // Arrange
        when(categoryRepository.save(Mockito.<Category>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));
        Optional<Category> ofResult = Optional.of(new Category("Name"));
        when(categoryRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(new Category("Name"), 1L));
        verify(categoryRepository).findById(eq(1L));
        verify(categoryRepository).save(isA(Category.class));
    }


    @Test
    void testUpdateCategory3() {
        // Arrange
        when(categoryRepository.save(Mockito.<Category>any())).thenReturn(null);
        Optional<Category> ofResult = Optional.of(new Category("Name"));
        when(categoryRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(new Category("Name"), 1L));
        verify(categoryRepository).findById(eq(1L));
        verify(categoryRepository).save(isA(Category.class));
    }


    @Test
    void testUpdateCategory4() {
        // Arrange
        Optional<Category> emptyResult = Optional.empty();
        when(categoryRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(new Category("Name"), 1L));
        verify(categoryRepository).findById(eq(1L));
    }


    @Test
    void testDeleteCategory() {
        // Arrange
        doNothing().when(categoryRepository).delete(Mockito.<Category>any());
        Optional<Category> ofResult = Optional.of(new Category("Name"));
        when(categoryRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        categoryService.deleteCategory(1L);

        // Assert that nothing has changed
        verify(categoryRepository).delete(isA(Category.class));
        verify(categoryRepository).findById(eq(1L));
    }


    @Test
    void testDeleteCategory2() {
        // Arrange
        Optional<Category> emptyResult = Optional.empty();
        when(categoryRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository).findById(eq(1L));
    }
}
