package es.home.properties.plugin.goals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugins.annotations.Parameter;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.exception.PluginDocumentationExceptionCode;
import es.home.properties.model.DocumenterUnit;
import es.home.properties.model.FileMap;

public abstract class DocuPropertiesAbstractGenerateProperties extends DocuPropertiesAbstractMojo{
	
	/** Patrón necesario para extraer el nombre de una propiedad */
	public static final String PATTERN_PROPERTY = ".*public static final .*=.*\"(.*)\".*;";
	
	/**
	 * Conjunto de ficheros a mantener.
	 * <pre>
	 * 		&lt;fileMaps&gt;
	 * 			&lt;fileMap&gt;
	 * 				&lt;docuPropertiesFile&gt;docuProperties.properties&lt;/docuPropertiesFile&gt;
	 * 				&lt;javaFile&gt;MyClas.java&lt;/javaFile&gt;
	 * 			&lt;/fileMap&gt;
	 *			...
	 * 		&lt;/fileMaps&gt;
	 * </pre> 
	 * */
	@Parameter
	private FileMap[] fileMaps;
	
	@Override
	public void proccesFile(Path file) throws PluginDocumentationException {
		PrintWriter writer=null;
		
		try{
			if(isFileInExtensions(file.toString())){
				
				// [0] Se obtienen las unidades de documentación del fichero doduproperties.
				getLog().info("Iniciando la lectura del fichero: "+file.getFileName());
				List<String> lines = getLines(file);
				List<DocumenterUnit> docuPropertiesUnits;
				if(lines==null){
					docuPropertiesUnits = new ArrayList<>();
				}else{
					docuPropertiesUnits = getDocumenterUnitsFromLines(lines);
				}
				
				// [1] Se obtienen las unidades de documentación del fichero de propiedades java asociado a ese fichero docu-properties
				String javaFile = getJavFileFromDocuPropertiesFile(file.getFileName().toString());
				if(javaFile!=null){
					List<String> javaLines = getLines(Paths.get(javaFile));
					Pattern p = Pattern.compile(PATTERN_PROPERTY);
					for (String line : javaLines) {
						Matcher m = p.matcher(line);
						if(m.find()){
							// Clave
							String keyProperty = m.group();
							int index = -1;
							if((index = getIndexByName(docuPropertiesUnits,keyProperty))>0){
								DocumenterUnit units = new DocumenterUnit(getLog());
								units.setPropertyName(keyProperty, getConfiguration().getVariables());
								docuPropertiesUnits.add(index+1,units);
							}
						}
					}
				}
				
				// [3] Se escribe el nuevo fichero docuProperties en la dirección de salida
				String result = getGenerationText(docuPropertiesUnits);
				
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
			if (writer != null ){
				writer.close();
			}
		}
	}
	
	private String getGenerationText(List<DocumenterUnit> docuPropertiesUnits) {
		StringBuilder builder = new StringBuilder();
		for (DocumenterUnit documenterUnit : docuPropertiesUnits) {
			builder.append(documenterUnit.getDocumenterUnitToDocumentPorpertyFile(getConfiguration()));
			builder.append("\n\n");
		}
		return builder.toString();
	}

	private int getIndexByName(List<DocumenterUnit> docuPropertiesUnits, String keyProperty) {
		int size = docuPropertiesUnits.size();
		for (int i = 0; i < size; i++) {
			if(docuPropertiesUnits.get(i).getPropertyName()!=null && docuPropertiesUnits.get(i).getPropertyName().equals(keyProperty)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Obtiene la dirección del fichero hava asociado a un fichero docuPorperties
	 * @return
	 */
	public String getJavFileFromDocuPropertiesFile(String docuPropertiesFile){
		for (FileMap fileMap : fileMaps) {
			if(fileMap.getDocuPropertiesFile().equals(docuPropertiesFile)){
				return fileMap.getJavaFile();
			}
		}
		return null;
		
	}
}
