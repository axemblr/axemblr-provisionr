package com.axemblr.provisionr.amazon;

import com.axemblr.provisionr.api.Provisionr;
import com.axemblr.provisionr.api.pool.Pool;
import com.axemblr.provisionr.api.pool.PoolBuilder;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AmazonProvisionrLiveTest {

    private ProcessEngine engine;
    private Provisionr provisionr;

    @Before
    public void setUp() {
        engine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
            .setJobExecutorActivate(true).setHistory("full").buildProcessEngine();
        engine.getRepositoryService().createDeployment()
            .addClasspathResource("OSGI-INF/activiti/amazon.bpmn20.xml").deploy();

        provisionr = new AmazonProvisionr(engine);
    }

    @After
    public void tearDown() {
        engine.close();
    }

    @Test
    public void testDeploymentRegisteredAsExpected() {
        List<Deployment> deployments = engine.getRepositoryService().createDeploymentQuery().list();
        assertThat(deployments).hasSize(1);
    }

    @Test
    public void testCreatePoolWithOneMachine() {
        Pool pool = new PoolBuilder().createPool();

        provisionr.createPool("j-12345", pool);

    }
}
