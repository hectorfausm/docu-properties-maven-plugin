package es.home.properties.plugin.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Clase interna con los datos del fichero excel que permite a la clase {@link ExcelAcces} manejar el fichero
 * */
public class ExcelFile{
	/** Dirección del fichero */
	private String pathFile;
	/** Semilla del fichero */
	private Sheet sheet;
	/** Objeto de la librería poi que representa una hoja */
	private Workbook workbook;
	/** Fila del fichero excel que representa la cabecera */
	private HeaderBean header;
	/** Tipode fichero excel */
	private ExcelType type;
	
	//Accedentes
	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}
	public Sheet getSheet() {
		return sheet;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setHeader(HeaderBean headerBean) {
		this.header = headerBean;
	}
	public HeaderBean getHeader() {
		return header;
	}
	public String getPathFile() {
		return pathFile;
	}
	public void setPathFile(String pathFile) {
		this.pathFile = pathFile;
	}
	public ExcelType getType() {
		return type;
	}
	protected void setType(ExcelType type) {
		this.type = type;
	}
}
