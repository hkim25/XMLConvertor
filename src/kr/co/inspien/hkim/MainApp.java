package kr.co.inspien.hkim;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kr.co.inspien.hkim.controller.CommonController;
import kr.co.inspien.hkim.controller.ConvertorController;
import kr.co.inspien.hkim.controller.RootController;
import kr.co.inspien.hkim.service.Common;
import kr.co.inspien.hkim.service.Service;
import kr.co.inspien.hkim.service.ServiceImpl;

public class MainApp extends Application {
	public Service service;
	private Logger logger;
	private Stage primaryStage;
	private BorderPane root;
	private ConvertorController convertorController;
	private AnchorPane convertor;
	private CommonController recentView;
	private String recentPath;
	
	@Override
	public void start(Stage primaryStage) {
		this.service = new ServiceImpl();
		this.logger = Common.getLogger(this.getClass());
		this.logger.info("program start");
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("XMLConvertor");
		this.primaryStage.getIcons().add(new Image("file:resources\\images\\xml.png"));
		initRootLayout();
		showConvertor();
	}
	
	@Override
	public void stop(){
		this.logger.info("bye");
	}
	
	public void initRootLayout() {
		this.logger.info("root layout initializing");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/Root.fxml"));
			this.root = (BorderPane)loader.load();
			Scene scene = new Scene(this.root);
			primaryStage.setScene(scene);
			RootController controller = loader.getController();
			controller.setMainApp(this);
			primaryStage.show();
		} catch (IOException e) {
			this.service.alert(this.primaryStage, AlertType.WARNING, 
					"Error", "Application Error", e.getMessage());
			this.logger.error(e.getMessage(),e);
		}
	}
	
	public void showConvertor() {
		this.logger.info("show convertor");
		try {
			if(this.convertorController==null) {
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource("view/Convertor.fxml"));
				this.convertor = (AnchorPane)loader.load();
				this.convertorController = loader.getController();
			}
			this.root.setCenter(this.convertor);
			this.convertorController.setMainApp(this);
			this.recentView = this.convertorController;
		} catch (IOException e) {
			this.service.alert(this.primaryStage, AlertType.WARNING, 
					"Error", "Application Error", e.getMessage());
			this.logger.error(e.getMessage(),e);
		}
	}
	
	public void doOpen() {
		this.recentView.open();
	}
	
	public void doSave() {
		this.recentView.save();
	}
	
	public void doClear() {
		this.recentView.clear();
	}
	
	public void doPretty() {
		this.recentView.pretty();
	}
	
	public void doClose() {
		Platform.exit();
	}
	
	public void doAbout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/About.fxml"));
			AnchorPane page = (AnchorPane)loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("about");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(this.primaryStage);
			dialogStage.getIcons().add(new Image("file:resources\\images\\xml.png"));
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.showAndWait();
		} catch (IOException e) {
			this.service.alert(this.primaryStage, AlertType.WARNING, 
					"Error", "Application Error", e.getMessage());
			this.logger.error(e.getMessage(),e);
		}
	}

	public Stage getPrimaryStage() {
		return this.primaryStage;
	}
	
	public String getRecentPath() {
		return recentPath;
	}

	public void setRecentPath(String recentPath) {
		this.recentPath = recentPath;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
