package kr.co.inspien.hkim.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import kr.co.inspien.hkim.MainApp;
import kr.co.inspien.hkim.model.Result;

public interface Service {
	public Result convert(String input);
	public String fileToString(Path path) throws IOException;
	public List<String>listFiles(Path path);
	public void writeTextFile(String data, Path path) throws IOException;
	public void alert(Stage oner, AlertType type, String title, String header, String content);
	public String traceToString(Throwable throwable);
	public byte[] openFile(MainApp mainApp);
	public void saveFile(MainApp mainApp, byte[] data);
	public String prettyXml(String xmlString, int indent, boolean declaration, boolean isRetry);
	public String prettyPrint(String input);
}
