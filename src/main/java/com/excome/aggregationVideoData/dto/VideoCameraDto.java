package com.excome.aggregationVideoData.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCameraDto implements Serializable {
    private Long id;
    private String sourceDataUrl;
    private String tokenDataUrl;
}
