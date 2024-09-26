package com.dailycode.dreamshops.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dailycode.dreamshops.dto.ImageDto;
import com.dailycode.dreamshops.exception.ResourceNotFoundException;
import com.dailycode.dreamshops.model.Category;
import com.dailycode.dreamshops.model.Image;
import com.dailycode.dreamshops.model.Product;
import com.dailycode.dreamshops.repository.CategoryRepository;
import com.dailycode.dreamshops.repository.ImageRepository;
import com.dailycode.dreamshops.repository.ProductRepository;
import com.dailycode.dreamshops.response.ApiResponse;
import com.dailycode.dreamshops.service.image.ImageService;
import com.dailycode.dreamshops.service.product.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {ImageController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class ImageControllerTest {
    @Autowired
    private ImageController imageController;

    @MockBean
    private ImageService imageService;


    @Test
    void testSaveImages() {


        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        BigDecimal price = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ImageController imageController = new ImageController(new ImageService(mock(ImageRepository.class),
                new ProductService(productRepository, mock(CategoryRepository.class))));
        ArrayList<MultipartFile> files = new ArrayList<>();

        // Act
        ResponseEntity<ApiResponse> actualSaveImagesResult = imageController.saveImages(files, 1L);

        // Assert
        verify(productRepository).findById(eq(1L));
        ApiResponse body = actualSaveImagesResult.getBody();
        assertEquals("Upload Success !!", body.getMessage());
        assertEquals(200, actualSaveImagesResult.getStatusCodeValue());
        assertTrue(actualSaveImagesResult.hasBody());
        assertTrue(actualSaveImagesResult.getHeaders().isEmpty());
        assertEquals(files, body.getData());
    }


    @Test
    void testSaveImages2() {


        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        Optional<Product> emptyResult = Optional.empty();
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);
        ImageController imageController = new ImageController(new ImageService(mock(ImageRepository.class),
                new ProductService(productRepository, mock(CategoryRepository.class))));

        // Act
        ResponseEntity<ApiResponse> actualSaveImagesResult = imageController.saveImages(new ArrayList<>(), 1L);

        // Assert
        verify(productRepository).findById(eq(1L));
        ApiResponse body = actualSaveImagesResult.getBody();
        assertEquals("Product not found", body.getData());
        assertEquals("Upload Failed !!", body.getMessage());
        assertEquals(500, actualSaveImagesResult.getStatusCodeValue());
        assertTrue(actualSaveImagesResult.hasBody());
        assertTrue(actualSaveImagesResult.getHeaders().isEmpty());
    }


    @Test
    void testSaveImages3() {


        // Arrange
        ImageController imageController = new ImageController(null);

        // Act
        ResponseEntity<ApiResponse> actualSaveImagesResult = imageController.saveImages(new ArrayList<>(), 1L);

        // Assert
        ApiResponse body = actualSaveImagesResult.getBody();
        assertEquals("Cannot invoke \"com.dailycode.dreamshops.service.image.ImageService.saveImage(java.util.List,"
                + " java.lang.Long)\" because \"this.imageService\" is null", body.getData());
        assertEquals("Upload Failed !!", body.getMessage());
        assertEquals(500, actualSaveImagesResult.getStatusCodeValue());
        assertTrue(actualSaveImagesResult.hasBody());
        assertTrue(actualSaveImagesResult.getHeaders().isEmpty());
    }


    @Test
    void testSaveImages4() throws IOException {


        // Arrange
        Image image = new Image();
        image.setDownloadUrl("https://example.org/example");
        image.setFileName("foo.txt");
        image.setFileType("File Type");
        image.setId(1L);
        image.setImage(mock(Blob.class));
        BigDecimal price = new BigDecimal("2.3");
        image.setProduct(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        ImageRepository imageRepository = mock(ImageRepository.class);
        when(imageRepository.save(Mockito.<Image>any())).thenReturn(image);
        ProductRepository productRepository = mock(ProductRepository.class);
        BigDecimal price2 = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price2, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ImageController imageController = new ImageController(
                new ImageService(imageRepository, new ProductService(productRepository, mock(CategoryRepository.class))));

        ArrayList<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("Upload Success !!",
                new ByteArrayInputStream(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1})));

        // Act
        ResponseEntity<ApiResponse> actualSaveImagesResult = imageController.saveImages(files, 1L);

        // Assert
        verify(productRepository).findById(eq(1L));
        verify(imageRepository, atLeast(1)).save(Mockito.<Image>any());
        ApiResponse body = actualSaveImagesResult.getBody();
        assertEquals(1, ((List<ImageDto>) body.getData()).size());
        ImageDto getResult = ((List<ImageDto>) body.getData()).get(0);
        assertEquals("/api/v1/images/image/download/1", getResult.getDownloadUrl());
        assertEquals("Upload Success !!", body.getMessage());
        assertEquals("foo.txt", getResult.getImageName());
        assertEquals(1L, getResult.getImageId().longValue());
        assertEquals(200, actualSaveImagesResult.getStatusCodeValue());
        assertTrue(actualSaveImagesResult.hasBody());
        assertTrue(actualSaveImagesResult.getHeaders().isEmpty());
    }


    @Test
    void testSaveImages5() throws IOException {


        // Arrange
        Image image = new Image();
        image.setDownloadUrl("https://example.org/example");
        image.setFileName("foo.txt");
        image.setFileType("File Type");
        image.setId(1L);
        image.setImage(mock(Blob.class));
        BigDecimal price = new BigDecimal("2.3");
        image.setProduct(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        ImageRepository imageRepository = mock(ImageRepository.class);
        when(imageRepository.save(Mockito.<Image>any())).thenReturn(image);
        ProductRepository productRepository = mock(ProductRepository.class);
        BigDecimal price2 = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price2, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ImageController imageController = new ImageController(
                new ImageService(imageRepository, new ProductService(productRepository, mock(CategoryRepository.class))));

        ArrayList<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("/api/v1/images/image/download/",
                new ByteArrayInputStream(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1})));
        files.add(new MockMultipartFile("Upload Success !!",
                new ByteArrayInputStream(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1})));

        // Act
        ResponseEntity<ApiResponse> actualSaveImagesResult = imageController.saveImages(files, 1L);

        // Assert
        verify(productRepository).findById(eq(1L));
        verify(imageRepository, atLeast(1)).save(Mockito.<Image>any());
        ApiResponse body = actualSaveImagesResult.getBody();
        assertEquals(2, ((List<ImageDto>) body.getData()).size());
        ImageDto getResult = ((List<ImageDto>) body.getData()).get(0);
        assertEquals("/api/v1/images/image/download/1", getResult.getDownloadUrl());
        ImageDto getResult2 = ((List<ImageDto>) body.getData()).get(1);
        assertEquals("/api/v1/images/image/download/1", getResult2.getDownloadUrl());
        assertEquals("Upload Success !!", body.getMessage());
        assertEquals("foo.txt", getResult.getImageName());
        assertEquals("foo.txt", getResult2.getImageName());
        assertEquals(1L, getResult.getImageId().longValue());
        assertEquals(1L, getResult2.getImageId().longValue());
        assertEquals(200, actualSaveImagesResult.getStatusCodeValue());
        assertTrue(actualSaveImagesResult.hasBody());
        assertTrue(actualSaveImagesResult.getHeaders().isEmpty());
    }


    @Test
    void testSaveImages6() {


        // Arrange
        ProductRepository productRepository = mock(ProductRepository.class);
        BigDecimal price = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ImageController imageController = new ImageController(new ImageService(mock(ImageRepository.class),
                new ProductService(productRepository, mock(CategoryRepository.class))));

        ArrayList<MultipartFile> files = new ArrayList<>();
        files.add(null);

        // Act
        ResponseEntity<ApiResponse> actualSaveImagesResult = imageController.saveImages(files, 1L);

        // Assert
        verify(productRepository).findById(eq(1L));
        ApiResponse body = actualSaveImagesResult.getBody();
        assertEquals(
                "Cannot invoke \"org.springframework.web.multipart.MultipartFile.getOriginalFilename()\" because \"file\""
                        + " is null",
                body.getData());
        assertEquals("Upload Failed !!", body.getMessage());
        assertEquals(500, actualSaveImagesResult.getStatusCodeValue());
        assertTrue(actualSaveImagesResult.hasBody());
        assertTrue(actualSaveImagesResult.getHeaders().isEmpty());
    }


    @Test
    void testSaveImages7() throws IOException {


        // Arrange
        ImageRepository imageRepository = mock(ImageRepository.class);
        when(imageRepository.save(Mockito.<Image>any())).thenThrow(new ResourceNotFoundException("An error occurred"));
        ProductRepository productRepository = mock(ProductRepository.class);
        BigDecimal price = new BigDecimal("2.3");
        Optional<Product> ofResult = Optional.of(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        when(productRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ImageController imageController = new ImageController(
                new ImageService(imageRepository, new ProductService(productRepository, mock(CategoryRepository.class))));

        ArrayList<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("Upload Success !!",
                new ByteArrayInputStream(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1})));

        // Act
        ResponseEntity<ApiResponse> actualSaveImagesResult = imageController.saveImages(files, 1L);

        // Assert
        verify(productRepository).findById(eq(1L));
        verify(imageRepository).save(isA(Image.class));
        ApiResponse body = actualSaveImagesResult.getBody();
        assertEquals("An error occurred", body.getData());
        assertEquals("Upload Failed !!", body.getMessage());
        assertEquals(500, actualSaveImagesResult.getStatusCodeValue());
        assertTrue(actualSaveImagesResult.hasBody());
        assertTrue(actualSaveImagesResult.getHeaders().isEmpty());
    }


    @Test
    void testDownloadImage() throws SQLException {


        // Arrange
        Blob image = mock(Blob.class);
        when(image.length()).thenThrow(new SQLException());

        Image image2 = new Image();
        image2.setDownloadUrl("https://example.org/example");
        image2.setFileName("foo.txt");
        image2.setFileType("File Type");
        image2.setId(1L);
        image2.setImage(image);
        BigDecimal price = new BigDecimal("2.3");
        image2.setProduct(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        Optional<Image> ofResult = Optional.of(image2);
        ImageRepository imageRepository = mock(ImageRepository.class);
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(SQLException.class, () -> (new ImageController(new ImageService(imageRepository,
                new ProductService(mock(ProductRepository.class), mock(CategoryRepository.class))))).downloadImage(1L));
        verify(image).length();
        verify(imageRepository).findById(eq(1L));
    }


    @Test
    void testUpdateImage() throws IOException {


        // Arrange
        Image image = new Image();
        image.setDownloadUrl("https://example.org/example");
        image.setFileName("foo.txt");
        image.setFileType("File Type");
        image.setId(1L);
        image.setImage(mock(Blob.class));
        BigDecimal price = new BigDecimal("2.3");
        image.setProduct(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        Optional<Image> ofResult = Optional.of(image);

        Image image2 = new Image();
        image2.setDownloadUrl("https://example.org/example");
        image2.setFileName("foo.txt");
        image2.setFileType("File Type");
        image2.setId(1L);
        image2.setImage(mock(Blob.class));
        BigDecimal price2 = new BigDecimal("2.3");
        image2.setProduct(
                new Product("Name", "Brand", price2, 1, "The characteristics of someone or something", new Category("Name")));
        ImageRepository imageRepository = mock(ImageRepository.class);
        when(imageRepository.save(Mockito.<Image>any())).thenReturn(image2);
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ImageController imageController = new ImageController(new ImageService(imageRepository,
                new ProductService(mock(ProductRepository.class), mock(CategoryRepository.class))));

        // Act
        ResponseEntity<ApiResponse> actualUpdateImageResult = imageController.updateImage(1L,
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Assert
        verify(imageRepository, atLeast(1)).findById(eq(1L));
        verify(imageRepository).save(isA(Image.class));
        ApiResponse body = actualUpdateImageResult.getBody();
        assertEquals("Update Success !!", body.getMessage());
        assertNull(body.getData());
        assertEquals(200, actualUpdateImageResult.getStatusCodeValue());
        assertTrue(actualUpdateImageResult.hasBody());
        assertTrue(actualUpdateImageResult.getHeaders().isEmpty());
    }


    @Test
    void testUpdateImage2() throws IOException {


        // Arrange
        Image image = new Image();
        image.setDownloadUrl("https://example.org/example");
        image.setFileName("foo.txt");
        image.setFileType("File Type");
        image.setId(1L);
        image.setImage(mock(Blob.class));
        BigDecimal price = new BigDecimal("2.3");
        image.setProduct(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        Optional<Image> ofResult = Optional.of(image);
        ImageRepository imageRepository = mock(ImageRepository.class);
        when(imageRepository.save(Mockito.<Image>any())).thenThrow(new ResourceNotFoundException("An error occurred"));
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        ImageController imageController = new ImageController(new ImageService(imageRepository,
                new ProductService(mock(ProductRepository.class), mock(CategoryRepository.class))));

        // Act
        ResponseEntity<ApiResponse> actualUpdateImageResult = imageController.updateImage(1L,
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Assert
        verify(imageRepository, atLeast(1)).findById(eq(1L));
        verify(imageRepository).save(isA(Image.class));
        ApiResponse body = actualUpdateImageResult.getBody();
        assertEquals("An error occurred", body.getMessage());
        assertNull(body.getData());
        assertEquals(404, actualUpdateImageResult.getStatusCodeValue());
        assertTrue(actualUpdateImageResult.hasBody());
        assertTrue(actualUpdateImageResult.getHeaders().isEmpty());
    }


    @Test
    void testUpdateImage3() throws IOException {


        // Arrange
        ImageRepository imageRepository = mock(ImageRepository.class);
        Optional<Image> emptyResult = Optional.empty();
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);
        ImageController imageController = new ImageController(new ImageService(imageRepository,
                new ProductService(mock(ProductRepository.class), mock(CategoryRepository.class))));

        // Act
        ResponseEntity<ApiResponse> actualUpdateImageResult = imageController.updateImage(1L,
                new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Assert
        verify(imageRepository).findById(eq(1L));
        ApiResponse body = actualUpdateImageResult.getBody();
        assertEquals("No image found for id 1", body.getMessage());
        assertNull(body.getData());
        assertEquals(404, actualUpdateImageResult.getStatusCodeValue());
        assertTrue(actualUpdateImageResult.hasBody());
        assertTrue(actualUpdateImageResult.getHeaders().isEmpty());
    }


    @Test
    void testDeleteImage() {


        // Arrange
        Image image = new Image();
        image.setDownloadUrl("https://example.org/example");
        image.setFileName("foo.txt");
        image.setFileType("File Type");
        image.setId(1L);
        image.setImage(mock(Blob.class));
        BigDecimal price = new BigDecimal("2.3");
        image.setProduct(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        Optional<Image> ofResult = Optional.of(image);
        ImageRepository imageRepository = mock(ImageRepository.class);
        doNothing().when(imageRepository).delete(Mockito.<Image>any());
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        ResponseEntity<ApiResponse> actualDeleteImageResult = (new ImageController(new ImageService(imageRepository,
                new ProductService(mock(ProductRepository.class), mock(CategoryRepository.class))))).deleteImage(1L);

        // Assert
        verify(imageRepository).delete(isA(Image.class));
        verify(imageRepository, atLeast(1)).findById(eq(1L));
        ApiResponse body = actualDeleteImageResult.getBody();
        assertEquals("Delete Success !!", body.getMessage());
        assertNull(body.getData());
        assertEquals(200, actualDeleteImageResult.getStatusCodeValue());
        assertTrue(actualDeleteImageResult.hasBody());
        assertTrue(actualDeleteImageResult.getHeaders().isEmpty());
    }


    @Test
    void testDeleteImage2() {


        // Arrange
        Image image = new Image();
        image.setDownloadUrl("https://example.org/example");
        image.setFileName("foo.txt");
        image.setFileType("File Type");
        image.setId(1L);
        image.setImage(mock(Blob.class));
        BigDecimal price = new BigDecimal("2.3");
        image.setProduct(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));
        Optional<Image> ofResult = Optional.of(image);
        ImageRepository imageRepository = mock(ImageRepository.class);
        doThrow(new ResourceNotFoundException("An error occurred")).when(imageRepository).delete(Mockito.<Image>any());
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        ResponseEntity<ApiResponse> actualDeleteImageResult = (new ImageController(new ImageService(imageRepository,
                new ProductService(mock(ProductRepository.class), mock(CategoryRepository.class))))).deleteImage(1L);

        // Assert
        verify(imageRepository).delete(isA(Image.class));
        verify(imageRepository, atLeast(1)).findById(eq(1L));
        ApiResponse body = actualDeleteImageResult.getBody();
        assertEquals("An error occurred", body.getMessage());
        assertNull(body.getData());
        assertEquals(404, actualDeleteImageResult.getStatusCodeValue());
        assertTrue(actualDeleteImageResult.hasBody());
        assertTrue(actualDeleteImageResult.getHeaders().isEmpty());
    }


    @Test
    void testDeleteImage3() {


        // Arrange
        ImageRepository imageRepository = mock(ImageRepository.class);
        Optional<Image> emptyResult = Optional.empty();
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act
        ResponseEntity<ApiResponse> actualDeleteImageResult = (new ImageController(new ImageService(imageRepository,
                new ProductService(mock(ProductRepository.class), mock(CategoryRepository.class))))).deleteImage(1L);

        // Assert
        verify(imageRepository).findById(eq(1L));
        ApiResponse body = actualDeleteImageResult.getBody();
        assertEquals("No image found for id 1", body.getMessage());
        assertNull(body.getData());
        assertEquals(404, actualDeleteImageResult.getStatusCodeValue());
        assertTrue(actualDeleteImageResult.hasBody());
        assertTrue(actualDeleteImageResult.getHeaders().isEmpty());
    }

}
