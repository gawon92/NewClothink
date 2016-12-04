//package gawonjoo0.newclothink;
//
//import android.util.Log;
//
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//
//import java.io.BufferedReader;
//import java.io.PrintWriter;
//import java.net.Socket;
//
//import cz.msebera.android.httpclient.Header;
//
///**
// * Created by USER on 2016-12-04.
// */
//public class GetArduinoActivity {
//
//    String result="";
//    Thread thread;
//
//    public GetArduinoActivity(){
//        thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                RequestParams params = new RequestParams();
//                params.add("cmd", "humidityGet");
//
//                MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        Log.i("아두이노 값 가져오기","성공");
//                        result=new String(responseBody);
//                        Log.i("현재 습도는 ",result+"!!");
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                        Log.i("아두이노 값 가져오기","실패");
//                    }
//                });
//
//
//            }
//        });
//        thread.start();
//
//    }
//
//    public String getArduinoData(){
//        return result;
//    }
//
//}
