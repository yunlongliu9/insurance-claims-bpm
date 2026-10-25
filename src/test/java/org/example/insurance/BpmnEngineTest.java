package org.example.insurance;

import org.cibseven.bpm.engine.ProcessEngine;
import org.cibseven.bpm.engine.ProcessEngineConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BpmnEngineTest {

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