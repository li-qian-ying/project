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
2. 双击根目录的 `运行后端.bat`；也可以用 IntelliJ IDEA 单独打开 `backend`，运行 `NepAqiApplication.java`。
3. 用 VS Code 打开 `frontend`，在终端运行 `npm.cmd install` 和 `npm.cmd run dev`。
4. 浏览器打开终端显示的网址，一般是 `http://localhost:5173`。

## MySQL配置

配置文件：`backend/src/main/resources/application.yml`

```text
用户名：nep_app
密码：NepAqi@2026
数据库：nep_aqi_system
```

数据库脚本会自动创建后端专用账号，前端注册手机号和密码不要填写到 `application.yml`。

## 测试账号

```text
NEPS：13924689016 / 123456
NEPG：grid001 / 123456
NEPM：admin001 / 123456
NEPV：decision001 / 123456
```

四种角色均可登录。只有 NEPS 公众监督员允许自主注册；NEPG、NEPM、NEPV 账号由数据库或管理员统一创建。

## 高分增强内容

- NEPS：公众首页、业务动态、AQI指引、反馈提交、进度追踪和个人中心。
- NEPG：任务队列、班次与SLA、巡检路线、三项污染物录入、AQI自动计算和工作负载。
- NEPM：运营看板、AQI构成、区域热度、处置趋势、条件筛选、智能指派、人员负载和网格覆盖。
- NEPV：决策驾驶舱、区域风险、治理指标、区域对比和自动生成的决策简报。
- 组员意见已落实：公众历史记录组合查询、统一反馈详情弹窗、网格员任务列表去除“选择录入”、NEPM独立多维统计页、NEPV地图/柱状/饼状/折线分析。
- 数据库包含跨辽宁、广东、北京、上海、四川的演示数据，重新执行SQL后即可展示完整统计效果。

## 答辩说明

系统使用 Vue3、Axios、Element-Plus 构建前端，Spring Boot、MyBatis-Plus 提供 RESTful API，MySQL 持久化业务数据。完整业务链路为：公众提交反馈 -> 管理员指派网格员 -> 网格员录入 SO2、CO、PM2.5 -> 系统按最高等级计算最终AQI -> 决策端统计分析。
