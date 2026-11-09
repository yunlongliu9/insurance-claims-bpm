package org.example.insurance.client;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "cibseven")
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CibSevenClient {

    @POST
    @Path("/process-definition/key/{key}/start")
    Map<String, Object> startProcess(
            @PathParam("key") String key,
            Map<String, Object> body);


    //send fetchAndLock req to CIB seven，
    // get the  task with specific Topic and lock it to avoid other workers' handling
    @POST
    @Path("/external-task/fetchAndLock")
    List<Map<String, Object>> fetchAndLock(Map<String, Object> body);


    //External Task 3 phase：
    //1. fetchAndLock：Worker get the task
    //2. CoverageService：execute the date check
    //3. complete：Worker submit the result to CIB seven，continue to execute BPMN process
    @POST
    @Path("/external-task/{id}/complete")
    void completeTask(
            @PathParam("id") String id,
            Map<String, Object> body
    );
}

