package es.home.properties.plugin.goals;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import es.home.properties.exception.PluginDocumentationException;

/**
 * Permite escribir un fichero de propiedades en función de un entorno para uno o n directorios.
 * */
@Mojo(name="docu-properties-input-write", requiresProject=true, aggregator=true)
public class DocuPropertiesInputWriteProperties extends DocuPropertiesAbstractWriteProperties{

	/** Directorios donde ir a buscar los recursos  */
	@Parameter(required=true)
	private String[] inputs;
	
	/** Atributo de sólo lectura que permite obtener el directorio output por defecto */
	@Parameter(defaultValue="${project.build.directory}/docu-properties", readonly=true)
	private String defaultOutput;
	
	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Recorriendo los recursos inputs");
			boolean isOutputDefault = defaultOutput.equals(getConfiguration().getOutput());
			getLog().debug("El directorio por defecto "+((isOutputDefault)?"no ":"")+"ha cambiado");
			for (String input : inputs) {
				if(isOutputDefault){
					getConfiguration().setOutput(input+"/docu-properties");
				}
			
				proccessPath(input);
			}
		}catch (PluginDocumentationException e) {
			getLog().error(e.geti18Message(),e);
		}
	}
}
