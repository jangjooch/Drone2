package syk.sample.gcs.main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GcsMain extends Application {
	// static 이기에 GcsMain 객체를 만들지 않고 클래스 자체로 접근이 가능하다.
	// GcsMain.instance GcsMain 객체를 얻는다.
	// GcsMain.instance.controller.flightMap 을 통하여 다른곳에서 접근이 가능하다.
	// 이렇게 되면 어디서든 instance를 통해 GcsMain의 객체를 어디서든 접근이 가능하다.
	public static GcsMain instance;
	public Stage primaryStage;
	public BorderPane ui;
	public GcsMainController controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;

		this.primaryStage = primaryStage;

		//실행시 하얀 백그라운드를 보이지 않도록 하기 위해서 필요
		primaryStage.setOpacity(0.0);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("GcsMain.fxml"));
		ui = loader.load();
		controller = loader.getController();

		Scene scene = new Scene(ui);
		scene.getStylesheets().add(GcsMain.class.getResource("style_dark.css").toExternalForm());


		primaryStage.setTitle("Drone Ground Control Station");
		primaryStage.setScene(scene);

		primaryStage.setMaximized(true);

		primaryStage.show();

		//백그라운드 보여주기
		primaryStage.setOpacity(1.0);

		//윈도우 닫기 버튼을 클릭했을 때
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
	}

	@Override
	public void stop() {
		//Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}