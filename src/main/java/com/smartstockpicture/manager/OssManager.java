package com.smartstockpicture.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.smartstockpicture.config.OssClientConfig;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.io.File;
import java.io.InputStream;

/**
 * 阿里云 OSS 对象存储操作
 */
@Component
@SuppressWarnings("unused")
public class OssManager {

    @Resource
    private OssClientConfig ossClientConfig;

    @Resource
    private OSS ossClient;

    /**
     * 上传对象（本地文件）
     *
     * @param objectName 对象名称（OSS 中的路径）
     * @param localFilePath 本地文件路径
     * @return PutObjectResult
     */
    public PutObjectResult putObject(String objectName, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossClientConfig.getBucketName(),
                objectName,
                new File(localFilePath)
        );
        return ossClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象（File 对象）
     *
     * @param objectName 对象名称（OSS 中的路径）
     * @param file 文件对象
     * @return PutObjectResult
     */
    public PutObjectResult putObject(String objectName, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossClientConfig.getBucketName(),
                objectName,
                file
        );
        return ossClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象（输入流）
     *
     * @param objectName 对象名称（OSS 中的路径）
     * @param inputStream 输入流
     * @return PutObjectResult
     */
    public PutObjectResult putObject(String objectName, InputStream inputStream) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossClientConfig.getBucketName(),
                objectName,
                inputStream
        );
        return ossClient.putObject(putObjectRequest);
    }

    /**
     * 删除对象
     *
     * @param objectName 对象名称（OSS 中的路径）
     */
    public void deleteObject(String objectName) {
        ossClient.deleteObject(ossClientConfig.getBucketName(), objectName);
    }

    /**
     * 获取对象的访问 URL
     *
     * @param objectName 对象名称（OSS 中的路径）
     * @return 访问 URL
     */
    public String getObjectUrl(String objectName) {
        return String.format("https://%s.%s/%s",
                ossClientConfig.getBucketName(),
                normalizeEndpoint(ossClientConfig.getEndpoint()),
                normalizeObjectName(objectName));
    }

    private String normalizeEndpoint(String endpoint) {
        if (endpoint == null) {
            return "";
        }
        String normalizedEndpoint = endpoint.trim();
        if (normalizedEndpoint.startsWith("https://")) {
            normalizedEndpoint = normalizedEndpoint.substring("https://".length());
        } else if (normalizedEndpoint.startsWith("http://")) {
            normalizedEndpoint = normalizedEndpoint.substring("http://".length());
        }
        return normalizedEndpoint.endsWith("/") ? normalizedEndpoint.substring(0, normalizedEndpoint.length() - 1)
                : normalizedEndpoint;
    }

    private String normalizeObjectName(String objectName) {
        if (objectName == null) {
            return "";
        }
        return objectName.startsWith("/") ? objectName.substring(1) : objectName;
    }
}
