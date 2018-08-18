package com.prookie.nettylibrary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.prookie.nettylibrary.event.ConnectStatusEvent;
import com.prookie.nettylibrary.event.OpenDoorEvent;
import com.prookie.nettylibrary.litebean.DevicePassword;
import com.prookie.nettylibrary.netty.MessageSender;
import com.prookie.nettylibrary.netty.NettyClient;
import com.prookie.nettylibrary.netty.NettyService;
import com.prookie.nettylibrary.util.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, NettyService.class));
            }
        });

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NettyClient.getInstance().sendMessage(MessageSender.deviceTokenReport());
            }
        });

        findViewById(R.id.btn_getPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String count = null;
//                String date = null;
//                DevicePassword devicePassword = new DevicePassword();
//                devicePassword.setDeviceToken("123456789");
//                devicePassword.setType("1");
//                devicePassword.setCount(count);
//                devicePassword.setDate(date);
//                devicePassword.setPassword("999999");
//                devicePassword.saveThrows();

                List<DevicePassword> list = LitePal.findAll(DevicePassword.class);
                for (DevicePassword item : list) {
                    Logger.d(TAG, "NettyClient:密码：" + item.toString());
                }
            }
        });

        findViewById(R.id.btn_disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NettyClient.getInstance().disconnect();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 订阅开门事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OpenDoorEvent event) {
        if (event != null) {
            Toast.makeText(this, "开门成功", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 订阅连接状态监听事件
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConnectStatusEvent event) {
        if (event != null) {
            if (event.getStatus() == ConnectStatusEvent.CONNECT_SUCCESS) {
                Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
