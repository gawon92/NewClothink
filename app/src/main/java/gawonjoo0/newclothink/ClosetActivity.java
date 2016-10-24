package gawonjoo0.newclothink;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.StringTokenizer;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jooyoung on 2016-10-22.
 */

public class ClosetActivity extends Fragment {

    public int count1,count2=0;

    public static int dataNum=0;

    private Button[] closet=new Button[4];
    private Button [] addbtn=new Button[4];

    private ClosetDto infoDto;

    StringTokenizer stk1,stk2;

    ArrayList<String> allList = new ArrayList<String>();
    public static ArrayList <ClosetDto> closetInfo;

    private int result=0;

    ClosetDto closetDto;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.closet_layout,container,false);

        closetInfo=new ArrayList<ClosetDto>();


        RequestParams params = new RequestParams();
        params.add("cmd", "dataGet");


        MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                String info = new String(bytes);
                stk1 = new StringTokenizer(info, "^");
                while (stk1.hasMoreTokens()) {
                    allList.add(stk1.nextToken());
                }
                dataNum = allList.size() - 1;

                for (int k = 0; k < dataNum; k++) {

                    stk2 = new StringTokenizer(allList.get(k), ",");
                    infoDto = new ClosetDto();
                    infoDto.setName(stk2.nextToken().trim());
                    infoDto.setBunho(stk2.nextToken().trim());
                    infoDto.setAge(Integer.parseInt(stk2.nextToken().trim()));
                    closetInfo.add(infoDto);
                    Log.i(closetInfo.get(k).getName(), "");
                }


                viewDisplay(dataNum);


            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getActivity().getApplicationContext(), "연결실패", Toast.LENGTH_SHORT).show();

            }
        });

        closet[0]=(Button) view.findViewById(R.id.closet1);
        closet[1]=(Button) view.findViewById(R.id.closet2);
        closet[2]=(Button) view.findViewById(R.id.closet3);
        closet[3]=(Button) view.findViewById(R.id.closet4);

        addbtn[0]=(Button) view.findViewById(R.id.addbtn1);
        addbtn[1]=(Button) view.findViewById(R.id.addbtn2);
        addbtn[2]=(Button) view.findViewById(R.id.addbtn3);
        addbtn[3]=(Button) view.findViewById(R.id.addbtn4);


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
                                    closetDto.setBunho("010-XXXX-XXXX");
                                    closetDto.setAge(18);
                                    closetInfo.add(closetDto);

                                    dataNum += 1;
                                    viewDisplay(dataNum);

                                    RequestParams params = new RequestParams();
                                    params.add("cmd", "closetInfoInsert");
                                    params.add("name", closetDto.getName());

                                    MyFirstRestClient.post("/pb", params, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                            String info = new String(bytes);
                                            result = Integer.parseInt(info.trim());

                                            if (result != 0) {
                                                Toast.makeText(getActivity().getApplicationContext(), "[ " + closetDto.getName() + " ]의 옷장 만들어짐!", Toast.LENGTH_SHORT).show();
                                            }

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

        Log.i(count1+"는"," count1");

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
                                            viewDisplay(dataNum);
                                            Toast.makeText(getActivity().getApplicationContext(), "삭제되었습니다!", Toast.LENGTH_SHORT).show();
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
    }

}
