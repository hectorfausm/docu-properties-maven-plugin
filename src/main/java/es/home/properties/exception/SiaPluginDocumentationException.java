package es.home.properties.exception;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Excepciones del plugin
 * @author hfaus
 * */
public class SiaPluginDocumentationException extends Exception{
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
	private SiaPluginDocumentationExceptionCode code;
	
	/**
	 * Contructor
	 * */
	public SiaPluginDocumentationException(String msg, SiaPluginDocumentationExceptionCode code, Throwable e) {
		super(msg, e);
		this.code = code;
	}
	
	/**
	 * Contructor
	 * */
	public SiaPluginDocumentationException(String msg, SiaPluginDocumentationExceptionCode code) {
		super(msg);
		this.code = code;
	}
	
	/**
	 * Contructor
	 * */
	public SiaPluginDocumentationException(String msg) {
		super(msg);
	}
	
	/**
	 * Contructor
	 * */
	public SiaPluginDocumentationException(SiaPluginDocumentationExceptionCode code) {
		super();
		this.code = code;
	}
	
	/**
	 * Obtiene el mensaje asociado al codigo
	 * */
	public String geti18Message() {
		return bundle.getString(getCode().getCode());
	}
	
	// ACCEDENTES
	public SiaPluginDocumentationExceptionCode getCode() {
		return code;
	}
}
