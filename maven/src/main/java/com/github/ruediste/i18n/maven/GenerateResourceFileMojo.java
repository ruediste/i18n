package com.github.ruediste.i18n.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.AbstractScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.label.LabelUtil;
import com.google.common.base.Charsets;

/**
 * Generate a properties file for all defined labels and messages
 */
@Mojo(name = "generate-resource-file", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class GenerateResourceFileMojo extends AbstractMojo {

    private final class AllTypesScanner extends AbstractScanner {
        @SuppressWarnings("unchecked")
        @Override
        public void scan(Object cls) {
            String className = getMetadataAdapter().getClassName(cls);
            getStore().put(className, className);
        }
    }

    /**
     * Output directory of the build
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, readonly = true)
    private File outputDirectory;

    /**
     * Output file
     */
    @Parameter(defaultValue = "${project.build.directory}/translations.properties", required = true)
    private File outputFile;

    /**
     * Base package to scan
     */
    @Parameter(required = true)
    private String basePackage;

    @Component
    private MavenProject project;

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating Resource File " + outputFile + " ...");
        try {
            // get list of all URLs on the compile classpath
            List<URL> projectClasspathList = new ArrayList<URL>();
            for (String element : (List<String>) project
                    .getCompileClasspathElements()) {
                try {
                    getLog().debug(
                            "Classpath URL: "
                                    + new File(element).toURI().toURL());

                    projectClasspathList.add(new File(element).toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new MojoExecutionException(element
                            + " is an invalid classpath element", e);
                }
            }

            // create classlader
            URLClassLoader loader = new URLClassLoader(
                    projectClasspathList.toArray(new URL[] {}),
                    LabelUtil.class.getClassLoader());

            // scan classes
            AllTypesScanner scanner = new AllTypesScanner();
            new Reflections(new ConfigurationBuilder()
                    .setScanners(scanner)
                    .addUrls(projectClasspathList)
                    .addClassLoader(loader)
                    .filterInputsBy(
                            new FilterBuilder().includePackage(basePackage)));

            LabelUtil util = new LabelUtil(null);

            // collect labels
            ArrayList<TranslatedString> labels = new ArrayList<>();
            for (String clsName : scanner.getStore().keySet()) {
                Class<?> cls;
                try {
                    cls = loader.loadClass(clsName);
                    getLog().debug("Processing " + cls);
                    List<TranslatedString> labelsDefinedOn = util
                            .getLabelsDefinedOn(cls);
                    getLog().debug("defined lables " + labelsDefinedOn);
                    labels.addAll(labelsDefinedOn);
                } catch (ClassNotFoundException e) {
                    getLog().warn("Error while loading " + clsName);
                }
            }

            // write labels
            try (OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(outputFile), Charsets.UTF_8)) {
                util.toProperties(labels).store(out, "Labels");
            } catch (IOException e) {
                throw new MojoExecutionException("error while writing output",
                        e);
            }
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Dependency resolution failed", e);
        }

        getLog().info("Resource File generated");
    }
}
