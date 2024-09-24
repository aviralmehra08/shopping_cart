package com.dailycode.dreamshops.service.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dailycode.dreamshops.dto.ImageDto;
import com.dailycode.dreamshops.exception.ResourceNotFoundException;
import com.dailycode.dreamshops.model.Category;
import com.dailycode.dreamshops.model.Image;
import com.dailycode.dreamshops.model.Product;
import com.dailycode.dreamshops.repository.ImageRepository;
import com.dailycode.dreamshops.service.product.IProductService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {ImageService.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class ImageServiceTest {
    @MockBean
    private IProductService iProductService;

    @MockBean
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;


    @Test
    void testGetImageById() {
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
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        Image actualImageById = imageService.getImageById(1L);

        // Assert
        verify(imageRepository).findById(eq(1L));
        assertSame(image, actualImageById);
    }


    @Test
    void testGetImageById2() {
        // Arrange
        Optional<Image> emptyResult = Optional.empty();
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> imageService.getImageById(1L));
        verify(imageRepository).findById(eq(1L));
    }


    @Test
    void testGetImageById3() {
        // Arrange
        when(imageRepository.findById(Mockito.<Long>any())).thenThrow(new RuntimeException("foo"));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> imageService.getImageById(1L));
        verify(imageRepository).findById(eq(1L));
    }


    @Test
    void testDeleteImageById() {
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
        doNothing().when(imageRepository).delete(Mockito.<Image>any());
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        imageService.deleteImageById(1L);

        // Assert that nothing has changed
        verify(imageRepository).delete(isA(Image.class));
        verify(imageRepository).findById(eq(1L));
    }


    @Test
    void testDeleteImageById2() {
        // Arrange
        Optional<Image> emptyResult = Optional.empty();
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> imageService.deleteImageById(1L));
        verify(imageRepository).findById(eq(1L));
    }


    @Test
    void testSaveImage() {
        // Arrange
        BigDecimal price = new BigDecimal("2.3");
        when(iProductService.getProductById(Mockito.<Long>any())).thenReturn(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));

        // Act
        List<ImageDto> actualSaveImageResult = imageService.saveImage(new ArrayList<>(), 1L);

        // Assert
        verify(iProductService).getProductById(eq(1L));
        assertTrue(actualSaveImageResult.isEmpty());
    }



    @Test
    void testSaveImage2() throws IOException {
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
        when(imageRepository.save(Mockito.<Image>any())).thenReturn(image);
        BigDecimal price2 = new BigDecimal("2.3");
        when(iProductService.getProductById(Mockito.<Long>any())).thenReturn(
                new Product("Name", "Brand", price2, 1, "The characteristics of someone or something", new Category("Name")));

        ArrayList<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Act
        List<ImageDto> actualSaveImageResult = imageService.saveImage(files, 1L);

        // Assert
        verify(iProductService).getProductById(eq(1L));
        verify(imageRepository, atLeast(1)).save(Mockito.<Image>any());
        assertEquals(1, actualSaveImageResult.size());
        ImageDto getResult = actualSaveImageResult.get(0);
        assertEquals("/api/v1/images/image/download/1", getResult.getDownloadUrl());
        assertEquals("foo.txt", getResult.getImageName());
        assertEquals(1L, getResult.getImageId().longValue());
    }


    @Test
    void testSaveImage3() throws IOException {
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
        when(imageRepository.save(Mockito.<Image>any())).thenReturn(image);
        BigDecimal price2 = new BigDecimal("2.3");
        when(iProductService.getProductById(Mockito.<Long>any())).thenReturn(
                new Product("Name", "Brand", price2, 1, "The characteristics of someone or something", new Category("Name")));

        ArrayList<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("/api/v1/images/image/download/",
                new ByteArrayInputStream(new byte[]{'A', 1, 'A', 1, 'A', 1, 'A', 1})));
        files.add(new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Act
        List<ImageDto> actualSaveImageResult = imageService.saveImage(files, 1L);

        // Assert
        verify(iProductService).getProductById(eq(1L));
        verify(imageRepository, atLeast(1)).save(Mockito.<Image>any());
        assertEquals(2, actualSaveImageResult.size());
        ImageDto getResult = actualSaveImageResult.get(0);
        assertEquals("/api/v1/images/image/download/1", getResult.getDownloadUrl());
        ImageDto getResult2 = actualSaveImageResult.get(1);
        assertEquals("/api/v1/images/image/download/1", getResult2.getDownloadUrl());
        assertEquals("foo.txt", getResult.getImageName());
        assertEquals("foo.txt", getResult2.getImageName());
        assertEquals(1L, getResult.getImageId().longValue());
        assertEquals(1L, getResult2.getImageId().longValue());
    }


    @Test
    void testSaveImage4() throws IOException {
        // Arrange
        when(imageRepository.save(Mockito.<Image>any())).thenThrow(new RuntimeException("/api/v1/images/image/download/"));
        BigDecimal price = new BigDecimal("2.3");
        when(iProductService.getProductById(Mockito.<Long>any())).thenReturn(
                new Product("Name", "Brand", price, 1, "The characteristics of someone or something", new Category("Name")));

        ArrayList<MultipartFile> files = new ArrayList<>();
        files.add(new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> imageService.saveImage(files, 1L));
        verify(iProductService).getProductById(eq(1L));
        verify(imageRepository).save(isA(Image.class));
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
        when(imageRepository.save(Mockito.<Image>any())).thenReturn(image2);
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act
        imageService.updateImage(new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), 1L);

        // Assert
        verify(imageRepository).findById(eq(1L));
        verify(imageRepository).save(isA(Image.class));
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
        when(imageRepository.save(Mockito.<Image>any())).thenThrow(new RuntimeException("foo"));
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> imageService
                .updateImage(new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), 1L));
        verify(imageRepository).findById(eq(1L));
        verify(imageRepository).save(isA(Image.class));
    }


    @Test
    void testUpdateImage3() throws IOException {
        // Arrange
        Optional<Image> emptyResult = Optional.empty();
        when(imageRepository.findById(Mockito.<Long>any())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> imageService
                .updateImage(new MockMultipartFile("Name", new ByteArrayInputStream("AXAXAXAX".getBytes("UTF-8"))), 1L));
        verify(imageRepository).findById(eq(1L));
    }
}
