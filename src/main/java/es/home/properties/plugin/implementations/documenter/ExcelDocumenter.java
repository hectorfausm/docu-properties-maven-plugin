package es.home.properties.plugin.implementations.documenter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.exception.SiaPluginDocumentationException;
import es.home.properties.model.DocumenterUnit;
import es.home.properties.model.MavenDocumenterPropertiesConfiguration;
import es.home.properties.plugin.excel.CellHeaderBean;
import es.home.properties.plugin.excel.ExcelAcces;
import es.home.properties.plugin.excel.ExcelFile;
import es.home.properties.plugin.excel.ExcelType;
import es.home.properties.plugin.excel.HeaderBean;

/**
 * Documentador excel
 * */
public class ExcelDocumenter extends PseudoImplementationDocumenter{

	/** Identificador del campo propiedad */
	public static final String PROPERTY_ID = "property";
	/** identificador del campo Descripción */
	public static final String DESCRIPTION_ID = "description";
	/** Identificador del campo valores */
	public static final String VALUES_ID = "values";
	/** Identificador del campo ejemplo */
	public static final String EXAMPLE_ID = "example";
	/** identificador del campo estado */
	public static final String STATE_ID = "state";
	/** identificador del campo valor del nombre de la propiedad */
	private static final String PROPERTY_NAME_ID = "propertyName";
	
	/** Fichero excel */
	public ExcelFile excelFile;
	
	/** {@inheritDoc} */
	public ExcelDocumenter(
			MavenDocumenterPropertiesConfiguration configuration, Log logger) {
		super(configuration, logger);
	}

	/** {@inheritDoc} 
	 * @throws SiaException */
	@Override
	public void closeFile() throws SiaPluginDocumentationException {
		getLogger().debug("Cerrando el fichero");
		ExcelAcces.closeExcel(excelFile);
	}

	/** {@inheritDoc} 
	 * @throws SiaPluginDocumentationException */
	@Override
	public void documentUnit(DocumenterUnit documenterUnit) throws SiaPluginDocumentationException {
		ExcelAcces.addBeanToRow(excelFile, documenterUnit);
		getLogger().debug("La cabecera para esta fila es: "+excelFile.getHeader().getBeans());
		if(documenterUnit.getEnvironments()!=null){
			for (String key :documenterUnit.getEnvironments().keySet()) {
				getLogger().debug("Añadiendo la propiedad de entorno con cabecera ("+key+"): "+excelFile.getHeader().getBeans().get(key));
				ExcelAcces.addCellValue(
					excelFile,
					documenterUnit.getEnvironments().get(key),
					key,
					-1
				);
			}
		}else{
			for (String environment : getConfiguration().getEnvironments()) {
				getLogger().debug("Añadiendo la propiedad de entorno con cabecera ("+environment+"): "+excelFile.getHeader().getBeans().get(environment));
				ExcelAcces.addCellValue(
					excelFile,
					documenterUnit.getDefaultValue(),
					environment,
					-1
				);
			}
		}
	}

	/** {@inheritDoc} 
	 * @throws SiaPluginDocumentationException */
	@Override
	public void initializeFile() throws SiaPluginDocumentationException {
		Map<String,CellHeaderBean> beanMap = new HashMap<String, CellHeaderBean>();
		int cont = 0;
		beanMap.put(PROPERTY_NAME_ID, new CellHeaderBean("Propiedad", cont++));
		beanMap.put(DESCRIPTION_ID, new CellHeaderBean("Descripción", cont++));
		beanMap.put(VALUES_ID, new CellHeaderBean("Valores", cont++));
		beanMap.put(EXAMPLE_ID, new CellHeaderBean("Ejemplo", cont++));
		beanMap.put(STATE_ID, new CellHeaderBean("Estado", cont++));
		if(getConfiguration().getEnvironments()!=null){
			for (String environment : getConfiguration().getEnvironments()) {
				beanMap.put(environment, new CellHeaderBean(environment, cont++));
			}
		}
		HeaderBean bean = new HeaderBean(beanMap);
		excelFile = ExcelAcces.initExcel(ExcelType.XSSF,bean);
	}

	/** {@inheritDoc} */
	@Override
	public void writeFile(String fileName)
			throws SiaPluginDocumentationException {
		
		getLogger().info("Autoajustando el tamaño de las columnas");
		ExcelAcces.autoSizeColumns(excelFile);
		
		String url = getConfiguration().getOutput()+"/"+fileName;
		File f = new File(url);
		if(f.exists()){
			f.delete();
		}
		getLogger().info("Guardando el fichero en: "+url.replace(fileName, ""));
		ExcelAcces.writeFile(excelFile, url);
	}
}
