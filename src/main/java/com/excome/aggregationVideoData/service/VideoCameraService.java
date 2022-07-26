package com.excome.aggregationVideoData.service;

import com.excome.aggregationVideoData.dto.VideoCamera;
import com.excome.aggregationVideoData.dto.VideoCameraDto;
import com.excome.aggregationVideoData.dto.VideoCameraSourceData;
import com.excome.aggregationVideoData.dto.VideoCameraTokenData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class VideoCameraService {
    private static final Logger LOGGER = LogManager.getLogger(VideoCameraService.class);
    private static String VIDEO_CAMERAS_DESTINATION = "http://www.mocky.io/v2/5c51b9dd3400003252129fb5";

    private RestTemplate restTemplate;

    @Autowired
    public VideoCameraService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<VideoCamera> getAvailableCameraAsync(){
        ResponseEntity<VideoCameraDto[]> response = restTemplate.getForEntity(VIDEO_CAMERAS_DESTINATION, VideoCameraDto[].class);
        VideoCameraDto[] responseBody = response.getBody();
        if (responseBody != null) {
            try {
                List<VideoCameraDto> cameraDtoList = List.of(responseBody);
                ConcurrentHashMap<String, VideoCamera> cameraMap = new ConcurrentHashMap<>();
                for (var cameraDto : cameraDtoList) {
                    cameraMap.put(cameraDto.getId().toString(), new VideoCamera(cameraDto.getId()));
                }

                List<CompletableFuture<Void>> futures = cameraDtoList.stream()
                        .map(dto -> CompletableFuture.supplyAsync(() -> restTemplate.getForObject(dto.getSourceDataUrl(), VideoCameraSourceData.class))
                                .thenAccept(sourceData -> {
                                    VideoCamera videoCamera = cameraMap.get(dto.getId().toString());
                                    videoCamera.updateSourceData(sourceData);
                                    cameraMap.replace(dto.getId().toString(), videoCamera);
                                }))
                        .collect(Collectors.toList());

                futures.addAll(cameraDtoList.stream()
                        .map(dto -> CompletableFuture.supplyAsync(() -> restTemplate.getForObject(dto.getTokenDataUrl(), VideoCameraTokenData.class))
                                .thenAccept(tokenData -> {
                                    VideoCamera videoCamera = cameraMap.get(dto.getId().toString());
                                    videoCamera.updateTokenData(tokenData);
                                    cameraMap.replace(dto.getId().toString(), videoCamera);
                                }))
                        .collect(Collectors.toList()));

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

                return cameraMap.values().stream().toList();
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException in thread '{}'", Thread.currentThread().getName(), e);
                return List.of();
            } catch (ExecutionException e) {
                LOGGER.error("Failed retrieve data of available video cameras: {}", e.getMessage(), e);
                return List.of();
            }
        } else {
            LOGGER.error("Video cameras destination is currently unavailable");
            return List.of();
        }
    }

    public List<VideoCamera> getAvailableCameraSync(){
        ResponseEntity<VideoCameraDto[]> response = restTemplate.getForEntity(VIDEO_CAMERAS_DESTINATION, VideoCameraDto[].class);
        List<VideoCameraDto> cameraDtoList = List.of(response.getBody());
        ArrayList<VideoCamera> cameras = new ArrayList<>();
        if (!cameraDtoList.isEmpty()) {
            for (VideoCameraDto dto : cameraDtoList) {
                VideoCameraSourceData sourceData = restTemplate.getForObject(dto.getSourceDataUrl(), VideoCameraSourceData.class);
                VideoCameraTokenData tokenData = restTemplate.getForObject(dto.getTokenDataUrl(), VideoCameraTokenData.class);
                VideoCamera videoCamera = new VideoCamera(dto.getId());
                videoCamera.updateSourceData(sourceData);
                videoCamera.updateTokenData(tokenData);
                cameras.add(videoCamera);
            }
        } else {
            LOGGER.warn("Unable to receive available cameras from destination '{}'. The count of cameras received is zero", VIDEO_CAMERAS_DESTINATION);
        }

        return cameras;

    }
}
