package gawonjoo0.newclothink;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity implements View.OnClickListener{


    Fragment fr1;
    Fragment fr2;
    Fragment fr3;

    ExpandableListView expandableListView;

    ImageButton closetBtn;
    ImageButton washerBtn;
    ImageButton settingBtn;

    ImageView weatherIconImage;

    int threadStopFlag=1;
    int n=0;

    static int closetCount=0;
    static int washerCount=0;

    String str1="흰옷은 흰옷끼리";
    String str2="검은옷은 검은옷끼리";
    String str3="빨간옷은 빨간옷끼리";
    String str4="노란옷은 노란옷끼리";

    private ArrayList<String> arrayGroup=new ArrayList<String>();;
    private HashMap<String, ArrayList<String>> arrayChild=new HashMap<String, ArrayList<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        threadStopFlag=0;
        new Thread(tipThreadRun).start();

        closetBtn.setOnClickListener(this);
        washerBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                try {
                    threadStopFlag=1;
//                    Log.i("그룹상태",""+threadStopFlag);
                    Thread.interrupted();
                }catch(Throwable t){
                }
                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                threadStopFlag=0;
//                Log.i("차일드상태",""+threadStopFlag);
                new Thread(tipThreadRun).start();
                return false;
            }
        });


    }

    @Override
    protected void attachBaseContext(Context newBase) { //Application 클래스 (폰트 적용)
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
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
                            arrayGroup.clear();
                            arrayChild.clear();
                            setArrayData();
                            expandableListView.setAdapter(new ListViewAdapter(getApplicationContext(), arrayGroup, arrayChild));
                        }
                    }
                });
            }
        }
    };


    private void setArrayData(){
//        String tipData="str"+n;   // 저렇게 입력하면 str1의 값을 갖고올 수 있을 줄 알았는데 그냥 문자열 str1 이 들어가서 출력됨 ㅜㅜ
//        arrayGroup.add(tipData);

        String tipData="";

        if(n==1){
            tipData=str1;
        }else if(n==2){
            tipData=str2;
        }else if(n==3){
            tipData=str3;
        }else if(n==4){
            tipData=str4;
        }

        arrayGroup.add(tipData.toString());
        ArrayList<String> arr1=new ArrayList<String>();

        if(tipData.equals(str1)){
            arr1.add("하얗게 하얗게 히히히히히");
        }else if(tipData.equals(str2)){
            arr1.add("까맣게 까맣게 어둡게 어둡게");
        }else if(tipData.equals(str3)){
            arr1.add("빨강은 강렬하죠");
        }else if(tipData.equals(str4)){
            arr1.add("미쳐써요 주영이는");
        }

        arrayChild.put(arrayGroup.get(0),arr1);
    }

    private void updateThread(){
        if(n==4){   // 정보 데이터 개수까지
            n=1;
        }else{
            n++;
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
//                fr=new WasherActivity();
                break;
            case R.id.settingBtn:
                settingBtn.setBackgroundResource(R.color.clothinkMainColor);
                settingBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.settingbtnfocus));

                closetBtn.setBackgroundResource(R.color.clothinkWhite);
                closetBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.closetbtn));

                washerBtn.setBackgroundResource(R.color.clothinkWhite);
                washerBtn.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.washerbtn));

                fragmentTransaction.replace(R.id.fragmentLayout, fr3);

//                fr=new SettingActivity();
                break;
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
