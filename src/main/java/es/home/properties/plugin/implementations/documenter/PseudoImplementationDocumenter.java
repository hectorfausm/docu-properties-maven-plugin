package es.home.properties.plugin.implementations.documenter;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.model.MavenDocumenterPropertiesConfiguration;
import es.home.properties.plugin.interfaces.documenter.MavenPropertiesDocumenter;

/**
 * Clase abstracta que implementa los elementos comunes de todos los Documentadores 
 * */
public abstract class PseudoImplementationDocumenter implements MavenPropertiesDocumenter{
	
	/** Objeto de configuración */
	private MavenDocumenterPropertiesConfiguration configuration;
	/** Logger del Mojo */
	private Log logger;
	
	/**
	 * Constructor por defecto
	 * @param configuration Objeto de configuración
	 * @param logger Logger del Mojo
	 * */
	public PseudoImplementationDocumenter(MavenDocumenterPropertiesConfiguration configuration, Log logger) {
		this.configuration = configuration;
		this.logger = logger;
	}
	
	/** {@inheritDoc} */
	@Override
	public MavenDocumenterPropertiesConfiguration getConfiguration() {
		return configuration;
	}
	
	// Accedentes
	public Log getLogger() {
		return logger;
	}
}
