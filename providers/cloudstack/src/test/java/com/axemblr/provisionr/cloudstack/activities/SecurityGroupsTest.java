package com.axemblr.provisionr.cloudstack.activities;

import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;

public class SecurityGroupsTest {

    @Test
    public void testSecurityNameFromBusinessProcessKey() throws Exception {
        assertThat(SecurityGroups.formatSecurityGroupNameFromProcessBusinessKey("test")).isEqualTo("network-test");
    }
}
