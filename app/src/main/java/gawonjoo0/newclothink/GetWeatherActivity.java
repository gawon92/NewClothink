package gawonjoo0.newclothink;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.net.Socket;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.Header;

/**
 * Created by USER on 2016-12-04.
 */
public class GetWeatherActivity{

    Document doc=null;
    public static NodeList nodeList;

    float temper=0.0f;
    String max_temper="0";
    String min_temper="0";

    Thread thread;

    public GetWeatherActivity(){
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
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
                    Element nameElement1 = (Element) nameList.item(0);
                    nameList = nameElement1.getChildNodes();

                    temper=Float.parseFloat(((Node) nameList.item(0)).getNodeValue().toString());

                    int i=0;
                    while(i<19) {    //sequence="18"까지 loop문 돌린다.
                        node = nodeList.item(i);
                        fstElemnt = (Element) node;

                        NodeList max_temperature = fstElemnt.getElementsByTagName("tmx");
                        Element max_element = (Element) max_temperature.item(0);
                        max_temperature = max_element.getChildNodes();

                        NodeList min_temperature = fstElemnt.getElementsByTagName("tmn");
                        Element min_element = (Element) min_temperature.item(0);
                        min_temperature = min_element.getChildNodes();

                        max_temper = ((Node) max_temperature.item(0)).getNodeValue().toString();
                        min_temper = ((Node) min_temperature.item(0)).getNodeValue().toString();

                        if ((max_temper.equals("-999.0")) || (min_temper.equals("-999.0"))) {
                            max_temper = "미제공";
                            min_temper = "미제공";
                            i++;
                        } else {
                            break;
                        }
                    }

//                    Log.i("지금 기온은", "잘찍힘"+temper);
//                    Log.i("최고 기온은", "잘찍힘"+max_temper);
//                    Log.i("최저 기온은", "잘찍힘"+min_temper);

                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        try{
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getTemperature(){
        return temper+"";
    }
    public String getMaxTemperature(){
        return max_temper;
    }
    public String getMinTemperature(){
        return min_temper;
    }

}
