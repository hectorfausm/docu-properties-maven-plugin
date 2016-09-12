package es.home.properties.model;

/**
 * Tipos posibles de documentadores de ficheros de propiedades
 * */
public enum DocumenterType {
	/** Fichero Excel xlsx, se generará una atabla excel */
	EXCEL,
	/** Fichero txt. se generará un árbol de elementos en formato plano  */
	PLAIN,	
	/** Fihcero html. Se generará una tabla html */
	HTML;
}
