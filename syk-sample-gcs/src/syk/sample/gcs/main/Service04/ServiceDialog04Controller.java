package syk.sample.gcs.main.Service04;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import syk.sample.gcs.main.GcsMain;

import java.net.URL;
import java.util.ResourceBundle;

public class ServiceDialog04Controller implements Initializable {

    private Making_Connect client;

    private boolean mobileRequest;

    public ServiceDialog04Controller() throws MqttException {
        client = new Making_Connect();
        System.out.println("Service04Controller client Created");
        GcsMain.instance.controller.flightMap.controller.setMode("GUIDED");
        mobileRequest = false;
    }

    @FXML private Button Btn_Up;
    @FXML private Button Btn_Down;
    @FXML private Button Btn_Right;
    @FXML private Button Btn_Left;
    @FXML private Button Btn_Drop;

    // fxml 에서 적용한 ID 그대로 가져와 사용하여야 객체 주입이 가능하다
    // <Button fx:id="Hbox_Btn_Ok" 에서 설정한 fx:id의 값 그대로 사용한다.

    // implements 받은 Initializable 를 구현
    // 위에서 객체 주입받은 것을 동기화를 통해 이벤트 핸들러를 작성한다.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Button 이 클릭하면 진행될 이벤트 헨들러를 등록한다.
        Btn_Up.setOnAction(Btn_Up_Handler);
        Btn_Down.setOnAction(Btn_Down_Handler);
        Btn_Left.setOnAction(Btn_Left_Handler);
        Btn_Right.setOnAction(Btn_Right_Handler);
        Btn_Drop.setOnAction(Btn_Drop_Handler);
    }

    private EventHandler<ActionEvent> Btn_Drop_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            String msg = null;
            client.ControlMagnet(msg);
            System.out.println("Published Message to Raspi Magnet " + msg);
        }
    };

    private EventHandler<ActionEvent> Btn_Up_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                client.DroneControl("up");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };

    private EventHandler<ActionEvent> Btn_Down_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                client.DroneControl("down");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };

    private EventHandler<ActionEvent> Btn_Left_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                client.DroneControl("left");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };

    private EventHandler<ActionEvent> Btn_Right_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            try {
                client.DroneControl("right");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    };

    public class Making_Connect{

        private MqttClient client;

        public Making_Connect() throws MqttException {
            while(true){
                try {
                    client = new MqttClient("tcp://106.253.56.124:1881", MqttClient.generateClientId(), null);
                    MqttConnectOptions options = new MqttConnectOptions();
                    client.connect();
                    break;
                }
                catch (MqttException e){
                    System.out.println(e.getMessage());
                }
            }

            System.out.println("ServiceDialog04Controller Making_Connect MQTT Connected");

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("ServiceDialog04Controller Connection Lost");
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    System.out.println("Message Received from Raspi");
                    String getMsg = new String(mqttMessage.getPayload());
                    System.out.println("Received : " + getMsg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            client.subscribe("/gcs/service04");
        }

        public void DroneControl(String message) throws MqttException {
            JSONObject controlJson = new JSONObject();
            controlJson.put("msgid","MAVJSON_MSG_ID_FINE_CONTROL");
            if(message.equals("up")){
                controlJson.put("velocityNorth", 1);
                controlJson.put("velocityEast", 0);
            }
            else if(message.equals("down")){
                controlJson.put("velocityNorth", -1);
                controlJson.put("velocityEast", 0);
            }
            else if(message.equals("right")){
                controlJson.put("velocityNorth", 0);
                controlJson.put("velocityEast", 1);
            }
            else if(message.equals("left")){
                controlJson.put("velocityNorth", 0);
                controlJson.put("velocityEast", -1);
            }
            client.publish("/drone/fc/sub", controlJson.toString().getBytes(), 0, false);
            System.out.println("Published Message to Raspi Direction " + message);
        }

        public void ControlMagnet(String message){
            try {
                System.out.println("Try Publish Message to Raspi Magnet");
                client.publish("/drone/fc/sub", "magnet".getBytes(), 0, false);
                System.out.println("Done Published Message to Raspi Magnet " + message);
            } catch (MqttException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
