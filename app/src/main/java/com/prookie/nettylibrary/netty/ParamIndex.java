package com.prookie.nettylibrary.netty;

/**
 * ParamIndex
 * 用户解析socket通讯中报文信息，当前设备为客户端
 * Created by brin on 2018/7/6.
 */
public class ParamIndex {


    /* **********      客户端请求服务端       **************************************************************************/

    /**
     * 发送心跳
     */
    public static class HeartBeatArg extends BaseParamIndex {

    }

    /**
     * 接收心跳返回
     */
    public static class HeartBeatResult extends BaseParamIndex {

        public static final int STATUS = 6;
        public static final int ERROR = 7;
        public static final int PARAM_COUNT = 7;
    }

    /**
     * 设备信息请求
     */
    public static class DeviceInfoArg extends BaseParamIndex {

    }

    /**
     * 设备信息返回
     */
    public static class DeviceInfoResult extends BaseParamIndex {

        public static final int GROUP_ID = 6;
        public static final int STATUS = 7;
        public static final int ERROR = 8;
        public static final int PARAM_COUNT = 9;


    }


    /**
     * 设备信息请求
     */
    public static class DevicePasswordArg extends BaseParamIndex {

    }

    /**
     * 设备信息返回
     */
    public static class DevicePasswordResult extends BaseParamIndex {

        public static final int PASSWORD = 6;
        public static final int STATUS = 7;
        public static final int ERROR = 8;
        public static final int PARAM_COUNT = 9;
    }

    /**
     * 设备信息请求
     */
    public static class OpenReportArg extends BaseParamIndex {

        public static final int FACE_URL = 6;
        public static final int FACE_USER_ID = 7;

    }

    /**
     * 设备信息返回
     */
    public static class OpenReportResult extends BaseParamIndex {

        public static final int FACE_URL = 6;
        public static final int FACE_USER_ID = 7;
        public static final int STATUS = 8;
        public static final int ERROR = 9;
        public static final int PARAM_COUNT = 9;
    }

    public static class DeviceTokenReportArg extends BaseParamIndex {
        public static final int PARAM_COUNT = 5;
    }


    public static class DeviceTokenReportResult extends BaseParamIndex {
        public static final int STATUS = 6;
        public static final int ERROR = 7;
        public static final int PARAM_COUNT = 7;
    }

    /* **********   服务端请求客户端     **************************************************************************/

    /**
     * 远程开门（解析服务端发送的报文）
     */
    public static class RemoteOpenResult extends BaseParamIndex {
        public static final int PARAM_COUNT = 6;

    }

    /**
     * 远程开门 （发送开门结果给服务端）
     */
    public static class RemoteOpenArg extends BaseParamIndex {

        public static final int STATUS = 6;
        public static final int ERROR = 7;
        public static final int PARAM_COUNT = 7;
    }


    /**
     * 新增密码
     */
    public static class AddPsdResult extends BaseParamIndex {

        public static final int PASSWORD = 6;
        public static final int PARAM_COUNT = 7;
    }

    /**
     * 新增密码结果返回
     */
    public static class AddPsdArg extends BaseParamIndex {

        public static final int PASSWORD = 6;
        public static final int STATUS = 7;
        public static final int ERROR = 8;
        public static final int PARAM_COUNT = 8;
    }


    /**
     * 修改密码
     */
    public static class ModifyPsdResult extends BaseParamIndex {

        public static final int PASSWORD = 6;
//        public static final int STATUS = 7;
//        public static final int ERROR = 8;
        public static final int PARAM_COUNT = 7;
    }

    /**
     * 修改密码结果返回
     */
    public static class ModifyPsdArg extends BaseParamIndex {

        public static final int PASSWORD = 6;
        public static final int STATUS = 7;
        public static final int ERROR = 8;
        public static final int PARAM_COUNT = 8;
    }


    /**
     * 删除密码
     */
    public static class DelPsdResult extends BaseParamIndex {

        public static final int PASSWORD = 6;
//        public static final int STATUS = 7;
//        public static final int ERROR = 8;
        public static final int PARAM_COUNT = 7;
    }

    /**
     * 删除密码结果返回
     */
    public static class DelPsdArg extends BaseParamIndex {

        public static final int PASSWORD = 6;
        public static final int STATUS = 7;
        public static final int ERROR = 8;
        public static final int PARAM_COUNT = 8;
    }


}
