package es.home.properties.plugin.excel;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Clase que permite la construcción de una cabecera con identificadores para un manejador de ficheros excel
 * */
public class HeaderBean {
	
	// PROPIEDADES ESTÁTICAS
	/** Color por defecto de las cabeceras */
	private static final IndexedColors DEFAULT_HEADER_COLOR = IndexedColors.AQUA; 
	
	// PROPIEDADES
	/** Mapeo de claves */
	private Map<String,CellHeaderBean> beans;
	/** Color de la cabecera */
	private IndexedColors indexedColor;
	
	/**
	 * Constructor que permite la isntanciación de una cabecera de manejador de ficheros excel
	 * @param beanMap Mapa de claves. Por cada elemento del mapa se encuentra:
	 * 	- key: Clave identificadora de la columna
	 *  - value: Valor que se mostrará en al columna
	 * */
	public HeaderBean(Map<String,CellHeaderBean> beanMap) {
		this(beanMap,null);
	}
	
	/**
	 * Constructor que permite la isntanciación de una cabecera de manejador de ficheros excel
	 * @param beans Mapa de claves. Por cada elemento del mapa se encuentra:
	 * 	- key: Clave identificadora de la columna
	 *  - value: Valor que se mostrará en al columna
	 * @param indexedColor Color a mostrar en la cabecera del fichero excel cuando este sea escrito.
	 * */
	public HeaderBean(Map<String,CellHeaderBean> beans,IndexedColors indexedColor) {
		this.indexedColor = indexedColor;
		this.beans = beans;
	}
	
	/**
	 * Obtiene el valor a mostrar en una celda de la cabecera a partir de la posciión de al columna
	 * @param index Posición de la columna
	 * @return Devuelve el valor a mostrar en la cabecera de un fichero excel según la posición pasad por parámetro
	 * */
	public String getCellFromIndex(int index){
		for (String keyAttr : beans.keySet()) {
			if(beans.get(keyAttr).getPosition()==index);
				return beans.get(keyAttr).getExcelKey();
		}
		return null;
	}
	
	public String getKeyFromIndex(int index) {
		for (String keyAttr : beans.keySet()) {
			if(beans.get(keyAttr).getPosition()==index)
				return keyAttr;
		}
		return null;
	}
	
	//Accedentes
	public Map<String, CellHeaderBean> getBeans() {
		return beans;
	}
	public IndexedColors getIndexedColor() {
		if(indexedColor == null)
			indexedColor = DEFAULT_HEADER_COLOR;
		return indexedColor;
	}
	
	/**
	 * Genera una cabecera a partir de un mapa con las posiciones que ocupa cada cabecera de forma aleatoria
	 * @param keysMap Mapa con las claves de la cabecera
	 * 	- key: Identificador de la celda
	 *  - value: Decripción de la celda
	 * */
	public static Map<String,CellHeaderBean> generateHeaderBeanMapFromKeysMap(Map<String,String>keysMap){
		Map<String,CellHeaderBean> beans = new HashMap<String, CellHeaderBean>(); 
		int i = 0;
		for (String keyAttr : keysMap.keySet()) {
			CellHeaderBean chb = new CellHeaderBean(
				keysMap.get(keyAttr),
				i++
			);
			beans.put(keyAttr,chb);
		}
		return beans;
	}
}
