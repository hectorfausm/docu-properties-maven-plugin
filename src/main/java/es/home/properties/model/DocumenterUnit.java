package es.home.properties.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.plugin.utils.StringUtils;

/** Unidad de documentación para cada una de las propiedades documentables de un fichero */
public class DocumenterUnit implements Serializable, Comparable<DocumenterUnit> {
	/** Identificador d ela serialización */
	private static final long serialVersionUID = 1L;

	/** Nombre de la propiedad */
	private String propertyName;
	
	/** Descripción de la propiedad */
	private String description;
	
	/** Estado de la propiedad */
	private String state;
	
	/** TODO Obligatorio */
	private String mandatory;
	
	/** Patrón de la propiedad */
	private String pattern;
	
	/** Ejemplos */
	private String example;
	
	/** Valores posibles para la propiedad */
	private String values;
	
	/** TODO Valores posibles para la propiedad */
	private String[] possibleValues;
	
	/** Valores para los diferentes entornos */
	private Map<String,String> environments;
	
	/** Valores que se asignarán por defecto para aquellos entornos que cumplan el patrón especificado en la key cuando el entorno no tenga valor */
	private Map<String,String> specialDefaultEnvironments;
	
	/** Logger de la aplicación */
	private Log logger;
	
	/**
	 * Determina si la unidad de documentación debe ser solo vivisble si contiene valor
	 * En caso de ser false. Siempre será visible
	 * */
	private boolean visibleWithValue;
	
	/**
	 * Valor por defecto que será asignado a una propiedad en caso de no exisitir 
	 * el entorno especificado por el usuario
	 * */
	private String defaultValue;
	
	/**
	 * Constructor de la Unidad de documentación
	 * @param log
	 */
	public DocumenterUnit(Log log) {
		this.logger = log;
	}
	
	// ACCEDENTES
	public String getDescription() {
		return description;
	}
	public void setDescription(String description, Variable... filterVariables) {
		if(filterVariables!=null){
			this.description = StringUtils.filterStringWithVariables(description, filterVariables, logger);
		}else{
			this.description = description;
		}
	}
	public String getState() {
		return state;
	}
	public void setState(String state, Variable... filterVariables) {
		if(filterVariables!=null){
			this.state = StringUtils.filterStringWithVariables(state, filterVariables, logger);
		}else{
			this.state = state;
		}
	}
	public String getExample() {
		return example;
	}
	public void setExample(String example, Variable... filterVariables) {
		if(filterVariables!=null){
			this.example = StringUtils.filterStringWithVariables(example, filterVariables, logger);
		}else{
			this.example = example;
		}
	}
	public String getValues() {
		return values;
	}
	public void setValues(String values, Variable... filterVariables) {
		if(filterVariables!=null){
			this.values = StringUtils.filterStringWithVariables(values, filterVariables, logger);
		}else{
			this.values = values;
		}
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName, Variable... filterVariables) {
		if(filterVariables!=null){
			this.propertyName = StringUtils.filterStringWithVariables(propertyName, filterVariables, logger);
		}else{
			this.propertyName = propertyName;
		}
	}
	public Map<String, String> getEnvironments() {
		return environments;
	}
	public boolean isVisibleWithValue() {
		return visibleWithValue;
	}
	public void setVisibleWithValue(boolean visibleWithValue) {
		this.visibleWithValue = visibleWithValue;
	}
	public void addEnvironment(String key, String line, Variable... filterVariables) {
		if(key!=null && line!=null){
			if(environments==null){
				environments = new HashMap<>();
			}
			environments.put(
				key,
				StringUtils.filterStringWithVariables(line, filterVariables, logger)
			);
		}
	}
	public void addSpecialDefaultEnvironment(String key,
			String line, Variable[] variables) {
		if(key!=null && line!=null){
			if(specialDefaultEnvironments==null){
				specialDefaultEnvironments = new HashMap<>();
			}
			specialDefaultEnvironments.put(
				key,
				StringUtils.filterStringWithVariables(line, variables, logger)
			);
		}
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue, Variable... filterVariables) {
		if(filterVariables!=null){
			this.defaultValue = StringUtils.filterStringWithVariables(defaultValue, filterVariables, logger);
		}else{
			this.defaultValue = defaultValue;
		}
	}
	public String getMandatory() {
		return mandatory;
	}
	public void setMandatory(String mandatory,Variable... filterVariables) {
		if(filterVariables!=null){
			this.mandatory = StringUtils.filterStringWithVariables(mandatory, filterVariables, logger);
		}else{
			this.mandatory = mandatory;
		}
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern, Variable... filterVariables) {
		if(filterVariables!=null){
			this.pattern = StringUtils.filterStringWithVariables(pattern, filterVariables, logger);
		}else{
			this.pattern = pattern;
		}
	}
	public String[] getPossibleValues() {
		return possibleValues;
	}
	public void setPossibleValues(String[] possibleValues, Variable... filterVariables) {
		this.possibleValues = possibleValues;
		if(filterVariables!=null && this.possibleValues!=null){
			int size = possibleValues.length;
			for (int i = 0; i < size; i++) {
				this.possibleValues[i] = StringUtils.filterStringWithVariables(this.possibleValues[i], filterVariables, logger);
			}
		}
	}
	
	/**
	 * Permite obtener una cadena representando las propiedades tal cual van a ser mostradas en
	 * un fichero de propiedades.
	 * @param environment Entorno del cual se quiere obtener el documentador
	 * @param annotationString Cadena que representa una anotación para una línea en el fichero de propiedades
	 * @param assignationAnnotationString Cadena que representa uan asignación en un fichero de propiedades
	 * @param addState 
	 * @param addExample 
	 * @param addDescription 
	 * @param defaultValue
	 * */
	public String getDocumenterToEnvironmentFile(String environment, String annotationString,
			String assignationAnnotationString, boolean addDescription, boolean addExample,
			boolean addState){
		
		StringBuilder res = new StringBuilder();
		
		// Descripción tanto para las propiedades como para las descripciones simples
		if(addDescription){
			addElement(res,description,annotationString);
		}
		
		// Si se trata de una propiedad
		if(propertyName!=null){
			
			// Estado
			if(addState){
				addElement(res,state,annotationString);
			}
			
			// Ejemplo
			if(addExample){
				addElement(res,example,annotationString);
			}
			
			res.append("\n"+propertyName+assignationAnnotationString);
		}
		
		// Se calcula el valor de la propiedad
		String value = "";
		
		// Si no hay valor por entorno
		if(environments==null || !environments.containsKey(environment) || environments.get(environment)==null){
			
			// Se comprueba si hay valor por entorno especial por defecto
			boolean hashValue = false;
			if(specialDefaultEnvironments!=null && !specialDefaultEnvironments.isEmpty()){
				for (Entry<String, String> entry : specialDefaultEnvironments.entrySet()) {
					if(environment.matches(entry.getKey())){
						hashValue = true;
						value = entry.getValue().replaceAll("^\\s+", "");
					}
				}
			}
			
			// Si no hay valor se debe comprobar si hay valor por defecto base
			if(!hashValue && getDefaultValue()!=null){
				value = getDefaultValue();
			}
		}else{
			value = environments.get(environment).replaceAll("^\\s+", "");
		}
		
		if(value!=null && !value.isEmpty()){
			
			// Valor
			res.append(value);
			
			// Separación 
			res.append("\n\n");
			
		}else if(isVisibleWithValue()){
			return "";
		}else{
			
			// Solo se incluye el espacio
			res.append("\n\n");
		}
		
		return res.toString();
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getClass().getName()
				+ " {\n\t"
				+ (propertyName != null ? "propertyName: " + propertyName
						+ "\n\t" : "")
				+ (description != null ? "description: " + description + "\n\t"
						: "")
				+ (state != null ? "state: " + state + "\n\t" : "")
				+ (example != null ? "example: " + example + "\n\t" : "")
				+ (values != null ? "values: " + values + "\n\t" : "")
				+ (environments != null ? "environments: " + environments +"\n\t": "")
				+ (specialDefaultEnvironments != null ? "specialDefaultEnvironments: " + specialDefaultEnvironments +"\n\t": "")
				+ (defaultValue != null ? "defaultValue: " + defaultValue +"\n\t": "")
				+ "\n\t[super: " + super.toString() + "]\n}";
	}
	
	/** {@inheritDoc} */
	@Override
	public int compareTo(DocumenterUnit o) {
		return this.propertyName.compareTo(o.getPropertyName());
	}
	
	/**
	 * Añade un elemento del objeto a un String Buffer
	 * @param res String buffer donde se debe añadir el elemento
	 * @param element Elemento a añadir
	 * @param annotationString Cadena que representa una anotación
	 * */
	private void addElement(StringBuilder res, String element, String annotationString) {
		if(element==null){
			return;
		}
		if(element.contains("\n")){
			String[] lines = element.split("\n");
			boolean start = true;
			for (String line : lines) {
				if(!line.isEmpty()){
					if(!start){
						res.append("\n");
					}else{
						start = false;
					}
					res.append(annotationString+" "+line);
				}
			}
		}else{
			res.append(annotationString+" "+description);
		}
	}
	
	/**
	 * Determina si una propiedad no debe ser impresa para un determinado entorno de compilación
	 * @param environment Entorno de compilación
	 * @return Devuleve true si la propiedad no debe ser escrita en la compilación, en caso
	 * contrario, devuelve false
	 */
	private boolean propertyNotPrintedToEnvironment(String environment) {
		if(isVisibleWithValue()){
			if(environments==null){
				return getDefaultValue()==null || getDefaultValue().length()<=0;
			}else{
				return !environments.containsKey(environment) || environments.get(environment)==null ||  environments.get(environment).length()<=0;
			}
		}
		return false;
	}

	public String getDocumenterUnitToDocumentPorpertyFile(MavenDocumenterPropertiesConfiguration configuration) {
		StringBuilder builder = new StringBuilder();
		String annotationString = configuration.getAnnotationString()[0];
		String assignationAnnotationString = configuration.getAsignationAnnotationString();
		
		// Se crea un comentario simple
		if(this.propertyName!=null){
			builder.append(annotationString+" "+configuration.getAttrinit()+"\n");
			builder.append(annotationString+" "+this.description);
			
		// Se crea una propiedad
		}else{
			builder.append(annotationString+" "+configuration.getAttrsimplecomment()+"\n");
			if(this.description!=null){
				builder.append(annotationString+" "+configuration.getAttrdescription()+" "+this.description+"\n");
			}
			if(this.pattern!=null){
				builder.append(annotationString+" "+configuration.getAttrpattern()+" "+this.pattern+"\n");
			}
			if(this.example!=null){
				builder.append(annotationString+" "+configuration.getAttrexample()+" "+this.example+"\n");
			}
			if(this.state!=null){
				builder.append(annotationString+" "+configuration.getAttrstate()+" "+this.state+"\n");
			}
			if(this.values!=null){
				builder.append(annotationString+" "+configuration.getAttrvalues()+" "+this.values+"\n");
			}
			if(this.visibleWithValue){
				builder.append(annotationString+" "+configuration.getAttrvisiblewithvalue()+"\n");
			}
			if(this.environments!=null){
				for (Entry<String, String> entry : this.environments.entrySet()) {
					builder.append(annotationString+" "+MavenDocumenterPropertiesConfiguration.ANNOTATION_DEFINED_STRING+entry.getKey()+" "+entry.getValue()+"\n");
				}
			}
			builder.append(this.propertyName+assignationAnnotationString);
			if(this.defaultValue!=null){
				builder.append(this.defaultValue);
			}
		}
		return builder.toString();
	}
}
