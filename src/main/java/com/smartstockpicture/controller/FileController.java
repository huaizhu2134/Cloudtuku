package com.smartstockpicture.controller;

import cn.hutool.core.io.FileUtil;
import com.smartstockpicture.common.BaseResponse;
import com.smartstockpicture.common.ErrorCode;
import com.smartstockpicture.common.ResultUtils;
import com.smartstockpicture.config.FileStorageConfig;
import com.smartstockpicture.exception.BusinessException;
import com.smartstockpicture.manager.OssManager;
import com.smartstockpicture.model.dto.file.UploadFileRequest;
import com.smartstockpicture.model.entity.User;
import com.smartstockpicture.model.enums.FileUploadBizEnum;
import com.smartstockpicture.service.UserService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private OssManager ossManager;

    @Resource
    private FileStorageConfig fileStorageConfig;

    /**
     * 文件上传
     *
     * @param multipartFile 上传文件
     * @param uploadFileRequest 上传业务参数
     * @param request 当前请求
     * @return 文件访问地址
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
            UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String objectName = String.format("%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        byte[] fileBytes;
        try {
            fileBytes = multipartFile.getBytes();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }
        File file = null;
        try {
            // 上传文件
            String suffix = StringUtils.defaultIfBlank(FileUtil.getSuffix(multipartFile.getOriginalFilename()), "tmp");
            file = File.createTempFile("upload-", "." + suffix);
            Files.write(file.toPath(), fileBytes);
            ossManager.putObject(objectName, file);
            // 返回可访问地址
            return ResultUtils.success(ossManager.getObjectUrl(objectName));
        } catch (Exception e) {
            log.warn("oss upload failed, fallback to local storage, objectName = {}", objectName, e);
            try {
                saveToLocalStorage(fileBytes, objectName);
                return ResultUtils.success(buildLocalFileUrl(objectName));
            } catch (Exception localException) {
                log.error("local file save error, objectName = {}", objectName, localException);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
            }
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, objectName = {}", objectName);
                }
            }
        }
    }

    private void saveToLocalStorage(byte[] fileBytes, String objectName) throws IOException {
        Path localFilePath = Paths.get(fileStorageConfig.getLocalPath()).resolve(objectName);
        Path parent = localFilePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(localFilePath, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    private String buildLocalFileUrl(String objectName) {
        String requestPath = fileStorageConfig.getRequestPath();
        String normalizedPath = requestPath.startsWith("/") ? requestPath : "/" + requestPath;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(normalizedPath)
                .path("/")
                .path(objectName)
                .toUriString();
    }

    /**
     * 校验文件
     *
     * @param multipartFile 上传文件
     * @param fileUploadBizEnum 业务类型
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
