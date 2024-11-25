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

    //variables globales para given global
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Given global
         mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "test image content".getBytes()
        );

        //When global
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void shouldUploadImageAndReturnUrlSuccessfully() throws IOException {
        // Given
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("url", "http://cloudinary.com/test-image-url");

        //Whe
        when(uploader.upload(any(byte[].class), eq(ObjectUtils.emptyMap())))
                .thenReturn(uploadResult);

        //Llamando al metodo a testear
        String resultUrl = cloudinaryService.uploadImage(mockFile);

        //Then
        assertNotNull(resultUrl);
        assertEquals("http://cloudinary.com/test-image-url", resultUrl);
        verify(uploader, times(1))
                .upload(any(byte[].class), eq(ObjectUtils.emptyMap()));
    }

    @Test
    void shouldThrowIOExceptionWhenUploadFails() throws IOException {
        //When
        when(uploader.upload(any(byte[].class), eq(ObjectUtils.emptyMap())))
                .thenThrow(new IOException("Cloudinary upload failed"));
        //Then
        assertThrows(IOException.class, () -> cloudinaryService.uploadImage(mockFile));
        verify(uploader, times(1))
                .upload(any(byte[].class), eq(ObjectUtils.emptyMap()));
    }
}
