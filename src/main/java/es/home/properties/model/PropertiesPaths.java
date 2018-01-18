package es.home.properties.model;

/**
 * Paths de salida y entrada de propiedades
 */
public class PropertiesPaths {
	
	/** Path de entrada de ficheros */
	private String input;
	
	/** Path de salida de ficheros */
	private String output;
	
	/** Determina si se debe o no mantener la estructura de carpetas dentro del objetivo de compilación */
	private boolean maintainDirStructure; 
	
	// Métodos de salida de datos
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public void setMaintainDirStructure(boolean maintainDirStructure) {
		this.maintainDirStructure = maintainDirStructure;
	}
	public boolean isMaintainDirStructure() {
		return maintainDirStructure;
	}
}
