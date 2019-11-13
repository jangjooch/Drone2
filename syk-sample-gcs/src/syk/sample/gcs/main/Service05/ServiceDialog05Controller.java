package syk.sample.gcs.main.Service05;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import syk.gcs.mapview.FlightMapController;
import syk.sample.gcs.main.GcsMain;

import java.net.URL;
import java.util.ResourceBundle;

public class ServiceDialog05Controller implements Initializable {

    @FXML private Button Btn_Reset;
    @FXML private WebView WebView_Center;

    private WebEngine webEngine;
    public ServiceDialog05Controller() throws MqttException {
        System.out.println("Service05Controller client Created");
        // webEngine 얻기
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Btn_Reset.setOnAction(Btn_Reset_Handler);
    }

    private EventHandler<ActionEvent> Btn_Reset_Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Try webEngine.load");
            WebViewCall();
        }
    };

    public void WebViewCall() {
        System.out.println("Before getEngine()");
        webEngine = WebView_Center.getEngine();
        System.out.println("getEngine() Done");
        // webEngine.load("http://www.naver.com/");
        webEngine.load("http://106.253.56.124:8081/finalWebProject/?msgid=gcs");
        // 나중에 구현되면 이렇게 하기
    }

}
