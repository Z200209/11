package com.example.app.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@Accessors(chain = true)
public class OssConfigVO {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

}