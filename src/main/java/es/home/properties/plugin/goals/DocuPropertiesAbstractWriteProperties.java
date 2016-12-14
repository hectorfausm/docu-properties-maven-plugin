package es.home.properties.plugin.goals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.exception.PluginDocumentationExceptionCode;
import es.home.properties.model.DocumenterUnit;
import es.home.properties.model.Variable;

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
	public void proccesFile(Path file) throws PluginDocumentationException {
		
		PrintWriter writer=null;
		
		try{
			if(isFileInExtensions(file.toString())){
				// Se obtienen las líneas del fichero
				List<String> lines = getLines(file);
				
				if(lines!=null){
					
					// Se obtienen las unidades de documentación del fichero
					List<DocumenterUnit> units = getDocumenterUnitsFromLines(lines);
					
					// Se obtiene el texto destino de los objetos de documentación en el fichero
					StringBuffer propertieFileContent = getText(units);
										
					// Si se deben validar las propiedades. TODO
					//if(getConfiguration().isValidate()){}
					
					// Se filtra el fichero con las variables del plugin
					String result = filterStringWithVariables(propertieFileContent,getConfiguration().getVariables());
					
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
					writer.println(result);
					writer.close();
					getLog().info("Guardando el fichero en: "+url);
				}
			}
		}catch (IOException e) {
			throw new PluginDocumentationException(
	    		"Excepción producida al procesar un path",
	    		PluginDocumentationExceptionCode.ON_PROCESS_MOJO_FILE,
	    		e
	    	);
		}catch(Exception e){
			throw new PluginDocumentationException(
	    		"Excepción producida al procesar un path",
	    		PluginDocumentationExceptionCode.ON_PROCESS_MOJO_FILE_UNKNOWN,
	    		e
	    	);
		}finally{
			if (writer != null ) writer.close();
		}
	}

	/**
	 * Filtra la información del fichero con las variabels del plugin, si el plugin no tene variables, 
	 * devuelve la información del fichero en formato {@link String}
	 * @param propertieFileContent
	 * @param variables
	 * @return DEvuelve la ifnromación del fichero filtrada con las variables de cofniguración en formato {@link String}
	 */
	private String filterStringWithVariables(
			StringBuffer propertieFileContent, Variable[] variables) {
		
		// Si hay variables en configuración se ejecuta la sustitución
		if(variables!=null){
			
			// Si hay propiedades en el texto
			String result = propertieFileContent.toString();
			for (Variable variable : variables) {
				getLog().debug("Filtrando resultado para la variable: "+variable.getKey()+" con valor: "+variable.getValue());
				result = result.replace("${"+variable.getKey()+"}", variable.getValue());
			}
			return result;
			
		// SI no hay variables se devuelve toString
		}else{
			return propertieFileContent.toString();
		}
	}

	/**
	 * Método que obtiene la cadena a escribir en el ficheor de propiedades resultado
	 * @param units Líneas obtenidas del fichero origen
	 * @return Una instancia de {@link StringBuffer} con el resultado
	 * */
	private StringBuffer getText(List<DocumenterUnit> units) {
		StringBuffer propertieFileContent = new StringBuffer();
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
