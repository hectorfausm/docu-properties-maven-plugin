package es.home.properties.model;

/**
 * Tipos de línea procesada dentro de un fichero de propiedades
 * */
public enum DocumenterLineType {
	/** Línea de inicio de documentación de propiedad, la notación de inicio */
	INIT,
	/** Línea de final de documentación de propiedad, la propia propiedad */
	FINISH,
	/** Línea de Descripción de propiedad, la anotación de descripción */
	DESCRIPTION,
	/** Línea de estado de propiedad, la anotación de estado */
	STATE,
	/** Línea de ejemplo de propiedad, la anotación de ejemplo */
	EXAMPLE,
	/** Línea de valores de la propiedad, la anotación de valores */
	VALUES,
	/** Determina el estado de obligatoriedad o no de la propiedad */
	MANDATORY,
	/** Patrón que aceptan los valores de la propiedad*/
	PATTERN,
	/** Posibles valores de la propieadd */
	POSSIBLE_VALUES,
	/** Línea de valor de variable en un entorno, la anotación de este tipo pasada par por configuración */
	ENVIRONMENT_VALUE,
	/** Línea, que si existe, determina que una propiedad solo será escribible si tiene valor */
	VISIBLE_WITH_VALUE,
	/** Ningún tipo útil para la aplicación */
	ANYTHING,
	/** Línea de iniciaode comentario simple */
	INIT_SIMPLE_COMMENT
}
