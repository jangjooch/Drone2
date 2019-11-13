package exam03;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button Hbox_Btn_Ok;
    @FXML
    private Button Hbox_Btn_Cancel;
    @FXML
    private Button Hbox_Btn_Print;
    // fxml 에서 적용한 ID 그대로 가져와 사용하여야 객체 주입이 가능하다
    // <Button fx:id="Hbox_Btn_Ok" 에서 설정한 fx:id의 값 그대로 사용한다.

    // implements 받은 Initializable 를 구현
    // 위에서 객체 주입받은 것을 동기화를 통해 이벤트 핸들러를 작성한다.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Button 이 클릭하면 진행될 이벤트 헨들러를 등록한다.
        Hbox_Btn_Ok.setOnAction(Hbox_Btn_OkEventHandler);
        Hbox_Btn_Cancel.setOnAction(Hbox_Btn_CancelEventHandler);
        Hbox_Btn_Print.setOnAction(Hbox_Btn_PrintEventHandler);
    }
    private EventHandler<ActionEvent> Hbox_Btn_CancelEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Cancel Action Activate");
        }
    };

    private EventHandler<ActionEvent> Hbox_Btn_OkEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Ok Action Activate");
        }
    };

    private  EventHandler<ActionEvent> Hbox_Btn_PrintEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("Hbox_Btn_Print Action Activate");
        }
    };
}
