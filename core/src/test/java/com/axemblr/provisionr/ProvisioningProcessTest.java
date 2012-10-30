package com.axemblr.provisionr;

import java.util.Map;
import java.util.UUID;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:activiti-memory-context.cfg.xml")
public class ProvisioningProcessTest {

	@Autowired
	private RuntimeService runtimeService;

	@Test
	public void testBuildAndRunASimpleProcess() {
		String jobId = "j-" + UUID.randomUUID();

		Map<String, Object> variables = Maps.newHashMap();
		variables.put("test", 1);

		ProcessInstance instance = runtimeService.startProcessInstanceByKey(
				"provisioning", jobId, variables);
	}

}
