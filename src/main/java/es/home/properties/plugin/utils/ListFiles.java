package es.home.properties.plugin.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.plugin.goals.DocuPropertiesAbstractMojo;

/**
 * Clase necesaria para recorrer los recursos
 * */
public class ListFiles extends SimpleFileVisitor<Path> {
	
	/** LOGGER del Mojo */
	private Log LOGGER;
	
	/** Puntero al mojo de la aplicación */
	private DocuPropertiesAbstractMojo mojoPlugin;

	/**
	 * Constructor
	 * @param logger LOGGER del Mojo
	 * @param docuPropertiesAbstractMojo Puntero al mojo de la aplicación
	 * */
	public ListFiles(Log logger, DocuPropertiesAbstractMojo docuPropertiesAbstractMojo) {
		this.LOGGER = logger;
		this.mojoPlugin = docuPropertiesAbstractMojo;
	}

	/** {@inheritDoc} */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
		try{
			LOGGER.debug("Procesando el fichero:" + file.toString());
			
			// Si no se debe excluri el fichero se ejecuta la compilación
			if(isNoExcluded(file.toFile().getAbsolutePath().toString(), mojoPlugin.getDocumenter().getConfiguration().getExcludes())){
				mojoPlugin.proccesFile(file);	
			}
		}catch(SecurityException se){
			LOGGER.error("Excepción producida por la seguridad del fichero: "+file.toString(),se);
		} catch (PluginDocumentationException e) {
			LOGGER.error("Excepción interna: "+file.toString(),e);
		}
		return FileVisitResult.CONTINUE;
	}
	
	/**
	 * Determina si la cadena pasada por parámetro tiene o no coincidencia con las expresiones regulares pasadas en las exclusiones
	 * @param resourcePath Cadena a comrpobar
	 * @param excludes Expresiones regulares que marcan las expresiones
	 * @return Devuelve true si no existen coincidencias, en caso contrario, devuelve false
	 */
	private boolean isNoExcluded(String resourcePath, String[] excludes){
		for (String exclude : excludes) {
			try{
				if(resourcePath.replace("\\", "/").matches(exclude.replace("\\", "/"))){
					LOGGER.info("Se exluye el fichero: "+resourcePath+" por al limitación: "+exclude);
					return false;
				}
			}catch(PatternSyntaxException e){
				LOGGER.error("Sintáxis incorrecta para el patrón de exclusión: "+exclude);
				throw e;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public FileVisitResult postVisitDirectory(Path directory, IOException e)
	  throws IOException {
		LOGGER.debug("Terminando con el directorio: "+ directory.getFileName());
		return FileVisitResult.CONTINUE;
	}

	/** {@inheritDoc} */
	@Override
	public FileVisitResult preVisitDirectory(Path directory,
	  BasicFileAttributes attributes) throws IOException {
		LOGGER.debug("Entrando al directorio: "+ directory.getFileName());
		return FileVisitResult.CONTINUE;
	}

	/** {@inheritDoc} */
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
	  throws IOException {
		LOGGER.debug("Ocurrió un error visitando el directorio");
		return super.visitFileFailed(file, exc);
	}
}
