package gawonjoo0.newclothink;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by USER on 2016-12-04.
 */
public class GetArduinoData extends Activity{
//    Thread thread;
//    static Socket socket = null;
//    static BufferedReader socket_in;
//    public static String data="";
//    final static String SERVER_ADDR = "172.20.10.2";
//    final static int PORT_NO = 8888;
//
//    public GetArduinoData(){
//        ConnectSocket conSock = new ConnectSocket();
//        conSock.setDaemon(true);
//        conSock.start();
//    }
//
//    static class ConnectSocket extends Thread{
//        public void run(){
//            try{
//                socket=new Socket(SERVER_ADDR,PORT_NO);
//                socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
//                try{
//                    while(true) {
//                        data = socket_in.readLine();
//                        Log.i("data1는 ", data);
//                        Thread.sleep(2000);
//                    }
//
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//        }
//
//        public String getArduino(){
//            Log.i("data2는 ", data);
//            return data;
//        }
//    }
//

}
