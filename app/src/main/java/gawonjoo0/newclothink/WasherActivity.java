package gawonjoo0.newclothink;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jooyoung on 2016-10-22.
 */

public class WasherActivity extends Fragment {

    int vibrate=0;
    int database_flag=0;
    ArrayList<Integer> countList=new ArrayList<Integer>();

    String arduino_data="";

    LinearLayout laundryWaitLinear;
    RelativeLayout laundryStartRelative;
    ImageView washerOnImage;
    TextView washerOnTv;

    int washerImageCount=0;
    int washerOnFlag=0;     //나중에 진동 들어오면 플래그 바껴서 세탁기화면 뜨고 다시 대기화면 바꾸는 기준이 되는 변수
    int alarmFlag=1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.washer_layout,container,false);

        laundryWaitLinear=(LinearLayout) view.findViewById(R.id.laundryWaitLinear);
        laundryStartRelative=(RelativeLayout) view.findViewById(R.id.laundryStartRelative);
        washerOnImage=(ImageView) view.findViewById(R.id.washerOnImage);
        washerOnTv=(TextView) view.findViewById(R.id.washerOnTv);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    RequestParams params=new RequestParams();
                    params.add("cmd", "getWasherArduino");

                    MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            arduino_data = new String(responseBody);
                            vibrate = Integer.parseInt(arduino_data.trim());

                            if(vibrate==0){
                                countList.add(1);
                            }else{
                                if(countList.size()<10){
                                    countList=new ArrayList<Integer>();

                                    if(database_flag==0){
                                        RequestParams params2=new RequestParams();

                                        Calendar calendar=Calendar.getInstance();
                                        params2.add("cmd", "setWasherDate");
                                        params2.add("date1",calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1));
                                        params2.add("date2", calendar.get(Calendar.DATE) + "");

                                        MyFirstRestClient.post("/pb", params2, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                Log.i("세탁한 날 보내기", "성공");

                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                Log.i("세탁한 날 보내기", "실패");
                                            }
                                        });
                                        database_flag=1;

                                    }
                                }

                            }

                            if(countList.size()>=10){  // 0이 연속적으로 10번이상 들어오는 경우
                                countList=new ArrayList<Integer>();
                                database_flag=0;
                                Log.i("10 열번 이상 들어옴", "ㅇㅇ");
                                if(alarmFlag==1 && washerOnFlag==1){
                                    AlarmManager alarmManager=(AlarmManager) MainActivity.mainContext.getSystemService(Context.ALARM_SERVICE);
                                    Intent intent=new Intent(MainActivity.mainContext,BroadcastClass3.class);

                                    PendingIntent sender=PendingIntent.getBroadcast(MainActivity.mainContext,0,intent,0);
                                    Calendar calendar=Calendar.getInstance();
                                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

                                    alarmFlag=0;
                                    washerOnFlag=0;
                                }

                            }



                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.i("아두이노 진동 값 가져오기", "실패");
                        }
                    });

                    Log.i("진동값",vibrate+"");
                    if(vibrate>0){
                        try {
                            washerOnFlag=1;
                            handler.sendMessage(handler.obtainMessage());
                            Thread.sleep(400);

                        } catch (Throwable t) {
                        }
                    }else{
                        try {
                            if(washerOnFlag==0){
                                alarmFlag=1;
                            }
                            handler.sendMessage(handler.obtainMessage());
                            Thread.sleep(400);
                        } catch (Throwable t) {
                        }
                    }

                }
            }
        }).start();

        return view;
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            washerOn();
        }
    };

    private void washerOn(){
        if(washerImageCount==9){
            washerImageCount=0;
        }else{
            washerImageCount++;
        }

        if(washerOnFlag!=0){
            laundryWaitLinear.setVisibility(View.GONE);
            laundryStartRelative.setVisibility(View.VISIBLE);

            if(washerImageCount==0){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer01));
                washerOnTv.setTextColor(Color.parseColor("#BCBEC0"));
            }else if(washerImageCount==1){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer02));
            }else if(washerImageCount==2){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer03));
            }else if(washerImageCount==3){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer04));
            }else if(washerImageCount==4){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer05));
            }else if(washerImageCount==5){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer06));
                washerOnTv.setTextColor(Color.parseColor("#FFFFFF"));
            }else if(washerImageCount==6){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer07));
            }else if(washerImageCount==7){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer08));
            }else if(washerImageCount==8){
                washerOnImage.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washer09));
            }
        }else{
            laundryStartRelative.setVisibility(View.GONE);
            laundryWaitLinear.setVisibility(View.VISIBLE);
        }
    }

}
