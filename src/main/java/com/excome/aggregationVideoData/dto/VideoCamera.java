package com.excome.aggregationVideoData.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCamera implements Serializable {
    private static final Logger LOGGER = LogManager.getLogger(VideoCamera.class);

    private Long id;
    private String urlType;
    private String videoUrl;
    /** Secure token */
    private String value;
    private Integer ttl;

    public VideoCamera(Long id){
        this.id = id;
    }

    public boolean updateSourceData(VideoCameraSourceData sourceData){
        if (sourceData != null) {
            this.urlType = sourceData.getUrlType();
            this.videoUrl = sourceData.getVideoUrl();

            LOGGER.info("VideCamera '{}': source data updated successfully '{}'", this.id, sourceData);
            return true;
        } else{
            LOGGER.warn("VideCamera '{}': failed to update source data.", this.id);
            return false;
        }
    }

    public boolean updateTokenData(VideoCameraTokenData tokenData){
        if (tokenData != null) {
            this.value = tokenData.getValue();
            this.ttl = tokenData.getTtl();

            LOGGER.info("VideCamera '{}': token data updated successfully '{}'", this.id, tokenData);
            return true;
        } else{
            LOGGER.warn("VideCamera '{}': failed to update token data.", this.id);
            return false;
        }
    }
}
