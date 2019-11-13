package exam04;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    // Application 은 추상클래스 이기에 구현이 필요하다.
    @Override
    public void init()throws Exception{
        System.out.println("init() Activate");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("start(Stage primaryStage) Activate");
        // Main의 launch(args)가 실행 될 때,
        // 기본 생성자와 init() 다음에 자동적으로 실행된다.
        // 즉 primartStage 용도의 기본 창이 생성된다.
        primaryStage.setTitle("Primary Title");
        // primaryStage title 설정


        // AnchorPane anchorPane = (AnchorPane) FXMLLoader.load(getClass().getResource("Main.fxml"));
        // FXML 설정파일을 지정하도록 함.

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        BorderPane borderPane = (BorderPane)fxmlLoader.load();
        // 이러한 방법을 사용하여야 Controller 객체를 얻을 수 있다.

        Scene scene01 = new Scene(borderPane);
        // 매게 변수로 해당 장면의 layout 이 들어간다.
        // AnchorPane 객체 -> 좌표를 이용하여 디자인이 가능하다.
        primaryStage.setScene(scene01);
        // primaryStage의 장면을 scene01로 설정한다.

        // primaryStage.setWidth(600);
        // primaryStage.setHeight(400);
        // 너비 높이 크기 설정
        primaryStage.setMaximized(true);
        // 전체 창으로 설정

        primaryStage.show();
        // primaryStage 실행
    }

    @Override
    public void stop() throws Exception {
        // 소멸자 역할
        System.out.println("stop() Activate");
    }

    public static void main(String[] args){
        launch(args);
        // 실행 옵션을 위함. Application 이 가지고 있는 정적 메소드 이다.
    }
}
