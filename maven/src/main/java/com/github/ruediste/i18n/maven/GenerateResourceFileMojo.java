package com.github.ruediste.i18n.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Checks if the constraints satisfied by the modules are respected.
 */
@Mojo(name = "generate-resource-file", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class GenerateResourceFileMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
	private File outputDirectory;

	@Parameter(defaultValue = "${project.build.directory}/translations.properties", required = true)
	private File outputFile;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generating Resource File ...");
		getLog().warn("NOT YET IMPLEMENTED");
		getLog().info("Resource File generated");
	}

}
