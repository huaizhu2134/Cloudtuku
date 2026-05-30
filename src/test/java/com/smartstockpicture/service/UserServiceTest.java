package com.smartstockpicture.service;

import com.smartstockpicture.common.ErrorCode;
import com.smartstockpicture.exception.BusinessException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务测试
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        try {
            userService.userRegister(userAccount, userPassword, checkPassword);
            Assertions.fail("应该抛出异常");
        } catch (BusinessException e) {
            Assertions.assertEquals(ErrorCode.PARAMS_ERROR.getCode(), e.getCode());
        }
    }
}
