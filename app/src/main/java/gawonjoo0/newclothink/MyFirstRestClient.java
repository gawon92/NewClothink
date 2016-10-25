package gawonjoo0.newclothink;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by USER on 2016-10-19.
 */
public class MyFirstRestClient {
//    private static final String BASE_URL="http://172.30.1.39:7777/Middle";
    private static final String BASE_URL="http://192.168.0.19:7777/Middle";

    private static AsyncHttpClient client=new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl){
        return BASE_URL+relativeUrl;
    }

}
