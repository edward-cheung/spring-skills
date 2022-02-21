/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : 127.0.0.1:3306
 Source Schema         : springbootdemo

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 23/11/2020 20:33:41
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cloud_menu
-- ----------------------------
DROP TABLE IF EXISTS `cloud_menu`;
CREATE TABLE `cloud_menu`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`         varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '菜单名称',
    `code`         char(32) CHARACTER SET utf8 COLLATE utf8_general_ci     NOT NULL COMMENT '菜单编码',
    `img`          varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci          DEFAULT NULL COMMENT '图标',
    `url`          varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单地址',
    `pid`          bigint(20) UNSIGNED DEFAULT 0 COMMENT '菜单父级Id',
    `level`        tinyint(4) UNSIGNED NOT NULL COMMENT '菜单等级，分一级菜单和二级菜单',
    `orders`       smallint(8) UNSIGNED NOT NULL COMMENT '顺序',
    `status`       bit(1)                                                  NOT NULL DEFAULT b'1' COMMENT '状态：0禁用，1启用',
    `cloud_code`   char(32) CHARACTER SET utf8 COLLATE utf8_general_ci     NOT NULL COMMENT '系统编码',
    `is_delete`    bit(1)                                                  NOT NULL DEFAULT b'0' COMMENT '是否删除：0否，1是',
    `gmt_create`   datetime(0) NOT NULL COMMENT '创建日期',
    `gmt_modified` datetime(0) NOT NULL COMMENT '修改日期',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `udx_code`(`code`, `is_delete`) USING BTREE COMMENT '菜单编码'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cloud_operate
-- ----------------------------
DROP TABLE IF EXISTS `cloud_operate`;
CREATE TABLE `cloud_operate`
(
    `id`           bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`         varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '功能名称',
    `code`         char(32) CHARACTER SET utf8 COLLATE utf8_general_ci     NOT NULL COMMENT '功能编码',
    `url`          varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '功能跳转路径地址',
    `menu_code`    char(32) CHARACTER SET utf8 COLLATE utf8_general_ci     NOT NULL COMMENT '所属菜单',
    `status`       bit(1)                                                  NOT NULL DEFAULT b'1' COMMENT '状态：0禁用，1启用',
    `cloud_code`   char(32) CHARACTER SET utf8 COLLATE utf8_general_ci     NOT NULL COMMENT '系统编码',
    `is_delete`    bit(1)                                                  NOT NULL DEFAULT b'0' COMMENT '是否删除：0否，1是',
    `gmt_create`   datetime(0) NOT NULL COMMENT '创建日期',
    `gmt_modified` datetime(0) NOT NULL COMMENT '修改日期',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '操作表' ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;
