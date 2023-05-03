package kr.co.inspien.hkim.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AboutController implements Initializable{
	@FXML
	private ImageView logo;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.logo.setImage(new Image("file:resources\\images\\logo.png"));
	}
}
