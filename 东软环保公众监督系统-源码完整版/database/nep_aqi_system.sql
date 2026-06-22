CREATE DATABASE IF NOT EXISTS nep_aqi_system DEFAULT CHARSET utf8mb4;
USE nep_aqi_system;

DROP TABLE IF EXISTS aqi_measurement;
DROP TABLE IF EXISTS aqi_feedback;
DROP TABLE IF EXISTS public_supervisor;
DROP TABLE IF EXISTS grid_worker;
DROP TABLE IF EXISTS system_admin;
DROP TABLE IF EXISTS decision_user;
DROP TABLE IF EXISTS grid_area;

CREATE TABLE public_supervisor (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
  password VARCHAR(100) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  age INT COMMENT '年龄',
  gender VARCHAR(10) COMMENT '性别',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='NEPS公众监督员表';

CREATE TABLE grid_worker (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  worker_code VARCHAR(50) NOT NULL UNIQUE COMMENT '网格员登录编号',
  password VARCHAR(100) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名',
  province VARCHAR(50) COMMENT '所属省份',
  city VARCHAR(50) COMMENT '所属城市',
  work_status VARCHAR(20) DEFAULT '可工作' COMMENT '工作状态'
) COMMENT='NEPG检测网格员表';

CREATE TABLE system_admin (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  admin_code VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员登录编号',
  password VARCHAR(100) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名'
) COMMENT='NEPM系统管理员表';

CREATE TABLE decision_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  decision_code VARCHAR(50) NOT NULL UNIQUE COMMENT '决策者登录编号',
  password VARCHAR(100) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名'
) COMMENT='NEPV决策者表';

CREATE TABLE grid_area (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  province VARCHAR(50) NOT NULL COMMENT '省份',
  city VARCHAR(50) NOT NULL COMMENT '城市网格',
  enabled TINYINT DEFAULT 1 COMMENT '是否启用'
) COMMENT='网格区域表';

CREATE TABLE aqi_feedback (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  feedback_no VARCHAR(50) NOT NULL UNIQUE COMMENT '反馈编号',
  supervisor_id BIGINT COMMENT '公众监督员ID',
  province VARCHAR(50) NOT NULL COMMENT '省份',
  city VARCHAR(50) NOT NULL COMMENT '城市',
  address VARCHAR(255) NOT NULL COMMENT '具体观测地址',
  estimated_aqi_level VARCHAR(20) NOT NULL COMMENT '公众预估AQI等级',
  description TEXT COMMENT '环境状况描述',
  status VARCHAR(20) DEFAULT '待指派' COMMENT '待指派、已指派、已完成',
  assigned_worker_id BIGINT COMMENT '指派网格员ID',
  assign_type VARCHAR(20) COMMENT '本地指派或异地指派',
  assign_time DATETIME COMMENT '指派时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='空气质量公众监督反馈表';

CREATE TABLE aqi_measurement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  feedback_id BIGINT NOT NULL COMMENT '反馈信息ID',
  worker_id BIGINT NOT NULL COMMENT '网格员ID',
  feedback_no VARCHAR(50) COMMENT '反馈编号',
  so2_level VARCHAR(20) NOT NULL COMMENT 'SO2二氧化硫AQI等级',
  co_level VARCHAR(20) NOT NULL COMMENT 'CO一氧化碳AQI等级',
  pm25_level VARCHAR(20) NOT NULL COMMENT 'PM2.5悬浮颗粒物AQI等级',
  final_aqi_level VARCHAR(20) NOT NULL COMMENT '最终AQI等级',
  measure_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='网格员实测AQI表';

INSERT INTO public_supervisor(phone,password,real_name,age,gender)
VALUES ('13924689016','123456','林子涵',22,'女');

INSERT INTO grid_worker(worker_code,password,real_name,province,city,work_status) VALUES
('grid001','123456','张网格员','辽宁省','沈阳市','可工作'),
('grid002','123456','李网格员','辽宁省','大连市','可工作'),
('grid003','123456','陈网格员','广东省','深圳市','可工作'),
('grid004','123456','赵网格员','广东省','广州市','支援'),
('grid005','123456','刘网格员','四川省','成都市','可工作');

INSERT INTO system_admin(admin_code,password,real_name)
VALUES ('admin001','123456','系统管理员');

INSERT INTO decision_user(decision_code,password,real_name)
VALUES ('decision001','123456','决策者');

INSERT INTO grid_area(province,city) VALUES
('辽宁省','沈阳市'),('辽宁省','大连市'),('辽宁省','鞍山市'),('辽宁省','抚顺市'),
('北京市','北京市'),('上海市','上海市'),
('广东省','广州市'),('广东省','深圳市'),('广东省','珠海市'),
('四川省','成都市'),('四川省','绵阳市');

INSERT INTO aqi_feedback(feedback_no,supervisor_id,province,city,address,estimated_aqi_level,description,status,assigned_worker_id,assign_type)
VALUES
('NEP20260616001',1,'辽宁省','沈阳市','浑南区高新路2号','轻度污染','能见度一般，有轻微异味。','已指派',1,'本地指派'),
('NEP20260615002',1,'辽宁省','大连市','中山区人民路附近','良','空气状况较好。','已完成',2,'本地指派'),
('NEP20260614003',1,'广东省','深圳市','南山区科技园北区','中度污染','有扬尘，午后气味明显。','待指派',NULL,NULL);

INSERT INTO aqi_measurement(feedback_id,worker_id,feedback_no,so2_level,co_level,pm25_level,final_aqi_level)
VALUES (2,2,'NEP20260615002','良','优','良','良');
