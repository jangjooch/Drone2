package syk.sample.drone.main;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import syk.sample.gcs.main.GcsMain;

public class X_PublishTest {

    public static void main(String[] args){
        MqttClient client;
        while(true){
            try {
                client = new MqttClient("tcp://106.253.56.124:1881", MqttClient.generateClientId(), null);
                MqttConnectOptions options = new MqttConnectOptions();
                client.connect();
                System.out.println("GcsMainMqtt client Connect Done");
                break;
            }
            catch (MqttException e){
                System.out.println("GcsMainMqtt client Connect Error");
                System.out.println(e.getMessage());
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgid", "missionStart");
        //jsonObject.put("lat",37.504000);
        //jsonObject.put("lng",127.122000);
        // 협회 좌표
        jsonObject.put("lat",37.5469722);
        jsonObject.put("lng",127.1196016);
        try {
            System.out.println("Publish Try");
            client.publish("/gcs/main", jsonObject.toString().getBytes(),0,false);
            System.out.println("Publish Done");
        } catch (MqttException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
