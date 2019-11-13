package exam04.service02;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ServiceDialog02Controller implements Initializable {

    @FXML
    private javafx.scene.control.Button Hbox_Btn_Ok;
    @FXML
    private javafx.scene.control.Button Hbox_Btn_Cancel;
    @FXML
    private Button Hbox_Btn_Print;
    // fxml 에서 적용한 ID 그대로 가져와 사용하여야 객체 주입이 가능하다
    // <Button fx:id="Hbox_Btn_Ok" 에서 설정한 fx:id의 값 그대로 사용한다.

    // implements 받은 Initializable 를 구현
    // 위에서 객체 주입받은 것을 동기화를 통해 이벤트 핸들러를 작성한다.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Button 이 클릭하면 진행될 이벤트 헨들러를 등록한다.
        Hbox_Btn_Ok.setOnAction(BtnService01Handler);
        Hbox_Btn_Cancel.setOnAction(BtnService02Handler);
        Hbox_Btn_Print.setOnAction(BtnService03Handler);
    }

    private javafx.event.EventHandler<ActionEvent> BtnService01Handler = new javafx.event.EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService01 Action Activate");

        }
    };

    private javafx.event.EventHandler<ActionEvent> BtnService02Handler = new javafx.event.EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService02 Action Activate");
            Stage stage = (Stage)Hbox_Btn_Cancel.getScene().getWindow();
            // Hbox_Btn_Cancel 이 위치한 Scene이 있는 Window의 위치를 받아
            stage.close();
        }
    };

    private javafx.event.EventHandler<ActionEvent> BtnService03Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService03 Action Activate");
        }
    };
}
