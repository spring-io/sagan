package sagan;

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
     * application is running locally, i.e. on a developer machine as opposed to running
     * on {@link #CLOUDFOUNDRY} and should expect to find data sources, etc in-memory as
     * opposed to finding them as Cloud Foundry services. This profile constant is named
     * "STANDALONE" to clearly communicate its role, but its value is actually "default",
     * as this is the "reserved default profile name" in Spring. This means that
     * "STANDALONE" will always be treated as the default profile without requiring any
     * code to programmatically activate it. This makes running integration tests that
     * expect in-memory resources simple to set up.
     *
     * @see sagan.StandaloneDatabaseConfig
     * @see org.springframework.core.env.AbstractEnvironment#RESERVED_DEFAULT_PROFILE_NAME
     */
    public static final String STANDALONE = "default";

    /**
     * Profile to switch on collecting Spring Boot Actuator metrics inside Redis
     * TODO: When confirmed, replace this with CLOUDFOUNDRY
     */
    public static final String REDISMETRICS = "redis-metrics";
}
