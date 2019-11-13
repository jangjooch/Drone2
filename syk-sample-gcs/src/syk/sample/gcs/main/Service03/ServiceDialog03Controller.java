package syk.sample.gcs.main.Service03;

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

public class ServiceDialog03Controller implements Initializable {

    @FXML
    private Button Hbox_Btn_Ok;
    @FXML
    private Button Hbox_Btn_Cancel;
    @FXML private Button Hbox_Btn_Print;
    @FXML private Button Hbox_Btn_Attach;
    @FXML private Button Hbox_Btn_Dettach;

    @FXML private Button Hbox_Btn_Mo;
    @FXML private TextField Hbox_Text_Magnet;

    private Making_Connect client;

    private int destinationSeq;

    public ServiceDialog03Controller() throws MqttException {
        destinationSeq = 0;
        client = new Making_Connect();
        System.out.println("Service03Controller client Created");
    }

    // fxml 에서 적용한 ID 그대로 가져와 사용하여야 객체 주입이 가능하다
    // <Button fx:id="Hbox_Btn_Ok" 에서 설정한 fx:id의 값 그대로 사용한다.

    // implements 받은 Initializable 를 구현
    // 위에서 객체 주입받은 것을 동기화를 통해 이벤트 핸들러를 작성한다.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Button 이 클릭하면 진행될 이벤트 헨들러를 등록한다.
        Hbox_Btn_Ok.setOnAction(Hbox_Btn_Ok_Handler);
        Hbox_Btn_Cancel.setOnAction(Hbox_Btn_Cancel_Handler);
        Hbox_Btn_Print.setOnAction(Btn_Print_Handler);
        Hbox_Btn_Attach.setOnAction(Hbox_Btn_Attach_Handler);
        Hbox_Btn_Dettach.setOnAction(Hbox_Btn_Dettach_Handler);
        Hbox_Btn_Mo.setOnAction(Hbox_Btn_Mo_Handler_version2);

    }

    private EventHandler<ActionEvent> Hbox_Btn_Ok_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService01 Action Activate");

        }
    };

    private EventHandler<ActionEvent> Hbox_Btn_Cancel_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService02 Action Activate");
            Stage stage = (Stage)Hbox_Btn_Cancel.getScene().getWindow();
            // Hbox_Btn_Cancel 이 위치한 Scene이 있는 Window의 위치를 받아
            stage.close();
        }
    };

    private EventHandler<ActionEvent> Btn_Print_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Btn_Print_Handler Action Activate");

            JSONArray array = GcsMain.instance.controller.flightMap.controller.getMissionItems();
            destinationSeq = array.length() - 1;
            // seq가 0부터 시작함으로
            // GcsMain.instance.controller.flightMap.controller.missionClear();
            JSONArray root = GcsMain.instance.controller.flightMap.controller.getMissionItems();
            int limit = array.length();
            System.out.println("First Get Mission Items");
            for(int i = 0 ; i < limit ; i++){
                JSONObject object = (JSONObject) array.get(i);
                System.out.println(object.toString());
            }

            System.out.println("---------");

            JSONObject destinationGet = (JSONObject) root.get(destinationSeq);
            System.out.println("Destinaion : " + destinationGet.toString());
            double destiLat = (double) destinationGet.get("x");
            double destiLng = (double) destinationGet.get("y");

            root.remove(destinationSeq);

            JSONObject setDesti = new JSONObject();
            setDesti.put("seq", destinationSeq);
            setDesti.put("command",16);
            setDesti.put("x",destiLat);
            setDesti.put("y",destiLng);
            setDesti.put("z",10);
            setDesti.put("param1", 0);
            setDesti.put("param2", 0);
            setDesti.put("param3", 0);
            setDesti.put("param4", 0);
            System.out.println("setDesti : " + setDesti);
            root.put(setDesti);

            JSONObject delayObject = new JSONObject();
            delayObject.put("seq", destinationSeq +1 );
            delayObject.put("command",93);
            delayObject.put("x",destiLat);
            delayObject.put("y",destiLng);
            delayObject.put("z",10);
            delayObject.put("param1", 10);
            delayObject.put("param2", 0);
            delayObject.put("param3", 0);
            delayObject.put("param4", 0);
            System.out.println("delayObject : " + delayObject);
            root.put(delayObject);



            // root 목적지 제외하여 변경토록 함
            System.out.println("--------------");
            System.out.println("Will add");
            int idx = root.length();
            for(int i = limit - 2 ; i >= 0 ; i--){
                JSONObject object = (JSONObject) array.get(i);
                object.put("seq",idx);
                System.out.println(object.toString());
                idx++;
                root.put(object);
            }
            System.out.println("-------------");
            System.out.println("root");
            for (int i = 0 ; i < root.length(); i++){
                System.out.println(root.get(i).toString());
            }

            GcsMain.instance.controller.flightMap.controller.missionClear();
            // 설계한 미션들 삭제
            //GcsMain.instance.controller.flightMap.controller.missionMake();
            GcsMain.instance.controller.flightMap.controller.setMissionItems_Customize(root);
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

    private EventHandler<ActionEvent> Hbox_Btn_Mo_Handler_version2 = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Mo_Handler");
            JSONArray array = GcsMain.instance.controller.flightMap.controller.getMissionItems();
            // 목적지 seq 저장
            destinationSeq = array.length() - 1;
            // seq가 0부터 시작함으로
            // GcsMain.instance.controller.flightMap.controller.missionClear();
            JSONArray root = GcsMain.instance.controller.flightMap.controller.getMissionItems();
            int limit = array.length();
            System.out.println("First Get Mission Items");
            for(int i = 0 ; i < limit ; i++){
                JSONObject object = (JSONObject) array.get(i);
                System.out.println(object.toString());
            }

            System.out.println("---------");

            // 목적지 좌표 저장용
            JSONObject destinationGet = (JSONObject) root.get(destinationSeq);
            System.out.println("Destinaion : " + destinationGet.toString());
            double destiLat = (double) destinationGet.get("x");
            double destiLng = (double) destinationGet.get("y");

            root.remove(destinationSeq);

            JSONObject setDesti = new JSONObject();
            setDesti.put("seq", destinationSeq);
            setDesti.put("command",16);
            setDesti.put("x",destiLat);
            setDesti.put("y",destiLng);
            setDesti.put("z",10);
            setDesti.put("param1", 0);
            setDesti.put("param2", 0);
            setDesti.put("param3", 0);
            setDesti.put("param4", 0);
            System.out.println("setDesti : " + setDesti);
            root.put(setDesti);

            JSONObject delayObject = new JSONObject();
            delayObject.put("seq", destinationSeq +1 );
            delayObject.put("command",93);
            delayObject.put("x",destiLat);
            delayObject.put("y",destiLng);
            delayObject.put("z",10);
            delayObject.put("param1", 10);
            delayObject.put("param2", 0);
            delayObject.put("param3", 0);
            delayObject.put("param4", 0);
            System.out.println("delayObject : " + delayObject);
            root.put(delayObject);



            // root 목적지 제외하여 변경토록 함
            System.out.println("--------------");
            System.out.println("Will add");
            int idx = root.length();
            for(int i = limit - 2 ; i >= 0 ; i--){
                JSONObject object = (JSONObject) array.get(i);
                object.put("seq",idx);
                System.out.println(object.toString());
                idx++;
                root.put(object);
            }
            System.out.println("-------------");
            System.out.println("root");
            for (int i = 0 ; i < root.length(); i++){
                System.out.println(root.get(i).toString());
            }

            // 마지막 root를 지우고 이를 RTL로 변경
            root.remove(root.length()-1);
            JSONObject RTL = new JSONObject();
            RTL.put("seq", root.length());
            RTL.put("x", destiLat);
            RTL.put("y", destiLng);
            RTL.put("z", 0);
            RTL.put("command", 20);
            RTL.put("param1", 0);
            RTL.put("param2", 0);
            RTL.put("param3", 0);
            RTL.put("param4", 0);
            System.out.println("Last RTL : " + RTL.toString());
            root.put(RTL);

            // 이륙 추가


            GcsMain.instance.controller.flightMap.controller.missionClear();
            // 설계한 미션들 삭제
            //GcsMain.instance.controller.flightMap.controller.missionMake();
            GcsMain.instance.controller.flightMap.controller.setMissionItems_Customize(root);
        }
    };


    private EventHandler<ActionEvent> Hbox_Btn_Mo_Handler_version3 = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Mo_Handler");
            JSONArray array = GcsMain.instance.controller.flightMap.controller.getMissionItems();
            // 목적지 seq 저장
            destinationSeq = array.length() - 1;
            // seq가 0부터 시작함으로
            // GcsMain.instance.controller.flightMap.controller.missionClear();
            JSONArray root = GcsMain.instance.controller.flightMap.controller.getMissionItems();
            int limit = array.length();
            System.out.println("First Get Mission Items");
            for(int i = 0 ; i < limit ; i++){
                JSONObject object = (JSONObject) array.get(i);
                System.out.println(object.toString());
            }

            System.out.println("---------");

            // 목적지 좌표 저장용
            JSONObject destinationGet = (JSONObject) root.get(destinationSeq);
            System.out.println("Destinaion : " + destinationGet.toString());
            double destiLat = (double) destinationGet.get("x");
            double destiLng = (double) destinationGet.get("y");

            root.remove(destinationSeq);

            JSONObject setDesti = new JSONObject();
            setDesti.put("seq", destinationSeq);
            setDesti.put("command",16);
            setDesti.put("x",destiLat);
            setDesti.put("y",destiLng);
            setDesti.put("z",10);
            setDesti.put("param1", 0);
            setDesti.put("param2", 0);
            setDesti.put("param3", 0);
            setDesti.put("param4", 0);
            System.out.println("setDesti : " + setDesti);
            root.put(setDesti);

            JSONObject delayObject = new JSONObject();
            delayObject.put("seq", destinationSeq +1 );
            delayObject.put("command",93);
            delayObject.put("x",destiLat);
            delayObject.put("y",destiLng);
            delayObject.put("z",10);
            delayObject.put("param1", 10);
            delayObject.put("param2", 0);
            delayObject.put("param3", 0);
            delayObject.put("param4", 0);
            System.out.println("delayObject : " + delayObject);
            root.put(delayObject);



            // root 목적지 제외하여 변경토록 함
            System.out.println("--------------");
            System.out.println("Will add");
            int idx = root.length();
            for(int i = limit - 2 ; i >= 0 ; i--){
                JSONObject object = (JSONObject) array.get(i);
                object.put("seq",idx);
                System.out.println(object.toString());
                idx++;
                root.put(object);
            }
            System.out.println("-------------");
            System.out.println("root");
            for (int i = 0 ; i < root.length(); i++){
                System.out.println(root.get(i).toString());
            }

            // 마지막 root를 지우고 이를 RTL로 변경
            root.remove(root.length()-1);
            JSONObject RTL = new JSONObject();
            RTL.put("seq", root.length());
            RTL.put("x", destiLat);
            RTL.put("y", destiLng);
            RTL.put("z", 0);
            RTL.put("command", 20);
            RTL.put("param1", 0);
            RTL.put("param2", 0);
            RTL.put("param3", 0);
            RTL.put("param4", 0);
            System.out.println("Last RTL : " + RTL.toString());
            root.put(RTL);

            // Drone Arm
            // Drone TakeOff
            client.ControlDroneStatus();

            GcsMain.instance.controller.flightMap.controller.missionClear();
            // 설계한 미션들 삭제
            //GcsMain.instance.controller.flightMap.controller.missionMake();
            GcsMain.instance.controller.flightMap.controller.setMissionItems_Customize(root);

        }
    };

    private EventHandler<ActionEvent> Hbox_Text_Magnet_Change = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {

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

        public void ControlDroneStatus(){
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
            GcsMain.instance.controller.flightMap.controller.missionMapSync();
        }
    }
}
