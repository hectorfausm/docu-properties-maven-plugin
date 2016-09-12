package es.home.properties.plugin.excel;

/**
 * Bean que idneitifica una celda de una cabecera
 * */
public class CellHeaderBean {
	
	/** Clave excel */
	private String excelKey;
	
	/** Posición que ocupa esa clave/columna en el fichero */
	private int position;
	
	/**
	 * Constructor
	 * @param excelKey Clave excel
	 * @param position Posición que ocupa esa clave/columna en el fichero
	 * */
	public CellHeaderBean(String excelKey, int position) {
		this.excelKey = excelKey;
		this.position = position;
	}

	//Accedentes
	public String getExcelKey() {
		return excelKey;
	}
	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return getClass().getName() + " {\n\t"
				+ (excelKey != null ? "excelKey: " + excelKey + "\n\t" : "")
				+ "position: " + position + "\n\t[super: " + super.toString()
				+ "]\n}";
	}
}
