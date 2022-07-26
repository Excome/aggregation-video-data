package com.excome.aggregationVideoData.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoCameraTokenData implements Serializable {
    private String value;
    private Integer ttl;
}
