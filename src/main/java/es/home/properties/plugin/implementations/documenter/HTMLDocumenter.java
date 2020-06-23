package es.home.properties.plugin.implementations.documenter;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.model.DocumenterUnit;
import es.home.properties.model.MavenDocumenterPropertiesConfiguration;

/**
 * Documentador para el destino de un fichero HTML
 * */
public class HTMLDocumenter extends PseudoImplementationDocumenter{

	/** {@inheritDoc} */
	public HTMLDocumenter(MavenDocumenterPropertiesConfiguration configuration,
			Log logger) {
		super(configuration, logger);
	}

	/** {@inheritDoc} */
	@Override
	public void initializeFile() throws PluginDocumentationException {
		// TODO
	}

	/** {@inheritDoc} */
	@Override
	public void closeFile() throws PluginDocumentationException {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void documentUnit(DocumenterUnit documenterUnit)
			throws PluginDocumentationException {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void writeFile(String fileName)
			throws PluginDocumentationException {
		// TODO Auto-generated method stub
		
	}

}
