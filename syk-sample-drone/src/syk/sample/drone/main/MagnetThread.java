package syk.sample.drone.main;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MagnetThread extends Thread {

    private MqttClient client;

    public MagnetThread(){
        while(true){
            try {
                client = new MqttClient("tcp://106.253.56.124:1881", MqttClient.generateClientId(), null);
                MqttConnectOptions options = new MqttConnectOptions();
                client.connect();
                System.out.println("MagnetThread MQTT Connected");
                break;
            } catch (MqttException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        
    }

    public void SendMessage(String str) throws MqttException {
        client.publish("/jang/gcs/serviceDialog03",str.getBytes(),0,false);
    }
}
