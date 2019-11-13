package syk.sample.gcs.main.Service02;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import syk.sample.gcs.main.GcsMain;

public class ServiceDialog02 {

    private Stage stage;

    public ServiceDialog02(){

        try {
            stage = new Stage(StageStyle.DECORATED);
            //stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(GcsMain.instance.primaryStage);
            // GcsMain에 종속되도록 한다.
            BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("ServiceDialog02.fxml"));
            Scene scene = new Scene(borderPane);
            scene.getStylesheets().add(GcsMain.class.getResource("style_dark.css").toExternalForm());
            // GcsMain.java 클래스가 있는 위치.style_dark.css
            // css 적용
            stage.setScene(scene);
            stage.setResizable(false);
            // 확장 불가능 하게 변경 가능
        }
        catch (Exception e){
            System.out.println("ServiceDialog02 Error");
        }
    }
    public void show(){
        stage.show();
    }

    public void close(){
        stage.close();
    }

}
