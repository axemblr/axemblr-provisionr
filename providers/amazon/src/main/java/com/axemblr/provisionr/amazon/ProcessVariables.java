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
    public static final String CACHED_IMAGE_ID = "cached-image-id";

    /**
     * The reservation ID for a pool as String
     */
    public static final String RESERVATION_ID = "reservation-id";

    /**
     * String[] of instance IDs as returned by Amazon
     */
    public static final String INSTANCES = "instances";

}
