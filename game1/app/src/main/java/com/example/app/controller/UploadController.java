package com.example.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping()
public class UploadController {
    @Value("${file.path}")
    private String dirPath;

    @RequestMapping(value = "upload")
    public Map<String, String> upload(MultipartFile file) throws IOException {
        if(!dirPath.endsWith(File.separator)) {
            dirPath += File.separator;
        }

        String prefix = file.getOriginalFilename(). substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = UUID.randomUUID() + prefix;

        // 完整的本地保存路径
        String localPath = dirPath + fileName;

        // 保存文件到本地
        Files.copy(
                file.getInputStream(),
                Paths.get(localPath),
                StandardCopyOption.REPLACE_EXISTING
        );

        // 生成访问URL
        String accessUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")  // 需要与资源映射路径一致
                .path(fileName)
                .toUriString();

        return Map.of(
                "localPath", localPath,
                "accessUrl", accessUrl,
                "message", "文件保存位置: " + localPath
        );
    }


}
