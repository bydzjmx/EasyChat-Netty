# EasyChat App

一款使用Netty+SpringBoot+MUI+HTML5+制作的仿微信的聊天APP，包括聊天，通讯录，发现，个人等模块。

此工程为后端工程，前端工程参照：https://github.com/bydzjmx/EasyChat

## 功能说明

聊天界面能够保存最近聊天的好友及快照。
通讯录界面提供按A-Z的数组排列和查找。
发现界面提供朋友圈、添加好友和扫一扫功能。可以通过扫一扫添加好友。
个人界面提供头像修改、昵称修改等功能。
使用个推进行APP端消息的推送，使用前后端心跳保持WebSocket连接。

## API文档
使用ShowDoc生成项目文档，文档地址：https://www.showdoc.cc/easychat

# APP截图
<img src="https://github.com/bydzjmx/EasyChat-Netty/blob/master/images/screenshots/login.jpg" width="20%" height="20%"><img src="https://github.com/bydzjmx/EasyChat-Netty/blob/master/images/screenshots/chatList.jpg" width="20%" height="20%"><img src="https://github.com/bydzjmx/EasyChat-Netty/blob/master/images/screenshots/contact.jpg" width="20%" height="20%"><img src="https://github.com/bydzjmx/EasyChat-Netty/blob/master/images/screenshots/discovery.jpg" width="20%" height="20%"><img src="https://github.com/bydzjmx/EasyChat-Netty/blob/master/images/screenshots/userInfo.jpg" width="20%" height="20%">

## 使用的主要技术：

### 前端

1. MUI
2. HTML5+
3. AJAX
4. WebSocket
5. Image Cropper

### 后端

1. Netty
2. SpringBoot
3. tk.mapper
4. FastDFS
5. 个推
6. Google Zxing
7. IdWorker

# 部署

1. 导入sql文件
2. 修改后端工程中application.yml
   1. 端口号（默认8080）
   2. 数据库datasource配置
   3. FastDFS的tracker-list配置
3. 修改前端工程中app.js的三个服务器地址
   1. nettyServerUrl-----netty服务器地址
   2. serverUrl------后端服务器地址
   3. imgServerUrl----文件服务器地址
4. 后端工程打成jar包运行
5. 前端项目使用HBuilder打包成ipa/apk，在相应系统上运行。也可以用HBuilder运行到手机端或模拟器调试
6. 首页登录和注册功能合并，未注册的用户名在键入后自动注册登录

## 说明

此项目为学习Netty及移动端App编写所作，代码在有些地方设计可能不合理，同时用户体验、应用流畅性、代码健壮性和可扩展性还有待改进。部分功能仍待完善，欢迎大家指正。

# License

The Apache Software License, Version 2.0

Copyright [2016] [Anumbrella]

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
