package com.appointment.management.presentation.controller;

import com.appointment.management.domain.service.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ImageUploadControllerTest {

    @InjectMocks
    private ImageUploadController imageUploadController;

    @Mock
    private CloudinaryService cloudinaryService;

    private MockMultipartFile mockFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given Global
        mockFile = new MockMultipartFile("file", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());
    }

    @Test
    public void uploadImage_ShouldReturnUrl_WhenUploadIsSuccessful() throws IOException {
        // Given
        String expectedUrl = "http://example.com/image.jpg";
        when(cloudinaryService.uploadImage(mockFile)).thenReturn(expectedUrl);

        // When
        ResponseEntity<?> response = imageUploadController.uploadImage(mockFile);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals(expectedUrl, responseBody.get("url"));
        verify(cloudinaryService, times(1)).uploadImage(mockFile);
    }

    @Test
    public void uploadImage_ShouldReturnErrorMessage_WhenUploadFails() throws IOException {
        // Given
        when(cloudinaryService.uploadImage(mockFile)).thenThrow(new IOException("Upload failed"));

        // When
        ResponseEntity<?> response = imageUploadController.uploadImage(mockFile);

        // Then
        assertEquals(500, response.getStatusCodeValue());
        String responseBody = (String) response.getBody();
        assertEquals("Error uploading image: Upload failed", responseBody);
        verify(cloudinaryService, times(1)).uploadImage(mockFile);
    }

}