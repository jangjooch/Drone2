package syk.sample.drone.main;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import syk.common.MavJsonMessage;
import syk.drone.device.Device;
import syk.drone.device.FlightController;
import syk.drone.mavlink.MavLinkListener;
import syk.drone.mavlink.Messages.MAVLinkMessage;
import syk.drone.mavlink.common.msg_mission_current;
import syk.sample.drone.network.NetworkConfig;

public class SimulationMain {
    private static final Logger logger = LoggerFactory.getLogger(SimulationMain.class);
    public static void main(String[] args) {
        NetworkConfig networkConfig = new NetworkConfig();

        FlightController flightController = new FlightController();
        flightController.mavlinkConnectTcp("localhost", 5760);
        // 현재 내꺼로 연결하는 것이니 localhost 이다. 만약 다른 곳의 가상 머신에 접근한다면
        // host 정보를 해당 pc의 ip로 변경하고 포트 또한 5762로 변경한다.
        // 왜? 이미 해당 pc에서 5760 포트를 사용하고 있을 것이기에
        flightController.mqttConnect(
                networkConfig.mqttBrokerConnStr,
                networkConfig.droneTopic +"/fc/pub",
                networkConfig.droneTopic +"/fc/sub"
        );

        flightController.addDevice(new Device(1) {
            @Override
            public void off() {
                logger.info("Action Off");
            }

            @Override
            public void on() {
                logger.info("Action On");
            }
        });
    }
}
