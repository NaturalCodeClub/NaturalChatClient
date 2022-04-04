package org.wangxyper.chatclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        EventLoopGroup group =new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            p.addLast("encoder", new ObjectEncoder());
                            p.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = b.connect("server.mcnatural.top", 192).sync();
            HashMap h = new HashMap();
            h.put("head","REG");
            h.put("username","JL_NPE");
            h.put("password","2333");
            future.channel().writeAndFlush(h);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                HashMap a = new HashMap();
                a.put("head","CHAT");
                a.put("chatmessage",scanner.nextLine());
                future.channel().writeAndFlush(a);
            }
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}
