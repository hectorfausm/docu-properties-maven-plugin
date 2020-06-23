package es.home.properties.plugin.interfaces.documenter;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.model.DocumenterUnit;
import es.home.properties.model.MavenDocumenterPropertiesConfiguration;

/** Interfaz para la creación de Documentadores */
public interface MavenPropertiesDocumenter {
	
	/** Permite obtener la configuración del POM */
	public abstract MavenDocumenterPropertiesConfiguration getConfiguration();
	
	/** Permite inicializar el Documentador
	 * @throws PluginDocumentationException */
	public abstract void initializeFile() throws PluginDocumentationException;
	
	/** Permite derrar el documentador 
	 * @throws PluginDocumentationException */
	public abstract void closeFile() throws PluginDocumentationException;

	/** Permite documentar una unidad de documentación 
	 * @param documenterUnit Unidad de documentación a documentar
	 * @throws PluginDocumentationException */
	public abstract void documentUnit(DocumenterUnit documenterUnit) throws PluginDocumentationException;
	
	/**
	 * Permite escribir el fichero en disco
	 * @param fileName Nombre del fichero. Sólo nombre, si extensión ni dirección dentro del disco.
	 * @throws PluginDocumentationException
	 * */
	public abstract void writeFile(String fileName) throws PluginDocumentationException;
}
