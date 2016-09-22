package es.home.properties.plugin.goals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

import es.home.properties.exception.SiaPluginDocumentationException;
import es.home.properties.exception.SiaPluginDocumentationExceptionCode;
import es.home.properties.model.DocumenterUnit;

public abstract class DocuPropertiesAbstractWriteProperties extends DocuPropertiesAbstractMojo{
	
	/**
	 * The envirnoment value to write in properties
	 * */
	@Parameter(required=true, alias="write.environment")
	private String writeEnvironment;
	
	/**
	 * Determine what is the number of annotations that be write in the result property
	 * */
	@Parameter(alias="write.annotationString", defaultValue="0")
	private int writeAnnotationString;

	/**
	 * Determine if the state will be writted in compiled file
	 */
	@Parameter(defaultValue="false")
	private boolean addState;
	
	/**
	 * Determine if the example will be writted in compiled file
	 */
	@Parameter(defaultValue="false")
	private boolean addExample;
	
	/**
	 * Determine if the Description will be writted in compiled file
	 */
	@Parameter(defaultValue="true")
	private boolean addDescription;

	/** {@inheritDoc} */
	@Override
	public void proccesFile(Path file) throws SiaPluginDocumentationException {
		
		PrintWriter writer=null;
		
		try{
			if(isFileInExtensions(file.toString())){
				// Se obtienen las líneas del fichero
				List<String> lines = getLines(file); 
				
				if(lines!=null){
					
					// Se recorren los objetos de documentación en el fichero
					StringBuffer propertieFileContent = getText(lines);
					
					// Se crea el fichero
					String url = getConfiguration().getOutput()+"/"+file.getFileName();
					getLog().debug("Creando el fichero: "+url);
					
					// Se crea la estructura de carpetas
					File f = new File(url);
					if(!f.getParentFile().exists()){
						f.getParentFile().mkdirs();
					}
					
					// Se Borra el fichero si existe
					if(f.exists()){
						f.delete();
					}
					
					// Se escribe el fichero
					writer = new PrintWriter(f, getConfiguration().getCharset());
					writer.println(propertieFileContent);
					writer.close();
					getLog().info("Guardando el fichero en: "+url);
				}
			}
		}catch (IOException e) {
			throw new SiaPluginDocumentationException(
	    		"Excepción producida al procesar un path",
	    		SiaPluginDocumentationExceptionCode.ON_PROCESS_MOJO_FILE,
	    		e
	    	);
		}catch(Exception e){
			throw new SiaPluginDocumentationException(
	    		"Excepción producida al procesar un path",
	    		SiaPluginDocumentationExceptionCode.ON_PROCESS_MOJO_FILE_UNKNOWN,
	    		e
	    	);
		}finally{
			if (writer != null ) writer.close();
		}
	}

	/**
	 * Método que obtiene la cadena a escribir en el ficheor de propiedades resultado
	 * @param lines Líneas obtenidas del fichero origen
	 * @return Una instancia de {@link StringBuffer} con el resultado
	 * */
	private StringBuffer getText(List<String> lines) {
		StringBuffer propertieFileContent = new StringBuffer();
		List<DocumenterUnit> units = getDocumenterUnitsFromLines(lines);
		getLog().debug("Parseando las unidades: "+units);
		new StringBuffer();
		for (DocumenterUnit documenterUnit : units) {
			propertieFileContent.append(documenterUnit.getDocumenterToEnvironmentFile(
				writeEnvironment,
				getConfiguration().getAnnotationString()[writeAnnotationString],
				getConfiguration().getAsignationAnnotationString(),
				addDescription,
				addExample,
				addState
			));
		}
		
		return propertieFileContent;
	}
}
