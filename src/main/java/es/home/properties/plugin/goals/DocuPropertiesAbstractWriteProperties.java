package es.home.properties.plugin.goals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugins.annotations.Parameter;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.exception.PluginDocumentationExceptionCode;
import es.home.properties.model.DocumenterUnit;
import es.home.properties.model.ErrorType;
import es.home.properties.model.ValidationError;

import static es.home.properties.model.MavenDocumenterPropertiesConfiguration.CYPHER_PREFIX;

public abstract class DocuPropertiesAbstractWriteProperties extends DocuPropertiesAbstractMojo{
	
	/**
	 * The envirnoment value to write in properties
	 * */
	@Parameter(required=true, alias="write.environment")
	private String writeEnvironment;
	
	/**
	 * Determine what is the number of annotation that be write in the result property
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
				
				getLog().info("Iniciando la lectura del fichero: "+file.getFileName());
				
				// Se obtienen las líneas del fichero
				List<String> lines = getLines(file);
				
				// Si no hay líneas no se ejecuta nada
				if(lines==null){
					return;
				}
					
				// Se obtienen las unidades de documentación del fichero filtradas por variables
				List<DocumenterUnit> units = getDocumenterUnitsFromLines(lines);
				
				// se validan las propiedades.
				List<ValidationError> errors = validate(units);
				if(!errors.isEmpty()){
					throwValidationErrors(errors);
				}
				
				// Se obtiene el texto a imprimir en el fichero a partir de las unidades de documentación
				String result = getText(units);
				
				// Se crea el fichero
				String relativePath = getConfiguration().getOutput();
				String url;
				if(getConfiguration().getInput()!=null && getConfiguration().isMaintainFolderStructure()){
					String absolutePath = file.toFile().getCanonicalPath().replace("\\", "/");
					relativePath = (relativePath+"/"+absolutePath.replace(getConfiguration().getInput().replace("\\", "/"), "")).replace("//", "/");
					url = relativePath;
				}else{
					url = relativePath+"/"+file.getFileName();
				}
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
		}catch (PluginDocumentationException e) {
			throw e;
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

	/**
	 * Arroja una excepción de validación
	 * @param errors
	 * @throws PluginDocumentationException
	 */
	private void throwValidationErrors(List<ValidationError> errors) throws PluginDocumentationException {
		for (ValidationError validationError : errors) {
			getLog().error(validationError.toString());
		}
	}

	/**
	 * Valida las propiedades
	 * @param units 
	 * */
	private List<ValidationError> validate(List<DocumenterUnit> units) {
		List<ValidationError> result = new ArrayList<>();
		if(getConfiguration().isValidate()){
			for (DocumenterUnit documenterUnit : units) {
				
				// Valor por entorno
				String realValue = "";
				if(documenterUnit.getEnvironments()==null && documenterUnit.getDefaultValue()!=null){
					realValue = documenterUnit.getDefaultValue();
				}else if(documenterUnit.getEnvironments()!=null && documenterUnit.getEnvironments().containsKey(this.writeEnvironment) && documenterUnit.getEnvironments().get(this.writeEnvironment)!=null){
					realValue = documenterUnit.getEnvironments().get(this.writeEnvironment).replaceAll("^\\s+", "");
				}
				
				// Validaciones de patrón
				result.addAll(validatePattern(realValue,documenterUnit));
				
				// TODO mandatory
				// TODO possibleValues
			}
		}
		return result;
	}

	/**
	 * Valida el patrón pasadop en la configuración del la unidad de documentación
	 * @param realValue
	 * @param documenterUnit
	 * @return 
	 */
	private List<ValidationError> validatePattern(String realValue, DocumenterUnit documenterUnit) {
		List<ValidationError> result = new ArrayList<>();
		if(documenterUnit.getPattern()!=null){
			Pattern p = Pattern.compile(documenterUnit.getPattern());
			Matcher m = p.matcher(realValue);
			if(!m.matches()){
				ValidationError error = new ValidationError();
				error.setErrorType(ErrorType.PATTERN);
				error.setPropertyKey(documenterUnit.getPropertyName());
				error.setValue(realValue);
				error.setAnexo(documenterUnit.getPattern());
				result.add(error);
			}
		}
		return result;
	}

	/**
	 * Obtiene la cadena a escribir en el fichero de propiedades resultado
	 * @param units Líneas obtenidas del fichero origen
	 * @return Una instancia de {@link StringBuffer} con el resultado
	 * */
	private String getText(List<DocumenterUnit> units) {
		StringBuilder propertiesFileContent = new StringBuilder();
		getLog().debug("Parseando las unidades: "+units);
		new StringBuffer();

		// Para la impresión ordenada
		Map<String, List<String>> orderedTenants = new HashMap<>();
		List<String> defaultValues = new ArrayList<>();
		for (DocumenterUnit documenterUnit : units) {

			// Si la unidad es multitenant, ordenada por tenant y es una propiedad
			if(documenterUnit.isMultitenant() && documenterUnit.isOrderByTenant() && documenterUnit.getPropertyName() != null){

				// Se obtienen los valores de la propiedad multitenant (comentario + propiedad + valor)
				Map<String, String> valuesByTenant = documenterUnit.getDocumenterToEnvironmentFileOrderedByTenants(
					writeEnvironment,
					getConfiguration().getAnnotationString()[writeAnnotationString],
					getConfiguration().getAsignationAnnotationString(),
					addDescription,
					addExample,
					addState
				);

				// Se añaden a la lista final
				for (Map.Entry<String, String> entry: valuesByTenant.entrySet()){
					String tenant = entry.getKey();
					String tenantNotCypher = tenant.replace(CYPHER_PREFIX, "");
					String value = entry.getValue();

					// Se añaden los elementos a la lista por defecto
					if(getConfiguration().getDefaultTenant().equals(tenantNotCypher)){
						defaultValues.add(value);
					}

					// Si no es por defecto, se añade de forma natural
					else{

						// Se genera la lista para cada tenant
						if(!orderedTenants.containsKey(tenantNotCypher)){
							orderedTenants.put(tenantNotCypher, new ArrayList<String>());
						}

						// Se añade el elemento de tenant
						orderedTenants.get(tenantNotCypher).add(value);
					}
				}
			}

			// Si no, se ordena por tenant, el append se hace por cada propiedad.
			else{
				propertiesFileContent.append(documenterUnit.getDocumenterToEnvironmentFile(
					writeEnvironment,
					getConfiguration().getAnnotationString()[writeAnnotationString],
					getConfiguration().getAsignationAnnotationString(),
					addDescription,
					addExample,
					addState
				));

				// Se añade separación entre cada propiedad solo si se pudo imprimir
				if(documenterUnit.isWasVisible()) {
					propertiesFileContent.append("\n\n");
				}
			}
		}

		// Si existen tenants ordenables
		if(!orderedTenants.isEmpty()){
			List<String> sortedKeys=new ArrayList<>(orderedTenants.keySet());
			Collections.sort(sortedKeys);
			for (String sortedKey : sortedKeys){
				propertiesFileContent.append(tenantHeader(sortedKey, true));
				propertiesFileContent.append("\n\n");
				List<String> fullProperties = orderedTenants.get(sortedKey);
				for (String fullProperty : fullProperties){
					propertiesFileContent.append(fullProperty);
					propertiesFileContent.append("\n\n");
				}
				propertiesFileContent.append(tenantHeader(sortedKey, false));
				propertiesFileContent.append("\n\n");
			}
		}

		// Bloque multitenant de propiedades ordenadas por tenant, La calve por defecto va al final.
		if(!defaultValues.isEmpty()){
			propertiesFileContent.append(tenantHeader(getConfiguration().getDefaultTenant(), true));
			propertiesFileContent.append("\n\n");
			for (String defaultValue : defaultValues){
				propertiesFileContent.append(defaultValue);
				propertiesFileContent.append("\n\n");
			}
			propertiesFileContent.append(tenantHeader(getConfiguration().getDefaultTenant(), false));
			propertiesFileContent.append("\n\n");
		}

		// Resultado final
		propertiesFileContent.delete(propertiesFileContent.length()-2, propertiesFileContent.length());
		return propertiesFileContent.toString();
	}

	/**
	 * Crea el bloque de apertura o cierre de un tenant
	 *
	 * @param tenant Tenant sobre el que crear las cabeceras (o piés)
	 * @param isOpen Determina si crear la apertura o el cierre
	 * @return Devuelve el bloque deseado
	 */
	private String tenantHeader(String tenant, boolean isOpen) {
		int longitudTexto = tenant.length();

		// Definir longitud total de línea 1 (20 o 21)
		int longitudLinea = (longitudTexto % 2 == 0) ? 20 : 21;

		// Construir primera línea: solo '#'
		String linea1 = repeat("#", longitudLinea);

		// Calcular longitud del prefijo y sufijo de '#'
		int totalHashes = longitudLinea - (longitudTexto + 4);

		int hashesPrefijo = totalHashes / 2;
		int hashesSufijo = totalHashes - hashesPrefijo;

		String prefijo = repeat("#", hashesPrefijo);
		String sufijo = repeat("#", hashesSufijo);

		// Construir segunda línea
		String linea2 = prefijo + "--" + tenant + "--" + sufijo;

		// Apertura o cierre
		if (isOpen) {
			return linea1 + "\n" + linea2;
		} else {
			return linea2 + "\n" + linea1;
		}
	}

	/**
	 *
	 * @param headerCharacter carácter de la repetición
	 * @param times	Número de veces que se repite el carácter
	 * @return Devuelve el carácter n veces repetido en la cadena
	 */
	private String repeat(String headerCharacter, int times) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < times; i++) {
			sb.append(headerCharacter);
		}
		return sb.toString();
	}
}
