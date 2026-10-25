package org.example.insurance;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.cibseven.bpm.engine.ProcessEngine;
import org.cibseven.bpm.engine.ProcessEngineConfiguration;
import org.junit.jupiter.api.Test;

class BpmnEngineTest {

    @Test
    void testBpmnExecution() {
        ProcessEngine engine = ProcessEngineConfiguration
            .createStandaloneInMemProcessEngineConfiguration()
            .setDatabaseSchemaUpdate("true")
            .buildProcessEngine();

        try {
            engine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("bpmn/coverage-check.bpmn")
                .deploy();

            var instance = engine.getRuntimeService()
                    .startProcessInstanceByKey(
                            "coverageCheck",
                            java.util.Map.of(
                                    "policyStart", "2026-01-01",
                                    "policyEnd", "2026-12-31",
                                    "treatmentDate", "2026-06-15"));

            assertTrue(instance.isEnded());
        } finally {
            engine.close();
        }
    }

    @Test
    void testEngineStartup() {
        ProcessEngine engine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .setDatabaseSchemaUpdate("true")
                .buildProcessEngine();

        try {
            assertNotNull(engine);
            assertNotNull(engine.getRuntimeService());
        } finally {
            engine.close();
        }
    }
}