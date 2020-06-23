package es.home.properties.plugin.factories;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.model.MavenDocumenterPropertiesConfiguration;
import es.home.properties.plugin.implementations.documenter.ExcelDocumenter;
import es.home.properties.plugin.interfaces.documenter.MavenPropertiesDocumenter;

/**
 * Factoría de documentadores
 * */
public class DocumenterFactory {
	
	/**
	 * Método que permite obtener un documentador a partir de la configuración
	 * @param configuration Configuración que contiene el tipo de documentador
	 * @param log Logger del mojo
	 * */
	public static final MavenPropertiesDocumenter createDocumenter(MavenDocumenterPropertiesConfiguration configuration, Log log){
		switch (configuration.getType()) {
			case EXCEL:
				return new ExcelDocumenter(configuration,log);
			default:
				log.info("El tipo de documenter: "+configuration.getType()+" todavía no ha sido desarrollado");
				break;
			}
		return null;
	};
}
