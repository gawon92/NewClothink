package gawonjoo0.newclothink;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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


                    NodeList max_temperature=fstElemnt.getElementsByTagName("tmx");
                    Element nameElement2 = (Element) max_temperature.item(0);
                    max_temperature=nameElement2.getChildNodes();

                    NodeList min_temperature=fstElemnt.getElementsByTagName("tmn");
                    Element nameElement3 = (Element) min_temperature.item(0);
                    min_temperature=nameElement3.getChildNodes();


                    temper=Float.parseFloat(((Node) nameList.item(0)).getNodeValue().toString());
                    String max=((Node) max_temperature.item(0)).getNodeValue().toString();
                    String min=((Node) min_temperature.item(0)).getNodeValue().toString();


                    if(max.equals("-999.0")||min.equals("-999.0")){
                        node = nodeList.item(2);
                        fstElemnt = (Element) node;

                        max_temperature=fstElemnt.getElementsByTagName("tmx");
                        nameElement2 = (Element) max_temperature.item(0);
                        max_temperature=nameElement2.getChildNodes();

                        min_temperature=fstElemnt.getElementsByTagName("tmn");
                        nameElement3 = (Element) min_temperature.item(0);
                        min_temperature=nameElement3.getChildNodes();

                        max_temper=((Node) max_temperature.item(0)).getNodeValue().toString();
                        min_temper=((Node) min_temperature.item(0)).getNodeValue().toString();

                    }else{
                        max_temper=((Node) max_temperature.item(0)).getNodeValue().toString();
                        min_temper=((Node) min_temperature.item(0)).getNodeValue().toString();
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
