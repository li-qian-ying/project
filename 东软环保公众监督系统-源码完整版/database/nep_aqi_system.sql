CREATE DATABASE IF NOT EXISTS nep_aqi_system
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;
USE nep_aqi_system;

-- 后端专用数据库账号，不使用前端用户手机号，也不依赖本机 root 密码。
CREATE USER IF NOT EXISTS 'nep_app'@'localhost' IDENTIFIED BY 'NepAqi@2026';
ALTER USER 'nep_app'@'localhost' IDENTIFIED BY 'NepAqi@2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON nep_aqi_system.* TO 'nep_app'@'localhost';
FLUSH PRIVILEGES;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='NEPS公众监督员表';

CREATE TABLE grid_worker (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  worker_code VARCHAR(50) NOT NULL UNIQUE COMMENT '网格员登录编号',
  password VARCHAR(100) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名',
  province VARCHAR(50) COMMENT '所属省份',
  city VARCHAR(50) COMMENT '所属城市',
  work_status VARCHAR(20) DEFAULT '可工作' COMMENT '工作状态',
  INDEX idx_worker_area (province, city),
  INDEX idx_worker_status (work_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='NEPG检测网格员表';

CREATE TABLE system_admin (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  admin_code VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员登录编号',
  password VARCHAR(100) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='NEPM系统管理员表';

CREATE TABLE decision_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  decision_code VARCHAR(50) NOT NULL UNIQUE COMMENT '决策者登录编号',
  password VARCHAR(100) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='NEPV决策者表';

CREATE TABLE grid_area (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  province VARCHAR(50) NOT NULL COMMENT '省份',
  city VARCHAR(50) NOT NULL COMMENT '城市网格',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  UNIQUE KEY uk_grid_area (province, city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网格区域表';

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
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_feedback_supervisor (supervisor_id),
  INDEX idx_feedback_worker (assigned_worker_id),
  INDEX idx_feedback_status (status),
  INDEX idx_feedback_area (province, city),
  CONSTRAINT fk_feedback_supervisor FOREIGN KEY (supervisor_id) REFERENCES public_supervisor(id),
  CONSTRAINT fk_feedback_worker FOREIGN KEY (assigned_worker_id) REFERENCES grid_worker(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='空气质量公众监督反馈表';

CREATE TABLE aqi_measurement (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  feedback_id BIGINT NOT NULL COMMENT '反馈信息ID',
  worker_id BIGINT NOT NULL COMMENT '网格员ID',
  feedback_no VARCHAR(50) COMMENT '反馈编号',
  so2_level VARCHAR(20) NOT NULL COMMENT 'SO2二氧化硫AQI等级',
  co_level VARCHAR(20) NOT NULL COMMENT 'CO一氧化碳AQI等级',
  pm25_level VARCHAR(20) NOT NULL COMMENT 'PM2.5悬浮颗粒物AQI等级',
  final_aqi_level VARCHAR(20) NOT NULL COMMENT '最终AQI等级',
  measure_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_measurement_feedback (feedback_id),
  INDEX idx_measurement_worker (worker_id),
  INDEX idx_measurement_level (final_aqi_level),
  CONSTRAINT fk_measurement_feedback FOREIGN KEY (feedback_id) REFERENCES aqi_feedback(id),
  CONSTRAINT fk_measurement_worker FOREIGN KEY (worker_id) REFERENCES grid_worker(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网格员实测AQI表';

INSERT INTO public_supervisor(phone,password,real_name,age,gender)
VALUES ('13924689016','123456','林子涵',22,'女');

INSERT INTO grid_worker(worker_code,password,real_name,province,city,work_status) VALUES
('grid001','123456','张网格员','辽宁省','沈阳市','可工作'),
('grid002','123456','李网格员','辽宁省','大连市','可工作'),
('grid003','123456','陈网格员','广东省','深圳市','可工作'),
('grid004','123456','赵网格员','广东省','广州市','支援'),
('grid005','123456','刘网格员','四川省','成都市','可工作'),
('grid006','123456','周网格员','上海市','上海市','可工作'),
('grid007','123456','王网格员','北京市','北京市','可工作'),
('grid008','123456','孙网格员','四川省','绵阳市','可工作');

INSERT INTO system_admin(admin_code,password,real_name)
VALUES ('admin001','123456','系统管理员');

INSERT INTO decision_user(decision_code,password,real_name)
VALUES ('decision001','123456','决策者');

INSERT INTO grid_area(province,city) VALUES
('辽宁省','沈阳市'),('辽宁省','大连市'),('辽宁省','鞍山市'),('辽宁省','抚顺市'),
('北京市','北京市'),('上海市','上海市'),
('广东省','广州市'),('广东省','深圳市'),('广东省','珠海市'),
('四川省','成都市'),('四川省','绵阳市');

INSERT INTO aqi_feedback(feedback_no,supervisor_id,province,city,address,estimated_aqi_level,description,status,assigned_worker_id,assign_type,assign_time)
VALUES
('NEP20260616001',1,'辽宁省','沈阳市','浑南区高新路2号','轻度污染','能见度一般，有轻微异味。','已指派',1,'本地指派','2026-06-16 10:10:00'),
('NEP20260615002',1,'辽宁省','大连市','中山区人民路附近','良','空气状况较好。','已完成',2,'本地指派','2026-06-15 10:10:00'),
('NEP20260614003',1,'广东省','深圳市','南山区科技园北区','中度污染','有扬尘，午后气味明显。','待指派',NULL,NULL,NULL),
('NEP20260613004',1,'北京市','北京市','朝阳区建国路88号','轻度污染','早高峰期间能见度下降。','已完成',7,'本地指派','2026-06-13 11:20:00'),
('NEP20260612005',1,'上海市','上海市','浦东新区世纪大道','重度污染','道路施工，扬尘明显。','已完成',6,'本地指派','2026-06-12 09:45:00'),
('NEP20260611006',1,'四川省','成都市','高新区天府大道','轻度污染','午后有轻微灰霾。','已完成',5,'本地指派','2026-06-11 14:10:00'),
('NEP20260610007',1,'广东省','广州市','天河区体育西路','良','空气状况正常，申请例行核验。','已完成',4,'本地指派','2026-06-10 10:05:00'),
('NEP20260609008',1,'辽宁省','沈阳市','铁西区建设大路','中度污染','工厂周边存在异味。','已完成',1,'本地指派','2026-06-09 15:30:00'),
('NEP20260608009',1,'四川省','绵阳市','涪城区临园路','良','天气晴朗，能见度良好。','已完成',8,'本地指派','2026-06-08 09:10:00'),
('NEP20260607010',1,'广东省','深圳市','宝安区机场南路','轻度污染','车流量较大，有尾气气味。','已指派',3,'本地指派','2026-06-07 12:40:00'),
('NEP20260606011',1,'辽宁省','鞍山市','铁东区胜利南路','中度污染','空气中颗粒物较明显。','待指派',NULL,NULL,NULL),
('NEP20260605012',1,'广东省','珠海市','香洲区情侣中路','优','海风较大，空气清新。','待指派',NULL,NULL,NULL);

INSERT INTO aqi_measurement(feedback_id,worker_id,feedback_no,so2_level,co_level,pm25_level,final_aqi_level)
VALUES
(2,2,'NEP20260615002','良','优','良','良'),
(4,7,'NEP20260613004','良','轻度污染','良','轻度污染'),
(5,6,'NEP20260612005','中度污染','良','重度污染','重度污染'),
(6,5,'NEP20260611006','良','良','轻度污染','轻度污染'),
(7,4,'NEP20260610007','优','良','良','良'),
(8,1,'NEP20260609008','中度污染','良','轻度污染','中度污染'),
(9,8,'NEP20260608009','优','优','良','良');
