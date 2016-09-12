package es.home.properties.plugin.interfaces.documenter;

import es.home.properties.exception.SiaPluginDocumentationException;
import es.home.properties.model.DocumenterUnit;
import es.home.properties.model.MavenDocumenterPropertiesConfiguration;

/** Interfaz para la creación de Documentadores */
public interface MavenPropertiesDocumenter {
	
	/** Permite obtener la configuración del POM */
	public abstract MavenDocumenterPropertiesConfiguration getConfiguration();
	
	/** Permite inicializar el Documentador
	 * @throws SiaPluginDocumentationException */
	public abstract void initializeFile() throws SiaPluginDocumentationException;
	
	/** Permite derrar el documentador 
	 * @throws SiaException */
	public abstract void closeFile() throws SiaPluginDocumentationException;

	/** Permite documentar una unidad de documentación 
	 * @param documenterUnit Unidad de documentación a documentar
	 * @throws SiaPluginDocumentationException */
	public abstract void documentUnit(DocumenterUnit documenterUnit) throws SiaPluginDocumentationException;
	
	/**
	 * Permite escribir el fichero en disco
	 * @param fileName Nombre del fichero. Sólo nombre, si extensión ni dirección dentro del disco.
	 * @throws SiaPluginDocumentationException
	 * */
	public abstract void writeFile(String fileName) throws SiaPluginDocumentationException;
}
