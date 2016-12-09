package gawonjoo0.newclothink;

import android.os.Looper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by USER on 2016-10-19.
 */
public class MyFirstRestClient {
//    private static final String BASE_URL="http://172.30.1.32:7070/Middle";
    private static final String BASE_URL="http://192.168.43.99:7070/Middle";

    public static AsyncHttpClient client=new AsyncHttpClient();
    public static AsyncHttpClient client2=new SyncHttpClient();

    public static  void setCookieStore(PersistentCookieStore cookieStore){
        getClient().setCookieStore(cookieStore);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
//        client.get(getAbsoluteUrl(url), params, responseHandler);
        getClient().get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
//        client.get(getAbsoluteUrl(url), params, responseHandler);
        getClient().get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static AsyncHttpClient getClient(){
        if(Looper.myLooper()==null){
            return client2;
        }else{
            return client;
        }
    }

    private static String getAbsoluteUrl(String relativeUrl){
        return BASE_URL+relativeUrl;
    }

}
