package sagan;

import sagan.search.service.SearchService;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Spring profiles under which a {@link SaganApplication} can be run. If no profiles are
 * specified, the {@link #STANDALONE} profile will be implicitly activated, making
 * available for example an in-memory database as opposed to expecting a full-blown
 * PostgreSQL instance to be available. This arrangement reflects the assumption that most
 * users of this reference application will want to clone the repo and run the app locally
 * before deploying to Cloud Foundry.
 *
 * Mutual exclusivity and implicit activation of profiles as described below is enforced
 * by {@link sagan.SaganApplication} {@code configureProfiles} method.
 */
public final class SaganProfiles {
    /**
     * When active, indicates that the application is being deployed to the "staging"
     * space of a Cloud Foundry instance. Implicitly activates {@link #CLOUDFOUNDRY}
     * profile, and is mutually exclusive with {@link #PRODUCTION} and {@link #STANDALONE}
     * profiles.
     */
    public static final String STAGING = "staging";

    /**
     * When active, indicates that the application is being deployed to the "production"
     * space of a Cloud Foundry instance. Implicitly activates {@link #CLOUDFOUNDRY}
     * profile, and is mutually exclusive with {@link #STAGING} and {@link #STANDALONE}
     * profiles.
     */
    public static final String PRODUCTION = "production";

    /**
     * Implicitly activated when either {@link #PRODUCTION} or {@link #STAGING} profiles
     * are active, this profile indicates that the application is running on Cloud Foundry
     * as opposed to running {@link #STANDALONE}, and should expect to find data sources,
     * etc as Cloud Foundry services, as opposed to finding them in-memory.
     *
     * @see sagan.CloudFoundryDatabaseConfig
     */
    public static final String CLOUDFOUNDRY = "cloudfoundry";

    /**
     * The default profile for any {@link SaganApplication}. Indicates that the
     * application is running locally, i.e. on a developer machine, or on any server
     * in standalone mode. All services are either registered automatically by the application
     * (e.g. in-memory datasources) or should be provided to the application by environment
     * variables or configuration values.
     * As opposed to {@link #CLOUDFOUNDRY}, the application won't try to detect Marketplace
     * services provided by the PaaS environment.
     * This profile is automatically added to the environment if neither {@link #PRODUCTION}
     * nor {@link #STAGING} are activated.
     *
     * @see sagan.StandaloneDatabaseConfig
     */
    public static final String STANDALONE = "standalone";

    /**
     * A profile that activates the internal {@link SearchService} based on Jest.
     *
     */
    public static final String JEST = "jest";
}
