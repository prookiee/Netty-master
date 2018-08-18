package com.prookie.nettylibrary.netty;


import android.util.Log;


import com.prookie.nettylibrary.MainApplication;
import com.prookie.nettylibrary.util.AppUtil;

import java.util.Locale;
import java.util.Random;


/**
 * MessageSender
 * Created by brin on 2018/7/6.
 */

public class MessageSender {
    private static final String TAG = MessageSender.class.getSimpleName();





    /**
     * 心跳
     *
     * @return
     */
    public static String heartBeat() {
        StringBuilder sb = createMessage(SocketContract.HEART_BEAT);
        String realLen = String.format(Locale.getDefault(), "%05d", sb.length());
        return sb.toString().replace(SocketContract.DEFAULT_LENGTH, realLen);
    }

    /**
     * 获取设备信息
     *
     * @return
     */
    public static String deviceInfo() {
        StringBuilder sb = createMessage(SocketContract.DEVICE_INFO);
        String realLen = String.format(Locale.getDefault(), "%05d", sb.length());
        return sb.toString().replace(SocketContract.DEFAULT_LENGTH, realLen);
    }

    /**
     * 设备token上报
     *
     * @return
     */
    public static String deviceTokenReport() {
        StringBuilder sb = createMessage(SocketContract.DEVICE_TOKEN_REPORT);
        String realLen = String.format(Locale.getDefault(), "%05d", sb.length());
        Log.e(TAG, sb.toString().replace(SocketContract.DEFAULT_LENGTH, realLen));
        return sb.toString().replace(SocketContract.DEFAULT_LENGTH, realLen);
    }

    /**
     * 获取设备密码
     *
     * @return
     */
    public static String devicePassword() {
        StringBuilder sb = createMessage(SocketContract.DELETE_PASSWORD);
        String realLen = String.format(Locale.getDefault(), "%05d", sb.length());
        return sb.toString().replace(SocketContract.DEFAULT_LENGTH, realLen);
    }


    /**
     * 开门记录上报
     *
     * @param url
     * @param faceUserId
     * @return
     */
    public static String openReport(String url, String faceUserId) {
        StringBuilder sb = createMessage(SocketContract.OPEN_REPORT);
        sb.append(SocketContract.MESSAGE_SEPARATOR).append(url)
                .append(SocketContract.MESSAGE_SEPARATOR).append(faceUserId);
        String realLen = String.format(Locale.getDefault(), "%05d", sb.length());
        return sb.toString().replace(SocketContract.DEFAULT_LENGTH, realLen);
    }

    /**
     * createBaseMessage
     *
     * @param type
     * @return
     */
    private static StringBuilder createMessage(String type) {

        Random random = new Random();
        long randomId = random.nextInt(100000000);
        String id = String.format(Locale.getDefault(), "%08d", randomId);
        String length = SocketContract.DEFAULT_LENGTH;
        String version = "01";
        String own = "1234567890";
        String deviceToken = AppUtil.getDeviceUUID(MainApplication.getContext());
        //拼接消息基本参数
        StringBuilder msgBuilder = new StringBuilder()
                .append(id).append(SocketContract.MESSAGE_SEPARATOR)//消息Id
                .append(type).append(SocketContract.MESSAGE_SEPARATOR)//消息类型
                .append(length).append(SocketContract.MESSAGE_SEPARATOR)//消息长度
                .append(version).append(SocketContract.MESSAGE_SEPARATOR)//app版本号
                .append(own).append(SocketContract.MESSAGE_SEPARATOR)//设别拥有者
                .append(deviceToken);//设备token

//        int lenStartPos = (id + type).length();
//        String realLen = String.format(Locale.getDefault(), "%08d", msgBuilder.length());
//        msgBuilder = msgBuilder.replace(lenStartPos, lenStartPos + 8, realLen);

        return msgBuilder;
    }


}
