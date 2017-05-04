package es.home.properties.plugin.goals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.exception.PluginDocumentationExceptionCode;
import es.home.properties.model.DocumenterLineType;
import es.home.properties.model.DocumenterType;
import es.home.properties.model.DocumenterUnit;
import es.home.properties.model.MavenDocumenterPropertiesConfiguration;
import es.home.properties.model.Variable;
import es.home.properties.plugin.factories.DocumenterFactory;
import es.home.properties.plugin.interfaces.documenter.MavenPropertiesDocumenter;
import es.home.properties.plugin.utils.ListFiles;

public abstract class DocuPropertiesAbstractMojo extends AbstractMojo{
	
	/** Objeto de configuración */
	MavenDocumenterPropertiesConfiguration configuration;

	// PROPIEDADES PRIVADAS
	MavenPropertiesDocumenter documenter;

	//	ELEMENTOS DEL PLUGIN
	/** Proyecto maven */
	@Component
	private MavenProject project;

	/**
	 * Entornos de desarrollo a documentar. Cada uno de los entornos especificados se comportarán como un elemento más de las propiedades
	 * <pre>
	 * 		&lt;environments&gt;
	 * 			&lt;environment&gt;ALGO&lt;/environments&gt;
	 * 			&lt;environment&gt;OTRO&lt;/environments&gt;
	 *			...
	 * 		&lt;/environments&gt;
	 * </pre> 
	 * */
	@Parameter
	private String[] environments;
	
	/**
	 * Entornos de desarrollo a documentar. Cada uno de los entornos especificados se comportarán como un elemento más de las propiedades
	 * <pre>
	 * 		&lt;variables&gt;
	 * 			&lt;variable&gt;
	 * 				&lt;key&gt;THE_KEY&lt;/key&gt;
	 * 				&lt;value&gt;VALUE&lt;/value&gt;
	 * 			&lt;/variable&gt;
	 *			...
	 * 		&lt;/variables&gt;
	 * </pre> 
	 * */
	@Parameter
	private Variable[] variables;
	
	
	/** Atributo que indica el nombre del fichero donde se guardará la documentación de las propiedades.
	 * Los posibles valores son:
	 * <pre>
	 * 	- EXCEL -> 	Genera un fichero excel
	 * 	- PLAIN -> 	Fichero de texto plano
	 * 	- HTML ->	Fichero con un atabla HTML
	 * </pre>
	 * */
	@Parameter(defaultValue = "EXCEL")
	private DocumenterType type;

	/** Extensiones */
	@Parameter
	private String[] extensions;
	
	/** 
	 * Permite identificar que líneas son anotaciones 
	 * */
	@Parameter(defaultValue = "#")
	private String[] annotationString;
	
	/** permite idnetificar el signo de asignación de la propiedad */
	@Parameter(defaultValue = "=")
	private String asignationAnnotationString;

	/** Atributo que indica el inicio de una propiedad a documentar */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"SimpleComment")
	private String attrsimplecomment;
	
	/** Atributo que indica el inicio de una propiedad a documentar */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"Documented")
	private String attrinit;

	/** Atributo que indica la descripción de una propiedad */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"Description")
	private String attrdescription;

	/** Atributo que indica el estado de una propiedad */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"State")
	private String attrstate;
	
	/** Atributo que indica el estado de obligatoriedad de una propiedad */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"Mandatory")
	private String attrmandatory;
	
	/** Atributo que indica el patrón soportado por una propiedad */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"Pattern")
	private String attrpattern;
	
	/** Atributo que indica lso valores soportados por una propiedad */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"PossibleValues")
	private String possibleValues;

	/** Atributo que indica un ejemplo de una propiedad */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"Example")
	private String attrexample;

	/** Atributo que indica los valores posibles de una propiedad */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"Values")
	private String attrvalues;
	
	/** Atributo que indica que un elemento es visible sólo si tiene valor */
	@Parameter(defaultValue = MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+"VisibleWithValue")
	private String attrvisiblewithvalue;

	/** Atributo que indica el directorio donde se guardará el recurso final */
	@Parameter(defaultValue="${project.build.directory}/docu-properties")
	private String output;
	
	/** 
	 * <pre>
	 * 	Atributo que determina el charset por defecto para la escritura de los ficheros de propiedades. La opción 
	 * 	por defecto es UTF-8. Las opciones más comunes utilizadas son:
	 * 		US-ASCII	Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
	 *		ISO-8859-1  ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
	 *		UTF-8		Eight-bit UCS Transformation Format
	 *		UTF-16BE	Sixteen-bit UCS Transformation Format, big-endian byte order
	 *		UTF-16LE	Sixteen-bit UCS Transformation Format, little-endian byte order
	 *		UTF-16		Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
	 * </pre>
	 * */
	@Parameter(defaultValue="UTF-8", alias="write.charset")
	private String writeCharset;
	
	/** 
	 * <pre>
	 * Atributo que determina el conjunto de charset por defecto para la lectura de los ficheros de propiedades. La opción 
	 * por defecto es {"UTF-8","ISO-8859-1","UTF-16"}. Las opciones más comunes utilizadas son:
	 * 		US-ASCII	Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
	 *		ISO-8859-1  ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
	 *		UTF-8		Eight-bit UCS Transformation Format
	 *		UTF-16BE	Sixteen-bit UCS Transformation Format, big-endian byte order
	 *		UTF-16LE	Sixteen-bit UCS Transformation Format, little-endian byte order
	 *		UTF-16		Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
	 * </pre>
	 * */
	@Parameter(alias="read.charsets")
	private String[] readCharsets = new String[]{"UTF-8","ISO-8859-1","UTF-16"};
	
	/** 
	 * Permite ejecutar las validaciones de las propiedades
	 * */
	@Parameter(defaultValue="false")
	private boolean validate;
	
	/**
	 * Obtiene el objeto de configuración
	 * @return Devuelve una instancia de {@link MavenDocumenterPropertiesConfiguration} con las
	 * propiedades de configuración
	 * */
	protected MavenDocumenterPropertiesConfiguration getConfiguration(){
		if(this.configuration==null){
			getLog().info("Obteniendo la configuración del plugin");
			this.configuration = new MavenDocumenterPropertiesConfiguration();
			this.configuration.setAnnotationString(this.annotationString);
			this.configuration.setAttrSimpleComment(this.attrsimplecomment);
			this.configuration.setAsignationAnnotationString(this.asignationAnnotationString);
			this.configuration.setAttrdescription(this.attrdescription);
			this.configuration.setAttrexample(this.attrexample);
			this.configuration.setAttrinit(this.attrinit);
			this.configuration.setAttrstate(this.attrstate);
			this.configuration.setAttrvalues(this.attrvalues);
			this.configuration.setAttrpattern(this.attrpattern);
			this.configuration.setAttrvisiblewithvalue(this.attrvisiblewithvalue);
			this.configuration.setEnvironments(this.environments);
			this.configuration.setExtensions(this.extensions);
			this.configuration.setOutput(this.output);
			this.configuration.setProject(this.project);
			this.configuration.setType(this.type);
			this.configuration.setLogger(getLog());
			this.configuration.setWriteCharset(this.writeCharset);
			this.configuration.setReadCharsets(this.readCharsets);
			this.configuration.setVariables(this.variables);
			this.configuration.setValidate(this.validate);
		}
		return this.configuration;
	}

	/**
	 * Permite procesar los recoursos de un path
	 * @param resourcePath Path a procesar
	 * @throws PluginDocumentationException 
	 * */
	protected void proccessPath(String resourcePath) throws PluginDocumentationException {
		getLog().info("Codificando los recursos alojados en: "+resourcePath);
		try {
	      	Path path = Paths.get(resourcePath);
	      	if(new File(resourcePath).exists()){
	      		ListFiles listFiles = new ListFiles(getLog(),this);
		      	Files.walkFileTree(path, listFiles);
	      	}
	    } catch (IOException ex) {
	    	throw new PluginDocumentationException(
	    		"Excepción producida al procesar un path: "+resourcePath,
	    		PluginDocumentationExceptionCode.ON_PROCESS_MOJO_PATH,
	    		ex
	    	);
	  	} catch(Exception e){
	  		throw new PluginDocumentationException(
	    		"Excepción desconocida producida al procesar un path",
	    		PluginDocumentationExceptionCode.ON_PROCESS_MOJO_PATH_UNKNOWN,
	    		e
	    	);
	  	}
	}

	/** 
	 * Permite procesar un fichero de texto y documentarlo
	 * @param file Fichero a procesar 
	 * @throws PluginDocumentationException 
	 */
	public abstract void proccesFile(Path file) throws PluginDocumentationException;

	/**
	 * Obtiene un documentador singleton a partir de la factoría fde documentadores
	 * @return Devuelva la instancia de {@link MavenPropertiesDocumenter} que se crea pro configuración
	 * */
	public MavenPropertiesDocumenter getDocumenter() {
		if(this.documenter==null){
			this.documenter = DocumenterFactory.createDocumenter(getConfiguration(), getLog());
		}
		return this.documenter;
	}
	
	// METODOS PRIVADOS
	/**
	 * Determina si la extensión del nombre del fichero se encunetra entre las extensiones permitidas por
	 * configuración
	 * @param fileName Nombre del fichero a comprobar.
	 * @return Devuelve true si la extensión está permitida, en caso contrario, devuelve false.
	 * */
	protected boolean isFileInExtensions(String fileName){
		for (String extension: getConfiguration().getExtensions()) {
			if(fileName.endsWith("."+extension)){
				getLog().debug(fileName+" contiene la extensión: "+extension);
				return true;
			}
		}
		getLog().debug(fileName+" no contiene ninguna de las extensiones deseadas");
		return false;
	}

	/**
	 * Permite obtener las unidades de documentación a partir de la líneas de un fichero
	 * de propiedades
	 * @param lines Líneas del fichero de propiedades a analizar
	 * @return Devuelve una lista de {@link DocumenterUnit} con la infomrmación de documentación
	 * de las undiades
	 * */
	protected List<DocumenterUnit> getDocumenterUnitsFromLines(List<String> lines) {
		getLog().debug("Obteniendo las unidades de documentación");
		List<DocumenterUnit> units = new ArrayList<DocumenterUnit>();
		Boolean startDocumenterUnit = false;
		DocumenterUnit auxDocumenterUnit = null;
		DocumenterLineType lastDocumenterLineType = null;
		for (String line : lines) {
			DocumenterLineType type = getConfiguration().getLineType(line);
			getLog().debug("Obteniendo la información de la línea: \""+line+"\" y tipo: "+type);
			switch(type){
				case INIT:
					auxDocumenterUnit = setInitValue(auxDocumenterUnit,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.INIT;
					startDocumenterUnit = true;
					break;
				case FINISH:
					auxDocumenterUnit = setFinishValue(startDocumenterUnit,line,auxDocumenterUnit,units,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.FINISH;
					startDocumenterUnit = false;
					break;
				case DESCRIPTION:
					auxDocumenterUnit = setDescriptionValue(startDocumenterUnit,line,auxDocumenterUnit,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.DESCRIPTION;
					break;
				case EXAMPLE:
					setExampleValue(startDocumenterUnit,line,auxDocumenterUnit,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.EXAMPLE;
					break;
				case PATTERN:
					setPatternValue(startDocumenterUnit,line,auxDocumenterUnit);
					lastDocumenterLineType = DocumenterLineType.PATTERN;
					break;
				case STATE:
					setStateValue(startDocumenterUnit,line,auxDocumenterUnit,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.STATE;
					break;
				case VALUES:
					setValuesValue(startDocumenterUnit,line,auxDocumenterUnit,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.VALUES;
					break;
				case ENVIRONMENT_VALUE:
					setEnvironmentValue(startDocumenterUnit,line,auxDocumenterUnit,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.ENVIRONMENT_VALUE;
					break;
				case SPECIAL_DEFAULT_ENVIRONMENT:
					setSpecialDefaultEnvironmentValue(startDocumenterUnit,line,auxDocumenterUnit,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.SPECIAL_DEFAULT_ENVIRONMENT;
					break;
				case ANYTHING:
					setAnythingValue(startDocumenterUnit,line,auxDocumenterUnit,lastDocumenterLineType);
					break;
				case VISIBLE_WITH_VALUE:
					setVisibleWithValue(auxDocumenterUnit);
					lastDocumenterLineType = DocumenterLineType.VISIBLE_WITH_VALUE;
					break;
				case INIT_SIMPLE_COMMENT:
					auxDocumenterUnit = setInitValue(auxDocumenterUnit,lastDocumenterLineType);
					lastDocumenterLineType = DocumenterLineType.INIT_SIMPLE_COMMENT;
					startDocumenterUnit = true;
					break;
				default:
					// DO NOTHING
					break;
			}
		}
		return units;
	}

	private void setSpecialDefaultEnvironmentValue(Boolean startAnnotation,
			String line, DocumenterUnit auxDocumenterUnit,
			DocumenterLineType lastDocumenterLineType) {
		if(startAnnotation){
			String key = getDocumenter().getConfiguration().getSpecialDefaultEnvironmentFromLine(line);
			String value = getDocumenter().getConfiguration().getValueFromLine(line, DocumenterLineType.SPECIAL_DEFAULT_ENVIRONMENT);
			getLog().debug("Obteniendo una variable de entorno especial: key -> "+((key==null)?"null":key)+" - value-> "+((value==null)?"null":value));
			if(key!=null && value!=null){
				auxDocumenterUnit.addSpecialDefaultEnvironment(
					key,
					value,
					getConfiguration().getVariables()
				);
			}
		}
	}

	/**
	 * Permite establecer que la porpiedad será solamente visible si contiene valor
	 * @param auxDocumenterUnit
	 */
	private void setVisibleWithValue(DocumenterUnit auxDocumenterUnit) {
		auxDocumenterUnit.setVisibleWithValue(true);		
	}

	/**
	 * Permite añadir cualquier valor. Esto implica realizar una concatenación de aquellos elementos que sean 
	 * propios de la aplicación, no de los creados por el usuario
	 * */
	private void setAnythingValue(Boolean startAnnotation, String line,
			DocumenterUnit auxDocumenterUnit,
			DocumenterLineType lastDocumenterLineType) {
		// Sólo se pueden añadir líneas a los valores propios de la aplicación, no a los extendidos
		if(lastDocumenterLineType!=DocumenterLineType.FINISH 
		   && lastDocumenterLineType!=DocumenterLineType.INIT
		   && lastDocumenterLineType!=DocumenterLineType.ENVIRONMENT_VALUE
		   && startAnnotation){
			switch(lastDocumenterLineType){
				case DESCRIPTION:
				case INIT_SIMPLE_COMMENT:
					String description = auxDocumenterUnit.getDescription();
					if(description == null){
						description="";
					}
					description+="\n"+getConfiguration().getLineWithoutAnnotation(line);
					auxDocumenterUnit.setDescription(description, getConfiguration().getVariables());
					break;
				case EXAMPLE:
					String example = auxDocumenterUnit.getExample();
					example+="\n"+getConfiguration().getLineWithoutAnnotation(line);
					auxDocumenterUnit.setExample(example, getConfiguration().getVariables());
					break;
				case STATE:
					String sate = auxDocumenterUnit.getState();
					sate+="\n"+getConfiguration().getLineWithoutAnnotation(line);
					auxDocumenterUnit.setState(sate,getConfiguration().getVariables());
					break;
				case VALUES:
					String values = auxDocumenterUnit.getValues();
					values+="\n"+getConfiguration().getLineWithoutAnnotation(line);
					auxDocumenterUnit.setValues(values, getConfiguration().getVariables());
					break;
				default:
					// DO NOTHING
					break;
			}
		}
	}

	/**
	 * Obtiene el valor de las variables definidas por el usuario
	 * */
	private void setEnvironmentValue(Boolean startAnnotation,
			String line, DocumenterUnit auxDocumenterUnit,
			DocumenterLineType lastDocumenterLineType) {
		if(startAnnotation){
			String key = getDocumenter().getConfiguration().getEnvironmentFromLine(line);
			String value = getDocumenter().getConfiguration().getValueFromLine(line, DocumenterLineType.ENVIRONMENT_VALUE);
			getLog().debug("Obteniendo una variable de entorno: key -> "+((key==null)?"null":key)+" - value-> "+((value==null)?"null":value));
			if(key!=null && value!=null){
				auxDocumenterUnit.addEnvironment(
					getConfiguration().getEnvironmentFromLine(line),
					getConfiguration().getValueFromLine(line, DocumenterLineType.ENVIRONMENT_VALUE),
					getConfiguration().getVariables()
				);
			}
		}
	}
	
	/**
	 * Obtiene un valor inicial para los posibles valores
	 * */
	private void setValuesValue(Boolean startAnnotation, String line,
			DocumenterUnit auxDocumenterUnit,
			DocumenterLineType lastDocumenterLineType) {
		if(startAnnotation){
			getLog().debug("Obteniendo los valores de la unidad de documentación");
			auxDocumenterUnit.setValues(
				getConfiguration().getValueFromLine(line,DocumenterLineType.VALUES),
				getConfiguration().getVariables()
			);
		}
	}
	
	/**
	 * Obtiene un valor inicial para el estado
	 * */
	private void setStateValue(Boolean startAnnotation, String line,
			DocumenterUnit auxDocumenterUnit,
			DocumenterLineType lastDocumenterLineType) {
		if(startAnnotation){
			getLog().debug("Obteniendo el estado de la unida de documentación");
			auxDocumenterUnit.setState(
				getConfiguration().getValueFromLine(line,DocumenterLineType.STATE),
				getConfiguration().getVariables()
			);
		}
	}
	
	/**
	 * Establece un valor inicial para los ejemplos
	 * */
	private void setExampleValue(Boolean startAnnotation, String line,
			DocumenterUnit auxDocumenterUnit,
			DocumenterLineType lastDocumenterLineType) {
		if(startAnnotation){
			getLog().debug("Obteniendo la llinea ejemplo de la unidad de documentación");
			auxDocumenterUnit.setExample(
				getConfiguration().getValueFromLine(line,DocumenterLineType.EXAMPLE),
				getConfiguration().getVariables()
			);
		}
	}
	
	/**
	 * Establece la propiedad patrón para la unidad de documentación
	 * */
	private void setPatternValue(Boolean startAnnotation, String line,
			DocumenterUnit auxDocumenterUnit) {
		if(startAnnotation){
			getLog().debug("Obteniendo el patrón de la unidad de documentación");
			auxDocumenterUnit.setPattern(
				getConfiguration().getValueFromLine(line,DocumenterLineType.PATTERN),
				getConfiguration().getVariables()
			);
		}
	}

	/**
	 * Establece un valor inicial para la descripción
	 * @return 
	 * */
	private DocumenterUnit setDescriptionValue(Boolean startAnnotation, String line,
			DocumenterUnit auxDocumenterUnit,
			DocumenterLineType lastDocumenterLineType) {
		if(startAnnotation){
			getLog().debug("Obteniendo la descripción de la unidad de documentación");
			auxDocumenterUnit.setDescription(
				getConfiguration().getValueFromLine(line,DocumenterLineType.DESCRIPTION),
				getConfiguration().getVariables()
			);
		}
		return auxDocumenterUnit;
	}

	/**
	 * Establece un valor final para una Unidad de documentación
	 * @return 
	 * */
	private DocumenterUnit setFinishValue(Boolean startAnnotation, String line,
			DocumenterUnit auxDocumenterUnit, List<DocumenterUnit> units,
			DocumenterLineType lastDocumenterLineType) {
		getLog().debug("Finalizando unidad de documentación iniciada. startAnnotation: "+startAnnotation);
		if(startAnnotation){
			String lineThis = getConfiguration().getValueFromLine(line,DocumenterLineType.FINISH);
			int indexOf = lineThis.indexOf(asignationAnnotationString);
			if(indexOf>=0){
				String propertyName = lineThis.substring(0,indexOf);
				String defaultValue = lineThis.substring(indexOf+1,lineThis.length());
				auxDocumenterUnit.setPropertyName(propertyName, getConfiguration().getVariables());
				auxDocumenterUnit.setDefaultValue(defaultValue, getConfiguration().getVariables());
			}
			units.add(auxDocumenterUnit);
		}
		return auxDocumenterUnit;
	}
	
	/**
	 * Establece un valor inicial para una unidad de documentación
	 * @return 
	 * */
	private DocumenterUnit setInitValue(DocumenterUnit auxDocumenterUnit, DocumenterLineType lastDocumenterLineType) {
		getLog().debug("Iniciando una unidad de documentación");
		auxDocumenterUnit = new DocumenterUnit(getLog());
		return auxDocumenterUnit;
	}
	
	/**
	 * Método que permite obtener una lista de las líneas de un fichero de configuración
	 * @param file Fichero de configración
	 * @return Devuelve una lista con las cadenas de un fichero de configuración. Cada uan de las 
	 * líneas del fichero vendrá determinada por si siguiente retorno de carro.
	 * */
	protected List<String> getLines(Path file) throws IOException {
		List<String> lines = getLinesRecursive(file, 0); 
		if(lines == null){
			getLog().warn("El fichero de propiedades: "+file.toString()+" no ha podido ser leído por"
					+ " ninguna de las codificaciones facilitadas: "+Arrays.toString(getConfiguration().getReadCharsets()));
			return null;
		}else{
			return lines;
		}
	}
	
	/**
	 * Método que permite obtener la líneas de un fichero. Si el primer charset intentado no es correcto,
	 * se ejecuta de forma recurisva el método hasta haber agotasdo las posibilidades de los charset
	 * pasados por parámetro. En caso de que ninguno de los charset sea correcto, se devuelve null.
	 * @throws IOException 
	 * */
	private List<String> getLinesRecursive(Path file, int position) throws IOException{
		if(position>=getConfiguration().getReadCharsets().length){
			return null;
		}else{
			try{
				Charset charset = Charset.forName(getConfiguration().getReadCharsets()[position]);
				getLog().debug("Leyendo el fichero: "+file.toString()+" en base a la codificación: "+charset);
				return Files.readAllLines(file, charset);
			}catch(MalformedInputException mie){
				int aux = position+1;
				return getLinesRecursive(file, aux);
			}
		}
	}
}
