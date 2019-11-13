package syk.sample.gcs.main.Service01;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import syk.sample.gcs.main.GcsMain;

public class ServiceDialog01 {

    private Stage stage;

    public ServiceDialog01(){
        try {
            stage = new Stage(StageStyle.DECORATED);
            // DECORATED         일반적 윈도우 스타일, 흰 배경, 제목줄에 장식(아이콘, 타이틀, 축소, 복원, 닫기  버튼 장식)이 있다.
            // UNDECORATED       흰 배경, 제목줄 X
            // TRANSPARENT       투명 배경, 제목줄 X
            // UNIFIED           DECORATED와 같지만, 내용물의 경계선 X
            // UTILITY           흰 배경, 제목줄에 타이틀, 종료버튼만 있다.

            //stage.initModality(Modality.APPLICATION_MODAL);
            // Modality.WINDOW_MODAL
            //      -> 창이 구현되어도 다른 곳의 창 사용가능
            // Modality.APPLICATION_MODAL
            //      -> 창이 구현되면 다른 창의 사용을 막음
            stage.initOwner(GcsMain.instance.primaryStage);
            // GcsMain에 종속되도록 한다.
            BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("ServiceDialog01.fxml"));
            Scene scene = new Scene(borderPane);
            scene.getStylesheets().add(GcsMain.class.getResource("style_dark.css").toExternalForm());
            // GcsMain.java 클래스가 있는 위치.style_dark.css
            // css 적용
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
