package com.axemblr.provisionr.amazon.core;

import com.amazonaws.services.ec2.AmazonEC2;
import com.axemblr.provisionr.api.provider.Provider;
import com.google.common.cache.LoadingCache;

/**
 * Marker interface only used to hide generic type arguments
 * from Apache Aries and make Blueprint DI work as expected
 */
public interface ProviderClientCache extends LoadingCache<Provider, AmazonEC2> {
}
