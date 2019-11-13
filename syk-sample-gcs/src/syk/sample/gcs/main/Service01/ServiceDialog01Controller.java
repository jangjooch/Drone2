package syk.sample.gcs.main.Service01;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import syk.sample.gcs.main.GcsMain;

import java.net.URL;
import java.util.ResourceBundle;

public class ServiceDialog01Controller implements Initializable {


    @FXML
    private Button Hbox_Btn_Ok;
    @FXML
    private Button Hbox_Btn_Cancel;
    @FXML
    private Button Hbox_Btn_Print;
    // fxml 에서 적용한 ID 그대로 가져와 사용하여야 객체 주입이 가능하다
    // <Button fx:id="Hbox_Btn_Ok" 에서 설정한 fx:id의 값 그대로 사용한다.
    @FXML private TextField txtLat;
    @FXML private TextField txtLng;


    // implements 받은 Initializable 를 구현
    // 위에서 객체 주입받은 것을 동기화를 통해 이벤트 핸들러를 작성한다.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Button 이 클릭하면 진행될 이벤트 헨들러를 등록한다.
        Hbox_Btn_Ok.setOnAction(Hbox_Btn_OkHandler);
        Hbox_Btn_Cancel.setOnAction(Hbox_Btn_CancelHandler);
        Hbox_Btn_Print.setOnAction(Hbox_Btn_PrintHandler);
    }

    private EventHandler<ActionEvent> Hbox_Btn_OkHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Ok Action Activate");
            double lat = Double.parseDouble(txtLat.getText());
            double lng = Double.parseDouble(txtLng.getText());
            System.out.println("Lat : " + lat + "\t Lng : " + lng);
            GcsMain.instance.controller.flightMap.controller.setMissionItems(lat,lng);
            // GcsMain 의 static 인 instance 를 받아 공용적인 GcsMain을 받아 생성하고
            // 해당 GcsMain 의 미션에 대한 객체인 flightMap 을 얻어내고
            // 이후 여기에 대한 컨트롤을 위하여 .controller 를 받고 function을 통해
            // 미션 플레너에 추가한다.
            Stage stage = (Stage)Hbox_Btn_Cancel.getScene().getWindow();
            // Hbox_Btn_Cancel 이 위치한 Scene이 있는 Window의 위치를 받아
            stage.close();
        }
    };

    private EventHandler<ActionEvent> Hbox_Btn_CancelHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Cancel Action Activate");
            Stage stage = (Stage)Hbox_Btn_Cancel.getScene().getWindow();
            // Hbox_Btn_Cancel 이 위치한 Scene이 있는 Window의 위치를 받아
            stage.close();
        }
    };

    private  EventHandler<ActionEvent> Hbox_Btn_PrintHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Print Action Activate");
        }
    };
}
