package com.prookie.nettylibrary.netty;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


import android.support.annotation.Nullable;
import android.util.Log;

import com.prookie.nettylibrary.event.ConnectStatusEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * NettyService
 * Created by brin on 2018/7/10.
 */
public class NettyService extends Service {

    private static final String TAG = NettyService.class.getSimpleName();

    //    private ScheduledExecutorService mScheduledExecutorService;

//    private void shutdown() {
//        if (mScheduledExecutorService != null) {
//            mScheduledExecutorService.shutdown();
//            mScheduledExecutorService = null;
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBus.getDefault().register(this);
        Log.e(TAG, "ServiceMain:threadName:" + Thread.currentThread().getName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "ServiceSubThread:threadName:" + Thread.currentThread().getName());
                NettyClient.getInstance().connect();
            }
        }).start();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        NettyClient.getInstance().setReconnectNum(0);
        NettyClient.getInstance().disconnect();

    }

    /**
     * 连接状态监听事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(ConnectStatusEvent event) {
        if (event.getStatus() == 0) {
            NettyClient.getInstance().reconnect();
        }
    }


}
