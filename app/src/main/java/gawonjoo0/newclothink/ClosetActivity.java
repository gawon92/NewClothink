package gawonjoo0.newclothink;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jooyoung on 2016-10-22.
 */

public class ClosetActivity extends Fragment {

    public int count1=0;

    int dataNum=MainActivity.dataNum;
    public static int texture=0;

    CheckBox check1,check2,check3,check4;

    private Button[] closet=new Button[4];
    private Button [] addbtn=new Button[4];
    Button cancelButton;

    private ClosetDto infoDto;

    StringTokenizer stk1,stk2;

    String arduino_data="";
    public static int [] humidity=new int[4];

    public static ArrayList <ClosetDto> closetInfo=MainActivity.closetInfo;

    private int result=0;

    ClosetDto closetDto;

    LinearLayout closetInside,buttonLinear;
    TextView closetName, humi_TextView, humi_State;

    //아두이노 이름 매칭해줘야 되서 만든 String배열
    private String[] arduino_name={"arduino1", "arduino2", "arduino3", "arduino4"};
    private int[] arduino_state={0,0,0,0};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.closet_layout,container,false);

        closet[0]=(Button) view.findViewById(R.id.closet1);
        closet[1]=(Button) view.findViewById(R.id.closet2);
        closet[2]=(Button) view.findViewById(R.id.closet3);
        closet[3]=(Button) view.findViewById(R.id.closet4);

        addbtn[0]=(Button) view.findViewById(R.id.addbtn1);
        addbtn[1]=(Button) view.findViewById(R.id.addbtn2);
        addbtn[2]=(Button) view.findViewById(R.id.addbtn3);
        addbtn[3]=(Button) view.findViewById(R.id.addbtn4);

        check1=(CheckBox) view.findViewById(R.id.check1);
        check2=(CheckBox) view.findViewById(R.id.check2);
        check3=(CheckBox) view.findViewById(R.id.check3);
        check4=(CheckBox) view.findViewById(R.id.check4);

        viewDisplay(dataNum);

        for(int i=0;i<dataNum;i++){
            closet[i].setTextColor(Color.BLACK);
            closet[i].setText(closetInfo.get(i).getName());
        }

        for(int i=0;i<4;i++){
           humidity[i]=0;
        }

        closetInside=(LinearLayout)view.findViewById(R.id.closetInside);
        buttonLinear=(LinearLayout)view.findViewById(R.id.buttonLinear);
        closetName=(TextView)view.findViewById(R.id.closetName);
        humi_TextView=(TextView)view.findViewById(R.id.humi_TextView);
        humi_State=(TextView)view.findViewById(R.id.humi_State);

        cancelButton=(Button)view.findViewById(R.id.cancelButton);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    RequestParams params=new RequestParams();
                    params.add("cmd", "getHumidityArduino");

                    MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            arduino_data = new String(responseBody);
                            String[] data_split = (arduino_data).split(",");
//                          //습도값 1, 습도값2, 습도값3, 습도값4 이 순으로 들어옴
                            for(int i=0;i<4;i++){
                                humidity[i]=Integer.parseInt(data_split[i].trim());
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.i("아두이노 습도 값 가져오기", "실패");
                        }
                    });

                    try {
                        if(humidity[0]>=15||humidity[1]>=15||humidity[2]>=15||humidity[3]>=15){
                            handler.sendMessage(handler.obtainMessage());
                        }
                        Thread.sleep(400);
                    } catch (Throwable t) {
                    }

                }
            }
        }).start();

        for(count1=0;count1<4;count1++){
            addbtn[count1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final EditText closet_name = new EditText(getActivity().getApplicationContext());
                    closet_name.setTextColor(Color.parseColor("#4d4d4d"));
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("옷장정보 입력").setMessage("옷장의 이름을 지정해주세요!")
                            .setNegativeButton("Yes!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    closetDto = new ClosetDto();
                                    closetDto.setName(closet_name.getText().toString());
                                    closetDto.setFur(0);
                                    closetDto.setKnit(0);
                                    closetDto.setLeather(0);
                                    closetDto.setSilk(0);

                                    int index=0;

                                    if(dataNum==0){
                                        index=0;
                                    }else{
                                        index=findArduino();
                                    }
                                    Log.i("인덱스는",index+"");
                                    closetDto.setArduino(arduino_name[index]);
                                    Log.i("들어간 아두이노 이름은", arduino_name[index]);

                                    closetInfo.add(closetDto);

                                    dataNum += 1;
                                    MainActivity.dataNum=dataNum;

                                    viewDisplay(dataNum);

                                    RequestParams params = new RequestParams();
                                    params.add("cmd", "closetInfoInsert");
                                    params.add("name", closetDto.getName());
                                    params.add("arduino",closetDto.getArduino());

                                    MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                            String info = new String(bytes);
                                            result = Integer.parseInt(info.trim());

                                            if (result != 0) {
                                                Toast.makeText(getActivity().getApplicationContext(), "[ " + closetDto.getName() + " ]의 옷장 만들어짐!", Toast.LENGTH_SHORT).show();
                                            }

                                            closet[dataNum-1].setText(closetInfo.get(dataNum-1).getName());

                                        }

                                        @Override
                                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                            Toast.makeText(getActivity().getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();

                                        }
                                    });


                                }
                            })
                            .setPositiveButton("No!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setView(closet_name).show();

                }

            });
        }

       closet[0].setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               closetInside.setVisibility(View.VISIBLE);
               buttonLinear.setVisibility(View.INVISIBLE);
               closetName.setText(closetInfo.get(0).getName());

               int temp_index=0;
               for(int i=0;i<arduino_name.length;i++){
                   if(closetInfo.get(0).getArduino().equals(arduino_name[i])){
                       temp_index=i;
                       break;
                   }
               }
               humi_TextView.setText(humidity[temp_index]+"");

               if(humidity[temp_index]>=15&&humidity[temp_index]<20){
                   humi_State.setText("주의");
                   humi_State.setTextColor(Color.parseColor("#FFCD12"));
               }else if(humidity[temp_index]>=20){
                   humi_State.setText("위험");
                   humi_State.setTextColor(Color.RED);
               }else{
                   humi_State.setText("안전");
                   humi_State.setTextColor(Color.parseColor("#07621A"));
               }

               viewCheck(0);
           }
       });

        closet[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closetInside.setVisibility(View.VISIBLE);
                buttonLinear.setVisibility(View.INVISIBLE);
                closetName.setText(closetInfo.get(1).getName());

                int temp_index=0;
                for(int i=0;i<arduino_name.length;i++){
                    if(closetInfo.get(1).getArduino().equals(arduino_name[i])){
                        temp_index=i;
                        break;
                    }
                }
                humi_TextView.setText(humidity[temp_index]+"");

                if(humidity[temp_index]>=15&&humidity[temp_index]<20){
                    humi_State.setText("주의");
                    humi_State.setTextColor(Color.parseColor("#FFCD12"));
                }else if(humidity[temp_index]>=20){
                    humi_State.setText("위험");
                    humi_State.setTextColor(Color.RED);
                }else{
                    humi_State.setText("안전");
                    humi_State.setTextColor(Color.parseColor("#07621A"));
                }

                viewCheck(1);
            }
        });

        closet[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closetInside.setVisibility(View.VISIBLE);
                buttonLinear.setVisibility(View.INVISIBLE);
                closetName.setText(closetInfo.get(2).getName());

                int temp_index=0;
                for(int i=0;i<arduino_name.length;i++){
                    if(closetInfo.get(2).getArduino().equals(arduino_name[i])){
                        temp_index=i;
                        break;
                    }
                }
                humi_TextView.setText(humidity[temp_index]+"");

                if(humidity[temp_index]>=15&&humidity[temp_index]<20){
                    humi_State.setText("주의");
                    humi_State.setTextColor(Color.parseColor("#FFCD12"));
                }else if(humidity[temp_index]>=20){
                    humi_State.setText("위험");
                    humi_State.setTextColor(Color.RED);
                }else{
                    humi_State.setText("안전");
                    humi_State.setTextColor(Color.parseColor("#07621A"));
                }

                viewCheck(2);

            }
        });

        closet[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closetInside.setVisibility(View.VISIBLE);
                buttonLinear.setVisibility(View.INVISIBLE);
                closetName.setText(closetInfo.get(3).getName());

                int temp_index=0;
                for(int i=0;i<arduino_name.length;i++){
                    if(closetInfo.get(3).getArduino().equals(arduino_name[i])){
                        temp_index=i;
                        break;
                    }
                }
                humi_TextView.setText(humidity[temp_index]+"");
                if(humidity[temp_index]>=15&&humidity[temp_index]<20){
                    humi_State.setText("주의");
                    humi_State.setTextColor(Color.parseColor("#FFCD12"));
                }else if(humidity[temp_index]>=20){
                    humi_State.setText("위험");
                    humi_State.setTextColor(Color.RED);
                }else{
                    humi_State.setText("안전");
                    humi_State.setTextColor(Color.parseColor("#07621A"));
                }
                viewCheck(3);

            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("저장").setMessage("바뀐 사항을 저장하시겠습니까?")
                        .setNegativeButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i=0;i<dataNum;i++){
                                    if(closetInfo.get(i).getName().equals(closetName.getText())){
                                        texture=i;
                                    }
                                }

                                if(check1.isChecked()){
                                    closetInfo.get(texture).setFur(1);
                                }else{
                                    closetInfo.get(texture).setFur(0);
                                }
                                if(check2.isChecked()){
                                    closetInfo.get(texture).setLeather(1);
                                }else{
                                    closetInfo.get(texture).setLeather(0);
                                }
                                if(check3.isChecked()){
                                    closetInfo.get(texture).setSilk(1);
                                }else{
                                    closetInfo.get(texture).setSilk(0);
                                }
                                if(check4.isChecked()){
                                    closetInfo.get(texture).setKnit(1);
                                }else{
                                    closetInfo.get(texture).setKnit(0);
                                }

                                RequestParams params = new RequestParams();
                                params.add("cmd", "closetInfoUpdate");
                                params.add("name", closetInfo.get(texture).getName());
                                params.add("fur",closetInfo.get(texture).getFur()+"");
                                params.add("leather",closetInfo.get(texture).getLeather()+"");
                                params.add("silk",closetInfo.get(texture).getSilk()+"");
                                params.add("knit",closetInfo.get(texture).getKnit()+"");

                                MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                                        String info = new String(bytes);
                                        result = Integer.parseInt(info.trim());
                                        if (result != 0) {
                                            closetInside.setVisibility(View.GONE);
                                            buttonLinear.setVisibility(View.VISIBLE);

                                            Toast.makeText(getActivity().getApplicationContext(), "저장되었습니다!", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                                        Toast.makeText(getActivity().getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        })
                        .setPositiveButton("No!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                closetInside.setVisibility(View.GONE);
                                buttonLinear.setVisibility(View.VISIBLE);
                            }
                        }).create().show();

            }
        });

        closet[0].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("삭제").setMessage("정말 삭제하시겠습니까?")
                        .setNegativeButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RequestParams params = new RequestParams();
                                params.add("cmd", "closetInfoDelete");
                                params.add("name", closetInfo.get(0).getName());

                                MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                                        String info = new String(bytes);
                                        result = Integer.parseInt(info.trim());
                                        if (result != 0) {
                                            closetInfo.remove(0);
                                            dataNum -= 1;
                                            MainActivity.dataNum=dataNum;
                                            viewDisplay(dataNum);
                                            Toast.makeText(getActivity().getApplicationContext(), "삭제되었습니다!", Toast.LENGTH_SHORT).show();
                                            arduino_state[0]=0;
                                        }
                                    }

                                    @Override
                                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                                        Toast.makeText(getActivity().getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        })
                        .setPositiveButton("No!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity().getApplicationContext(), "삭제되지않았습니다!", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();

                return true;
            }
        });

        closet[1].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("삭제").setMessage("정말 삭제하시겠습니까?")
                        .setNegativeButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RequestParams params = new RequestParams();
                                params.add("cmd", "closetInfoDelete");
                                params.add("name", closetInfo.get(1).getName());

                                MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                                        String info = new String(bytes);
                                        result = Integer.parseInt(info.trim());
                                        if (result != 0) {
                                            closetInfo.remove(1);
                                            dataNum -= 1;
                                            viewDisplay(dataNum);
                                            Toast.makeText(getActivity().getApplicationContext(), "삭제되었습니다!", Toast.LENGTH_SHORT).show();
                                            arduino_state[1]=0;

                                        }
                                    }

                                    @Override
                                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                                        Toast.makeText(getActivity().getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        })
                        .setPositiveButton("No!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity().getApplicationContext(), "삭제되지않았습니다!", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();

                return true;
            }
        });

        closet[2].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("삭제").setMessage("정말 삭제하시겠습니까?")
                        .setNegativeButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("closetInfo Size는", closetInfo.size() + "");

                                RequestParams params = new RequestParams();
                                params.add("cmd", "closetInfoDelete");
                                params.add("name", closetInfo.get(2).getName());

                                MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                                        String info = new String(bytes);
                                        result = Integer.parseInt(info.trim());
                                        if (result != 0) {
                                            closetInfo.remove(2);
                                            dataNum -= 1;
                                            viewDisplay(dataNum);
                                            Toast.makeText(getActivity().getApplicationContext(), "삭제되었습니다!", Toast.LENGTH_SHORT).show();
                                            arduino_state[2]=0;

                                        }
                                    }

                                    @Override
                                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                                        Toast.makeText(getActivity().getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        })
                        .setPositiveButton("No!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity().getApplicationContext(), "삭제되지않았습니다!", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();

                return true;
            }
        });

        closet[3].setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("삭제").setMessage("정말 삭제하시겠습니까?")
                        .setNegativeButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RequestParams params = new RequestParams();
                                params.add("cmd", "closetInfoDelete");
                                params.add("name", closetInfo.get(3).getName());

                                MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                                        String info = new String(bytes);
                                        result = Integer.parseInt(info.trim());
                                        if (result != 0) {
                                            closetInfo.remove(3);
                                            dataNum -= 1;
                                            viewDisplay(dataNum);
                                            Toast.makeText(getActivity().getApplicationContext(), "삭제되었습니다!", Toast.LENGTH_SHORT).show();
                                            arduino_state[3]=0;

                                        }
                                    }

                                    @Override
                                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                                        Toast.makeText(getActivity().getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        })
                        .setPositiveButton("No!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity().getApplicationContext(), "삭제되지않았습니다!", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();

                return true;
            }
        });

        return view;
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            //10이상이면 주의
            //20이상이면 위험
            for(int i=0;i<dataNum;i++){
                for(int k=0;k<arduino_name.length;k++)
                if(closetInfo.get(i).getArduino().equals(arduino_name[k])){

                    if(humidity[k]>=15){
                        closet[i].setBackgroundResource(R.drawable.closeton_danger_btn);
//                        new SetAlarmClass(MainActivity.mainContext);

                        if(arduino_state[i]!=1){
                            AlarmManager alarmManager=(AlarmManager) MainActivity.mainContext.getSystemService(Context.ALARM_SERVICE);
                            Intent intent=new Intent(MainActivity.mainContext,BroadcastClass.class);

                            PendingIntent sender=PendingIntent.getBroadcast(MainActivity.mainContext,0,intent,0);
                            Calendar calendar=Calendar.getInstance();
                            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                            arduino_state[i]=1;
                        }

                    }else{
                        closet[i].setBackgroundResource(R.drawable.closetonbtn);
                        arduino_state[i]=0;

                    }
                }
            }



        }
    };

    public void viewDisplay(int num){

        if(num==0){
            addbtn[0].setVisibility(View.VISIBLE);
            addbtn[1].setVisibility(View.INVISIBLE);
            addbtn[2].setVisibility(View.INVISIBLE);
            addbtn[3].setVisibility(View.INVISIBLE);

            for(int i=0;i<4;i++){
                closet[i].setVisibility(View.GONE);
            }

        }else if(num==1){
            addbtn[0].setVisibility(View.GONE);
            addbtn[1].setVisibility(View.VISIBLE);
            addbtn[2].setVisibility(View.INVISIBLE);
            addbtn[3].setVisibility(View.INVISIBLE);

            closet[0].setVisibility(View.VISIBLE);
            closet[1].setVisibility(View.GONE);
            closet[2].setVisibility(View.GONE);
            closet[3].setVisibility(View.GONE);

        }else if(num==2){
            addbtn[0].setVisibility(View.GONE);
            addbtn[1].setVisibility(View.GONE);
            addbtn[2].setVisibility(View.VISIBLE);
            addbtn[3].setVisibility(View.INVISIBLE);

            closet[0].setVisibility(View.VISIBLE);
            closet[1].setVisibility(View.VISIBLE);
            closet[2].setVisibility(View.GONE);
            closet[3].setVisibility(View.GONE);

        }else if(num==3){
            addbtn[0].setVisibility(View.GONE);
            addbtn[1].setVisibility(View.GONE);
            addbtn[2].setVisibility(View.GONE);
            addbtn[3].setVisibility(View.VISIBLE);

            closet[0].setVisibility(View.VISIBLE);
            closet[1].setVisibility(View.VISIBLE);
            closet[2].setVisibility(View.VISIBLE);
            closet[3].setVisibility(View.GONE);

        }else if(num==4){

            for(int i=0;i<4;i++){
                addbtn[i].setVisibility(View.GONE);
            }

            for(int i=0;i<4;i++){
                closet[i].setVisibility(View.VISIBLE);
            }

        }


        for(int i=0;i<num;i++){
            closet[i].setTextColor(Color.BLACK);
            closet[i].setText(closetInfo.get(i).getName());
        }

    }

    public void viewCheck(int num){

        if(closetInfo.get(num).getFur()==0){
            check1.setChecked(false);
        }else{
            check1.setChecked(true);
        }
        if(closetInfo.get(num).getLeather()==0){
            check2.setChecked(false);
        }else{
            check2.setChecked(true);
        }
        if(closetInfo.get(num).getSilk()==0){
            check3.setChecked(false);
        }else{
            check3.setChecked(true);
        }
        if(closetInfo.get(num).getKnit()==0){
            check4.setChecked(false);
        }else{
            check4.setChecked(true);
        }

    }

    //사용하지 않고 있는 아두이노 이름 찾는 함수
    public int findArduino(){

        int [] temp_index={0,0,0,0};
        ArrayList<Integer> arduino_index = new ArrayList<Integer>();

        for(int i=0;i<arduino_name.length;i++){
            for(int k=0;k<closetInfo.size();k++){
                if(arduino_name[i].equals(closetInfo.get(k).getArduino())){
                    temp_index[i]=1;
                }
            }
        }

        for(int i=0;i<temp_index.length;i++){
            if(temp_index[i]==0){
                arduino_index.add(i);
            }
        }

        return arduino_index.get(0);
    }

}
