package com.jmx.enums;

public enum OperatorFriendRequestTypeEnum {

    IGNORE(0,"忽略"),
    PASS(1,"通过");

    //操作的类型,0为忽略,1为通过好友请求
    public final Integer type;
    public final String msg;

    OperatorFriendRequestTypeEnum(Integer type,String msg){
        this.type = type;
        this.msg = msg;
    }

    public Integer getType(){
        return type;
    }

    //通过类型获取msg信息
    public static String getMsgByType(Integer type){
        for (OperatorFriendRequestTypeEnum operaType : OperatorFriendRequestTypeEnum.values()) {
            if(operaType.getType() == type){
                return operaType.msg;
            }
        }
        //如果type不能和定义的枚举一直,返回null
        return null;
    }
}
