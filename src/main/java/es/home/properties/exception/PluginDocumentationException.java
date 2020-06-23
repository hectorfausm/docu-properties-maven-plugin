package es.home.properties.exception;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Excepciones del plugin
 * @author hfaus
 * */
public class PluginDocumentationException extends Exception{
	/** Serial Version UID */
	private static final long serialVersionUID = 1L;
	
	/** Path del bundle de mensajes */
	private static final String bundlePath = "es.home.properties.i18n.messages";
	
	/** Bundle de las excepciones del plugin */
	private final ResourceBundle bundle = ResourceBundle.getBundle(
		bundlePath,
		Locale.getDefault()
	);
	
	/**
	 * Código del mensaje.
	 * */
	private PluginDocumentationExceptionCode code;
	
	/**
	 * Constructor
	 * @param msg
	 * @param code
	 * @param e
	 */
	public PluginDocumentationException(String msg, PluginDocumentationExceptionCode code, Throwable e) {
		super(msg, e);
		this.code = code;
	}

	/**
	 * Cosntructor
	 * @param msg
	 * @param code
	 */
	public PluginDocumentationException(String msg, PluginDocumentationExceptionCode code) {
		super(msg);
		this.code = code;
	}
	
	/**
	 * Constructor
	 * @param msg
	 */
	public PluginDocumentationException(String msg) {
		super(msg);
	}
	
	/**
	 * Constructor 
	 * @param code
	 */
	public PluginDocumentationException(PluginDocumentationExceptionCode code) {
		super();
		this.code = code;
	}
	
	/**
	 * Obtiene el mensaje asociado al codigo
	 * @return String
	 * */
	public String geti18Message() {
		return bundle.getString(getCode().getCode());
	}
	
	// ACCEDENTES
	public PluginDocumentationExceptionCode getCode() {
		return code;
	}
}
