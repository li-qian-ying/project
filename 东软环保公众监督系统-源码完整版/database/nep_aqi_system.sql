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
('grid008','123456','孙网格员','四川省','绵阳市','可工作'),
('grid009','123456','冯网格员','河北省','石家庄市','可工作'),
('grid010','123456','钱网格员','山东省','济南市','可工作'),
('grid011','123456','吴网格员','浙江省','杭州市','可工作'),
('grid012','123456','郑网格员','湖北省','武汉市','可工作'),
('grid013','123456','何网格员','江苏省','南京市','可工作'),
('grid014','123456','高网格员','陕西省','西安市','支援');

INSERT INTO system_admin(admin_code,password,real_name)
VALUES ('admin001','123456','系统管理员');

INSERT INTO decision_user(decision_code,password,real_name)
VALUES ('decision001','123456','决策者');

INSERT INTO grid_area(province,city) VALUES
('辽宁省','沈阳市'),('辽宁省','大连市'),('辽宁省','鞍山市'),('辽宁省','抚顺市'),
('北京市','北京市'),('上海市','上海市'),
('广东省','广州市'),('广东省','深圳市'),('广东省','珠海市'),
('四川省','成都市'),('四川省','绵阳市'),
('河北省','石家庄市'),('河北省','唐山市'),
('山东省','济南市'),('山东省','青岛市'),
('浙江省','杭州市'),('浙江省','宁波市'),
('湖北省','武汉市'),('江苏省','南京市'),('江苏省','苏州市'),
('陕西省','西安市');

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
('NEP20260605012',1,'广东省','珠海市','香洲区情侣中路','优','海风较大，空气清新。','待指派',NULL,NULL,NULL),
('NEP20260604013',1,'河北省','石家庄市','长安区中山东路','中度污染','午间灰霾明显，能见度下降。','已完成',9,'本地指派','2026-06-04 11:00:00'),
('NEP20260603014',1,'山东省','济南市','历下区经十路','轻度污染','道路车流密集，有尾气气味。','已完成',10,'本地指派','2026-06-03 10:20:00'),
('NEP20260602015',1,'浙江省','杭州市','西湖区文三路','良','申请例行空气质量核验。','已完成',11,'本地指派','2026-06-02 09:45:00'),
('NEP20260601016',1,'湖北省','武汉市','洪山区珞喻路','重度污染','施工区域扬尘持续时间较长。','已完成',12,'本地指派','2026-06-01 13:30:00'),
('NEP20260531017',1,'江苏省','南京市','建邺区江东中路','轻度污染','晚高峰空气有轻微异味。','已完成',13,'本地指派','2026-05-31 18:10:00'),
('NEP20260530018',1,'陕西省','西安市','雁塔区长安南路','中度污染','天气静稳，颗粒物聚集。','已完成',14,'本地指派','2026-05-30 15:20:00'),
('NEP20260529019',1,'浙江省','宁波市','鄞州区首南中路','良','空气状况总体良好。','待指派',NULL,NULL,NULL),
('NEP20260528020',1,'山东省','青岛市','市南区香港中路','轻度污染','港区方向有轻微异味。','已指派',10,'异地指派','2026-05-28 14:20:00'),
('NEP20260527021',1,'河北省','唐山市','路北区建设路','中度污染','工业区域附近可见烟尘。','待指派',NULL,NULL,NULL),
('NEP20260526022',1,'江苏省','苏州市','工业园区星湖街','良','园区空气状况例行检测。','已完成',13,'异地指派','2026-05-26 10:40:00'),
('NEP20260525023',1,'辽宁省','沈阳市','和平区青年大街','良','交通主干道例行核验。','已完成',1,'本地指派','2026-05-25 09:30:00'),
('NEP20260524024',1,'辽宁省','沈阳市','大东区滂江街','轻度污染','车辆尾气气味较明显。','已指派',1,'本地指派','2026-05-24 11:10:00'),
('NEP20260523025',1,'辽宁省','沈阳市','沈北新区蒲河路','优','空气清新，申请对照检测。','已完成',1,'本地指派','2026-05-23 08:50:00'),
('NEP20260522026',1,'辽宁省','沈阳市','于洪区黄海路','中度污染','附近施工产生持续扬尘。','已指派',1,'本地指派','2026-05-22 16:20:00'),
('NEP20260521027',1,'辽宁省','沈阳市','苏家屯区枫杨路','良','居民区空气质量例行反馈。','已完成',1,'本地指派','2026-05-21 10:15:00'),
('NEP20260520028',1,'辽宁省','沈阳市','浑南区智慧大街','轻度污染','早间能见度略有下降。','已指派',1,'本地指派','2026-05-20 09:25:00'),
('NEP20260519029',1,'辽宁省','沈阳市','铁西区北二路','中度污染','工业厂区周边有异味。','已完成',1,'本地指派','2026-05-19 14:35:00'),
('NEP20260518030',1,'辽宁省','沈阳市','皇姑区黄河北大街','轻度污染','晚间道路扬尘较多。','已指派',1,'本地指派','2026-05-18 18:05:00');

INSERT INTO aqi_measurement(feedback_id,worker_id,feedback_no,so2_level,co_level,pm25_level,final_aqi_level)
VALUES
(2,2,'NEP20260615002','良','优','良','良'),
(4,7,'NEP20260613004','良','轻度污染','良','轻度污染'),
(5,6,'NEP20260612005','中度污染','良','重度污染','重度污染'),
(6,5,'NEP20260611006','良','良','轻度污染','轻度污染'),
(7,4,'NEP20260610007','优','良','良','良'),
(8,1,'NEP20260609008','中度污染','良','轻度污染','中度污染'),
(9,8,'NEP20260608009','优','优','良','良'),
(13,9,'NEP20260604013','轻度污染','良','中度污染','中度污染'),
(14,10,'NEP20260603014','良','轻度污染','良','轻度污染'),
(15,11,'NEP20260602015','优','良','良','良'),
(16,12,'NEP20260601016','中度污染','良','重度污染','重度污染'),
(17,13,'NEP20260531017','良','轻度污染','轻度污染','轻度污染'),
(18,14,'NEP20260530018','中度污染','良','中度污染','中度污染'),
(22,13,'NEP20260526022','优','良','良','良'),
(23,1,'NEP20260525023','优','良','良','良'),
(25,1,'NEP20260523025','优','优','优','优'),
(27,1,'NEP20260521027','良','优','良','良'),
(29,1,'NEP20260519029','中度污染','良','轻度污染','中度污染');
