package es.home.properties.plugin.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.exception.PluginDocumentationExceptionCode;

public abstract class ExcelAcces {
	
	//LOGGER
	/** Logger */
	private static Logger LOGGER = LoggerFactory.getLogger(ExcelAcces.class);
	
	//CONSTANTES
	/** Entero que representa la fila de la inicialización del fichero */
	private static final int INIT_FILE_SHEET_AT = 0;
	/** Entero que representa a fila donde se ecnuentra la cabecera */
	private static final int HEADER_ROW_DEFAULT = 0;
	/** Símbolo que identifica, dentro de un identificador de atributo. Los subatributos */
	private static final String IDENTIFIER_SIGN_ATTRS = ".";
	/** Regex para split */
	private static final String IDENTIFIER_SIGN_ATTRS_SPLIT = "\\.";
	
	/**
	 * Método que permite inicializar un fichero excel sin datos
	 * @param excelType Tipo de fichero a inicializar
	 * @return Devuelve un manejador de ficheros excel
	 * @throws PluginDocumentationException 
	 * */
	public static ExcelFile initExcel(ExcelType excelType, HeaderBean headerMap) throws PluginDocumentationException {
		return initExcel(null, excelType, headerMap);
	}
	
	/**
	 * Método que incializa el fichero excel. Cogiendo como cabecera del fichero, la primera de sus líneas
	 *  @param filePath Dirección del fichero -> Si la dirección del fichero es nula, se crea un manejador de ficheros
	 * sin datos
	 * @param excelType Tipo de fichero
	 * @param header Cabecera del fichero -> Si es nula, se toma como cabecera la primera fila del manejador
	 * @return Devuelve un {@link ExcelFile} con la infromación de inicialización del fichero
	 * */
	public static ExcelFile initExcel(String filePath, ExcelType excelType, HeaderBean headerMap) throws PluginDocumentationException{
		String errorMsg = "Error durante la inicialización del Fichero excel. ";
		try {
			ExcelFile instance = new ExcelFile();
			
			Workbook work = null;
			switch(excelType){
				case XSSF:
					if(filePath!=null)
						work = new XSSFWorkbook(new FileInputStream(new File(filePath)));
					else
						work = new XSSFWorkbook(); 
					break;
				default:
					//TODO Para el resto de formatos XML
			}
			instance.setWorkbook(work);
			try{
				instance.setSheet(instance.getWorkbook().getSheetAt(INIT_FILE_SHEET_AT));
			}catch(IllegalArgumentException e){
				instance.setSheet(work.createSheet());
			}
			
            addHeader(instance, headerMap);
            
            instance.setType(excelType);
            
            return instance;
		} catch (FileNotFoundException e) {
			throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_INIT_EXCEL,
				e
			);
		} catch (IOException e) {
			throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_INIT_EXCEL,
				e
			);
		} catch (Exception e){
			throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_INIT_EXCEL_UNKNOWN,
				e
			);
		}
	}
	
	/**
	 * Método que devuelve una lista de mapas con la información del fichero a partir de un {@link ExcelFile}
	 * @param excelFile Elemento con la información del fichero
	 * @return Devuelve una lista de mapas con la información del fichero
	 * */
	public static List<Map<String,Object>> getMapsFromFile(ExcelFile excelFile){
		//Get iterator to all the rows in current sheet
		Iterator<Row> rowIterator = excelFile.getSheet().iterator();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		int i = 1;
		//Se ignora la cabecera
		if(rowIterator.hasNext()){
			rowIterator.next();
		}
		//Se comienza la iteraciÃ³n sobre el resto de la tabla
		while(rowIterator.hasNext()){
			list.add(getMapFromRow(excelFile, i++));
			rowIterator.next();
		}
		return list;
	}
	
	/**
	 * Permite devolver un mapa a partir de una determinada fila del fichero excel
	 * @param excelFile Elemento con la informaciÃ³n del fichero
	 * @param rowIndex Fila de la cual se quiere obtener el mapa
	 * @return Devuelve un mapa// con la informaciÃ³n de la fila
	 * */
	public static Map<String,Object> getMapFromRow(ExcelFile excelFile, int rowIndex){
		if(excelFile.getSheet().getRow(rowIndex)!=null){
			Iterator<Cell> cellIterator = excelFile.getSheet().getRow(rowIndex).cellIterator();
			Map<String,Object> res = new HashMap<String, Object>();
			int counter = 0;
			while(cellIterator.hasNext()){
				Object value = getValueFromCell(cellIterator.next());
				String key = excelFile.getHeader().getKeyFromIndex(counter++);
				if(key!=null){
					res.put(key, value);
				}else{
					res.put(String.valueOf(counter++), value);
				}
			}
			return res;
		}
		return null;
	}
	
	/**
	 * Permite devolver un mapa a partir de una determinada fila del fichero excel
	 * @param excelFile Elemento con la información del fichero
	 * @param rowIndex Fila de la cual se quiere obtener el mapa
	 * @return Devuelve un mapa con la información de la fila
	 * */
	public static Map<String,Object> getObjectFromRow(ExcelFile excelFile, int rowIndex){
		
		Iterator<Cell> cellIterator = excelFile.getSheet().getRow(rowIndex).cellIterator();
		Map<String,Object> res = new HashMap<String, Object>();
		int counter = 0;
		while(cellIterator.hasNext()){
			Cell cell = cellIterator.next();
			
			Object value;
			switch(cell.getCellType()) {
	            case Cell.CELL_TYPE_BOOLEAN:
	                value = cell.getBooleanCellValue();
	                break;
	            case Cell.CELL_TYPE_NUMERIC:
	                value = cell.getNumericCellValue();
	                break;
	            case Cell.CELL_TYPE_STRING:
	                value = cell.getStringCellValue();
	                break;
	            case Cell.CELL_TYPE_BLANK:
	            	value = null;
	            	break;
	            default:
	            	//TODO Fórmulas
	            	value = null;
	            	break;
			}
			if(excelFile.getHeader().getCellFromIndex(rowIndex)!=null){
				String key = excelFile.getHeader().getCellFromIndex(rowIndex);
				res.put(key, value);
			}else{
				String errorMsg="La columna número: "+counter+" es nula";
				LOGGER.warn(errorMsg);
			}
			counter++;
		}
		return res;
	}
	
	/**
	 * Método que permite mopdificar el valor de una celda a partir de su identificador de columna
	 * y el número de fila
	 * @param file Manejador de ficheros excel
	 * @param value Nuevo valor a asignar
	 * @param attrKey Clave de la columna
	 * @param row Número de fila. Si el número de fila es menor que 0 o mayor que el máximo número
	 * de filas, entonces el número de fila equivale al número máximo de filas
	 * */
	public static void addCellValue(ExcelFile file, Object value, String attrKey, int row){
		if(row<0 || row > file.getSheet().getLastRowNum()){
			row = file.getSheet().getLastRowNum();
		}
		Row rowExcel = file.getSheet().getRow(row);
		if(rowExcel!=null && file.getHeader().getBeans().get(attrKey)!=null){
			Cell cellExcel = rowExcel.getCell(file.getHeader().getBeans().get(attrKey).getPosition());
			addValueToCell(cellExcel,value,file);
		}
	}
	
	/**
	 * Método que permite escribir un objeto como la última de las filas de una hoja excel
	 * @param file Fichero excel donde se escribirá la fila.
	 * @param row Fila a escribir
	 * @param headerStyle 
	 * */
	public static void addRowToFile(ExcelFile file, List<Object> row){
		int last = file.getSheet().getLastRowNum();
		Row newRow = file.getSheet().createRow(last+1);
		for (int i = 0; i<row.size(); i++) {
			Cell newCell = newRow.createCell(i);
			Object obj = row.get(i);
			addValueToCell(newCell,obj,file);
		}
	}

	/**
	 * Método que permite cambiar la cabecera de un manejador de ficheros excel
	 * @param file Manejador de ficheros excel
	 * @param header Nueva cabecera
	 * @param color Enumerado con el nuevo color de la cabecera
	 * @throws PluginDocumentationException 
	 * */
	public static void addHeader(ExcelFile file, HeaderBean header) throws PluginDocumentationException {
		
		try{
			//Se añade la cabecera
			file.setHeader(header);
			
			//Se crea la cabecera
			Row newRow = file.getSheet().getRow(HEADER_ROW_DEFAULT);
			if(newRow==null){
				newRow = file.getSheet().createRow(0);
			}
			
			//Se clona el estilo de la cabecera
			CellStyle clone = null;
			clone = file.getWorkbook().createCellStyle();
			clone.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND );
			clone.setFillForegroundColor(header.getIndexedColor().getIndex());
			
			//Se rellenan los datos
			for (String keyAttr: header.getBeans().keySet()) {
				int i = header.getBeans().get(keyAttr).getPosition();
				Cell newCell = newRow.createCell(i);
				
				//Se añade el estilo a la cabecera
				if(clone!=null){
					newCell.setCellStyle(clone);
				}
				
				//Se añade la cabecera al manejador
				String nameCell = header.getBeans().get(keyAttr).getExcelKey();
				newCell.setCellValue(nameCell);
			}
		}catch(Exception e){
			String errormsg = "Excecpión producida al añadir una cabecera";
			throw new PluginDocumentationException(
				errormsg,
				PluginDocumentationExceptionCode.ON_CREATE_HEADER,
				e
			);
		}
	}
	
	/**
	 * MÃ©todo que permite aÃ±adir una fila en un manejador excel a partir de un objeto plano Bean
	 * @param file Objeto manejador de ficheros excel
	 * @param obj Objeto con la informaciÃ³n de la fila
	 * @throws PluginDocumentationException 
	 * @throws SiaException 
	 * */
    public static void addBeanToRow(ExcelFile file, Serializable obj) throws PluginDocumentationException{
        List<Object> row = new ArrayList<Object>();
        for (String keyAttr: file.getHeader().getBeans().keySet()) {
        	int position = file.getHeader().getBeans().get(keyAttr).getPosition();
        	if(position>row.size()){
        		for(int i = row.size(); i < position; i++){
        			row.add(new Object());
        		}
        	}else{
        		if(position<row.size())
        			row.remove(position);
        	}
            row.add(position,getValueFromField(keyAttr,obj));
        }
        addRowToFile(file,row);
    }
    
	/**
	 * MÃ©todo que permite aÃ±adir un mapa como file de un manejador de ficheros
	 * @param file Manejador de ficheros excel
	 * @param obj Mapa con la informaciÃ³n de la fila
	 * */
	public static void addMapToRow(ExcelFile file, Map<String,Object> obj) {
        List<Object> row = new ArrayList<Object>(file.getHeader().getBeans().keySet().size());
        for (String keyAttr: file.getHeader().getBeans().keySet()) {
        	int position = file.getHeader().getBeans().get(keyAttr).getPosition();
        	if(position>row.size()){
        		for(int i = row.size(); i < position; i++){
        			row.add(new Object());
        		}
        		
        	}else{
        		if(position<row.size())
        			row.remove(position);
        	}
        	row.add(position,obj.get(keyAttr));
        }
        addRowToFile(file,row);
    }
    
    /**
     * Método que obtiene un valor a partir de un Atributo de un objeto
     * @param keyAttr Atributo del objeto.
     * @param obj Objeto con la información.
     * @return Devuelve el valor del objeto cuyo atributo contenido en obj coincide con la clave pasad por parámetro.
     * @throws PluginDocumentationException 
     * */
    public static final Object getValueFromField(String keyAttr, Serializable obj) throws PluginDocumentationException{
    	
    	try{
    	
	    	// Si los parámetros son nulos se devuelve nulo
	    	if(keyAttr==null || obj==null) return null;
	    	
	    	//Se determina si la clave es compuesta
	    	String[] keyAttrs = null;
	    	if(keyAttr.contains(IDENTIFIER_SIGN_ATTRS)){
	    		//Se hace split de la clave
	    		keyAttrs = keyAttr.split(IDENTIFIER_SIGN_ATTRS_SPLIT);
	    	}
	    	
	    	//Si la clave es compuesta
	    	if(keyAttrs!=null){
	    		Object actual = SerializationUtils.clone(obj);
	    		for (String thisKeyAttr:keyAttrs) {
	    			try{
	    				actual = getValueFromField(thisKeyAttr, (Serializable) actual);
	    			}catch(ClassCastException e){
	    				String errorMsg="El objeto de clase: "+actual.getClass()+" no es serializable."
	    						+ " No se puede obtener el valor de "+thisKeyAttr;
	    				throw new PluginDocumentationException(
	    					errorMsg,
							PluginDocumentationExceptionCode.ON_GET_VALUE_FROM_FIELD,
							e
						);
	    			}
				}
	    		return actual;
	    	//Si no es compuesta
	    	}else{
	    		
	    		//Se obtiene el field de forma recursiva
	    		Field f = getFieldRecursive(obj.getClass(),keyAttr);
	            if(f!=null){
	            	
	            	//Se obtiene le valor del field
	            	Object res = null;
	                boolean isAccesible = f.isAccessible();
	                f.setAccessible(true);
	                try {
	                    res = f.get(obj);
	                } catch (IllegalAccessException e) {
	                	String errorMsg="No se puede acceder al atributo: "+keyAttr+" del objeto de la clase "+obj.getClass();
	        			throw new PluginDocumentationException(
	    					errorMsg,
							PluginDocumentationExceptionCode.ON_GET_VALUE_FROM_FIELD,
							e
						);
	                }finally {
	                    f.setAccessible(isAccesible);
	                }
	                return res;
	            }
	    	}
    	}catch(PluginDocumentationException e){
    		throw e;
    	}catch(Exception e){
    		String errorMsg = "Excepción desconocida producida durante la obtención de un valor por field";
    		throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_GET_VALUE_FROM_FIELD_UNKNOWN,
				e
			);
    	}
        return null;
    }
	
	/**
	 * Método que permite reescribir el fichero excel
	 * @param file Objeto excel con los datos a reescribir
	 * @throws PluginDocumentationException 
	 * */
	public static void writeFile(ExcelFile file) throws PluginDocumentationException{
		writeFile(file, file.getPathFile());
	}
	
	/**
	 * Método que permite escribir un Objeto excel en la dirección indicada
	 * @param file Objeto excel con los datos a reescribir
	 * @param path Dirección y nombre del nuevo fichero sin extensión.
	 * @return Devuelve la dirección del fichero con su nombre y extensión
	 * @throws PluginDocumentationException 
	 * */
	public static String writeFile(ExcelFile file, String path) throws PluginDocumentationException{
		try {
			String realPath = path+"."+file.getType().getExtension();
			File fileMkdirs = new File(realPath);
			if(!fileMkdirs.getParentFile().exists()){
				fileMkdirs.getParentFile().mkdirs();
			}
			FileOutputStream out = new FileOutputStream(realPath);
            file.getWorkbook().write(out);
            out.close();
            return realPath;
        } catch (FileNotFoundException e) {
        	String errorMsg="El fichero que se intenta crear: "+path+" No es un fichero correcto o no puede ser abierto";
			throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_WRITE_FILE,
				e
			);
        } catch (IOException e) {
        	String errorMsg="Error de entrada salida en la escritura del fichero: "+path;
        	throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_WRITE_FILE,
				e
			);
        } catch(Exception e){
        	String errorMsg="Excepción desconocida al escribir un fichero excel";
        	throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_WRITE_FILE_UNKNOWN,
				e
			);
        }
	}
	
	/**
	 * Método que permite cerrar el fichero excel
	 * @param excelFile Elemento con la información del fichero
	 * */
	public static void closeExcel(ExcelFile excelFile) throws PluginDocumentationException{
		try {
			excelFile.getWorkbook().close();
		} catch (IOException e) {
			String errorMsg="No se pudo cerrar el manejador de ficheros";
			throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_CLOSE_XCEL,
				e
			);
		} catch(Exception e){
			String errorMsg="Excepción desconocida al cerrar un fichero excel";
			throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_CLOSE_XCEL_UNKNOWN,
				e
			);
		}
	}

	/**
	 * Método que permite la coneversión de un elemento numérico de Excell en una fecha
	 * @param date Fecha a formatear
	 * @param formatDate Formateador de la fecha. Este campo solo es necesario si la fecha es de formato cadena
	 * @throws ParseException En caso de que existe algún error con el parseo de la fecha en formato cadena
	 * */
	public static Date gateDateFromXMLNumeric(Object date, SimpleDateFormat formatDate) throws PluginDocumentationException{
		String errorMsg = "Excepción producida durante el parseo de un fecha excel a una fecha java";
		try {
			if(date instanceof String){
				if(((String) date).length()>0){
					
						return formatDate.parse(date.toString());
					
				}else{
					return null;
				}
			}else{
				return DateUtil.getJavaDate(((Number)date).doubleValue());
			}
		} catch (ParseException e) {
			throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_PARSE_DATE,
				e
			);
		} catch(Exception e){
			throw new PluginDocumentationException(
				errorMsg,
				PluginDocumentationExceptionCode.ON_PARSE_DATE_UNKNOWN,
				e
			);
		}
	}

	/**
	 * Método que permite ajustar automátciamente el tamaño de todas las columnas
	 * del fichero excel al máximo tamaño de todas las filas.
	 * @param excelFile Fichero excel al que se deben ajustar el tamño de las columnas
	 * @throws PluginDocumentationException 
	 * */
	public static void autoSizeColumns(ExcelFile excelFile) throws PluginDocumentationException {
		try{
			Row row = excelFile.getSheet().getRow(0);
			int size = row.getLastCellNum();
			for (int colNum = 0; colNum<size;colNum++) {
				excelFile.getSheet().autoSizeColumn(colNum,true);
			}
		}catch(Exception e){
			throw new PluginDocumentationException(
				"Excepción producida durante el ajuste automático del tamaño de las filas de un fichero excel",
				PluginDocumentationExceptionCode.ON_AUTOADJUST_EXCEL_ROWS,
				e
			);
		}
	}
    
    /**
     * Método recursivo que permite encontrar un Field de forma recursiva en una clase a partir de un
     * identificador de atributo
     * @param thisClass Clase actual en la que se busca el campo
     * @param keyAttr Atributo a buscar
     * @return Devuelve una instancia de {@link Field} correspondiente al atributo pasado por
     * parámetro. Si no se encuentra el atributo y la clase hereda de otra, se busca en su clase padre
     * hasta que se encuentre el resultado o no existan clases padres, en cuyo caso, devuelve null.
     * */
    private static Field getFieldRecursive(Class<?> thisClass, String keyAttr){
    	if(thisClass == null) return null;
    	else{
    		try {
    			return thisClass.getDeclaredField(keyAttr);
    		}catch(NoSuchFieldException e){
    			return getFieldRecursive(thisClass.getSuperclass(), keyAttr);
    		}
    	}
    }

	/**
	 * Método que permite añadir un valor a una celda
	 * @param newCell Celda a la que se desea añadir el valor
	 * @param obj Objeto a añadir como valro en la celda
	 * */
	private static void addValueToCell(Cell newCell, Object obj, ExcelFile excelFilePunt) {
		if(obj!=null){
			if(obj instanceof Date){
				newCell.setCellValue((Date)obj);
			}else if(obj instanceof Boolean){
            	newCell.setCellValue((Boolean)obj);
			}else if(Number.class.isAssignableFrom(obj.getClass())){
            	newCell.setCellValue(((Number)obj).doubleValue());
			}else if(obj instanceof RichTextString){
	           	newCell.setCellValue((RichTextString)obj);
			}else if(obj instanceof Boolean){
	           	newCell.setCellValue((Boolean)obj);
			}else if(Calendar.class.isAssignableFrom(obj.getClass())){
				newCell.setCellValue((Calendar)obj);
			}else{
				newCell.setCellValue(obj.toString());
				String newline = "\n";
				if(obj.toString().contains(newline)){
					CellStyle style = excelFilePunt.getWorkbook().createCellStyle();
				    style.setWrapText(true);
				    newCell.setCellStyle(style);
				}
			}
		}
	}
	
	/**
	 * Método que devuelve el valor de un celda
	 * @param cell Celda de la cual se quiere devolver su valor
	 * @return Devuelve un ojeto con el valor de la celda
	 * */
	private static Object getValueFromCell(Cell cell) {
        Object value = null;
        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                value = cell.getNumericCellValue();
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = null;
                break;
            default:
                //TODO Fórmulas
                value = null;
                break;
        }
        return value;
    }
}