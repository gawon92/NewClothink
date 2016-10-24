package gawonjoo0.newclothink;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by jooyoung on 2016-10-22.
 */

public class WasherActivity extends Fragment {

    LinearLayout laundryWaitLinear;
    RelativeLayout laundryStartRelative;
    ImageView washerOnImage;
    TextView washerOnTv;

    int washerImageCount=0;
    int washerOnFlag=1;     //나중에 진동 들어오면 플래그 바껴서 세탁기화면 뜨고 다시 대기화면 바꾸는 기준이 되는 변수

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.washer_layout,container,false);

        laundryWaitLinear=(LinearLayout) view.findViewById(R.id.laundryWaitLinear);
        laundryStartRelative=(RelativeLayout) view.findViewById(R.id.laundryStartRelative);
        washerOnImage=(ImageView) view.findViewById(R.id.washerOnImage);
        washerOnTv=(TextView) view.findViewById(R.id.washerOnTv);

        if(MainActivity.washerCount==1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            handler.sendMessage(handler.obtainMessage());
                            Thread.sleep(400);

                        } catch (Throwable t) {
                        }

                    }
                }
            }).start();
        }

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
