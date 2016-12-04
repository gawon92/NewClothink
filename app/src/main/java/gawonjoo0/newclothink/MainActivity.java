package gawonjoo0.newclothink;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity implements View.OnClickListener{


    Socket socket = null;
    BufferedReader socket_in;
    PrintWriter socket_out;

    Document doc=null;

    float temperature;


    Fragment fr1;
    Fragment fr2;
    Fragment fr3;

    ExpandableListView expandableListView;

    ImageButton closetBtn;
    ImageButton washerBtn;
    ImageButton settingBtn;

    ImageView weatherIconImage;

    TextView currentTempTv;

    int threadStopFlag=1;
    private static int n=0;
    int tipCount=0;

    static int dataNum=0;
    static int washerCount=0;

    public static NodeList nodeList;
    String data;

    private ArrayList<String> tipArray=new ArrayList<String>(); //DB에서 tip 가지고오는 Array
    private ArrayList<String> tipDetailArray=new ArrayList<String>();   //tip Array에서 내용 넣는 Array
    private ArrayList<String> arrayGroup=new ArrayList<String>();   //tip Array에서 제목 넣는 Array
    private ArrayList<String> arr=new ArrayList<String>(); //arrChild의 key에 String으로 넣기 위해서 여기 넣엇다가 뺌
    private HashMap<String, ArrayList<String>> arrayChild=new HashMap<String, ArrayList<String>>();

    static ClosetDto infoDto;
    static ArrayList<String> allList = new ArrayList<String>();
    static ArrayList <ClosetDto> closetInfo;


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

        closetInfo=new ArrayList<ClosetDto>();

        RequestParams params = new RequestParams();
        params.add("cmd", "tipGet");


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

                RequestParams params2=new RequestParams();
                params2.add("cmd", "dataGet");

                MyFirstRestClient.post("/pb", params2, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {

                        String info = new String(bytes);

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

//        threadStopFlag=0;
//        new Thread(tipThreadRun).start();
//
        closetBtn.setOnClickListener(this);
        washerBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

//        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                try {
//                    threadStopFlag=1;
////                    Log.i("그룹상태",""+threadStopFlag);
//                    Thread.interrupted();
//                }catch(Throwable t){
//                }
//                return false;
//            }
//        });
//
//        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                threadStopFlag=0;
////                Log.i("차일드상태",""+threadStopFlag);
//                new Thread(tipThreadRun).start();
//                return false;
//            }
//        });
        ConnectSocket conSock = new ConnectSocket();
        conSock.setDaemon(true);
        conSock.start();
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
                            currentTempTv.setText(""+temperature);
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

    class ConnectSocket extends Thread {
        public void run() {
            try {
//                DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
//                DocumentBuilder parser = f.newDocumentBuilder();
//
//                Document xmlDoc = null;
//                String url = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=1121571000";
//                xmlDoc = parser.parse(url);
//
//                Element root = xmlDoc.getDocumentElement();


                GetXMLTask task=new GetXMLTask(MainActivity.this);
                task.execute("http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=4113564000");

//                socket = new Socket(_SERVER_ADDR, _PORT_NO);
//                socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
//                socket_out = new PrintWriter(socket.getOutputStream(), true);

                int closet_humidity=0;
                int laundry_humidity=0;
                int laundry_day=0;
                int laundry_time=0;

                try{
                    while(true){
                            long now = System.currentTimeMillis();

                        URL url;
                        try {
                            url = new URL("http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=4113564000");
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance().newInstance();
                            DocumentBuilder db;

                            db = dbf.newDocumentBuilder();
                            doc = db.parse(new InputSource(url.openStream()));
                            doc.getDocumentElement().normalize();
                            nodeList = doc.getElementsByTagName("data");
                        } catch (Exception e) {
//                Toast.makeText(getBaseContext(),"ParsingError",Toast.LENGTH_SHORT).show();
                            Log.i("날씨파싱", "에러");
                        }

                            Node node = nodeList.item(0);
                            Element fstElemnt = (Element) node;
                            NodeList nameList = fstElemnt.getElementsByTagName("temp");
                            Element nameElement = (Element) nameList.item(0);
                            nameList = nameElement.getChildNodes();

                           temperature=Float.parseFloat(((Node) nameList.item(0)).getNodeValue().toString());




                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        private Activity context;

        public GetXMLTask(Activity context) {
            this.context = context;
        }

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance().newInstance();
                DocumentBuilder db;

                db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                nodeList = doc.getElementsByTagName("data");
            } catch (Exception e) {
//                Toast.makeText(getBaseContext(),"ParsingError",Toast.LENGTH_SHORT).show();
                Log.i("날씨파싱", "에러");
            }

            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {

        }
    }

}
