package es.home.properties.plugin.excel;


/**
 * Interface que contiene las constantes de las excepciones sobre configuracion que pueden ocurrir 
 * 	dentro de la aplicacion
 * @author SIA - jregidor
 * @version 1.0.0
 */
public interface SiaPluginDocumentationConstants {
	// Ponemos el prefijo del modulo
	final static String PREFIX = "EXCEL_ACCES_PLUGIN";
	
	/** Relacionadas con la carga masiva de datos en excel */
	public interface EXCEL_READER{
		/** Fichero no encontrado */
		public static final String FILE_NOT_FOUND 	= PREFIX+"3001";
		/** Excepción producida durante la lectura del fichero */
		public static final String IO_EXCEPTION 	= PREFIX+"3002";
		/** Excepción producida al cerrar el fichero excel */
		public static final String ON_CLOSE 		= PREFIX+"3003";
		/** Excepción producida durante la escritura del fichero Excel */
		public static final String ON_WRITE_FILE 	= PREFIX+"3004";
		/** Excepción producida al intentar obtener el valor del atributo de un objeto */
		public static final String GET_FIELD_VALUE 	= PREFIX+"3005";
		
	};
	
	/** Relacionadas con la carga masiva de datos en excel */
	public interface TREE{
		/** Fichero no encontrado */
		public static final String FILE_NOT_FOUND 	= PREFIX+"4001";
		/** Excepción producida durante la lectura del fichero */
		public static final String IO_EXCEPTION 	= PREFIX+"4002";
		/** Excepción producida durante la instanciación de la clase de un arbol jsonizable */
		public static final String INSTANTIATION 	= PREFIX+"4010";
		/** El elemento json introducido no representa un arbol */
		public static final String MAL_FORMED_TREE 	= PREFIX+"4011";
		/** Excecpión desconocida producida durante la creación de un árbol a partir de un fichero json */
		public static final String UNKNOWN 			= PREFIX+"4012";
		/** El tamño del stack de java es demasiado pequeño para el arbol que se desea leer desde fichero */
		public static final String LITLE_STACK 		= PREFIX+"4013";
		
	};
	
	/** Relacionadas con el portal */
	public interface PORTAL {
		public static final String SRVC_CONNECTION = PREFIX + "1001";
		public static final String SRVC_EXECUTION = PREFIX + "1002";
	}
	
	/** Relacionadas con el objeto de búsqueda y sus métodos */
	public interface SEARCHER{
		public static final String NO_ATTR_IN_GROUP = "2001";
		public static final String NO_ATTR_MULTIVALUATED = "2002";
	}
}