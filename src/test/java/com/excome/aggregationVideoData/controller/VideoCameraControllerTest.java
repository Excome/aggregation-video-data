package com.excome.aggregationVideoData.controller;

import com.excome.aggregationVideoData.dto.VideoCamera;
import com.excome.aggregationVideoData.service.VideoCameraService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoCameraController.class)
class VideoCameraControllerTest {
    private static final Long CAMERA_ID_1 = 10L;
    private static final Long CAMERA_ID_2 = 20L;
    private static final String URL_TYPE_LIVE = "LIVE";
    private static final String URL_TYPE_ARCHIVE = "ARCHIVE";
    private static final String VIDEO_URL = "http://www.mocky.io/1/video";
    private static final String TOKEN_VALUE = "fa4b588e-249b-11e9-ab14-d663bd873d93";
    private static final Integer TTL = 120;
    private static final String RESPONSE_JSON = "[{\"id\":10,\"urlType\":\"LIVE\",\"videoUrl\":\"http://www.mocky.io/1/video\",\"value\":\"fa4b588e-249b-11e9-ab14-d663bd873d93\",\"ttl\":120},{\"id\":20,\"urlType\":\"ARCHIVE\",\"videoUrl\":\"http://www.mocky.io/1/video\",\"value\":\"fa4b588e-249b-11e9-ab14-d663bd873d93\",\"ttl\":120}]";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoCameraService cameraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        VideoCamera camera1 = new VideoCamera(CAMERA_ID_1, URL_TYPE_LIVE, VIDEO_URL, TOKEN_VALUE, TTL);
        VideoCamera camera2 = new VideoCamera(CAMERA_ID_2, URL_TYPE_ARCHIVE, VIDEO_URL, TOKEN_VALUE, TTL);
        when(cameraService.getAvailableCameraAsync()).thenReturn(List.of(camera1, camera2));
        when(cameraService.getAvailableCameraSync()).thenReturn(List.of(camera1, camera2));
    }

    @Test
    void givenRequest_whenGetAvailableCamera_thenStatus200_andCorrectJson() throws Exception {
        mockMvc.perform(get("/available-cameras"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(RESPONSE_JSON));
    }

    @Test
    void givenRequest_whenGetAvailableCameraSync_thenStatus200_andCorrectJson() throws Exception {
        mockMvc.perform(get("/available-cameras-sync"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(RESPONSE_JSON));
    }
}