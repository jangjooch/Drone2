/*
java -Djava.library.path=/usr/lib/jni:/home/pi/opencv/opencv-3.4.5/build/lib -cp classes:lib/'*' companion.companion.RealMain
 */

package syk.sample.drone.main;

import com.pi4j.io.gpio.*;
import org.eclipse.paho.client.mqttv3.*;
import syk.drone.device.Camera;
import syk.drone.device.FlightController;
import syk.sample.drone.network.NetworkConfig;
import syk.sample.gcs.main.Service03.ServiceDialog03Controller;

public class RealMain {
    public static void main(String[] args) throws MqttException, InterruptedException {
        NetworkConfig networkConfig = new NetworkConfig();

        FlightController flightController = new FlightController();
        flightController.mavlinkConnectRxTx("/dev/ttyAMA0");
        flightController.mqttConnect(
                // networkConfig 내에서 networkconfig.properties 에 접근하여 메타 데이터를 가져와
                // 적용하여 연결한다.
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic +"/fc/pub",
                networkConfig.droneTopic +"/fc/sub"
        );
        // USB 0번 1번에 따른 카메라 설정.
        // 먼저 꽃는 것에 따라 0번과 1이 바뀐다.
        // 만약 전방이 0이 아닐경우 Topic 번호만
        // 바꾸어주면 바뀌게 될것이다.
        Camera camera0 = new Camera();
        // 하단캠
        camera0.cameraConnect(0, 320, 240, 90);
        // angle은 화면 돌아가는 방향
        camera0.mattConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic + "/cam1/pub",
                networkConfig.droneTopic + "/cam1/sub"
        );
        // 상단캠
        Camera camera1 = new Camera();
        camera1.cameraConnect(1, 320, 240, 0);
        camera1.mattConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic +"/cam0/pub",
                networkConfig.droneTopic +"/cam0/sub"
        );


        ControlMagnet controlMagnet = new ControlMagnet();
        controlMagnet.Creating_Connection(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic + "/magnet/pub",
                networkConfig.droneTopic + "/magnet/sub"
        );
        /*
        ControlMagnet controlMagnet = new ControlMagnet();
        controlMagnet.makeSub();
        Thread magnetState = new Thread(){

        };

        while (true){
            controlMagnet.sendMagnetState();
            Thread.sleep(1000);
        }
        */



        //MqttClient statusClient = new MqttClient("tcp://106.253.56.124:1881", MqttClient.generateClientId(), null);
        /*
        while(true){
            ElectroMagnet electroMagnet = controlMagnet.getElectroMagnet();
            String status = electroMagnet.getStatus();
            System.out.println(status);
            //statusClient.publish("/jang/gcs/serviceDialog03",status.getBytes(),0,false);
            Thread.sleep(1000);
        }
        */

    }
}
