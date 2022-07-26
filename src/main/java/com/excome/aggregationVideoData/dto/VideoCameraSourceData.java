package com.excome.aggregationVideoData.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCameraSourceData implements Serializable {
    private String urlType;
    private String videoUrl;
}
