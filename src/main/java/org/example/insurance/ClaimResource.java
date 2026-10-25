package org.example.insurance;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.cibseven.bpm.engine.ProcessEngine;

import java.util.Map;

@Path("/claims")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClaimResource {

    @Inject
    ProcessEngine engine;

    @POST
    public Map<String, Object> submit(Map<String, Object> claim) {
        var instance = engine.getRuntimeService()
                .startProcessInstanceByKey("coverageCheck", claim); // json in http as bpmn process var;

        var historicVariable = engine.getHistoryService()
                .createHistoricVariableInstanceQuery()
                .processInstanceId(instance.getId())
                .variableName("withinCoverage")
                .singleResult();

        return Map.of(
                "processInstanceId", instance.getId(),
                "withinCoverage", historicVariable.getValue());
    }
}