package es.home.properties.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.plugin.utils.StringUtils;

/** Unidad de documentación para cada una de las propiedades documentables de un fichero */
public class DocumenterUnit implements Serializable, Comparable<DocumenterUnit> {
	/** Identificador d ela serialización */
	private static final long serialVersionUID = 1L;

	/** Tenant por defecto para propiedades multitenant */
	private String defaultTenant;
	
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

	/** Determina si una propiedad ha sido o no visible */
	private boolean wasVisible = true;
	
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
	 * Determina si la unidad de documentación se generó o no para un fichero multitenant
	 */
	private boolean isMultitenant;
	
	/**
	 * Constructor de la Unidad de documentación
	 * @param log Logger de la aplicación
	 */
	public DocumenterUnit(Log log) {
		this.logger = log;
	}
	
	// Métodos de acceso a los valores de las prpiedades
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
	public void addEnvironment(String key, String value, Variable... filterVariables) {
		if(key!=null && (isMultitenant || value!=null)){
			if(environments==null){
				environments = new HashMap<>();
			}
			if(value!=null) {
				value = StringUtils.filterStringWithVariables(value, filterVariables, logger);
			}
			environments.put(
				key,
				value
			);
		}
	}
	public void addSpecialDefaultEnvironment(String key,
			String value, Variable[] variables) {
		if(key!=null && (isMultitenant || value!=null)){
			if(specialDefaultEnvironments==null){
				specialDefaultEnvironments = new HashMap<>();
			}
			if(value!=null) {
				value = StringUtils.filterStringWithVariables(value, variables, logger);
			}
			specialDefaultEnvironments.put(
				key,
				StringUtils.filterStringWithVariables(value, variables, logger)
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
	public boolean isMultitenant() {
		return isMultitenant;
	}
	public void setMultitenant(boolean isMultitenant) {
		this.isMultitenant = isMultitenant;
	}
	public void setDefaultTenant(String defaultTenant) {
		this.defaultTenant = defaultTenant;
	}
	public boolean isWasVisible() {
		return wasVisible;
	}
	
	/**
	 * Permite obtener una cadena representando las propiedades tal cual van a ser mostradas en
	 * un fichero de propiedades.
	 * @param environment Entorno del cual se quiere obtener el documentador
	 * @param annotationString Cadena que representa una anotación para una línea en el fichero de propiedades
	 * @param assignationAnnotationString Cadena que representa uan asignación en un fichero de propiedades
	 * @param addState Determina si se debe o no añadir el estado en la documentación de la propiedad
	 * @param addExample Determina si se debe o no añadir el ejemplo en la documentación de la propiedad 
	 * @param addDescription Determina si se debe o no añadir la descripción en la documentación de la propiedad
	 * @return Devuelve la cadena que representa la porpiedad compilada
	 * */
	public String getDocumenterToEnvironmentFile(String environment, String annotationString,
			String assignationAnnotationString, boolean addDescription, boolean addExample,
			boolean addState){
		
		StringBuilder res = new StringBuilder();
		
		// Se calcula el/los valor/es de la propiedad
		String value = "";
		Map<String, String> valuesByTenant = new HashMap<>();
		
		// Si la propiedad es multitenant, se obtienen los valores multitenant
		if(isMultitenant) {
			logger.info("environments: "+environments);
			logger.info("Special Environments: "+specialDefaultEnvironments);
			valuesByTenant = getMultitenantValuesFromEnvironment(environment);

			
		// Si no, se obtienen los valores simples
		}else {
			value = getValueFromEnvironment(environment);
		}
		
		// Si la propiedad no es visible en base a sus valores, se devuelve vacío
		if(!isPropertyVisible(value, valuesByTenant)) {
			wasVisible = false;
			return "";
		}
		
		// Descripción tanto para las propiedades como para las descripciones simples
		boolean containsComment = false;
		if(addDescription){
			containsComment = addElement(res,description,annotationString);
		}
		
		// Si se trata de una propiedad
		if(propertyName!=null){
			
			// Estado
			if(addState){
				containsComment |= addElement(res, state,annotationString);
			}
			
			// Ejemplo
			if(addExample){
				containsComment |= addElement(res, example,annotationString);
			}
			
			// Si existe comentario, se añade un salto de línea para separa el comentario de la propiedad
			if(containsComment) {
				res.append("\n");
			}
			
			// Si es multitenant, se obtienen las propiedades multitenant
			if(isMultitenant) {
				for (Entry<String, String> tenant : valuesByTenant.entrySet()) {
					String procesed = getProcesedPropertyAndValue(tenant.getKey()+"."+propertyName, tenant.getValue(), assignationAnnotationString);
					res.append(procesed+"\n");
				}
				
				// Se elimina el último espacio
				res.delete(res.length()-1, res.length());
			}else{
				String procesed = getProcesedPropertyAndValue(propertyName, value, assignationAnnotationString);
				res.append(procesed);
			}
		}
		return res.toString();
	}
	
	/**
	 * Obtiene los valores multitenant para una propiedad
	 * 
	 * @param environment {@link String} Enterono para el que se está compiando la propiedad
	 * 
	 * @return {@link Map} Mapa de valores por tenant
	 */
	private Map<String, String> getMultitenantValuesFromEnvironment(String environment) {
		
		// Se instancai como como el resultado vacío
		Map<String, String> valuesByTenant = new HashMap<>();
				
		// Se obtienen los tenant por patrón 
		valuesByTenant = getSpecialMultitenantValues(environment);
		
		// Se obtienen los tenant sin patrón
		Map<String, String> valuesByTenantSimple = getSimpleMultiTenantValues(environment);
		
		// Se recorren los tenant sin patrón para que permanezca su valor sobre el valor de los tenant por patrón
		for (Entry<String, String> entry : valuesByTenantSimple.entrySet()) {
			valuesByTenant.put(entry.getKey(), entry.getValue());
		}
		
		// Se rellenan los valores de tenants vacíos con el valor por defecto
		List<String> emptyTenants = new ArrayList<>();
		for (Entry<String, String> entry : valuesByTenant.entrySet()) {
			if(entry.getValue()==null || entry.getValue().isEmpty()) {
				emptyTenants.add(entry.getKey());
			}
		}
		for (String key : emptyTenants) {
			valuesByTenant.put(key, getDefaultValue());
		}
		
		// Si no hay tenant por deecto, se añade
		if(!valuesByTenant.containsKey(defaultTenant)) {
			valuesByTenant.put(defaultTenant, getDefaultValue());
		}
		
		return valuesByTenant;
	}

	/**
	 * Obtiene un valor para una propiedad no multitenant
	 * 
	 * @param environment {@link String} Entorno para el que obtener el valor de la propiedad
	 * 
	 * @return {@link String} con el valor de la porpiedad o null si no se encuentra un valor
	 */
	private String getValueFromEnvironment(String environment) {
		
		String value = null;
		
		// Si no hay valor por entorno
		if(environments==null || !environments.containsKey(environment) || environments.get(environment)==null){
			
			// Se comprueba si hay valor por entorno especial por defecto
			boolean hashValue = false;
			if(specialDefaultEnvironments!=null && !specialDefaultEnvironments.isEmpty()){
				for (Entry<String, String> entry : specialDefaultEnvironments.entrySet()) {
					if(environment.matches(entry.getKey())){
						hashValue = true;
						value = entry.getValue();
						if(value!=null) {
							value = value.trim();
						}
					}
				}
			}
			
			// Si no hay valor se debe comprobar si hay valor por defecto base
			if(!hashValue && getDefaultValue()!=null){
				value = getDefaultValue().trim();
			}
		}else{
			value = environments.get(environment);
			if(value!=null) {
				value = value.trim();
			}
		}
		return value;
	}

	/**
	 * En función de los valores obtenidos de una compilación de una propiedad, determina si la propiedad es o no visible
	 * 
	 * @param value				{@link String} 	Valor simple
	 * @param valuesByTenant	{@link Map}		Valores por tenant
	 * 
	 * @return true si la propiedad es visible o false en caso contrario
	 */
	private boolean isPropertyVisible(String value, Map<String, String> valuesByTenant) {
		if(isVisibleWithValue()) {
			if(isMultitenant) {
				return !isEmptyMultitenantValues(valuesByTenant);
			}else {
				return value!=null && !value.isEmpty();
			}
		}else {
			return true;
		}
	}

	/**
	 * Determina si los valores multitenant pasados por parámetro están o no vacíos
	 * 
	 * @param valuesByTenant {@link Map} Map de valores multitenant a validar
	 * 
	 * @return true, si los valores están vacíos o false en caso contrario
	 */
	private boolean isEmptyMultitenantValues(Map<String, String> valuesByTenant) {
		if(!valuesByTenant.isEmpty() && valuesByTenant!=null) {
			for (String value : valuesByTenant.values()) {
				if(value!=null && !value.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Obtiene el conjunto valor propiedad 
	 * 
	 * @param poperty						{@link String} Cadena de la propiedad
	 * @param value							{@link String} Cadena del valor asociado a la propiedad
	 * @param assignationAnnotationString	{@link String} Cadena que representa el separador entre valor y propiedad
	 * 
	 * @return Devuelve el conjunto deseado
	 */
	private String getProcesedPropertyAndValue(String poperty, String value, String assignationAnnotationString) {
		
		// Si existe valor
		if(value!=null && !value.isEmpty()){
			return poperty+assignationAnnotationString+value;
			
		// Si la propiedad es visible solamente con valor, se devuelve cadena vacía
		}else {
			return poperty+assignationAnnotationString;
		}
	}

	/**
	 * Obtiene los valores multitenant para una propiedad multitenant detipo patrón
	 * 
	 * @param environment {@link String} Entorno a compilar
	 * 
	 * @return {@link Map} Mapa con los tenants y su valor asociado para el entorno a compilar
	 */
	private Map<String, String> getSpecialMultitenantValues(String environment) {
		
		// resulktado por tenants
		Map<String, String> result = new HashMap<>();
		
		// Si no hay valores, se devuelve el resultado vacío
		if(specialDefaultEnvironments==null) {
			return result;
		}
		
		// Se recorren las propiedades de patrón
		for (String key : specialDefaultEnvironments.keySet()) {
			
			// Se obtiene la clave real.
			String realKey = key.substring(0, key.indexOf(MavenDocumenterPropertiesConfiguration.MULTITENANT_DEFINED_STRING));
			if(environment.matches(realKey)) {
				String tenantKey = key.substring(key.indexOf(MavenDocumenterPropertiesConfiguration.MULTITENANT_DEFINED_STRING)+1, key.length());
				String value = specialDefaultEnvironments.get(key);
				if(value!=null) {
					value = value.trim();
				}
				result.put(tenantKey, value);
			}
		}
		return result;
	}

	/**
	 * Obtiene los valores asociados a claves de propiedad para un entorno en base a tenants
	 * 
	 * @param environment Valor del entorno a compilar
	 * 
	 * @return Mapa de valores donde:
	 * 	- key = Conjunción de Entorno.ClaveDePropiedad
	 *  - value = Valor asociado al tenant
	 */
	private Map<String, String> getSimpleMultiTenantValues(String environment) {
		Map<String, String> values = new HashMap<>();
		
		// Si no hay entornos se devuelve vacío
		if(environments == null) {
			return values;
		}
		
		for (Entry<String, String> entry : environments.entrySet()) {
			
			// Clave de la propiedad
			String key = entry.getKey();
			
			// valor asociado a la propiedad
			String value = entry.getValue();
			
			// Si la propiedad contiene un atributo de multitenant, se comprueba si existe clave multitenant
			String realKey = key.substring(0, key.indexOf(MavenDocumenterPropertiesConfiguration.MULTITENANT_DEFINED_STRING));
			String tenantKey = key.substring(key.indexOf(MavenDocumenterPropertiesConfiguration.MULTITENANT_DEFINED_STRING)+1, key.length());
			if(environment.equals(realKey)) {
				if(value!=null) {
					value = value.trim();
				}
				values.put(tenantKey, value);
			}
		}
		return values;
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
	private boolean addElement(StringBuilder res, String element, String annotationString) {
		if(element==null){
			return false;
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
			res.append(annotationString+" "+element);
		}
		return true;
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
	
	@Override
	public String toString() {
		return "DocumenterUnit [propertyName=" + propertyName + ", description=" + description + ", state=" + state
				+ ", mandatory=" + mandatory + ", pattern=" + pattern + ", example=" + example + ", values=" + values
				+ ", possibleValues=" + Arrays.toString(possibleValues) + ", environments=" + environments
				+ ", specialDefaultEnvironments=" + specialDefaultEnvironments + ", logger=" + logger
				+ ", visibleWithValue=" + visibleWithValue + ", defaultValue=" + defaultValue + ", isMultitenant="
				+ isMultitenant + "]";
	}
}
