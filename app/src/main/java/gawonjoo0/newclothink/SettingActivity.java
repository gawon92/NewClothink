package gawonjoo0.newclothink;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jooyoung on 2016-10-23.
 */

public class SettingActivity extends Fragment {
    Switch set_alarm_switch;
    Switch set_week_alarm_switch;
    Switch set_before_alarm_switch;
    LinearLayout noClosetLinear, closetLinear1, closetLinear2, closetLinear3;

    private TextView[] closetName=new TextView[3];
    private TextView[] closetState=new TextView[3];
    private TextView washerFreqTextView;

    private String[] arduino_name={"arduino1", "arduino2", "arduino3", "arduino4"};

    public static ArrayList<ClosetDto> closetInfo=ClosetActivity.closetInfo;
    public static int [] humidity=ClosetActivity.humidity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.setting_layout,container,false);

        noClosetLinear=(LinearLayout)view.findViewById(R.id.noClosetLinear);
        closetLinear1=(LinearLayout)view.findViewById(R.id.closetLinear1);
        closetLinear2=(LinearLayout)view.findViewById(R.id.closetLinear2);
        closetLinear3=(LinearLayout)view.findViewById(R.id.closetLinear3);

        closetName[0]=(TextView)view.findViewById(R.id.closetName1);
        closetName[1]=(TextView)view.findViewById(R.id.closetName2);
        closetName[2]=(TextView)view.findViewById(R.id.closetName3);
        closetState[0]=(TextView)view.findViewById(R.id.closetState1);
        closetState[1]=(TextView)view.findViewById(R.id.closetState2);
        closetState[2]=(TextView)view.findViewById(R.id.closetState3);

        washerFreqTextView=(TextView)view.findViewById(R.id.washerFreqTextView);
        int washerFreq=MainActivity.washerFreq;

        washerFreqTextView.setText(washerFreq+"");

        if(closetInfo.size()==1){
            noClosetLinear.setVisibility(View.GONE);
            closetLinear1.setVisibility(View.VISIBLE);
        }else if(closetInfo.size()==2){
            noClosetLinear.setVisibility(View.GONE);
            closetLinear1.setVisibility(View.VISIBLE);
            closetLinear2.setVisibility(View.VISIBLE);
        }else if(closetInfo.size()==3){
            noClosetLinear.setVisibility(View.GONE);
            closetLinear1.setVisibility(View.VISIBLE);
            closetLinear2.setVisibility(View.VISIBLE);
            closetLinear3.setVisibility(View.VISIBLE);
        }

        for(int i=0;i<closetInfo.size();i++){
            closetName[i].setText(closetInfo.get(i).getName());
        }

        int temp_index=0;
        for(int i=0;i<closetInfo.size();i++) {
            for (int k = 0; k < arduino_name.length; k++) {
                if (closetInfo.get(i).getArduino().equals(arduino_name[k])) {
                    temp_index = i;

                    if(humidity[temp_index]>=15&&humidity[temp_index]<20){
                        closetState[temp_index].setText("주의");
                        closetState[temp_index].setTextColor(Color.parseColor("#FFCD12"));
                    }else if(humidity[temp_index]>=20){
                        closetState[temp_index].setText("위험");
                        closetState[temp_index].setTextColor(Color.RED);
                    }else{
                        closetState[temp_index].setText("안전");
                        closetState[temp_index].setTextColor(Color.parseColor("#07621A"));
                    }
                }
            }
        }


        set_alarm_switch=(Switch) view.findViewById(R.id.set_alarm_switch);
        set_week_alarm_switch=(Switch) view.findViewById(R.id.set_week_alarm_switch);
        set_before_alarm_switch=(Switch) view.findViewById(R.id.set_before_alarm_switch);

        set_alarm_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){

                    MainActivity.alarm_flag1=0;
                    MainActivity.alarm_flag2=0;

                    set_week_alarm_switch.setChecked(false);
                    set_before_alarm_switch.setChecked(false);

                    set_week_alarm_switch.setClickable(false);
                    set_before_alarm_switch.setClickable(false);
                }else{
                    set_week_alarm_switch.setClickable(true);
                    if(set_week_alarm_switch.isChecked()){
                        MainActivity.alarm_flag2=1;
                    }

                    set_before_alarm_switch.setClickable(true);
                    if(set_before_alarm_switch.isChecked()){
                        MainActivity.alarm_flag1=1;
                    }
                }
            }
        });

        return view;
    }
}