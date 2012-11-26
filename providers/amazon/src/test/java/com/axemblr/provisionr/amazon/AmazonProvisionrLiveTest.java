package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.ProvisionrLiveTest;
import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.pool.PoolBuilder;
import com.axemblr.provisionr.api.provider.Provider;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AmazonProvisionrLiveTest extends ProvisionrLiveTest {

    public static Logger LOG = Logger.getLogger(AmazonProvisionr.class.getCanonicalName());

    private ProcessEngine engine;

    private Provisionr provisionr;
    private Provider provider;

    @Before
    public void setUp() {
        provider = loadProviderWithOptionsOrSkipTests(AmazonProvisionr.ID, "region");

        engine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
            .setJobExecutorActivate(true).setHistory("full").buildProcessEngine();
        engine.getRepositoryService().createDeployment()
            .addClasspathResource("OSGI-INF/activiti/amazon.bpmn20.xml").deploy();

        provisionr = new AmazonProvisionr(engine);
    }

    @After
    public void tearDown() {
        if (engine != null) {
            engine.close();
        }
    }

    @Test
    public void testDeploymentRegisteredAsExpected() {
        List<Deployment> deployments = engine.getRepositoryService().createDeploymentQuery().list();
        assertThat(deployments).hasSize(1);
    }

    @Test
    public void testCreatePoolWithOneMachine() {
        Pool pool = new PoolBuilder().provider(provider).createPool();

        final String jobId = "j-" + UUID.randomUUID().toString();
        provisionr.startCreatePoolProcess(jobId, pool);

    }
}
