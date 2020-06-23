package es.home.properties.plugin.excel;

/**
 * Enumerado que representa los tipoa sw ficheros excel
 * */
public enum ExcelType {
	
	/** Reads and writes Microsoft Excel (XLS) format files */
	HSSF("Horrible SpreadSheet Format","Reads and writes Microsoft Excel (XLS) format files","xls"),
	
	/** Reads and writes Office Open XML (XLSX) format files */
	XSSF("XML SpreadSheet Format","Reads and writes Office Open XML (XLSX) format files","xlsx");
	
	/** Nombre del tipo fichero en la biblioteca POI */
	private String name;
	/** Descripción del tipo de fichero */
	private String description;
	/** Extensión de los ficheros recomendada para el tipo de fichero */
	private String extension;

	/**
	 * Constructor
	 * @param name Nombre del tipo fichero en la biblioteca POI
	 * @param description Descripciçon del tipo de fichero
	 * */
	private ExcelType(String name, String description, String extension) {
		this.name = name;
		this.description = description;
		this.extension = extension;
	}
	
	//Acedentes
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getExtension() {
		return extension;
	}
}	
