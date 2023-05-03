package kr.co.inspien.hkim.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public enum FilePath implements Supplier<Path>{
	//INPUT("\\input\\"),
	//OUTPUT("\\output\\"),
	//BACKUP("\\input\\backup\\"),
	LOG4J("\\resources\\log\\log4j\\log4j2.xml")
	;
	
	private Path v;
	
	FilePath(String v) {
		this.v = Paths.get(System.getProperty("user.dir")+v);
	}
	@Override
	public Path get() {
		return v;
	}

}
