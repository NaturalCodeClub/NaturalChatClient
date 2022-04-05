package org.wangxyper.chatclient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javazoom.jl.player.Player;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class ClientHandler extends SimpleChannelInboundHandler<HashMap> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.fireChannelActive();
    }
    public static String getRandomString(int minLength,int maxLength) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        int length=random.nextInt(maxLength) % (maxLength-minLength+1)+minLength;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
    private static List<Thread> players = new CopyOnWriteArrayList<>();
    public static void handleMusicPacket(String mp3URL){
        File cache = new File(getRandomString(5,10));
        try {
            DownloadFile(mp3URL,cache);
            BufferedInputStream s = new BufferedInputStream(new FileInputStream(cache));
            Thread t = new Thread(()->{
                try {
                    Player player = new Player(s);
                    System.out.println("[Player] playing...");
                    player.play(Integer.MAX_VALUE);
                    Thread.sleep(Long.MAX_VALUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            t.start();
            players.add(t);
        } catch (ExecutionException | InterruptedException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    public static void DownloadFile(String urlStr, File savefile) throws ExecutionException, InterruptedException {
        Future<Boolean> future = executor.submit(()->{
            try {
                if (savefile.exists()) {
                    savefile.delete();
                }
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                InputStream inputStream = conn.getInputStream();
                byte[] getData = readInputStream(inputStream);
                File file = savefile;
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(getData);
                fos.close();
                inputStream.close();
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        });
        boolean isFinished = future.get();
        if(isFinished){System.out.println("[Player]File downloaded!Url was: "+urlStr);}
    }
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte['Ѐ'];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx,HashMap msg) {
        if(msg.get("head").equals("MUSIC")){
            if(!players.isEmpty()){
                for(Thread t : players){
                    t.interrupt();
                    players.remove(t);
                }
            }
            handleMusicPacket(msg.get("url").toString());
            return;
        }
        System.out.println("["+new Date()+"]"+msg.get("chatmessage"));
    }

}