package es.home.properties.plugin.goals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.exception.PluginDocumentationExceptionCode;
import es.home.properties.model.DocumenterUnit;

/**
 * Permite crear la documentación de uno o varios directorios que contengan ficheros de propiedades.
 * */
@Mojo(name="docu-properties-input", requiresProject=true, aggregator=true)
public class DocuPropertiesInputMojo extends DocuPropertiesAbstractMojo{

	/** Directorios donde ir a buscar los recursos  */
	@Parameter
	private String[] inputs;
	
	/** Atributo de sólo lectura que permite obtener el directorio output por defecto */
	@Parameter(defaultValue="${project.build.directory}/docu-properties", readonly=true)
	private String defaultOutput;
	
	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if(inputs!=null){
				getLog().info("Recorriendo los recursos inputs");
				boolean isOutputDefault = defaultOutput.equals(getConfiguration().getOutput());
				getLog().debug("El directorio por defecto "+((isOutputDefault)?"no ":"")+"ha cambiado");
				for (String input : inputs) {
					if(isOutputDefault){
						getConfiguration().setOutput(input+"/docu-properties");
					}
				
					proccessPath(input);
				}
			}
		}catch (PluginDocumentationException e) {
			getLog().error(e.geti18Message(),e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void proccesFile(Path file) throws PluginDocumentationException {
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
