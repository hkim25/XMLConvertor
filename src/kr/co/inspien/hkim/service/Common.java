package kr.co.inspien.hkim.service;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import kr.co.inspien.hkim.model.FilePath;

public class Common {
	public static final Logger getLogger(Class<?> cls) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		File log4j2XmlFile = new File(FilePath.LOG4J.get().toString());
		context.setConfigLocation(log4j2XmlFile.toURI());
		return context.getLogger(cls);
	}
}
