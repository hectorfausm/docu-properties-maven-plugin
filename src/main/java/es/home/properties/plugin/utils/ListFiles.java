package es.home.properties.plugin.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.maven.plugin.logging.Log;

import es.home.properties.exception.SiaPluginDocumentationException;
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
			mojoPlugin.proccesFile(file);	
		}catch(SecurityException se){
			LOGGER.error("Excepción producida por la seguridad del fichero: "+file.toString(),se);
		} catch (SiaPluginDocumentationException e) {
			LOGGER.error("Excepción interna: "+file.toString(),e);
		}
		return FileVisitResult.CONTINUE;
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
