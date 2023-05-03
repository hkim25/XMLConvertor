package kr.co.inspien.hkim.controller;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import kr.co.inspien.hkim.MainApp;
import kr.co.inspien.hkim.model.Result;
import kr.co.inspien.hkim.service.Common;

public class ConvertorController implements CommonController{
	@FXML
	private TextArea input;
	@FXML
	private TextArea output;
	@FXML
	private Button button;
	private MainApp mainApp;
	private Logger logger;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.logger = Common.getLogger(this.getClass());
		this.logger.info("convertor initialization");
		this.input.setText("");
		this.output.setText("");
		this.input.setOnDragOver((DragEvent event)->{
			if(event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			}
			event.consume();
		});
		this.input.setOnDragDropped((DragEvent event)->{
			try {
				Path path = event.getDragboard().getFiles().get(0).toPath();
				this.input.setText(new String(Files.readAllBytes(path),StandardCharsets.UTF_8));
			} catch (IOException e) {
				this.mainApp.service.alert(this.mainApp.getPrimaryStage(), AlertType.ERROR, 
						"Error", "fail to open file", "check your file again.");
			}
			event.consume();
		});
	}
	
	public void convertButton() {
		if(this.input.getLength()==0) {
			this.mainApp.service.alert(this.mainApp.getPrimaryStage(), AlertType.WARNING, 
					"Warning", "Invalid input data", "Can not convert empty string");
		}else {
			Result result = this.mainApp.service.convert(this.input.getText());
			if(result.getThrowable()!=null) {
				this.output.setText(this.mainApp.service.traceToString(result.getThrowable()));
				this.mainApp.service.alert(this.mainApp.getPrimaryStage(), AlertType.ERROR, 
						"Error", "Fail to convert!!", result.getThrowable().getMessage());
			}else {
				this.output.setText(result.getConverted());
			}
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setInput(String value) {
		this.input.setText(value);
	}
	
	public void setOutput(String value) {
		this.output.setText(value);
	}

	@Override
	public void open() {
		byte[] file = null;
		file = this.mainApp.service.openFile(this.mainApp);
		if(file!=null) {
			input.setText(new String(file,StandardCharsets.UTF_8));
		}
	}

	@Override
	public void save() {
		byte[] data = null;
		data = this.output.getText().getBytes(StandardCharsets.UTF_8);
		if(data!=null&&data.length>0) {
			this.mainApp.service.saveFile(this.mainApp, data);
		}else {
			this.mainApp.service.alert(this.mainApp.getPrimaryStage(), AlertType.WARNING, 
					"Warning", "Output data is empty", "Please execute convertor first before save.");
		}
	}

	@Override
	public void clear() {
		this.input.setText("");
		this.output.setText("");
	}

	@Override
	public void pretty() {
		this.input.setText(this.mainApp.service.prettyPrint(this.input.getText()));
		this.output.setText(this.mainApp.service.prettyPrint(this.output.getText()));
	}
}
