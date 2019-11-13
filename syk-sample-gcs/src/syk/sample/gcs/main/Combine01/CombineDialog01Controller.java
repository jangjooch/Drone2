package syk.sample.gcs.main.Combine01;

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

public class CombineDialog01Controller implements Initializable {

    @FXML private Button Hbox_Btn_Mission_Create;
    @FXML private Button Hbox_Btn_Root_Set;
    @FXML private Button Hbox_Btn_Mission_Clear;
    @FXML private Button Hbox_Btn_Mission_Upload;
    @FXML private Button Hbox_Btn_Mission_Start;

    @FXML private Button Hbox_Btn_Attach;
    @FXML private Button Hbox_Btn_Dettach;

    @FXML private TextField Hbox_Text_Magnet;

    private Making_Connect client;

    private int destinationSeq;

    public CombineDialog01Controller() throws MqttException {
        destinationSeq = 0;
        client = new Making_Connect();
        // 클라이언트 연결 및 magnet 상태 받음
        System.out.println("Service03Controller client Created");
    }

    // fxml 에서 적용한 ID 그대로 가져와 사용하여야 객체 주입이 가능하다
    // <Button fx:id="Hbox_Btn_Ok" 에서 설정한 fx:id의 값 그대로 사용한다.

    // implements 받은 Initializable 를 구현
    // 위에서 객체 주입받은 것을 동기화를 통해 이벤트 핸들러를 작성한다.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Button 이 클릭하면 진행될 이벤트 헨들러를 등록한다.
        Hbox_Btn_Mission_Create.setOnAction(Hbox_Btn_Mission_Create_Handler);
        Hbox_Btn_Root_Set.setOnAction(Hbox_Btn_Root_Set_Handler);
        Hbox_Btn_Mission_Clear.setOnAction(Hbox_Btn_Mission_Clear_Handler);
        Hbox_Btn_Mission_Upload.setOnAction(Hbox_Btn_Mission_Upload_Handler);
        Hbox_Btn_Mission_Start.setOnAction(Hbox_Btn_Mission_Start_Handler);
        Hbox_Btn_Attach.setOnAction(Hbox_Btn_Attach_Handler);
        Hbox_Btn_Dettach.setOnAction(Hbox_Btn_Dettach_Handler);
    }

    private EventHandler<ActionEvent> Hbox_Btn_Mission_Create_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Mission_Create_Handler Action Activate");
        }
    };
    private EventHandler<ActionEvent> Hbox_Btn_Root_Set_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Mission_Create_Handler Action Activate");




        }
    };
    private EventHandler<ActionEvent> Hbox_Btn_Mission_Clear_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Mission_Create_Handler Action Activate");
        }
    };
    private EventHandler<ActionEvent> Hbox_Btn_Mission_Upload_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Mission_Create_Handler Action Activate");
        }
    };
    private EventHandler<ActionEvent> Hbox_Btn_Mission_Start_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Mission_Create_Handler Action Activate");
        }
    };

    private EventHandler<ActionEvent> Hbox_Btn_Cancel_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService02 Action Activate");
            //Stage stage = (Stage)Hbox_Btn_Cancel.getScene().getWindow();
            // Hbox_Btn_Cancel 이 위치한 Scene이 있는 Window의 위치를 받아
            //stage.close();
        }
    };

    private EventHandler<ActionEvent> Hbox_Btn_Attach_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Attach_Handler");
            try {
                client.ControlMagnet("on");
                System.out.println("GCS published to Activate Magnet");
            } catch (MqttException e) {
                System.out.println("GCS published MqttException Happened");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    };

    private EventHandler<ActionEvent> Hbox_Btn_Dettach_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Dettach_Handler");
            try {
                client.ControlMagnet("off");
                System.out.println("GCS published to DeActivate Magnet");
            } catch (MqttException e) {
                System.out.println("GCS published MqttException Happened");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    };


    // Magnet에서의 Subscribe를 하는 client
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

            System.out.println("ServiceDialog03Controller Making_Connect MQTT Connected");

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    System.out.println("Message Received from Raspi");
                    String getMsg = new String(mqttMessage.getPayload());
                    System.out.println("Received : " + getMsg);
                    Hbox_Text_Magnet.setText(getMsg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            client.subscribe("/jang/gcs/serviceDialog03");
        }

        public void ControlMagnet(String message) throws MqttException {
            JSONObject magnetJson = new JSONObject();
            magnetJson.put("magnet",message);
            client.publish("/jang/drone", magnetJson.toString().getBytes(), 0, false);
            System.out.println("Published Message to Raspi Magnet " + message);
        }

        public void ControlDroneState(String where){
            JSONObject sendding = new JSONObject();
            if(where.equals("create")){
                GcsMain.instance.controller.flightMap.controller.missionClear();
                GcsMain.instance.controller.flightMap.controller.missionMake();
            }
            else if(where.equals("root")){

            }
            else if(where.equals("clear")){
                GcsMain.instance.controller.flightMap.controller.missionClear();
            }
            else if(where.equals("upload")){
                sendding.put("msgid", "Mission_Upload");
            }
            else if(where.equals("start")){
                sendding.put("msgid", "ARM");
                sendding.put("arm", true);
            }
            JSONObject controlJson = new JSONObject();
            controlJson.put("msgid", "ARM");
            controlJson.put("arm", true);
            try {
                client.publish("/drone/fc/sub", controlJson.toString().getBytes(), 0, false);
                System.out.println("Published Message to Raspi To Arm True ");
            } catch (MqttException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            JSONObject takeOffJson = new JSONObject();
            takeOffJson.put("msgid","TAKEOFF");
            takeOffJson.put("alt",10);
            try {
                client.publish("/drone/fc/sub", takeOffJson.toString().getBytes(), 0, false);
                System.out.println("Published Message to Raspi To TakeOff ");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            // GcsMain.instance.controller.drone.flightController.sendMissionUpload();
        }
    }
}
