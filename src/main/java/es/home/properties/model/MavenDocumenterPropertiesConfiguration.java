package es.home.properties.model;

import org.apache.commons.lang.CharUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Objeto de configuración para la ejecución del plugin
 * */
public class MavenDocumenterPropertiesConfiguration {

	public static final String ANNOTATION_DEFINED_STRING = "@";
	
	//	ELEMENTOS DEL PLUGIN
	/** Proyecto maven */
	private MavenProject project;

	/** Entornos de desarrollo a documentar */
	private String[] environments;

	/** Extensiones */
	private String[] extensions;
	
	/** permite identificar que líneas son anotaciones */
	private String[] annotationString;
	
	/** Charsets requeridos para leer el fichero de propiedades */
	private String[] readCharsets;
	
	/** permite idnetificar el signo de asignación de la propiedad */
	private String asignationAnnotationString;

	/** Atributo que indica el inicio de una propiedad a documentar */
	private String attrinit;

	/** Atributo que indica la descripción de una propiedad */
	private String attrdescription;

	/** Atributo que indica el estado de una propiedad */
	private String attrstate;

	/** Atributo que indica un ejemplo de una propiedad */
	private String attrexample;
	
	/** Atributo que indica el estado de obligatoriedad de una propiedad */
	private String attrmandatory;
	
	/** Atributo que indica el patrón soportado por una propiedad */
	private String attrpattern;
	
	/** Atributo que indica el patrón soportado por una propiedad */
	private String possibleValues;
	
	/** Atributo que determina si una propiedad será visible solo si tiene valor */
	private String attrvisiblewithvalue;

	/** Atributo que indica los valores posibles de una propiedad */
	private String attrvalues;

	/** Atributo que indica el directorio donde se guardará el recurso final */
	private String output;
	
	/** Atributo que indica el nombre del fichero donde se guardará la documentación de las propiedades */
	private DocumenterType type;
	
	/** Atributo que indica un conjunto de rutas de acceso a ficheros de recursos */
	private String[] inputs;
	
	/** Atributo que permite consultar los ficheros de recursos por defecto de maven*/
	private boolean includeResourcesFolders;
	
	/** Atributo que determina si la salida de los ficheros es la establecida pro defecto */
	private boolean defaultOutput;
	
	/** Atributo que determina el tipo de codificación de los ficheros de propiedades */
	private String charset;
	
	/** Atributo que indica que un bloque de comentarios es un comentario simple */
	private String attrsimplecomment;
	
	/** Variables para transofrmar propiedades */
	private Variable[] variables;
	
	/** Logger */
	private Log logger;
	
	/**
	 * Permite identificar si una cadena es una anotación
	 * @return true en caso de ser una anotación, en caso contrario, false
	 * */
	public boolean isLineAnnotation(String line){
		for (String annotation : annotationString) {
			if(line.startsWith(annotation)){
				return true;
			}
		}
		return false;
	};
	
	// PROPIEDADES ESTATICAS PRIVADAS
	private String DEFAULT_PROPERTIES_EXTENSION = "properties";
	
	/**
	 * Obtiene el tipo de línea.
	 * @param line Línea de la que extraer el tipo de línea
	 * @param lastDocumenterLineType 
	 * @return Devuleve una instancia de {@link DocumenterLineType}
	 * */
	public DocumenterLineType getLineType(String line) {
		DocumenterLineType res = DocumenterLineType.ANYTHING;
		String auxLine = line.trim();
		// Si la línea no es un comentario y la línea es un retorno de carro o otro carácter imprimible, se trata de un final
		if(!isLineAnnotation(auxLine) && ((!auxLine.isEmpty() && CharUtils.isAsciiPrintable(line.charAt(0))) || line.isEmpty())){
			return DocumenterLineType.FINISH;
			
		// Si la línea es un comentario
		}else if(isLineAnnotation(line)){
			int firstAnnotationIn = line.indexOf(ANNOTATION_DEFINED_STRING);
			
			// Si la línea es una anotción
			if(firstAnnotationIn>0){
				String lineCopy = line.substring(firstAnnotationIn); 
				logger.debug("Obteniendo el tipo de la línea a partir de la subcadena: \""+lineCopy+"\"");
				if(lineCopy.startsWith(attrinit)){
					res = DocumenterLineType.INIT;
				}else if(lineCopy.startsWith(attrdescription)){
					res = DocumenterLineType.DESCRIPTION;
				}else if(lineCopy.startsWith(attrexample)){
					res = DocumenterLineType.EXAMPLE;
				}else if(lineCopy.startsWith(attrvalues)){
					res = DocumenterLineType.VALUES;
				}else if(lineCopy.startsWith(attrstate)){
					res = DocumenterLineType.STATE;
				}else if(isEnvironmentValue(lineCopy)){
					res = DocumenterLineType.ENVIRONMENT_VALUE;
				}else if(lineCopy.startsWith(attrvisiblewithvalue)){
					res = DocumenterLineType.VISIBLE_WITH_VALUE;
				}else if(lineCopy.startsWith(attrsimplecomment)){
					res = DocumenterLineType.INIT_SIMPLE_COMMENT;
				}
				
			// Si no, es cualquier cosa imprimible
			}else{
				res = DocumenterLineType.ANYTHING;
			}
		}
		return res;
	}
	
	/**
	 * Permite obtener el Environment de la línea
	 * @return Devuelve la clave que define el environment de la línea
	 * */
	public String getEnvironmentFromLine(String line) {
		int index = getEnvironmentIndexOf(line);
		if(index>0){
			int initIndx = line.indexOf(ANNOTATION_DEFINED_STRING);
			if(initIndx>0){
				logger.debug("getEnvironmentIndexOff: initIndex -> "+initIndx+" - index-> "+index);
				return line.substring(initIndx+1,index);
			}
		}
		return null;
	}

	/**
	 * Obtiene el valor de una línea de texto.
	 * @param line Línea de la que extraer el valor
	 * @param lineType Tipo de línea.
	 * @return Devuleve el valor según el tipo de línea
	 * */
	public String getValueFromLine(String line, DocumenterLineType lineType) {
		int indexOf = -1;
		switch(lineType){
			case DESCRIPTION:
				indexOf = line.indexOf(attrdescription)+attrdescription.length();
				if(indexOf>=0){
					indexOf++;
				}
				break;
			case EXAMPLE:
				indexOf = line.indexOf(attrexample)+attrexample.length();
				if(indexOf>=0){
					indexOf++;
				}
				break;
			case VALUES:
				indexOf = line.indexOf(attrvalues)+attrvalues.length();
				if(indexOf>=0){
					indexOf++;
				}
				break;
			case STATE:
				indexOf = line.indexOf(attrstate)+attrstate.length();
				if(indexOf>=0){
					indexOf++;
				}
				break;
			case FINISH:
				return line;
			case ENVIRONMENT_VALUE:
				indexOf = getEnvironmentIndexOf(line);
				break;
			default:
				break;
		}
		logger.debug("El valor del atributo: "+lineType+" comienza en el index: "+indexOf);
		if(indexOf>=0 && indexOf<line.length()){
			return line.substring(indexOf);
		}else{
			return null;
		}
	}
	
	/**
	 * Permite obtener una línea sin la anotación
	 * @param line Línea a transformar
	 * @return Devuelve una línea sin la anotación
	 * */
	public String getLineWithoutAnnotation(String line) {
		String res = line+"";
		if(isLineAnnotation(line)){
			for (String annotation : annotationString) {
				res = res.replace(annotation, "");
			}
		}
		return res;
	}

	// ACCEDENTES
	public MavenProject getProject() {
		return project;
	}

	public String[] getEnvironments() {
		return environments;
	}

	public String[] getExtensions() {
		if(extensions==null || extensions.length<=0){
			extensions = new String[]{DEFAULT_PROPERTIES_EXTENSION};
		}
		return extensions;
	}

	public String getAttrinit() {
		return attrinit;
	}

	public String getAttrdescription() {
		return attrdescription;
	}

	public String getAttrstate() {
		return attrstate;
	}

	public String getAttrexample() {
		return attrexample;
	}
	
	public String[] getAnnotationString() {
		return annotationString;
	}

	public String getAttrvalues() {
		return attrvalues;
	}

	public String getOutput() {
		return output;
	}
	
	public DocumenterType getType() {
		return type;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setEnvironments(String[] environments) {
		this.environments = environments;
	}

	public void setExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	public void setAnnotationString(String[] annotationString) {
		this.annotationString = annotationString;
	}

	public void setAsignationAnnotationString(String asignationAnnotationString) {
		this.asignationAnnotationString = asignationAnnotationString;
	}

	public String getAsignationAnnotationString() {
		return asignationAnnotationString;
	}
	
	public void setAttrinit(String attrinit) {
		this.attrinit = attrinit;
	}

	public void setAttrdescription(String attrdescription) {
		this.attrdescription = attrdescription;
	}

	public void setAttrstate(String attrstate) {
		this.attrstate = attrstate;
	}

	public void setAttrexample(String attrexample) {
		this.attrexample = attrexample;
	}

	public void setAttrvalues(String attrvalues) {
		this.attrvalues = attrvalues;
	}
	public void setAttrvisiblewithvalue(String attrvisiblewithvalue) {
		this.attrvisiblewithvalue = attrvisiblewithvalue;
	}
	public String getAttrvisiblewithvalue() {
		return attrvisiblewithvalue;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setType(DocumenterType type) {
		this.type = type;
	}
	
	public void setLogger(Log logger) {
		this.logger = logger;
	}
	
	public String[] getInputs() {
		return inputs;
	}
	
	public void setInputs(String[] inputs) {
		this.inputs = inputs;
	}
	
	public boolean isIncludeResourcesFolders() {
		return includeResourcesFolders;
	}
	
	public void setIncludeResourcesFolders(boolean includeResourcesFolders) {
		this.includeResourcesFolders = includeResourcesFolders;
	}
	
	public boolean isDefaultOutput() {
		return defaultOutput;
	}
	
	public void setDefaultOutput(boolean isDefaultOutput) {
		this.defaultOutput = isDefaultOutput;
	}
	
	public String getCharset() {
		return charset;
	}
	
	public void setWriteCharset(String charset) {
		this.charset = charset;
	}

	public void setReadCharsets(String[] readCharsets) {
		this.readCharsets = readCharsets;
	}
	
	public String[] getReadCharsets() {
		return readCharsets;
	}
	
	public String getAttrsimplecomment() {
		return attrsimplecomment;
	}
	
	public Variable[] getVariables() {
		return variables;
	}
	
	public void setVariables(Variable[] variables) {
		this.variables = variables;
	}

	/**
	 * Establece un Atributo para Un comentario simple
	 * @param attrsimplecomment
	 */
	public void setAttrSimpleComment(String attrsimplecomment) {
		this.attrsimplecomment = attrsimplecomment;
	}
	
	/**
	 * Determina el indexOf de un atributo de entorno
	 * @param line linea de la cual extraer el valor
	 * @return devuleve el indexOf si lo encuentra, en caso contrario, devuelve false.
	 * */
	private int getEnvironmentIndexOf(String line) {
		int firstAnnotationIn = line.indexOf(ANNOTATION_DEFINED_STRING);
		String lineCopy = line.substring(firstAnnotationIn+1);
		if(firstAnnotationIn>0 && lineCopy!=null){
			for (String environment : getEnvironments()) {
				if(lineCopy.startsWith(environment)){
					return line.indexOf(environment)+environment.length();
				}
			}					
		}
		return -1;
	}

	/**
	 * Determina si se trata de uno de los valores de entorno
	 * @param lineCopy Línea a tratar sin el comentario y con la arroba como primer elemento
	 * @return Devuelve true si se trata del caso, en caso contrario devuelve false.
	 * */
	private boolean isEnvironmentValue(String lineCopy) {
		if(getEnvironments()!=null){
			for (String environment : getEnvironments()) {
				if(lineCopy.startsWith((ANNOTATION_DEFINED_STRING)+environment)){
					return true;
				}
			}
		}
		return false;
	}
}
