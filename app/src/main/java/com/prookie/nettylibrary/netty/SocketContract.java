package com.prookie.nettylibrary.netty;

/**
 * SocketContract
 * Created by brin on 2018/7/6.
 */
class SocketContract {

    //请求
    static final String HEART_BEAT = "0001";//设备心跳
    static final String DEVICE_INFO = "0002";//获取设备信息-设备所在组
    static final String DEVICE_PASSWORD = "0003";//获取设备密码
    static final String OPEN_REPORT = "0004";//开门记录上报
    static final String DEVICE_TOKEN_REPORT= "0005";//开门记录上报
    //被请求
    static final String REMOTE_OPEN = "0061";//远程开门
    static final String ADD_PASSWORD = "0062";//新增密码
    static final String MODIFY_PASSWORD = "0063";//修改密码
    static final String DELETE_PASSWORD = "0064";//删除密码


    static final String MESSAGE_SEPARATOR = "|";//报文分隔符

    static final String PROCESS_MESSAGE_SEPARATOR = "\\|";//报文分隔符
    static final String PROPERTY_SEPARATOR = "#";//属性分隔符
    static final String ARRAY_SEPARATOR = "&";//数组分隔符
    static final String DEFAULT_LENGTH = "ABCDE";


}
