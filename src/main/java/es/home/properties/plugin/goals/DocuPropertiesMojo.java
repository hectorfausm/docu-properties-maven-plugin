package es.home.properties.plugin.goals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.model.Resource;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.exception.PluginDocumentationExceptionCode;
import es.home.properties.model.DocumenterUnit;

/**
 * Permite crear la documentación de los ficheros de propiedades alojados en los directorios 
 * de recursos de maven de los proyectos objetivo del plugin, ya sea el objeto del plugin o los hijos
 * del POM del objeto del plugin. 
 * */
@Mojo(name="docu-properties", requiresProject=true)
public class DocuPropertiesMojo extends DocuPropertiesAbstractMojo
{

	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException{
		
		try{
			
			if(getDocumenter()!=null){
				List<Resource> resources = getDocumenter().getConfiguration().getProject().getResources();
				
				// Leyendo los recursos por defecto
				if(resources!=null ){
					getLog().info("Recorriendo los recursos del proyecto");
					for (Resource resource:resources) {
						String resourcePath = resource.getDirectory();
						proccessPath(resourcePath);
					}
				}
			}
		}catch(PluginDocumentationException e){
			getLog().error(e.geti18Message(),e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void proccesFile(Path file) throws PluginDocumentationException{
		try{
			if(isFileInExtensions(file.toString())){
				
				// Se inicializa un fichero
				getDocumenter().initializeFile();
				
				// Se obtienen las líneas del fichero
				List<String> lines = getLines(file);
				
				if(lines!=null){
				
					// Se recorren los objetos de documentación en el fichero
					List<DocumenterUnit> units = getDocumenterUnitsFromLines(lines);
					getLog().debug("Parseando las unidades: "+units);
					for (DocumenterUnit documenterUnit : units) {
						getDocumenter().documentUnit(documenterUnit);
					}
					
					// Se guarda el fichero
					int indexOf = file.toString().lastIndexOf("/");
					if(indexOf<0){
						indexOf = file.toString().lastIndexOf("\\");
					}
					String nameFile = ""; 
					if(indexOf>0){
						nameFile = file.toString().substring(indexOf);
					}else{
						nameFile = Long.toString(System.currentTimeMillis());
					}
					getDocumenter().writeFile(nameFile);
					
					// Se cierra el fochero en curso
					getDocumenter().closeFile();
				}
			}
		}catch (IOException e) {
			throw new PluginDocumentationException(
	    		"Excepción producida al procesar un path",
	    		PluginDocumentationExceptionCode.ON_PROCESS_MOJO_FILE,
	    		e
	    	);
		}catch (PluginDocumentationException e){
			throw e;
		}catch(Exception e){
			throw new PluginDocumentationException(
	    		"Excepción producida al procesar un path",
	    		PluginDocumentationExceptionCode.ON_PROCESS_MOJO_FILE_UNKNOWN,
	    		e
	    	);
		}
	}
}