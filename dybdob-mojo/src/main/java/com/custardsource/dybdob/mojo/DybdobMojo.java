package com.custardsource.dybdob.mojo;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.custardsource.dybdob.ProjectVersion;
import com.custardsource.dybdob.WarningRecord;
import com.custardsource.dybdob.detectors.CheckstyleDetector;
import com.custardsource.dybdob.detectors.CpdDetector;
import com.custardsource.dybdob.detectors.FindBugsDetector;
import com.custardsource.dybdob.detectors.JavacWarningDetector;
import com.custardsource.dybdob.detectors.PmdDetector;
import com.custardsource.dybdob.detectors.WarningDetector;
import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

public abstract class DybdobMojo extends AbstractMojo {
    private static final List<WarningDetector> KNOWN_DETECTORS = ImmutableList.<WarningDetector>of(
            new JavacWarningDetector(), new CheckstyleDetector(), new CpdDetector(), new PmdDetector(),
            new FindBugsDetector());

    /**
     * @parameter default-value="${project}"
     * @readonly
     * */
    org.apache.maven.project.MavenProject mavenProject;

    private ProjectVersion projectVersion;

    /**
     * Which detectors are enabled
     *
     * @parameter
     * @required
     */
    private List<Detector> detectors;
    
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (!mavenProject.getPackaging().equals("jar")) {
            getLog().info("Skipping warning count for non-jar packaging type " + mavenProject.getPackaging());
            return;
        }

        projectVersion = DybdobMojoUtils.buildProjectVersionFrom(mavenProject);
        initialize();
        checkWarningCounts();
    }

    private void checkWarningCounts() throws MojoExecutionException {
        for (Detector detector : detectors) {
            getLog().debug("Running detector " + detector);
            checkWarningCountForDetector(detector);
        }
    }

    private void checkWarningCountForDetector(Detector detector) throws MojoExecutionException {
        File logFile = detector.logFile();
        WarningDetector warningDetector = getDetectorById(detector.id());

        Collection<WarningRecord> records = warningDetector.getRecords(DybdobMojoUtils.buildProjectVersionFrom(mavenProject), logFile);

        for (WarningRecord record : records) {
            checkSingleRecord(record, logFile, warningDetector);
        }
    }

    protected abstract void checkSingleRecord(WarningRecord record, File logFile, WarningDetector warningDetector) throws MojoExecutionException;

    private WarningDetector getDetectorById(String id) throws MojoExecutionException {
        for (WarningDetector detector : KNOWN_DETECTORS) {
            if (detector.getId().equals(id)) {
                return detector;
            }
        }
        throw new MojoExecutionException("Unknown detector id '" + id + "'; check your configuration");
    }
    
    protected abstract void initialize() throws MojoExecutionException;
}