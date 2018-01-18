package es.home.properties.plugin.goals;

import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import es.home.properties.exception.PluginDocumentationException;
import es.home.properties.model.PropertiesPaths;

/**
 * Permite compilar propiedades con n entradas de propiedades y n salidas de propiedades de forma recursiva.
 * La salidas de ficheros de propiedades, mantendrán la estructura de carpetas del origen de datos
 * @author Administrador
 *
 */
@Mojo(name="docu-properties-inputsoutputs-write", requiresProject=true, aggregator=true)
public class DocuPropertiesInputsOutputsWriteProperties extends DocuPropertiesAbstractWriteProperties{
	
	/** Directorios donde ir a buscar los recursos  */
	@Parameter(required=true)
	private List<PropertiesPaths> propertiesPaths;
	
	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			
			if(propertiesPaths!=null && !propertiesPaths.isEmpty()){
			
				getLog().info("Recorriendo la lista de recursos de entrada y salida");
				for (PropertiesPaths propertiePath : propertiesPaths) {
					getConfiguration().setOutput(propertiePath.getOutput());
					getConfiguration().setInput(propertiePath.getInput());
					getConfiguration().setMaintainFolderStructure(propertiePath.isMaintainDirStructure());
					proccessPath(propertiePath.getInput());
				}
			}else{
				getLog().warn("No se incluyeron recursos de propiedades para procesar");
			}
		}catch (PluginDocumentationException e) {
			getLog().error(e.geti18Message(),e);
		}
	}
}
