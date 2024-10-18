package com.appointment.management.domain.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void shouldUploadImageAndReturnUrlSuccessfully() throws IOException {
        // Arrange
        MultipartFile mockFile = new MockMultipartFile(
                "file",                    // nombre del archivo
                "test-image.png",           // nombre original
                "image/png",                // tipo de archivo
                "test image content".getBytes()  // contenido del archivo
        );

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://cloudinary.com/test-image-url");

        when(uploader.upload(any(byte[].class), eq(ObjectUtils.emptyMap())))
                .thenReturn(uploadResult);

        String resultUrl = cloudinaryService.uploadImage(mockFile);

        assertNotNull(resultUrl);
        assertEquals("http://cloudinary.com/test-image-url", resultUrl);

        verify(uploader, times(1))
                .upload(any(byte[].class), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void shouldThrowIOExceptionWhenUploadFails() throws IOException {
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "test image content".getBytes()
        );

        when(uploader.upload(any(byte[].class), eq(ObjectUtils.emptyMap())))
                .thenThrow(new IOException("Cloudinary upload failed"));

        assertThrows(IOException.class, () -> cloudinaryService.uploadImage(mockFile));

        verify(uploader, times(1))
                .upload(any(byte[].class), eq(ObjectUtils.emptyMap()));
    }
}
