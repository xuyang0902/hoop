CREATE DATABASE hoop_console DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE TABLE `hoop_app` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) NOT NULL COMMENT '接入系统名称',
  `urls` varchar(512) NOT NULL COMMENT 'http://192.168.1.1:8080/hoop,http://192.168.1.19:8080/hoop',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_name_uk` (`app_name`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='hoop客户端信息'