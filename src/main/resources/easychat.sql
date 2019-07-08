/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 50726
Source Host           : localhost:3306
Source Database       : easychat

Target Server Type    : MYSQL
Target Server Version : 50726
File Encoding         : 65001

Date: 2019-07-08 20:05:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for chat_msg
-- ----------------------------
DROP TABLE IF EXISTS `chat_msg`;
CREATE TABLE `chat_msg` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `send_user_id` varchar(64) NOT NULL COMMENT '发送人id',
  `accept_user_id` varchar(64) NOT NULL COMMENT '接收人id',
  `msg` varchar(255) NOT NULL COMMENT '消息内容',
  `sign_flag` tinyint(1) NOT NULL COMMENT '是否已读',
  `create_time` datetime NOT NULL COMMENT '消息创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of chat_msg
-- ----------------------------

-- ----------------------------
-- Table structure for friends_request
-- ----------------------------
DROP TABLE IF EXISTS `friends_request`;
CREATE TABLE `friends_request` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `send_user_id` varchar(64) NOT NULL COMMENT '发送人id',
  `accept_user_id` varchar(64) NOT NULL COMMENT '接收人id',
  `request_data_time` datetime NOT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of friends_request
-- ----------------------------

-- ----------------------------
-- Table structure for my_friends
-- ----------------------------
DROP TABLE IF EXISTS `my_friends`;
CREATE TABLE `my_friends` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `my_user_id` varchar(64) NOT NULL COMMENT '当前用户id',
  `my_friend_user_id` varchar(64) NOT NULL COMMENT '添加朋友的id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `my_and_friends_id` (`my_user_id`,`my_friend_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of my_friends
-- ----------------------------

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码，已加密',
  `face_image` varchar(255) NOT NULL COMMENT '用户头像',
  `face_image_big` varchar(255) NOT NULL COMMENT '用户大头像',
  `nickname` varchar(20) DEFAULT NULL COMMENT '用户昵称',
  `qrcode` varchar(255) DEFAULT NULL COMMENT '用户二维码',
  `cid` varchar(64) DEFAULT NULL COMMENT '用户客户端id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------
