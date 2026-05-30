# SmartStockPicture

基于 Java Spring Boot 3.1 + JDK 17 的项目模板，整合了主流框架和基础工具类。

## 技术栈

### 框架
- Spring Boot 3.1.x（Java 17）
- Spring MVC / AOP
- MyBatis + MyBatis Plus（分页）
- Spring Scheduler 定时任务

### 数据存储
- MySQL
- Redis
- Elasticsearch
- 阿里云 OSS 对象存储

### 工具库
- Easy Excel 表格处理
- Hutool 工具库
- Lombok 注解
- Apache Commons Lang3

### 通用能力
- 代码生成器（FreeMarker 模板，一键生成 Controller/Service/Model）
- Spring Session Redis 分布式登录
- 全局异常处理 + 自定义错误码
- 统一响应封装
- Knife4j 接口文档（OpenAPI 3）
- 权限注解 + AOP 校验
- 全局跨域处理
- 多环境配置（dev/test/prod）
- 微信公众平台/开放平台集成
- 文件上传（支持按业务分目录）

## 快速开始

### 1. 数据库配置

修改 `application.yml`：
```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db
    username: root
    password: 123456
```

执行 `sql/create_table.sql` 初始化表结构。

### 2. OSS 配置

修改 `application.yml`：
```yml
aliyun:
  oss:
    access-key-id: xxx
    access-key-secret: xxx
    endpoint: oss-cn-hangzhou.aliyuncs.com
    bucket-name: xxx
```

### 3. 启动项目

启动后访问 `http://localhost:8101/api/doc.html` 查看接口文档。

## 代码生成器

执行 `generate.CodeGenerator`，修改包名和实体参数后运行即可自动生成 Controller、Service、DTO、VO 等代码。

## 项目结构

```
src/main/java/com/smartstockpicture/
├── annotation/      # 自定义注解
├── aop/             # AOP 拦截器
├── common/          # 通用类（响应、错误码、分页）
├── config/          # 配置类
├── constant/        # 常量
├── controller/      # 控制器
├── exception/       # 异常处理
├── generate/        # 代码生成器
├── job/             # 定时任务
├── manager/         # 第三方服务管理
├── mapper/          # MyBatis Mapper
├── model/           # 实体、DTO、VO、枚举
├── service/         # 服务接口与实现
├── utils/           # 工具类
└── wxmp/            # 微信公众号
```
