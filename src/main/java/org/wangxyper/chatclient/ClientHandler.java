package org.wangxyper.chatclient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Date;
import java.util.HashMap;

public class ClientHandler extends SimpleChannelInboundHandler<HashMap> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Welcome to natural group!");
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,HashMap msg) throws Exception {
        System.out.println("["+new Date()+"]"+msg.get("chatmessage"));
    }

}