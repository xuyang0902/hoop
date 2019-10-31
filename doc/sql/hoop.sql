CREATE TABLE `hoop_global_transaction` (
  `ts_id` varchar(128) NOT NULL COMMENT '事务ID',
  `ts_type` varchar(8) DEFAULT NULL COMMENT '事务类型：TCC',
  `action_count` int(11) NOT NULL COMMENT 'action记数',
  `start_time` datetime NOT NULL COMMENT '启动时间',
  `context` varbinary(8000) COMMENT '全局事务开启时入参参数',
  `state` varchar(4) DEFAULT NULL COMMENT 'U:未知,I:初始,C:提交中,R:回滚中，F:完成',
  `recover_count` int(11) NOT NULL COMMENT '恢复记数',
  `timeout` int(11) NOT NULL DEFAULT '0' COMMENT '超时，秒',
  `resolver_bean` varbinary(512) DEFAULT NULL COMMENT '状态反查器,spring beanId',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modify` datetime NOT NULL COMMENT '修改时间',
  `env` varchar(20) DEFAULT NULL COMMENT '运行环境',
  PRIMARY KEY (`ts_id`),
  KEY `gmt_create_index` (`gmt_create`),
  KEY `gmt_modify_index_state` (`gmt_modify`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分布式全局事务';

CREATE TABLE `hoop_branch_transaction` (
  `branch_id` varchar(32) NOT NULL COMMENT '分支事务ID',
  `ts_id` varchar(128) NOT NULL COMMENT '事务ID',
  `action_name` varchar(1024) DEFAULT NULL COMMENT '应用:beanId:try方法:commit方法:rollBack方法:二阶段顺序:版本号',
  `state` varchar(4) DEFAULT NULL COMMENT 'I:初始，F:完成',
  `context` varbinary(8000) COMMENT '分支事务一阶段的入参',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modify` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`branch_id`),
  UNIQUE KEY `ts_id_branch_id` (`ts_id`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分布式分支事务';

CREATE TABLE `hoop_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app` varchar(100) NOT NULL COMMENT '接入hoop应用的名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `ts_type` varchar(8) DEFAULT NULL COMMENT '事务类型：TCC',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_type` (`app`,`ts_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='单据处理锁记录表';
