package com.prookie.nettylibrary.netty;


import com.prookie.nettylibrary.event.ConnectStatusEvent;
import com.prookie.nettylibrary.util.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * NettyClientHandler
 * Created by brin on 2018/7/10.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<String> {

    private static final String TAG = NettyClientHandler.class.getSimpleName();
    /**
     * 空闲次数
     */
    private int idle_count = 1;

    /**
     * 发送次数
     */
    private int count = 1;

    /**
     * 循环次数
     */
    private int fcount = 1;

    /**
     * 心跳序列
     */
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(MessageSender.heartBeat() + "\n",
            CharsetUtil.UTF_8));


    /**
     * 通道开启
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Logger.d(TAG, "------------channelActive---------------");
        EventBus.getDefault().post(new ConnectStatusEvent(ConnectStatusEvent.CONNECT_SUCCESS));
        NettyClient.getInstance().sendMessage(MessageSender.deviceTokenReport());
    }

    /**
     * 通道关闭
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Logger.d(TAG, "------------channelInactive---------------");
        EventBus.getDefault().post(new ConnectStatusEvent(ConnectStatusEvent.CONNECT_CLOSED));
//        NettyClient.getInstance().reconnect();
    }

    /**
     * handlerRemoved
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        Logger.d(TAG, "------------handlerRemoved---------------");
//        NettyClient.getInstance().reconnect();
    }


    /**
     * 接受消息
     *
     * @param channelHandlerContext
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        Logger.d(TAG, "------------channelRead0---------------:" + msg);
        MessageProcessor.execute(msg);
    }

    /**
     * 消息接收完成
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        Logger.d(TAG, "------------channelReadComplete---------------");
    }

    /**
     * 异常捕获
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Logger.d(TAG, "------------exceptionCaught---------------:" + cause);
    }

    /**
     * 心跳
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        Logger.d(TAG, "------------userEventTriggered---------------:" + new Date() + "，次数" + fcount);
        IdleStateEvent event = (IdleStateEvent) evt;
        if (IdleState.WRITER_IDLE.equals(event.state())) {  //如果写通道处于空闲状态,就发送心跳命令
//         if(true){   //设置发送次数
            idle_count++;
            ByteBuf buf = HEARTBEAT_SEQUENCE.duplicate();
            System.out.println(buf.toString());
            ctx.channel().writeAndFlush(buf);
            Logger.d(TAG, "userEventTriggered---mChannel:" + ctx.channel());
//                }else{
//                    Log.e(TAG,"不再发送心跳请求了！");
//                }ndFlush(buf);
            fcount++;
        }
    }

}
