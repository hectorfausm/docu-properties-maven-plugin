package es.home.properties.model;

public enum ErrorType {
	PATTERN("La propiedad no cumple el patrón:");
	
	private String errorMsg;

	private ErrorType(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
}
