package es.home.properties.plugin.utils;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.model.Variable;

public class StringUtils {
	
	/**
	 * Filtra la información del fichero con las variabels del plugin, si el plugin no tiene variables, 
	 * devuelve la información del fichero en formato {@link String}
	 * @param propertieFileContent
	 * @param variables
	 * @return DEvuelve la ifnromación del fichero filtrada con las variables de cofniguración en formato {@link String}
	 */
	public static final String filterStringWithVariables(
			String propertieFileContent, Variable[] variables, Log logger) {
		
		// Si hay variables en configuración se ejecuta la sustitución
		if(variables!=null && propertieFileContent!=null){
			
			// Si hay propiedades en el texto
			String result = propertieFileContent;
			for (Variable variable : variables) {
				if(variable.getKey()!=null && variable.getValue()!=null){
					logger.debug("Filtrando resultado para la variable: "+variable.getKey()+" con valor: "+variable.getValue());
					result = result.replace("${"+variable.getKey()+"}", variable.getValue());
				}
			}
			return result;
			
		// SI no hay variables se devuelve el dato
		}else{
			return propertieFileContent;
		}
	}
}
