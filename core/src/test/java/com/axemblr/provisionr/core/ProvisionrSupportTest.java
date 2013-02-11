package com.axemblr.provisionr.core;

import static org.fest.assertions.api.Assertions.assertThat;

import com.axemblr.provisionr.api.pool.Pool;

import org.junit.Test;

public class ProvisionrSupportTest {

    @Test
    public void testConvertTimeout() {
        ProvisionrSupport provisionr = new ProvisionrSupportTestable();
        assertThat(provisionr.convertTimeoutToISO8601TimeDuration(600)).isEqualTo("PT10M");
        assertThat(provisionr.convertTimeoutToISO8601TimeDuration(601)).isEqualTo("PT601S");
        assertThat(provisionr.convertTimeoutToISO8601TimeDuration(300)).isEqualTo("PT5M");
        assertThat(provisionr.convertTimeoutToISO8601TimeDuration(42)).isEqualTo("PT42S");
    }
}

class ProvisionrSupportTestable extends ProvisionrSupport {
    @Override
    public String getId() {
        return null;
    }
    @Override
    public String startPoolManagementProcess(String businessKey, Pool pool) {
        return null;
    }
    @Override
    public void destroyPool(String businessKey) {}
};
