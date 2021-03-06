package gawonjoo0.newclothink;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity implements View.OnClickListener{
    static Context mainContext;
    static int alarm_flag1=1, alarm_flag2=1;
    Socket socket = null;
    BufferedReader socket_in;
    PrintWriter socket_out;

    float temperature;

    Fragment fr1;
    Fragment fr2;
    Fragment fr3;

    ExpandableListView expandableListView;

    ImageButton closetBtn;
    ImageButton washerBtn;
    ImageButton settingBtn;

    ImageView weatherIconImage;

    TextView currentTempTv, minTempTv, maxTempTv;

    int threadStopFlag=1;
    int date_flag=0;    //  1일에 한번만 주기 가져오게 한다

    private static int n=0;
    int tipCount=0;

    static int dataNum=0;
    static int washerCount=0;

    private ArrayList<String> tipArray=new ArrayList<String>(); //DB에서 tip 가지고오는 Array
    private ArrayList<String> tipDetailArray=new ArrayList<String>();   //tip Array에서 내용 넣는 Array
    private ArrayList<String> arrayGroup=new ArrayList<String>();   //tip Array에서 제목 넣는 Array
    private ArrayList<String> arr=new ArrayList<String>(); //arrChild의 key에 String으로 넣기 위해서 여기 넣엇다가 뺌
    private HashMap<String, ArrayList<String>> arrayChild=new HashMap<String, ArrayList<String>>();

    static ClosetDto infoDto;
    static ArrayList<String> allList = new ArrayList<String>();
    static ArrayList <ClosetDto> closetInfo;

    static int washerFreq=0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder alertDlg=new AlertDialog.Builder(this);

            alertDlg.setMessage("종료 하시겠습니까?");
            alertDlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            alertDlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alert=alertDlg.create();
            alert.setTitle("앱 종료");
            alert.show();

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) { //Application 클래스 (폰트 적용)
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext=getApplicationContext();
        closetInfo=new ArrayList<ClosetDto>();

        currentTempTv=(TextView)findViewById(R.id.currentTempTv);
        minTempTv=(TextView)findViewById(R.id.minTempTv);
        maxTempTv=(TextView)findViewById(R.id.maxTempTv);

        RequestParams params = new RequestParams();
        params.add("cmd", "tipGet");

        //팁 가져오는 부분
        MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                Log.i("여기들어옴","팁가져오기 성공");
                String info = new String(bytes);
                StringTokenizer stk1 = new StringTokenizer(info, ":");
                while (stk1.hasMoreTokens()) {
                    tipArray.add(stk1.nextToken());
                }

                n = tipArray.size()-1;

                for (int k = 0; k < n; k++) {
                    StringTokenizer stk2 = new StringTokenizer(tipArray.get(k).toString().trim(), "^");
                    while(stk2.hasMoreTokens()){
                        arrayGroup.add(stk2.nextToken().toString().trim());
                        tipDetailArray.add(stk2.nextToken().toString().trim());
                    }
                }

                //입력되어 있는 전체 데이터 가져오는 부분
                RequestParams params2=new RequestParams();
                params2.add("cmd", "dataGet");

                MyFirstRestClient.post("/pb", params2, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                        String info = new String(bytes);
                        //이름, fur, Leather, silk, knit, 아두이노 ^

                        if(info.equals("0")){
                            dataNum=0;
                        }else{
                            StringTokenizer stk1 = new StringTokenizer(info, "^");
                            while (stk1.hasMoreTokens()) {
                                allList.add(stk1.nextToken());
                            }
                            dataNum = allList.size() - 1;

                            for (int k = 0; k < dataNum; k++) {

                                StringTokenizer stk2 = new StringTokenizer(allList.get(k), ",");
                                infoDto = new ClosetDto();
                                infoDto.setName(stk2.nextToken().trim());
                                infoDto.setFur(Integer.parseInt(stk2.nextToken().trim()));
                                infoDto.setLeather(Integer.parseInt(stk2.nextToken().trim()));
                                infoDto.setSilk(Integer.parseInt(stk2.nextToken().trim()));
                                infoDto.setKnit(Integer.parseInt(stk2.nextToken().trim()));
                                infoDto.setArduino(stk2.nextToken().trim());
                                closetInfo.add(infoDto);
                                Log.i(closetInfo.get(k).getName(), "");
                            }
                        }

                        fr1=new ClosetActivity();

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentLayout, fr1);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                        threadStopFlag=0;
                        new Thread(tipThreadRun).start();

                        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                try {
                                    threadStopFlag = 1;
//                    Log.i("그룹상태",""+threadStopFlag);
                                    Thread.interrupted();
                                } catch (Throwable t) {
                                }
                                return false;
                            }
                        });

                        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                threadStopFlag = 0;
//                Log.i("차일드상태",""+threadStopFlag);
                                new Thread(tipThreadRun).start();
                                return false;
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();
                    }
                });


//                Calendar calendar=Calendar.getInstance();
//                if(calendar.get(Calendar.DATE)==1&&date_flag==0){
                    RequestParams params3=new RequestParams();
                    params3.add("cmd", "getWasherFrq");

                    MyFirstRestClient.post("/pb", params3, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                            String temp = new String(bytes);
                            washerFreq=Integer.parseInt(temp.trim());
//                            Log.i("주기 잘가져와짐", washerFreq+" 이거임");
                            date_flag=1;

                            //하루 전날 알림
                            AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent=new Intent(MainActivity.mainContext,BroadcastClass2.class);
                            PendingIntent sender=PendingIntent.getBroadcast(MainActivity.mainContext, 0, intent, 0);

                            if(alarm_flag1==1){
                                Calendar calendar=Calendar.getInstance();
                                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);

//                                Date d=new Date(calendar.getTimeInMillis()+((washerFreq-1)*24*60*60*1000));
//                                calendar.setTime(d);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                            }


                            //월요일(주의 시작) 알림
                            if(alarm_flag2==1){
                                Calendar calendar=Calendar.getInstance();
                                if(calendar.get(Calendar.DAY_OF_WEEK)==2){
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

                                }
                            }



                        }

                        @Override
                        public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();
                        }
                    });

//                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();

            }
        });


        expandableListView=(ExpandableListView) findViewById(R.id.expandableListView1);
        weatherIconImage=(ImageView) findViewById(R.id.weatherIconImage);

        closetBtn=(ImageButton) findViewById(R.id.closetBtn);
        washerBtn=(ImageButton) findViewById(R.id.washerBtn);
        settingBtn=(ImageButton) findViewById(R.id.settingBtn);

        fr1=new ClosetActivity();
        fr2=new WasherActivity();
        fr3=new SettingActivity();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLayout, fr1);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        closetBtn.setOnClickListener(this);
        washerBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

    }

    Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            updateThread();
        }
    };

    Runnable tipThreadRun=new Runnable(){

        public void run() {
            // TODO Auto-generated method stub
            while(threadStopFlag!=1){
                try{
                    handler1.sendMessage(handler1.obtainMessage());
                    Thread.sleep(1500);

                }catch(Throwable t){}

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(threadStopFlag!=1) {
                            arr.clear();
                            arrayChild.clear();
                            setArrayData();
                            expandableListView.setAdapter(new ListViewAdapter(getApplicationContext(), arr, arrayChild));
                            GetWeatherActivity getWeatherActivity=new GetWeatherActivity();
                            currentTempTv.setText(getWeatherActivity.getTemperature());
                            minTempTv.setText(getWeatherActivity.getMinTemperature());
                            maxTempTv.setText(getWeatherActivity.getMaxTemperature());

                        }
                    }
                });
            }
        }
    };

    private void setArrayData(){
        ArrayList<String> arr1=new ArrayList<String>();
        arr.add(arrayGroup.get(tipCount).toString());
        arr1.add(tipDetailArray.get(tipCount).toString());

        arrayChild.put(arr.get(0),arr1);
    }

    private void updateThread(){
        if(tipCount==9){   // 정보 데이터 개수까지
            tipCount=0;
        }else{
            tipCount++;
        }
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        switch (v.getId()){
            case R.id.closetBtn:
                closetBtn.setBackgroundResource(R.color.clothinkMainColor);
                closetBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.closetbtnfocus));

                washerBtn.setBackgroundResource(R.color.clothinkWhite);
                washerBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washerbtn));

                settingBtn.setBackgroundResource(R.color.clothinkWhite);
                settingBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.settingbtn));

                fr1=new ClosetActivity();
                fragmentTransaction.replace(R.id.fragmentLayout, fr1);
                break;
            case R.id.washerBtn:
                washerBtn.setBackgroundResource(R.color.clothinkMainColor);
                washerBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washerbtnfocus));

                closetBtn.setBackgroundResource(R.color.clothinkWhite);
                closetBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.closetbtn));

                settingBtn.setBackgroundResource(R.color.clothinkWhite);
                settingBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.settingbtn));

                fragmentTransaction.replace(R.id.fragmentLayout, fr2);
                washerCount++;
                break;
            case R.id.settingBtn:
                settingBtn.setBackgroundResource(R.color.clothinkMainColor);
                settingBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.settingbtnfocus));

                closetBtn.setBackgroundResource(R.color.clothinkWhite);
                closetBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.closetbtn));

                washerBtn.setBackgroundResource(R.color.clothinkWhite);
                washerBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washerbtn));

                fragmentTransaction.replace(R.id.fragmentLayout, fr3);

                break;
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



}
