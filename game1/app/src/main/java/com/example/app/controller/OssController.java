package com.example.app.controller;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.example.app.domain.OssConfigVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@RestController
public class OssController {

    @RequestMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空，请重新上传";
        }
        if (!file.getContentType().startsWith("image") &&
        !file.getContentType().startsWith("video") &&
        !file.getContentType().startsWith("application")) {

            return "文件格式不正确，请重新上传";
        }


        // 生成唯一的文件名
        String originalFilename = file.getOriginalFilename(); // 获取原始文件名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 获取文件扩展名
        String fileName = "";

        // 生成日期路径
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String datePath = sdf.format(new Date());


        // 生成文件名
        String uniqueName = UUID.randomUUID().toString().replace("-", "");

        if (file.getContentType().startsWith("image")){
            try {
                BufferedImage image = ImageIO.read(file.getInputStream());
                int width = image.getWidth();
                int height = image.getHeight();
                fileName = "image/" + datePath + "/" + uniqueName + "_" + width + "x" + height + fileExtension;
            } catch (IOException e) {
                e.printStackTrace();
                return "读取图片尺寸失败";
            }
        }
        if (file.getContentType().startsWith("video")){
            fileName = "video/" + datePath + "/" + uniqueName + fileExtension;
        }
        if (file.getContentType().startsWith("application")){
            fileName = "file/" + datePath + "/" + uniqueName + fileExtension;
        }

        OssConfigVO ossConfigVO = new OssConfigVO();
ossConfigVO.setEndpoint(System.getenv("ALIBABA_CLOUD_ENDPOINT"));
ossConfigVO.setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"));
ossConfigVO.setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
ossConfigVO.setBucketName("Name");


        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(ossConfigVO.getEndpoint(), ossConfigVO.getAccessKeyId(), ossConfigVO.getAccessKeySecret());

        try {
            // 上传文件到OSS
            PutObjectResult result = ossClient.putObject(ossConfigVO.getBucketName(), fileName, file.getInputStream());

            // 返回文件的访问地址
            String fileUrl = "https://" + ossConfigVO.getBucketName() + "." + ossConfigVO.getEndpoint() + "/" + fileName;
            return "文件上传成功，访问地址：" + fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败";
        } finally {
            ossClient.shutdown();
        }
    }
}