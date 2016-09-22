package es.home.properties.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
	private String madatory;
	
	/** TODO Patrón de la propiedad */
	private String pattern;
	
	/** Ejemplos */
	private String example;
	
	/** Valores posibles para la propiedad */
	private String values;
	
	/** TODO Valores posibles para la propiedad */
	private String[] possibleValues;
	
	/** Valores para los diferentes entornos */
	private Map<String,String> environments;
	
	/**
	 * Determina si la unidad de documentación debe ser solo vivisble si contiene valor
	 * En caso de ser false. Siempre será visible
	 * */
	private boolean visibleWithValue;
	
	/**
	 * Valor por defecto que será asiognado a una propiedad en caso de no exisitir 
	 * el entorno especificado por el usuario
	 * */
	private String defaultValue;
	
	// ACCEDENTES
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getExample() {
		return example;
	}
	public void setExample(String example) {
		this.example = example;
	}
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String property) {
		this.propertyName = property;
	}
	public Map<String, String> getEnvironments() {
		return environments;
	}
	public void setEnvironments(Map<String, String> environments) {
		this.environments = environments;
	}
	public boolean isVisibleWithValue() {
		return visibleWithValue;
	}
	public void setVisibleWithValue(boolean visibleWithValue) {
		this.visibleWithValue = visibleWithValue;
	}
	public void addEnvironment(String key, String line) {
		if(key!=null && line!=null){
			if(environments==null){
				environments = new HashMap<String, String>();
			}
			environments.put(key, line);
		}
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultVlaue) {
		this.defaultValue = defaultVlaue;
	}
	
	/**
	 * Método que permite obtener una cadena representando las propiedades tal cual van a ser mostradas en
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
		
		if(propertyNotPrintedToEnvironment(environment)){
			return "";
		}
		
		StringBuffer res = new StringBuffer();
		
		// Descricpión
		if(addDescription){
			addElement(res,description,annotationString);
		}

		// Estado
		if(addState){
			addElement(res,state,annotationString);
		}
		
		// Ejemplo
		if(addExample){
			addElement(res,example,annotationString);
		}
		
		// Nombre de la porpiedad
		res.append("\n"+propertyName+assignationAnnotationString);
		
		// Valor por entorno
		if(environments==null){
			res.append(getDefaultValue());
		}else if(environments.containsKey(environment) && environments.get(environment)!=null){
			res.append(environments.get(environment).toString().replaceAll("^\\s+", ""));
		}
		
		// Separación 
		res.append("\n\n");
		
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
				+ (environments != null ? "environments: " + environments : "")
				+ (defaultValue != null ? "defaultValue: " + defaultValue : "")
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
	private void addElement(StringBuffer res, String element, String annotationString) {
		if(element==null) return;
		if(element.contains("\n")){
			String[] lines = element.split("\n");
			for (String line : lines) {
				res.append("\n"+annotationString+" "+line);
			}
		}else{
			res.append(" "+annotationString+description);
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
				return getDefaultValue()==null;
			}else{
				return !environments.containsKey(environment) || environments.get(environment)==null;
			}
		}
		return false;
	}
}
