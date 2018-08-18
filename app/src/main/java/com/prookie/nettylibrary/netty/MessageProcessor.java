package com.prookie.nettylibrary.netty;

import android.text.TextUtils;

import com.prookie.nettylibrary.MainApplication;
import com.prookie.nettylibrary.event.ConnectStatusEvent;
import com.prookie.nettylibrary.event.OpenDoorEvent;
import com.prookie.nettylibrary.litebean.DeviceInfo;
import com.prookie.nettylibrary.litebean.DevicePassword;
import com.prookie.nettylibrary.util.AppUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * MessageProcessor
 * 解析服务端发送的消息
 * Created by brin on 2018/7/6.
 */
public class MessageProcessor {

    private static final String TAG = MessageProcessor.class.getSimpleName();

    /**
     * 解析消息
     *
     * @param msg
     */
    public static void execute(String msg) {
        try {
            if (TextUtils.isEmpty(msg)) {
                return;
            }
            if (!msg.contains(SocketContract.MESSAGE_SEPARATOR)) {//这个判断可以省略
                reconnect(); // 断开，重新连接
                return;
            }
            String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);//这里需要转义后的分隔符
            if (array.length < 5) {//判断参数的数组长度是否小于公共参数长度
                reconnect(); // 断开，重新连接
                return;
            }
            //判断消息长度
            int length = Integer.parseInt(array[BaseParamIndex.LENGTH]);
            if (length != msg.length()) { //消息长度不对，断开重连
                reconnect(); // 断开，重新连接
                return;
            }
            //判断设备token是否一致
            if (!TextUtils.equals(AppUtil.getDeviceUUID(MainApplication.getContext()), array[BaseParamIndex.DEVICE_TOKEN])) {
                reconnect(); // 断开，重新连接
                return;
            }
            String type = array[1];//根据消息类型，处理不同的业务
            switch (type) {
                case SocketContract.HEART_BEAT://心跳
                    break;
                case SocketContract.DEVICE_INFO://设备信息
                    deviceInfo(msg);
                    break;
                case SocketContract.DEVICE_PASSWORD://设备密码
                    devicePassword(msg);
                    break;
                case SocketContract.OPEN_REPORT://开门记录上报
                    remoteOpen(msg);
                    break;
                case SocketContract.DEVICE_TOKEN_REPORT://设备token上报
                    deviceTokenReport(msg);
                    break;
                case SocketContract.REMOTE_OPEN://远程开门
                    remoteOpen(msg);
                    break;
                case SocketContract.ADD_PASSWORD://新增密码
                    addPassword(msg);
                    break;
                case SocketContract.MODIFY_PASSWORD://修改密码
                    modifyPassword(msg);
                    break;
                case SocketContract.DELETE_PASSWORD://删除密码
                    deletePassword(msg);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            reconnect();//抓到异常，断开重连
        }
    }


    /**
     * 获取设备信息
     *
     * @param msg
     */
    private static void deviceInfo(String msg) {

        String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);
        //判断消息长度是否正确
        if (array.length < ParamIndex.DeviceInfoResult.PARAM_COUNT) {
            reconnect(); // 断开，重新连接
            return;
        }
        //判断消息成功状态
        if (!TextUtils.equals(ErrorCode.SUCCESS, array[ParamIndex.DeviceInfoResult.STATUS])) {
            reconnect(); // 断开，重新连接
            return;
        }
        //判断设备token,和groupId是否为空
        if (TextUtils.isEmpty(array[ParamIndex.DeviceInfoResult.DEVICE_TOKEN]) || TextUtils.isEmpty(array[ParamIndex.DeviceInfoResult.GROUP_ID])) {
            reconnect(); // 断开，重新连接
            return;
        }
        //存储到数据库
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceToken(array[ParamIndex.DeviceInfoResult.DEVICE_TOKEN]);
        deviceInfo.setGroupId(array[ParamIndex.DeviceInfoResult.GROUP_ID]);
        // TODO: 2018/7/6 1.clear； 2.insert 数据库操作；  获取成功之后，再去获取密码
//        NettyClient.getInstance().sendMessage(MessageSender.devicePassword());


    }


    /**
     * 解析设备密码
     * 从服务端获取该设备的密码
     *
     * @param msg
     */
    private static void devicePassword(String msg) {
        MsgResult msgResult = new MsgResult();
        String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);
        if (array.length != ParamIndex.DevicePasswordResult.PARAM_COUNT) {//判断参数个数是否一致
            return;
        }
        String psdStr = array[ParamIndex.DevicePasswordResult.PASSWORD];//获取密码串
        if (TextUtils.isEmpty(psdStr)) {//判断密码串是否为空
            msgResult.setOk(msg, "no password");
            messageFeedback(msgResult);
            return;
        }
        String[] psdArray = psdStr.split(SocketContract.ARRAY_SEPARATOR);//数组分隔符，分割出每组密码
        List<DevicePassword> tobeAddPsdList = new ArrayList<>();//待插入的密码集合
        for (String item : psdArray) {//循环单条密码
            String[] pArray = item.split(SocketContract.PROPERTY_SEPARATOR);//属性分隔符
            String password = pArray[0];
            String count = pArray[1];
            String date = pArray[2];
            String type = pArray[3];
            //判断密码是否为空
            if (TextUtils.isEmpty(password)) {
                msgResult.setError(msg, "01", "password is null");
                messageFeedback(msgResult);
                return;
            }
            //根据type进行不同的校验，只要有信息缺失，直接返回失败
            switch (type) {
                case "1"://长期密码
                    break;
                case "2"://短期密码
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "date is null");
                        messageFeedback(msgResult);
                        return;
                    }
                    break;
                case "3"://次数密码
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "date is null");
                        messageFeedback(msgResult);
                        return;
                    }
                    if (TextUtils.isEmpty(count)) {
                        msgResult.setError(msg, "01", "count is null");
                        messageFeedback(msgResult);
                        return;
                    }
                    break;
                default://密码类型不存在
                    msgResult.setError(msg, "01", "type error");
                    messageFeedback(msgResult);
                    return;
            }

            //密码经过 类型，日期，次数等校验成功后，开始添加到待插入的密码集合中
            DevicePassword devicePassword = new DevicePassword();
            devicePassword.setPassword(password);
            devicePassword.setType(type);
            devicePassword.setDate(date);
            devicePassword.setCount(count);
            tobeAddPsdList.add(devicePassword);//放入待插入的密码集合中

        }

        //循环结束后，准备执行数据库更新操作
        //获取数据库中所有的密码，用于插入失败的手动数据回滚
        //但是由于，litepal的批量插入方法暂时没有返回值，目前无法判断是否插入成功
        //由于插入之前，已经对所有数据做了判断，正常情况下都会插入成功
        List<DevicePassword> dbList = LitePal.findAll(DevicePassword.class);
        //删除所有密码
        int delResult = LitePal.deleteAll(DevicePassword.class);
        //判断删除结果
        if (delResult != dbList.size()) {
            msgResult.setError(msg, "01", "update failure");
            messageFeedback(msgResult);
            return;
        }
        //删除成功，插入所有待插入的密码
        LitePal.saveAll(tobeAddPsdList);
        msgResult.setOk(msg, "success");
        messageFeedback(msgResult);

    }

    /**
     * 设备token上报
     *
     * @param msg
     */
    private static void deviceTokenReport(String msg) {
        String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);
        if (array.length != ParamIndex.DeviceInfoResult.PARAM_COUNT) {
            return;
        }
        String code = array[ParamIndex.DeviceInfoResult.STATUS];
        if (TextUtils.equals(code, ErrorCode.SUCCESS)) {
            // TODO: 2018/7/10  上报成功之后，去获取设备信息
//        NettyClient.getInstance().sendMessage(MessageSender.deviceInfo());
//            NettyClient.getInstance().sendMessage(MessageSender.devicePassword());
//            return;
        }
        //TODO: 如果返回错误，根据具体错误情况，判断是否重新上报设备token


    }


    /**
     * 远程开门
     *
     * @param msg
     */
    private static void remoteOpen(String msg) throws InterruptedException {
        MsgResult msgResult = new MsgResult();
        String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);
        if (array.length != ParamIndex.RemoteOpenResult.PARAM_COUNT) {//校验参数个数是否一致
            msgResult.setError(msg, "", "message loss");
            messageFeedback(msgResult);
            return;
        }
        // 使用eventbus通知activity去开门
        EventBus.getDefault().post(new OpenDoorEvent("", ""));
        //请求返回
        Thread.sleep(40000);
        msgResult.setOk(msg, "success");
        messageFeedback(msgResult);

    }


    /**
     * 新增密码
     *
     * @param msg
     */
    private static void addPassword(String msg) {

        MsgResult msgResult = new MsgResult();//消息返回结果

        String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);//使用数组去存储分割后的参数
        //判断消息参数个数是否一致
        if (array.length != ParamIndex.AddPsdResult.PARAM_COUNT) {
            reconnect(); // 断开，重新连接
            return;
        }
        String deviceToken = array[ParamIndex.AddPsdResult.DEVICE_TOKEN];//获取设备token
        String psdStr = array[ParamIndex.AddPsdResult.PASSWORD];//获取所有密码串（需要进一步解析，里面可能包含多个密码）
        if (TextUtils.isEmpty(psdStr)) {//判断密码串是否为空
            msgResult.setError(msg, "01", "message loss");
            messageFeedback(msgResult);
            return;
        }
        String[] psdArray = psdStr.split(SocketContract.ARRAY_SEPARATOR);//解析密码组
        //分别对每个密码进行插入数据库操作，实际上新增密码每次只有一个，
        // 这里增加for循环，是防止误操作而导致异常情况
        for (String item : psdArray) {
            String[] pArray = item.split(SocketContract.PROPERTY_SEPARATOR);//获取单个密码的属性组
            if (pArray.length != 4) {//判断属性组长度是否正确
                msgResult.setError(msg, "01", "message loss");
                messageFeedback(msgResult);
                return;
            }
            //提取密码各个参数
            String password = pArray[0];
            String count = pArray[1];
            String type = pArray[3];
            String date = pArray[2];
            DevicePassword devicePassword = new DevicePassword();
            devicePassword.setDeviceToken(deviceToken);
            devicePassword.setType(type);
            devicePassword.setCount(count);
            devicePassword.setDate(date);
            devicePassword.setPassword(password);
            //判断该密码是否已存在数据库中
            List<DevicePassword> list = LitePal.where("password = ? ", password).find(DevicePassword.class);
            if (list != null && list.size() > 0) {
                DevicePassword dbPassword = list.get(0);
                //判断密码类型是否相同
                if (!TextUtils.equals(dbPassword.getType(), type)) {
                    msgResult.setError(msg, "01", "password already exist");
                    messageFeedback(msgResult);
                    return;
                }
                msgResult.setOk(msg, "success");
                messageFeedback(msgResult);
                return;
            }
            //根据type插入不同类型的密码
            switch (type) {
                case "1"://长期密码
                    if (devicePassword.save()) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "密码新增失败");
                    break;
                case "2"://短期密码
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "date lose");
                        break;
                    }
                    if (devicePassword.save()) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "add failure");
                    break;
                case "3"://次数密码
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "date loss");
                        break;
                    }
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "count loss");
                        break;
                    }
                    if (devicePassword.save()) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "add failure");
                    break;
                default://密码类型错误
                    msgResult.setError(msg, "01", "type error");
                    break;
            }
            messageFeedback(msgResult);//返回服务端消息
        }
    }


    /**
     * 修改密码
     *
     * @param msg
     */
    private static void modifyPassword(String msg) {

        MsgResult msgResult = new MsgResult();//消息返回结果

        String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);//使用数组去存储分割后的参数
        //判断消息参数个数是否一致
        if (array.length != ParamIndex.ModifyPsdResult.PARAM_COUNT) {
            reconnect(); // 断开，重新连接
            return;
        }
        String deviceToken = array[ParamIndex.ModifyPsdResult.DEVICE_TOKEN];//获取设备token
        String psdStr = array[ParamIndex.ModifyPsdResult.PASSWORD];//获取所有密码串（需要进一步解析，里面可能包含多个密码）
        if (TextUtils.isEmpty(psdStr)) {//判断密码串是否为空
            msgResult.setError(msg, "01", "message loss");
            messageFeedback(msgResult);
            return;
        }
        String[] psdArray = psdStr.split(SocketContract.ARRAY_SEPARATOR);//解析密码组
        //分别对每个密码进行插入数据库操作，实际上修改密码每次只有一个，
        // 这里增加for循环，是防止误操作而导致异常情况
        for (String item : psdArray) {
            String[] pArray = item.split(SocketContract.PROPERTY_SEPARATOR);//获取单个密码的属性组
            if (pArray.length != 4) {//判断属性组长度是否正确
                msgResult.setError(msg, "01", "message loss");
                messageFeedback(msgResult);
                return;
            }
            //提取密码各个参数
            String password = pArray[0];
            String count = pArray[1];
            String type = pArray[3];
            String date = pArray[2];
            DevicePassword devicePassword = new DevicePassword();
            devicePassword.setDeviceToken(deviceToken);
            devicePassword.setType(type);
            devicePassword.setCount(count);
            devicePassword.setDate(date);
            devicePassword.setPassword(password);
            //判断该密码是否已存在数据库中
            List<DevicePassword> list = LitePal.where("password = ? ", password).find(DevicePassword.class);
            if (list == null || list.size() == 0) {
                msgResult.setError(msg, "01", "password not exist");
                messageFeedback(msgResult);
                return;
            }
            DevicePassword dbPsd = list.get(0);
            if (!TextUtils.equals(dbPsd.getType(), type)) {
                msgResult.setError(msg, "01", "type error");
                messageFeedback(msgResult);
                return;
            }
            //根据type修改不同类型的密码
            switch (type) {
                case "1"://长期密码
                    if (devicePassword.update(dbPsd.getId()) == 1) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "update failure");
                    break;
                case "2"://短期密码
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "date lose");
                        break;
                    }
                    if (devicePassword.save()) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "add failure");
                    break;
                case "3"://次数密码
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "date loss");
                        break;
                    }
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "count loss");
                        break;
                    }
                    if (devicePassword.update(dbPsd.getId()) == 1) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "update failure");
                    break;
                default://密码类型错误
                    msgResult.setError(msg, "01", "type error");
                    break;
            }
            messageFeedback(msgResult);//返回服务端消息
        }

    }

    /**
     * 删除密码
     *
     * @param msg
     */
    private static void deletePassword(String msg) {

        MsgResult msgResult = new MsgResult();//消息返回结果

        String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);//使用数组去存储分割后的参数
        //判断消息参数个数是否一致
        if (array.length != ParamIndex.DelPsdResult.PARAM_COUNT) {
            reconnect();
            return;
        }
        String deviceToken = array[ParamIndex.DelPsdResult.DEVICE_TOKEN];//获取设备token
        String psdStr = array[ParamIndex.DelPsdResult.PASSWORD];//获取所有密码串（需要进一步解析，里面可能包含多个密码）
        if (TextUtils.isEmpty(psdStr)) {//判断密码串是否为空
            msgResult.setError(msg, "01", "message loss");
            messageFeedback(msgResult);
            return;
        }
        String[] psdArray = psdStr.split(SocketContract.ARRAY_SEPARATOR);//解析密码组
        //分别对每个密码进行插入数据库操作，实际上修改密码每次只有一个，
        // 这里增加for循环，是防止误操作而导致异常情况
        for (String item : psdArray) {
            String[] pArray = item.split(SocketContract.PROPERTY_SEPARATOR);//获取单个密码的属性组
            if (pArray.length != 4) {//判断属性组长度是否正确
                msgResult.setError(msg, "01", "message loss");
                messageFeedback(msgResult);
                return;
            }
            //提取密码各个参数
            String password = pArray[0];
            String count = pArray[1];
            String type = pArray[3];
            String date = pArray[2];
            DevicePassword devicePassword = new DevicePassword();
            devicePassword.setDeviceToken(deviceToken);
            devicePassword.setType(type);
            devicePassword.setCount(count);
            devicePassword.setDate(date);
            devicePassword.setPassword(password);
            //判断该密码是否已存在数据库中
            List<DevicePassword> list = LitePal.where("password = ? ", password).find(DevicePassword.class);
            if (list == null || list.size() == 0) {
                msgResult.setError(msg, "01", "password not exist");
                messageFeedback(msgResult);
                return;
            }
            DevicePassword dbPsd = list.get(0);
            if (!TextUtils.equals(dbPsd.getType(), type)) {
                msgResult.setError(msg, "01", "type error");
                messageFeedback(msgResult);
                return;
            }
            //根据type修改不同类型的密码
            switch (type) {
                case "1"://长期密码
                    if (dbPsd.delete() == 1) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "delete failure");
                    break;
                case "2"://短期密码
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "date lose");
                        break;
                    }
                    if (dbPsd.delete() == 1) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "delete failure");
                    break;
                case "3"://次数密码
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "date loss");
                        break;
                    }
                    if (TextUtils.isEmpty(date)) {
                        msgResult.setError(msg, "01", "count loss");
                        break;
                    }
                    if (dbPsd.delete() == 1) {
                        msgResult.setOk(msg, "success");
                        break;
                    }
                    msgResult.setError(msg, "01", "delete failure");
                    break;
                default://密码类型错误
                    msgResult.setError(msg, "01", "type error");
                    break;
            }
            messageFeedback(msgResult);//返回服务端消息
        }

    }

    /**
     * 消息回馈服务端
     *
     * @param result
     */
    private static void messageFeedback(MsgResult result) {

        String msg = result.getResult().toString();//提取原始消息
        String[] array = msg.split(SocketContract.PROCESS_MESSAGE_SEPARATOR);
        String msgLength = array[2];//获取原始消息字符串长度
        //拼接消息
        StringBuilder sb = new StringBuilder(msg);
        sb.append(SocketContract.MESSAGE_SEPARATOR).append(result.getCode())//返回码
                .append(SocketContract.MESSAGE_SEPARATOR).append(result.getMessage());//错误信息
        //计算拼接后的消息长度
        String realLength = String.format(Locale.getDefault(), "%05d", sb.length());
//        Log.e(TAG, sb.toString().replace(msgLength, realLength));
        //替换消息长度，并发送给服务端
        NettyClient.getInstance().sendMessage(sb.toString().replace(msgLength, realLength));

    }


    /**
     * 重新连接
     */
    private static void reconnect() {
        EventBus.getDefault().post(new ConnectStatusEvent(ConnectStatusEvent.CONNECT_CLOSED));
    }


}
