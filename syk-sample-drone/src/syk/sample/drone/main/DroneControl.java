package syk.sample.drone.main;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

public class DroneControl {
    private MqttClient controlClient;

    public DroneControl(){

    }

    public void Creating_Connection() {
        while(true){
            try {
                controlClient = new MqttClient("tcp://106.253.56.124:1881", MqttClient.generateClientId(), null);
                MqttConnectOptions options = new MqttConnectOptions();
                controlClient.connect();
                System.out.println("RealMain Control Magnet Making_Connect MQTT Connected");
                break;
            } catch (MqttException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void make_Sub() throws MqttException {
        controlClient.setCallback(new MqttCallback(){
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Magnet Connection Lost");
                Creating_Connection();
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                System.out.println("Message Received");
                if(mqttMessage == null){
                    System.out.println("Message Error");
                }
                else if(mqttMessage.getPayload().toString().equals(" ")){
                    System.out.println("Message Error ** **");
                }
                else if(mqttMessage.getPayload().toString().equals("")){
                    System.out.println("Message Error ****");
                }
                else{
                    String getMsg = new String(mqttMessage.getPayload());
                    JSONObject object = new JSONObject(getMsg);
                    System.out.println("Received : " + object.get("control"));
                    if(object.get("control").equals("up")){
                        System.out.println("Up received");
                    }
                    else if(object.get("control").equals("down")){
                        System.out.println("Down received");
                    }
                    else if(object.get("control").equals("right")){
                        System.out.println("Right received");
                    }
                    else if(object.get("control").equals("left")){
                        System.out.println("Left received");
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        controlClient.subscribe("/jang/drone/control");
    }
}
