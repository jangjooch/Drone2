package exam04.service01;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class ServiceDialog01 {

    private Stage stage;

    public ServiceDialog01(){
        try {
            stage = new Stage(StageStyle.DECORATED);
            // StageStyle.UTILITY
            //      -> exit만 가능
            // StageStyle.DECORATED
            //      -> 일반적인 윈도우. exit, 확대, 확장 전부 가능
            // StageStyle.UNDECORATED
            //      -> exit, 확대만 가능. 확장 불가
            // StageStyle.TRANSPARENT
            //      -> exit, 확대, 확장 아무것도 불가능하다.
            stage.initModality(Modality.APPLICATION_MODAL);
            // Modality.WINDOW_MODAL
            //      -> 창이 구현되어도 다른 곳의 창 사용가능
            // Modality.APPLICATION_MODAL
            //      -> 창이 구현되면 다른 창의 사용을 막음
            BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("ServiceDialog01.fxml"));
            Scene scene = new Scene(borderPane);
            stage.setScene(scene);
            stage.setResizable(false);
            // 확장 불가능 하게 변경 가능
        }
        catch (Exception e){
            System.out.println("Error in ServiceDialog01");
        }
    }

    public void show(){
        stage.show();
    }

    public void close(){
        stage.close();
    }
}
