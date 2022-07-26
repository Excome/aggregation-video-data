package com.excome.aggregationVideoData.controller;

import com.excome.aggregationVideoData.dto.VideoCamera;
import com.excome.aggregationVideoData.service.VideoCameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VideoCameraController {

    private final VideoCameraService cameraService;

    @Autowired
    public VideoCameraController(VideoCameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping("/available-cameras")
    public ResponseEntity<List<VideoCamera>> getAvailableVideCamerasAsync(){
        List<VideoCamera> availableCamera = cameraService.getAvailableCameraAsync();
        if (!availableCamera.isEmpty()) {
            return new ResponseEntity(availableCamera, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/available-cameras-sync")
    public ResponseEntity<List<VideoCamera>> getAvailableVideoCamerasSync(){
        List<VideoCamera> availableCamera = cameraService.getAvailableCameraSync();

        if (!availableCamera.isEmpty()) {
            return new ResponseEntity(availableCamera, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}
