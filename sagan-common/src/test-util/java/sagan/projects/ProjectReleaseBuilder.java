package sagan.projects;

public class ProjectReleaseBuilder {
    private String versionName = "";
    private ProjectRelease.ReleaseStatus releaseStatus = null;
    private boolean current = false;
    private String refDocMultiUrl = "";
    private String refDocUrl = "";
    private String apiDocUrl = "";
    private String groupId = "";
    private String artifactId = "";

    public ProjectReleaseBuilder versionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public ProjectReleaseBuilder releaseStatus(ProjectRelease.ReleaseStatus releaseStatus) {
        this.releaseStatus = releaseStatus;
        return this;
    }

    public ProjectReleaseBuilder current(boolean current) {
        this.current = current;
        return this;
    }

    public ProjectReleaseBuilder refDocUrl(String refDocUrl) {
        this.refDocUrl = refDocUrl;
        return this;
    }

    public ProjectReleaseBuilder refDocMultiUrl(String refDocMultiUrl) {
        this.refDocMultiUrl = refDocMultiUrl;
        return this;
    }

    public ProjectReleaseBuilder apiDocUrl(String apiDocUrl) {
        this.apiDocUrl = apiDocUrl;
        return this;
    }

    public ProjectReleaseBuilder groupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public ProjectReleaseBuilder artifactId(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public ProjectRelease build() {
        return new ProjectRelease(versionName, releaseStatus, current, refDocUrl, refDocMultiUrl, apiDocUrl, groupId, artifactId);
    }
}
