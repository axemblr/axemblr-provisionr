package com.axemblr.provisionr.amazon;

public class ProcessVariables {

    private ProcessVariables() {
        /* singleton */
    }

    /**
     * Pool description object
     *
     * @see com.axemblr.provisionr.api.pool.Pool
     */
    public static final String POOL = "pool";

    /**
     * ID of the cached base image
     */
    public static final String CACHED_IMAGE_ID = "cachedImageId";

    /**
     * The reservation ID for a pool as String
     *
     * @see com.axemblr.provisionr.amazon.activities.RunOnDemandInstances
     */
    public static final String RESERVATION_ID = "reservationId";

    /**
     * String[] of instance IDs as returned by Amazon
     *
     * @see com.axemblr.provisionr.amazon.activities.RunOnDemandInstances
     */
    public static final String INSTANCES = "instanceIds";

    /**
     * Are all started instances running?
     *
     * @see com.axemblr.provisionr.amazon.activities.CheckAllInstancesAreRunning
     */
    public static final String ALL_INSTANCES_RUNNING = "allInstancesRunning";

    /**
     * Are all instances marked as terminated?
     *
     * @see com.axemblr.provisionr.amazon.activities.CheckAllInstancesAreTerminated
     */
    public static final String ALL_INSTANCES_TERMINATED = "allInstancesTerminated";

}
