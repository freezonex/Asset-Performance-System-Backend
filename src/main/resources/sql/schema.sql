-- apsdatabase.asset definition

CREATE TABLE `asset` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `asset_id` varchar(50) DEFAULT NULL,
                         `asset_name` varchar(100) DEFAULT NULL,
                         `asset_type_id` bigint(20) NOT NULL,
                         `asset_type` varchar(200) DEFAULT NULL,
                         `vendor_model` varchar(512) DEFAULT NULL,
                         `description` varchar(512) DEFAULT NULL,
                         `sn` varchar(100) DEFAULT NULL,
                         `status` int(11) DEFAULT NULL,
                         `used_status` int(11) NOT NULL DEFAULT 0 COMMENT '0: Not Used 1: Used',
                         `used_date` date DEFAULT NULL,
                         `department_id` int(11) DEFAULT NULL,
                         `department` varchar(100) DEFAULT NULL,
                         `location` varchar(100) DEFAULT NULL,
                         `value` varchar(100) DEFAULT NULL,
                         `responsible_person` varchar(100) DEFAULT NULL,
                         `installation_date` datetime DEFAULT NULL,
                         `maintenance_log` varchar(512) DEFAULT NULL,
                         `spare_parts` varchar(512) DEFAULT NULL,
                         `documentation` varchar(512) DEFAULT NULL,
                         `attachment_name` varchar(200) DEFAULT NULL,
                         `attachment_dir` varchar(512) DEFAULT NULL,
                         `gmt_create` datetime NOT NULL DEFAULT current_timestamp(),
                         `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                         `deleted` int(11) NOT NULL DEFAULT 0,
                         `model_url` varchar(2000) DEFAULT NULL COMMENT '3d-url',
                         `glb_url` varchar(2000) DEFAULT NULL COMMENT '3d-gbl',
                         `gbl_dir` varchar(1000) DEFAULT NULL COMMENT '3d-gbl System directory',
                         `gbl_file_name` varchar(1000) DEFAULT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47110 COMMENT='asset';


-- apsdatabase.asset_type definition

CREATE TABLE `asset_type` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `asset_type` varchar(200) NOT NULL,
                              `safety_stock_quantity` int(11) NOT NULL DEFAULT 0,
                              `unit` varchar(100) DEFAULT NULL,
                              `supplier_name` varchar(200) DEFAULT NULL,
                              `icon` varchar(1024) DEFAULT NULL,
                              `price_value` int(11) NOT NULL DEFAULT 0,
                              `gmt_create` datetime NOT NULL DEFAULT current_timestamp(),
                              `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                              `deleted` int(11) NOT NULL DEFAULT 0,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `asset_type_asset_type_uindex` (`asset_type`)
) ENGINE=InnoDB AUTO_INCREMENT=16  COMMENT='asset type';


-- apsdatabase.department definition

CREATE TABLE `department` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `department_name` varchar(200) NOT NULL,
                              `gmt_create` datetime NOT NULL DEFAULT current_timestamp(),
                              `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                              `deleted` int(11) NOT NULL DEFAULT 0,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `department_department_name_uindex` (`department_name`)
) ENGINE=InnoDB AUTO_INCREMENT=12  COMMENT='department';


-- apsdatabase.inventory definition

CREATE TABLE `inventory` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                             `asset_type_id` bigint(20) NOT NULL,
                             `asset_type` varchar(200) NOT NULL,
                             `quantity` int(11) DEFAULT NULL,
                             `unit` varchar(100) DEFAULT NULL,
                             `usage_rate` int(11) DEFAULT NULL,
                             `supplier_name` varchar(200) DEFAULT NULL,
                             `expected_quantity` int(11) DEFAULT NULL,
                             `creation_time` datetime NOT NULL,
                             `expected_date` date NOT NULL,
                             `gmt_create` datetime NOT NULL DEFAULT current_timestamp(),
                             `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                             `deleted` int(11) NOT NULL DEFAULT 0,
                             `ai` int(11) NOT NULL DEFAULT 0,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `inventory_asset_type_id_expected_date_uindex` (`asset_type_id`,`expected_date`)
) ENGINE=InnoDB AUTO_INCREMENT=8866 COMMENT='asset inventory';


-- apsdatabase.maintenance definition

CREATE TABLE `maintenance` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                               `asset_type_id` bigint(20) NOT NULL,
                               `scheduled_date` date NOT NULL,
                               `content` varchar(1024) NOT NULL,
                               `status` int(11) NOT NULL DEFAULT 0,
                               `completed_time` datetime DEFAULT NULL,
                               `gmt_create` datetime NOT NULL DEFAULT current_timestamp(),
                               `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                               `deleted` int(11) NOT NULL DEFAULT 0,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4  COMMENT='maintenance';


-- apsdatabase.work_order definition

CREATE TABLE `work_order` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `order_id` varchar(50) NOT NULL,
                              `order_name` varchar(100) NOT NULL,
                              `order_type` varchar(100) DEFAULT NULL,
                              `description` varchar(512) DEFAULT NULL,
                              `asset_id` varchar(50) NOT NULL,
                              `priority` int(11) NOT NULL,
                              `creation_time` datetime NOT NULL,
                              `due_time` datetime NOT NULL,
                              `completion_time` datetime DEFAULT NULL,
                              `status` int(11) NOT NULL COMMENT '1：Open 2：In Progress 3：Pending Review 4：Dued 5：Closed',
                              `assigned_to` bigint(20) NOT NULL,
                              `created_by` varchar(50) DEFAULT NULL,
                              `notes` varchar(512) DEFAULT NULL,
                              `gmt_create` datetime NOT NULL DEFAULT current_timestamp(),
                              `gmt_modified` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                              `deleted` int(11) NOT NULL DEFAULT 0,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12  COMMENT='work order';