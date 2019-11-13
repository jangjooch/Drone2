package exam04.service02;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ServiceDialog02 {

    private Stage stage;

    public ServiceDialog02(){

        try {
            stage = new Stage(StageStyle.DECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            BorderPane borderPane = (BorderPane) FXMLLoader.load(getClass().getResource("ServiceDialog01.fxml"));
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
