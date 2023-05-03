package kr.co.inspien.hkim.model;

import java.util.function.Supplier;

public enum DataType implements Supplier<String>{
	XML("xml"),
	JSON("json");
	private String value;
	DataType(String value){
		this.value = value;
	}
	@Override
	public String get() {
		return this.value;
	}
}
