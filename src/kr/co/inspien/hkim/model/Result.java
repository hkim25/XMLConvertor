package kr.co.inspien.hkim.model;

public class Result {
	private String converted;
	private Throwable throwable;
	private DataType dataType;
	@Override
	public String toString() {
		return "Result [converted=" + converted + ", throwable=" + throwable + ", dataType=" + dataType + "]";
	}
	public DataType getDataType() {
		return dataType;
	}
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	public String getConverted() {
		return converted;
	}
	public void setConverted(String converted) {
		this.converted = converted;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
