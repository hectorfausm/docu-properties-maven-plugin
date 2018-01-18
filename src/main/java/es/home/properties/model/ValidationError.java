package es.home.properties.model;

public class ValidationError {
	private String propertyKey;
	private String anexo;
	private ErrorType errorType;
	private String value;
	
	public ErrorType getErrorType() {
		return errorType;
	}
	public String getPropertyKey() {
		return propertyKey;
	}
	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}
	public void setPropertyKey(String propertyKey) {
		this.propertyKey = propertyKey;
	}
	public void setAnexo(String anexo) {
		this.anexo = anexo;
	}
	public String getValue() {
		if(value==null){
			return "null";
		}
		return value;
	}
	public String getAnexo() {
		if(anexo==null){
			return "";
		}
		return anexo;
	}
	
	@Override
	public String toString() {
		return "["+this.getPropertyKey()+"] - ["+this.getValue()+"] -> "+this.getErrorType().getErrorMsg()+" "+this.getAnexo();
	}
	public void setValue(String value) {
		this.value=value;
	}
}
