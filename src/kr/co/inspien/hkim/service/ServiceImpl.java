package kr.co.inspien.hkim.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import kr.co.inspien.hkim.MainApp;
import kr.co.inspien.hkim.model.DataType;
import kr.co.inspien.hkim.model.Result;

public class ServiceImpl implements Service{
	private Logger logger;
	
	public ServiceImpl() {
		this.logger = Common.getLogger(this.getClass());
	}
	
	public Result convert(String input){
		this.logger.info("start converting");
		JSONObject jsonObj = null;
		Result result = new Result();
		try {
			input = input.trim();
			this.logger.debug("input data : " + input);
			jsonObj = new JSONObject(input);
			this.logger.info("JSON -> XML");
			result.setConverted(this.prettyXml(XML.toString(jsonObj), 2, true, false));
			result.setDataType(DataType.XML);
			this.logger.info("success");
		}catch(JSONException je) {
			if(input.startsWith("<")) {
				try {
					this.logger.info("XML -> JSON");
					jsonObj = XML.toJSONObject(input);
					result.setConverted(jsonObj.toString(4));
					result.setDataType(DataType.JSON);
					this.logger.info("success");
				}catch(Exception e) {
					this.logger.info("error : " + e.getMessage());
					result.setThrowable(e);
				}
			}else if(input.startsWith("[")){
				result = convert("{\"row\":".concat(input).concat("}"));
			}else {
				this.logger.info("error : Invalid input data");
				result.setThrowable(new Exception("Invalid input data."));
			}
		}
		return result;
	}
	
	public String prettyPrint(String input) {
		String result = null;
		JSONObject jsonObj = null;
		try {
			input = input.trim();
			jsonObj = new JSONObject(input);
			result = jsonObj.toString(4);
		}catch(JSONException e) {
			if(input.startsWith("<")) {
				result = prettyXml(input, 2, true, false);
			}else if(input.startsWith("[")){
				String wrapped = "{\"row\":".concat(input).concat("}");
				String retried = prettyPrint(wrapped);
				result = retried.equals(wrapped)?input:retried;
			}else {
				result = input;
			}
		}
		return result;
	}
	
	public List<String> listFiles(Path path) {
		List<String> result = new ArrayList<>();
		File[] listOfFiles = path.toFile().listFiles();
	    for (File file : listOfFiles) {
	    	if(file.isDirectory()) {
	    		continue;
	    	}else if (file.isFile()) {
	            result.add(file.getName());
	        }
	    }
	    return result;
	}
	
	public String fileToString(Path path) throws IOException {
		return new String(Files.readAllBytes(path),StandardCharsets.UTF_8);
	}
	
	public void writeTextFile(String data, Path path) throws IOException {
		try(ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
			FileOutputStream fos = new FileOutputStream(path.toString());
			BufferedOutputStream bos = new BufferedOutputStream(fos);) {
			int readCount = 0;
			byte[] buffer = new byte[32];
			while ((readCount = bais.read(buffer))!= -1){
				bos.write(buffer, 0, readCount);
			}
		} catch (IOException e) {
			throw e;
		}
	}
	
	public void alert(Stage oner, AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
	    alert.initOwner(oner);
	    alert.setTitle(title);
	    alert.setHeaderText(header);
	    alert.setContentText(content);
	    alert.showAndWait();
	}
	
	public String traceToString(Throwable throwable) {
		StringWriter sw = null;
		PrintWriter pw = null;
		String result = "";
		try{
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			result = sw.toString();
		} catch (Exception e) {
			this.logger.error(e.getMessage(),e);
		} finally {
			if(sw!=null) {
				try {
					sw.close();
				} catch (IOException e) {
					this.logger.error(e.getMessage(),e);
				}
			}
			if(pw!=null) {
				pw.close();
			}
		}
		return result;
	}
	
	public byte[] openFile(MainApp mainApp) {
		byte[] result = null;
		File file = null;
		Path path = null;
		FileChooser fileChooser = null;
		this.logger.info("open file");
		try {
			fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));
			if(mainApp.getRecentPath()!=null) {
				fileChooser.setInitialDirectory(new File(mainApp.getRecentPath()));
			}
			file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
			if(file!=null) {
				path = file.toPath();
				this.logger.info("mime type : "+Files.probeContentType(path));
				mainApp.setRecentPath(file.isDirectory()?file.getPath():file.getParent());
				result = Files.readAllBytes(path);
				this.logger.info("open complete : " + path.toString());
			}else{
				this.logger.info("canceled");
			}
		} catch (IOException e) {
			mainApp.service.alert(mainApp.getPrimaryStage(), AlertType.ERROR, 
					"Error", "fail to open file", "check your file again.");
		}
		return result;
	}
	
	public void saveFile(MainApp mainApp, byte[] data) {
		File file = null;
		Path path = null;
		FileChooser fileChooser = null;
		this.logger.info("save file");
		try {
			fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));
			if(mainApp.getRecentPath()!=null) {
				fileChooser.setInitialDirectory(new File(mainApp.getRecentPath()));
			}
			file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
			if(file!=null&&data!=null&&data.length!=0) {
				path = file.toPath();
				Files.write(path, data);
				mainApp.setRecentPath(file.isDirectory()?file.getPath():file.getParent());
				this.logger.info("save complete : " + path.toString());
			}else {
				this.logger.info("canceled");
			}
		}catch(IOException e){
			mainApp.service.alert(mainApp.getPrimaryStage(), AlertType.ERROR, 
					"Error", "fail to save file", "check your data again.");
		}
	}
	
	public String prettyXml(String xmlString, int indent, boolean declaration, boolean isRetry){
		String result = null;
		String serialized = null;
		try(BufferedReader br = new BufferedReader(new StringReader(xmlString));){
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine())!=null) {
				sb.append(line.trim());
			}
			serialized = sb.toString();
		} catch (IOException e) {
			this.logger.error(e.getMessage());
			serialized = xmlString;
		}
		try (StringReader in = new StringReader(serialized); Writer out = new StringWriter();){
			InputSource src = new InputSource(in);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setIgnoringElementContentWhitespace(true);
			Document document = documentBuilderFactory.newDocumentBuilder().parse(src);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, declaration?"yes":"no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(document), new StreamResult(out));
			result = out.toString();
		} catch (SAXException e) {
			if(e.getMessage().equals("문서에서 루트 요소 다음에 오는 마크업은 올바른 형식이어야 합니다.")&&!isRetry) {
				xmlString = prettyXml("<root>"+xmlString+"</root>",indent,declaration,true);
			}else {
				this.logger.error(e.getMessage());
			}
			result = xmlString;
		} catch(IOException | ParserConfigurationException | TransformerException e) {
			this.logger.error(e.getMessage());
			result = xmlString;
		}
		return result;
	}
}
