package es.home.properties.plugin.goals;

import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import es.home.properties.exception.SiaPluginDocumentationException;

@Mojo(name="write-properties", requiresProject=true)
public class DocuPropertiesWriteProperties extends DocuPropertiesAbstractWriteProperties{
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try{
			// Se establece la configuración propia del Goal
			if(getDocumenter()!=null){
				List<Resource> resources = getDocumenter().getConfiguration().getProject().getResources();
				
				// Leyendo los recursos por defecto
				if(resources!=null){
					getLog().info("Recorriendo los recursos del proyecto");
					for (Resource resource:resources) {
						String resourcePath = resource.getDirectory();
						proccessPath(resourcePath);
					}
				}
			}
		}catch(SiaPluginDocumentationException e){
			getLog().error(e.geti18Message(),e);
		}
	}
}
