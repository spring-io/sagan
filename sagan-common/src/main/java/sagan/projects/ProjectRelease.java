package sagan.projects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Embeddable
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectRelease implements Comparable<ProjectRelease> {

    private static final Pattern PRERELEASE_PATTERN = Pattern.compile("[A-Za-z0-9\\.\\-]+?(M|RC)\\d+");
    private static final String SNAPSHOT_SUFFIX = "SNAPSHOT";

    public enum ReleaseStatus {
        SNAPSHOT, PRERELEASE, GENERAL_AVAILABILITY;

        public static ReleaseStatus getFromVersion(String version) {
            Assert.notNull(version, "Version must not be null");
            if (version.endsWith(SNAPSHOT_SUFFIX)) {
                return SNAPSHOT;
            }
            if (PRERELEASE_PATTERN.matcher(version).matches()) {
                return PRERELEASE;
            }
            return GENERAL_AVAILABILITY;
        }
    }

    private String versionName;
    private ReleaseStatus releaseStatus;
    private boolean isCurrent;
    private String refDocUrl;
    private String refDocMultiUrl;
    private String apiDocUrl;
    private String groupId;
    private String artifactId;
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private ProjectRepository repository;

    public ProjectRelease() {
    }

    public ProjectRelease(String versionName, ReleaseStatus releaseStatus, boolean isCurrent, String refDocUrl,
                          String refDocMultiUrl, String apiDocUrl, String groupId, String artifactId) {
        setVersion(versionName);
        if (releaseStatus != null) {
            this.releaseStatus = releaseStatus;
        }
        this.isCurrent = isCurrent;
        this.refDocUrl = refDocUrl;
        this.refDocMultiUrl = refDocMultiUrl;
        this.apiDocUrl = apiDocUrl;
        this.groupId = groupId;
        this.artifactId = artifactId;
        repository = ProjectRepository.get(versionName, this.releaseStatus);
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public boolean isGeneralAvailability() {
        return releaseStatus == ReleaseStatus.GENERAL_AVAILABILITY;
    }

    public boolean isPreRelease() {
        return releaseStatus == ReleaseStatus.PRERELEASE;
    }

    public boolean isSnapshot() {
        return releaseStatus == ReleaseStatus.SNAPSHOT;
    }

    public ReleaseStatus getReleaseStatus() {
        return releaseStatus;
    }

    public String getVersion() {
        return versionName;
    }

    public String getVersionDisplayName() {
        return getVersionDisplayName(true);
    }

    public String getVersionDisplayName(boolean includePreReleaseDescription) {
        String versionNumber = versionName;
        String versionLabel = "";
        if (versionName.contains(".")) {
            versionNumber = versionName.substring(0, versionName.lastIndexOf("."));
            versionLabel = " " + versionName.substring(versionName.lastIndexOf(".") + 1);
            if (versionLabel.contains("SNAPSHOT") || versionLabel.equals(" RELEASE")) {
                versionLabel = "";
            }
        }
        return versionNumber + versionLabel;
    }

    public String getRefDocUrl() {
        return refDocUrl;
    }

    public boolean hasRefDocUrl() {
        return !refDocUrl.isEmpty();
    }

    public String getRefDocMultiUrl() {
        return refDocMultiUrl;
    }

    public boolean hasRefDocMultiUrl() {
        return !refDocMultiUrl.isEmpty();
    }

    public String getApiDocUrl() {
        return apiDocUrl;
    }

    public boolean hasApiDocUrl() {
        return !apiDocUrl.isEmpty();
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public ProjectRepository getRepository() {
        return repository;
    }

    public void setVersion(String versionName) {
        this.releaseStatus = ReleaseStatus.getFromVersion(versionName);
        repository = ProjectRepository.get(versionName, this.releaseStatus);
        this.versionName = versionName;
    }

    public void setReleaseStatus(ReleaseStatus releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public void setRefDocUrl(String refDocUrl) {
        this.refDocUrl = refDocUrl;
    }

    public void setRefDocMultiUrl(String refDocMultiUrl) {
        this.refDocMultiUrl = refDocMultiUrl;
    }

    public void setApiDocUrl(String apiDocUrl) {
        this.apiDocUrl = apiDocUrl;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setRepository(ProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public int compareTo(ProjectRelease other) {
        if (this.getVersion() == null || other.getVersion() == null) {
            return 0;
        }

        Pattern pattern = Pattern.compile("([0-9]+)");

        Matcher thisMatcher = pattern.matcher(this.getVersion());
        Matcher otherMatcher = pattern.matcher(other.getVersion());

        int versionDiff = 0;

        boolean thisFind = thisMatcher.find();
        boolean otherFind = otherMatcher.find();

        while (thisFind && otherFind) {
            int thisFoundVersion = Integer.parseInt(thisMatcher.group(0));
            int otherFoundVersion = Integer.parseInt(otherMatcher.group(0));
            versionDiff = otherFoundVersion - thisFoundVersion;

            if (versionDiff != 0) {
                return versionDiff;
            }

            thisFind = thisMatcher.find();
            otherFind = otherMatcher.find();
        }

        if (thisFind) {
            return -1;
        }

        if (otherFind) {
            return 1;
        }

        return versionDiff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof ProjectRelease))
            return false;

        ProjectRelease that = (ProjectRelease) o;

        if (!versionName.equals(that.versionName))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return versionName.hashCode();
    }

    @Override
    public String toString() {
        return "ProjectRelease{" + "versionName='" + versionName + '\'' + ", release=" + releaseStatus
                + ", refDocUrl='" + refDocUrl + '\'' + ", refDocMultiUrl='" + refDocMultiUrl + '\''
                + ", apiDocUrl='" + apiDocUrl + '\'' + '}';
    }

}
