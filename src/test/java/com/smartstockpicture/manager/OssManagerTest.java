package com.smartstockpicture.manager;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 阿里云 OSS 操作测试
 */
@SpringBootTest
class OssManagerTest {

    @Resource
    private OssManager ossManager;

    @Test
    void putObject() {
        // todo 需要准备测试文件
        // ossManager.putObject("test/test.json", "本地文件路径");
    }
}
