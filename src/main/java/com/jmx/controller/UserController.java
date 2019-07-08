package com.jmx.controller;

import com.jmx.bo.UsersBo;
import com.jmx.enums.OperatorFriendRequestTypeEnum;
import com.jmx.enums.PreSearchFriendsEnum;
import com.jmx.netty.ChatData;
import com.jmx.pojo.ChatMsg;
import com.jmx.pojo.Users;
import com.jmx.service.UsersService;
import com.jmx.utils.FastDFSClient;
import com.jmx.utils.FileUtils;
import com.jmx.utils.MD5Utils;
import com.jmx.utils.ResponseResult;
import com.jmx.vo.FriendRequestVo;
import com.jmx.vo.MyFriendsVo;
import com.jmx.vo.UsersVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
 * 用户相关功能controller
 * 1. 判断登录还是注册
 * 2. 登录和注册功能的实现
 */

@RestController
@RequestMapping("user")
public class UserController {

    private final UsersService usersService;
    private final FastDFSClient fastDFSClient;

    @Autowired
    public UserController(UsersService usersService, FastDFSClient fastDFSClient) {
        this.usersService = usersService;
        this.fastDFSClient = fastDFSClient;
    }

    /**
     * 判断登录还是注册, User中包含用户名和密码
     * @param user
     * @return
     */
    @PostMapping("/registerOrLogin")
    public ResponseResult registerOrLogin(@RequestBody Users user) throws Exception{
        //1. 判断用户名或密码不能为空
        if(StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())){
            //返回错误信息
            return ResponseResult.errorMsg("用户名或密码不能为空");
        }
        //2. service中查询,判断用户名是否存在,如果存在则登录,否则转为注册
        boolean usernameIsExist =  usersService.queryUsernameIsExist(user.getUsername());
        Users result;
        if(usernameIsExist){
            //用户名存在,进行登录逻辑,传入用户名和密码,result有用户信息
            result  = usersService.queryUserForLogin(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if(result == null){
                //密码错误
                return new ResponseResult(500,"登陆密码错误",null);
            }
        }else{
            //用户名不存在,进入注册逻辑,传入User信息
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setNickname(user.getUsername());
            user.setCid(user.getCid());
            result = usersService.saveUser(user);
            if(result == null){
                //注册失败
                return new ResponseResult(500,"注册失败",null);
            }
        }
        UsersVo userVo = new UsersVo();
        BeanUtils.copyProperties(result,userVo);
        //返回给前端的是VO对象(不需要所有user都返回)
        return ResponseResult.ok(userVo);
    }

    /**
     * 用户上传头像逻辑
     * 1. 用户上传头像为Base64格式的字符串
     * 2. 将字符串转换为文件对象
     * 3. 上传到fastDFS
     * 4. 更新数据库的信息
     * @param usersBo
     * @return
     */
    @PostMapping("/uploadFaceBase64")
    public ResponseResult uploadFaceBase64(@RequestBody UsersBo usersBo) throws Exception{
        //1. 将字符串转换为文件对象
        String base64Data = usersBo.getFaceData();
        //win下的临时目录
        //String userFacePath = "d:\\" + usersBo.getUserId() + "userFace64.png";
        String userFacePath = File.separator + "home" + File.separator + "tempImg"
                + File.separator + usersBo.getUserId() + "userFace64.png";
        FileUtils.base64ToFile(userFacePath,base64Data);

        //2. 将文件转换为Multipart格式，上传到fastDfs
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = "";
        //2.1 上传后返回文件名
        if (faceFile != null){
            url = fastDFSClient.uploadBase64(faceFile);
        }

        //3. 获取缩略图的url _80x80.png
        String thumb = "_80x80.";
        String[] arr = url.split("\\.");
        String thumbImgUrl = arr[0] + thumb + arr[1];

        //4. 更新用户头像，存储到数据库中
        Users users = new Users();
        users.setId(usersBo.getUserId());
        //小图
        users.setFaceImage(thumbImgUrl);
        //大图
        users.setFaceImageBig(url);
        //上传到数据库
        Users userInfo = usersService.updateUsersInfo(users);
        //返回UserVo对象
        UsersVo userVo = new UsersVo();
        BeanUtils.copyProperties(userInfo,userVo);
        return ResponseResult.ok(userVo);
    }

    /**
     * 更新用户的昵称
     * @param usersBo
     * @return
     */
    @PostMapping("/setNickname")
    public ResponseResult setNickname(@RequestBody UsersBo usersBo){
        Users users = new Users();
        users.setId(usersBo.getUserId());
        users.setNickname(usersBo.getNickname());
        Users usersInfo = usersService.updateUsersInfo(users);
        return ResponseResult.ok(usersInfo);
    }

    /**
     * 搜索用户,根据账号做匹配查询而不是模糊查询
     * @param myUserId 我的用户id
     * @param username 搜索的用户名
     * @return
     */
    @PostMapping("/searchFriends")
    public ResponseResult searchFriends(String myUserId, String username){
        //判定传入的不能为空
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(username)){
            return ResponseResult.errorMsg("");
        }
        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer result = usersService.preSearchFriends(myUserId, username);

        if(result == PreSearchFriendsEnum.SUCCESS.getStatus()){
            //可以添加, 返回搜索的用户的信息
            Users user = usersService.queryUserByUsername(username);
            UsersVo usersVo = new UsersVo();
            BeanUtils.copyProperties(user,usersVo);
            return ResponseResult.ok(usersVo);
        }else {
            String errMsg = PreSearchFriendsEnum.getMsgByStatus(result);
            return ResponseResult.errorMsg(errMsg);
        }
    }

    /**
     * 发送添加好友的请求
     * @param myUserId
     * @param username
     * @return
     */
    @PostMapping("/sendAddFriendRequest")
    public ResponseResult sendAddFriendRequest(String myUserId, String username){
        //判定传入的不能为空
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(username)){
            return ResponseResult.errorMsg("");
        }
        // 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
        // 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
        // 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
        Integer result = usersService.preSearchFriends(myUserId, username);

        if(result == PreSearchFriendsEnum.SUCCESS.getStatus()){
            //可以发送添加请求
            usersService.sendAddFriendRequest(myUserId, username);
            return ResponseResult.ok();
        }else {
            String errMsg = PreSearchFriendsEnum.getMsgByStatus(result);
            return ResponseResult.errorMsg(errMsg);
        }
    }

    /**
     * 根据接收者的id，查询所有的好友请求列表
     * @param acceptUserId
     * @return
     */
    @PostMapping("/queryFriendRequestList")
    public ResponseResult queryFriendRequestList(String acceptUserId){
        if(StringUtils.isBlank(acceptUserId)){
            return ResponseResult.errorMsg("");
        }
        //查询相关列表
        List<FriendRequestVo> requestList = usersService.queryFriendRequestList(acceptUserId);

        if(requestList != null){
            return ResponseResult.ok(requestList);
        }else {
            return ResponseResult.errorMsg("未找到添加者");
        }
    }

    /**
     * 操作好友请求
     * @param acceptUserId 接收者id
     * @param senderId 发送请求者id
     * @param operaType 操作类型, 参照
     * @return
     */
    @PostMapping("/operatorFriendRequest")
    public ResponseResult queryFriendRequestList(String acceptUserId, String senderId, Integer operaType ) {
        //对传入参数对非空判断
        if (StringUtils.isBlank(acceptUserId) ||
            StringUtils.isBlank(senderId) ||
                operaType == null) {
            return ResponseResult.errorMsg("");
        }
        //判断传入的type是否在枚举类中定义
        if(StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operaType))){
            return ResponseResult.errorMsg("");
        }
        //判断type的类型,如果是0表示忽略,删除请求表中的数据
        if(operaType == OperatorFriendRequestTypeEnum.IGNORE.type){
            //调用service方法
            usersService.deleteFriendRequest(senderId,acceptUserId);
        }else if (operaType == OperatorFriendRequestTypeEnum.PASS.type){
            //调用service方法
            usersService.passFriendRequest(senderId, acceptUserId);
        }
        //TODO 查询并返回最新聊天的好友
        return ResponseResult.ok();
    }

    /**
     * 查询我的好友
     * @param userId
     * @return
     */
    @PostMapping("/queryMyFriends")
    public ResponseResult queryMyFriends(String userId){
        //1. 做非空判断
        if(StringUtils.isBlank(userId)){
            return ResponseResult.errorMsg("");
        }
        //2. 数据库查询我的好友
        List<MyFriendsVo> myFriendsVos = usersService.queryMyFriends(userId);
        return ResponseResult.ok(myFriendsVos);
    }

    /**
     * 查询未读的消息
     * @param acceptUserId
     * @return
     */
    @PostMapping("/queryUnReadMsg")
    public ResponseResult queryUnReadMsg(String acceptUserId){
        //1. 做非空判断
        if(StringUtils.isBlank(acceptUserId)){
            return ResponseResult.errorMsg("");
        }
        //2. 数据库查询我的好友
        List<ChatMsg> unReadMsg = usersService.queryUnReadMsg(acceptUserId);
        return ResponseResult.ok(unReadMsg);
    }
}
