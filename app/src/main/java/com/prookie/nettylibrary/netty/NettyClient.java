package com.prookie.nettylibrary.netty;

import android.util.Log;

import com.prookie.nettylibrary.util.Logger;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * NettyClient
 * Created by brin on 2018/7/10.
 */

public class NettyClient {

    private static final String TAG = NettyClient.class.getSimpleName();

    private EventLoopGroup mEventLoopGroup;
    private long reconnectIntervalTime = 2000;
    private boolean isConnect = false;
    private int reconnectNum = Integer.MAX_VALUE;
    private Channel mChannel;


    private static NettyClient INSTANCE;

    public static NettyClient getInstance() {
        if (null == INSTANCE) {
            synchronized (NettyClient.class) {
                if (null == INSTANCE) {
                    INSTANCE = new NettyClient();
                }
            }
        }
        return INSTANCE;
    }


    /**
     * ChannelInitializer
     */
    private ChannelInitializer<SocketChannel> mChannelInitializer = new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();
            pipeline.addLast(new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS));
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
            pipeline.addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
            pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8));
            pipeline.addLast(new NettyClientHandler());
        }
    };

    int reCount = 0;

    /**
     * ChannelFutureListener
     */
    private ChannelFutureListener mChannelFutureListener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (channelFuture.isSuccess()) {
                mChannel = channelFuture.channel();
//                Log.e(TAG, "连接成功");
            } else {
                isConnect = false;
                Log.e(TAG, "连接失败");
                Thread.sleep(30000);//休眠20s,继续连接
                Log.e(TAG, "ReConnectThreadName:" + Thread.currentThread().getName());
                reconnect();
                reCount++;
                Log.e(TAG, "连接失败后，继续连接:" + reCount);
            }
        }
    };


    /**
     * 连接
     */
    public void connect() {
        mEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap().group(mEventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .handler(mChannelInitializer);
        bootstrap.connect(Const.HOST, Const.TCP_PORT).addListener(mChannelFutureListener);

    }


    /**
     * 断开连接
     */
    public void disconnect() {
        mEventLoopGroup.shutdownGracefully();
    }


    /**
     * 重新连接
     */
    public void reconnect() {
        if (reconnectNum > 0) {
            reconnectNum--;
            try {
                Thread.sleep(reconnectIntervalTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "------------ reconnect ----------------");
            disconnect();
            connect();
        } else {
            disconnect();
        }
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(String message) {

        message = message + "\n";
        if (null == mChannel) {
            Logger.d(TAG, "send channel is null");
            reconnect();
            return;
        }
        if (!mChannel.isWritable()) {
            Log.e(TAG, "send: channel is not Writable");
            reconnect();
            return;
        }
        if (!mChannel.isActive()) {
            reconnect();
            Log.e(TAG, "send: channel is not active!");
            return;
        }
        mChannel.writeAndFlush(message);
        Logger.d(TAG, "sendMessage:" + message);
    }

    public void setReconnectNum(int reconnectNum) {
        this.reconnectNum = reconnectNum;
    }

    public void setReconnectIntervalTime(long reconnectIntervalTime) {
        this.reconnectIntervalTime = reconnectIntervalTime;
    }


}
