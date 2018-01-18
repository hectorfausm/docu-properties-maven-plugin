package es.home.properties.model;

/**
 * Mapeo de ficheros docuPropetie con fichero de propiedades java
 */
public class FileMap {
	
	/** Fichero de propiedades docu-properties */
	private String docuPropertiesFile;
	
	/** Fichero de porpiedades java asociado */
	private String javaFile;
	
	// METODOS DE ACCESO
	public String getDocuPropertiesFile() {
		return docuPropertiesFile;
	}
	public void setDocuPropertiesFile(String docuPropertiesFile) {
		this.docuPropertiesFile = docuPropertiesFile;
	}
	public String getJavaFile() {
		return javaFile;
	}
	public void setJavaFile(String javaFile) {
		this.javaFile = javaFile;
	}
}
