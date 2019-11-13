package exam04;

import exam04.service01.ServiceDialog01;
import exam04.service02.ServiceDialog02;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button BtnService01;
    @FXML
    private Button BtnService02;
    @FXML
    private Button BtnService03;
    // fxml 에서 적용한 ID 그대로 가져와 사용하여야 객체 주입이 가능하다
    // <Button fx:id="Hbox_Btn_Ok" 에서 설정한 fx:id의 값 그대로 사용한다.

    // implements 받은 Initializable 를 구현
    // 위에서 객체 주입받은 것을 동기화를 통해 이벤트 핸들러를 작성한다.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Button 이 클릭하면 진행될 이벤트 헨들러를 등록한다.
        BtnService01.setOnAction(BtnService01Handler);
        BtnService02.setOnAction(BtnService02Handler);
        BtnService03.setOnAction(BtnService03Handler);
    }
    private EventHandler<ActionEvent> BtnService01Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService01 Action Activate");
            ServiceDialog01 serviceDialog01 = new ServiceDialog01();
            serviceDialog01.show();
        }
    };

    private EventHandler<ActionEvent> BtnService02Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService02 Action Activate");
            ServiceDialog02 serviceDialog02 = new ServiceDialog02();
            serviceDialog02.show();
        }
    };

    private  EventHandler<ActionEvent> BtnService03Handler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            System.out.println("BtnService03 Action Activate");
        }
    };
}
