# 东软环保公众监督系统

本项目是前后端分离工程，正式验收运行 `frontend + backend + database`，不再提供容易混淆的旧静态演示入口。

## 源码结构

```text
frontend/src
├─ components    登录门户、工作区、统计卡片、AQI标签
└─ views         NEPS、NEPG、NEPM、NEPV 四端业务页面

backend/src/main/java/com/neusoft/nep
├─ entity        7张数据库表对应的Java实体类
├─ mapper        MyBatis-Plus数据库访问接口
├─ dto           登录、反馈、指派、实测请求对象
├─ service       登录、业务流转、AQI计算、统计服务
├─ controller    RESTful接口
└─ exception     统一异常处理
```

## 正式运行顺序

1. 用 MySQL Workbench 或 Navicat 运行 `database/nep_aqi_system.sql`。
2. 用 IntelliJ IDEA 打开 `backend`，运行 `NepAqiApplication.java`。
3. 用 VS Code 打开 `frontend`，在终端运行 `npm.cmd install` 和 `npm.cmd run dev`。
4. 浏览器打开终端显示的网址，一般是 `http://localhost:5173`。

## MySQL配置

配置文件：`backend/src/main/resources/application.yml`

```text
用户名：root
密码：123456
数据库：nep_aqi_system
```

如果本机MySQL密码不同，只修改 `application.yml` 中的 `password`。

## 测试账号

```text
NEPS：13924689016 / 123456
NEPG：grid001 / 123456
NEPM：admin001 / 123456
NEPV：decision001 / 123456
```

四种角色均可登录。只有 NEPS 公众监督员允许自主注册；NEPG、NEPM、NEPV 账号由数据库或管理员统一创建。

## 答辩说明

系统使用 Vue3、Axios、Element-Plus 构建前端，Spring Boot、MyBatis-Plus 提供 RESTful API，MySQL 持久化业务数据。完整业务链路为：公众提交反馈 -> 管理员指派网格员 -> 网格员录入 SO2、CO、PM2.5 -> 系统按最高等级计算最终AQI -> 决策端统计分析。
