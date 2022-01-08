package org.wangxyper.chatclient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Date;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Welcome to natural group!");
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx,String msg) throws Exception {
        System.out.println("["+new Date()+"]"+msg);
    }

}