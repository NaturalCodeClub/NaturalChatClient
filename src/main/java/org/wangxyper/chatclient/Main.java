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

            ChannelFuture future = b.connect("server.mcnatural.top", 1900).sync();
            Scanner scanner = new Scanner(System.in);
            HashMap packetAccess = new HashMap();
            System.out.println("Input your access action,Login:L Register: R");
            switch (scanner.nextLine()) {
                case "L" -> packetAccess.put("head", "LOGIN");
                case "R" -> packetAccess.put("head", "REG");
                default -> packetAccess.put("head", "LOGIN");
            }
            System.out.println("Enter your username");
            String username = scanner.nextLine();
            System.out.println("Enter your password");
            String password = scanner.nextLine();
            packetAccess.put("username",username);
            packetAccess.put("password",password);
            future.channel().writeAndFlush(packetAccess);
            while (scanner.hasNext()){
                HashMap message = new HashMap();
                message.put("head","CHAT");
                message.put("chatmessage",scanner.nextLine());
                future.channel().writeAndFlush(message);
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
