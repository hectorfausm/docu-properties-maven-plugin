package es.home.properties.model;

/**
 * Define un avariable par sobreescribir propiedades
 * @author hsfaus
 */
public class Variable {
	
	/** Clave de al variable */
	private String key;
	
	/** Valor de la variable */
	private String value;
	
	// Métodos de acceso
	public void setKey(String key) {
		this.key = key;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
}
