package gawonjoo0.newclothink;

/**
 * Created by USER on 2016-12-04.
 */
import android.util.Log;

import java.io.BufferedReader;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.InetAddress;
        import java.net.ServerSocket;
        import java.net.Socket;

public class HumidityGet{

    public static int closet_humidty;

    public static final int ServerPort = 7369;
    public static final String ServerIP = "192.168.10.39";

    Thread thread;

    public HumidityGet(){
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("S:Connecting...");
                    ServerSocket serverSocket = new ServerSocket(ServerPort);
                    BufferedReader br = null;

                    while (true) {
                        String str = null;
                        Socket client = serverSocket.accept();
                        InetAddress inetAddress = client.getInetAddress();
                        // System.out.println(inetAddress.getHostAddress());
                        // System.out.println("S:Receiving...");
                        InputStream is = client.getInputStream();
                        InputStreamReader ir = new InputStreamReader(is);
                        br = new BufferedReader(ir);

                        String str1 = "";

                        try {
                            while ((str1 = br.readLine()) != null) {
                                closet_humidty=(int)Double.parseDouble(new String(str1));
                                Log.i("아두이노 값",closet_humidty+"입니다!");
                            }

                        } catch (Exception e) {
                            System.out.println("S:Error");
                            e.printStackTrace();
                        } finally {
                            br.close();
                            ir.close();
                            client.close();
                            // System.out.println("S:Done.");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("S: ERROR");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try{
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getArduinoData(){
        return closet_humidty;
    }

}