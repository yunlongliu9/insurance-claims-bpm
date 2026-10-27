package org.example.insurance;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.annotation.PreDestroy;
import org.cibseven.bpm.engine.ProcessEngine;
import org.cibseven.bpm.engine.ProcessEngineConfiguration;

//create CIB seven BPMN engine，deploy our BPMN file，give it to Quarkus to begin our service.
@ApplicationScoped
public class ProcessEngineProducer {

    private ProcessEngine engine;

    @Produces
    @ApplicationScoped
    public ProcessEngine produceEngine() {
        engine = ProcessEngineConfiguration
                .createStandaloneProcessEngineConfiguration()
                .setJdbcDriver("org.postgresql.Driver")
                .setJdbcUrl("jdbc:postgresql://postgres:5432/insurance")
                .setJdbcUsername("insurance")
                .setJdbcPassword("insurance_dev")
                .setDatabaseSchemaUpdate("true")
                .buildProcessEngine();

        engine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("bpmn/coverage-check.bpmn")
                .deploy();

        return engine;
    }

    @PreDestroy
    void shutdown() {
        if (engine != null) {
            engine.close();
        }
    }
}