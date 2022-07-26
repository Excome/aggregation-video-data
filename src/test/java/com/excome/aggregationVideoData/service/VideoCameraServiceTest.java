package com.excome.aggregationVideoData.service;

import com.excome.aggregationVideoData.dto.VideoCamera;
import com.excome.aggregationVideoData.dto.VideoCameraDto;
import com.excome.aggregationVideoData.dto.VideoCameraSourceData;
import com.excome.aggregationVideoData.dto.VideoCameraTokenData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class VideoCameraServiceTest {

    private static final String VIDEO_CAMERAS_DESTINATION = "http://www.mocky.io/v2/5c51b9dd3400003252129fb5";
    private static final Long CAMERA_ID_1 = 10L;
    private static final Long CAMERA_ID_2 = 20L;
    private static final String SOURCE_URL_1 = "http://www.mocky.io/source1";
    private static final String SOURCE_URL_2 = "http://www.mocky.io/source2";
    private static final String TOKEN_URL = "http://www.mocky.io/token1";
    private static final String URL_TYPE_LIVE = "LIVE";
    private static final String URL_TYPE_ARCHIVE = "ARCHIVE";
    private static final String VIDEO_URL = "http://www.mocky.io/1/video";
    private static final String TOKEN_VALUE = "fa4b588e-249b-11e9-ab14-d663bd873d93";
    private static final Integer TTL = 120;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        VideoCameraDto cameraDto1 = new VideoCameraDto(CAMERA_ID_1, SOURCE_URL_1, TOKEN_URL);
        VideoCameraDto cameraDto2 = new VideoCameraDto(CAMERA_ID_2, SOURCE_URL_2, TOKEN_URL);
        ResponseEntity<VideoCameraDto[]> cameraDtoEntity = new ResponseEntity<VideoCameraDto[]>(new VideoCameraDto[]{cameraDto1, cameraDto2}, HttpStatus.OK);

        when(restTemplate.getForEntity(VIDEO_CAMERAS_DESTINATION, VideoCameraDto[].class)).thenReturn(cameraDtoEntity);
        when(restTemplate.getForObject(SOURCE_URL_1, VideoCameraSourceData.class)).thenAnswer((Answer<VideoCameraSourceData>) invocation -> {
            Thread.sleep(100);
            return new VideoCameraSourceData(URL_TYPE_LIVE, VIDEO_URL);
        });
        when(restTemplate.getForObject(SOURCE_URL_2, VideoCameraSourceData.class)).thenAnswer((Answer<VideoCameraSourceData>) invocation -> {
            Thread.sleep(150);
            return new VideoCameraSourceData(URL_TYPE_ARCHIVE, VIDEO_URL);
        });
        when(restTemplate.getForObject(TOKEN_URL, VideoCameraTokenData.class)).thenReturn(new VideoCameraTokenData(TOKEN_VALUE, TTL));
    }

    @Test
    void whenAvailableCameraAsyncExecuted_thenCorrectValueReturned() {
        VideoCameraService cameraService = new VideoCameraService(restTemplate);
        long startTime = System.currentTimeMillis();
        List<VideoCamera> cameras = cameraService.getAvailableCameraAsync();
        long endTime = System.currentTimeMillis();

        assertEquals(2, cameras.size());
        assertTrue(endTime - startTime < 250);
    }

    @Test
    void whenAvailableCameraSyncExecuted_thenCorrectValueReturned() {
        VideoCameraService cameraService = new VideoCameraService(restTemplate);
        long startTime = System.currentTimeMillis();
        List<VideoCamera> cameras = cameraService.getAvailableCameraSync();
        long endTime = System.currentTimeMillis();

        assertEquals(2, cameras.size());
        assertTrue(endTime - startTime >= 250);
    }
}