package org.example.insurance.server;

import org.example.insurance.client.CibSevenClient;
import org.example.insurance.server.CoverageService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Map;
import java.util.stream.Collectors;

@Path("/claims")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClaimController {

        @Inject
        @RestClient
        CibSevenClient client;

        @POST
        public Map<String, Object> submit(Map<String, Object> claim) {

                var variables = claim.entrySet().stream()
                                .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                entry -> Map.of(
                                                                "value", entry.getValue(),
                                                                "type", "String")));

                return client.startProcess(
                                "coverageCheck",
                                Map.of("variables", variables));
        }
}