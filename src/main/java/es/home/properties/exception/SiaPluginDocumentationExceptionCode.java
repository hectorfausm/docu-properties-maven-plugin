package es.home.properties.exception;

/**
 * Códigos de excepciones SPNEGO
 * @author hfaus
 * */
public enum SiaPluginDocumentationExceptionCode {
	
	// EXCECPIONES GENERICAS
	/** Excepción genérica */
	GENERAL_EXCEPTION(0,Prefix.GENERAL),
	
	// EXCEPCIONES EXCEL
	/** Conocida durante la inicialización del fichero excel */
	ON_INIT_EXCEL(10,Prefix.EXCEL),
	/** Desconocida durante la inicialización del fichero excel */
	ON_INIT_EXCEL_UNKNOWN(11,Prefix.EXCEL),
	/** Durante la creación de al acebcera */
	ON_CREATE_HEADER(12,Prefix.EXCEL),
	/** Durante la obtención de un valor a partir de un field */
	ON_GET_VALUE_FROM_FIELD(13,Prefix.EXCEL),
	/** Desconocida producida durante la obtención de un valor a partir de un field */
	ON_GET_VALUE_FROM_FIELD_UNKNOWN(14,Prefix.EXCEL),
	/** Al escribir un fichero excel */
	ON_WRITE_FILE(15,Prefix.EXCEL),
	/** Descnocida al escribir un fichero excel */
	ON_WRITE_FILE_UNKNOWN(16,Prefix.EXCEL),
	/** Al cerrar un fichero excel */
	ON_CLOSE_XCEL(17,Prefix.EXCEL),
	/** Desconocida al cerrar un fichero excel */
	ON_CLOSE_XCEL_UNKNOWN(18,Prefix.EXCEL),
	/** Durante el parseo de fechas excel */
	ON_PARSE_DATE(19,Prefix.EXCEL),
	/** Desconocida durante el parseo de fechas excel */
	ON_PARSE_DATE_UNKNOWN(20,Prefix.EXCEL),
	/** Al autoajustar las filas de un fichero excel */
	ON_AUTOADJUST_EXCEL_ROWS(21,Prefix.EXCEL),
	/** Al autoajustar las filas de un fichero excel */
	ON_AUTOADJUST_EXCEL_ROWS_UNKNOWN(22,Prefix.EXCEL), 
	
	// EXCEPCIONES MOJO
	/** Al procesar un Path */
	ON_PROCESS_MOJO_PATH(100,Prefix.MOJO),
	/** Desconocido al procesar un Path */
	ON_PROCESS_MOJO_PATH_UNKNOWN(101,Prefix.MOJO),
	/** Al procesar un file */
	ON_PROCESS_MOJO_FILE(110,Prefix.MOJO),
	/** Desconocido al procesar un file */
	ON_PROCESS_MOJO_FILE_UNKNOWN(111,Prefix.MOJO);
	
	/**
	 * Número interno de la excepción
	 * */
	private int internNumber;
	
	/**
	 * Prefijo de la excecpión
	 * */
	private String prefix;

	private SiaPluginDocumentationExceptionCode(int internNumber, String prefix) {
		this.internNumber = internNumber;
		this.prefix = prefix;
	}
	
	/**
	 * Método que permite obtener La cadena códgio de la excepción
	 * */
	public String getCode(){
		return this.prefix+"_"+String.format("%04d", internNumber);
	}
}
