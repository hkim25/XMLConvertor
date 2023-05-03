package kr.co.inspien.hkim.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import kr.co.inspien.hkim.MainApp;

public class RootController implements Initializable{
	
	private MainApp mainApp;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void handleOpen() {
		this.mainApp.doOpen();
	}
	
	public void handleSave() {
		this.mainApp.doSave();
	}
	
	public void handleClose() {
		this.mainApp.doClose();
	}
	
	public void handleClear() {
		this.mainApp.doClear();
	}
	
	public void handlePretty(){
		this.mainApp.doPretty();
	}
	
	public void handleAbout() {
		this.mainApp.doAbout();
	}
}
