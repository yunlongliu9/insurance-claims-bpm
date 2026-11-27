package org.example.insurance.server;

import org.apache.camel.ProducerTemplate;
import org.example.insurance.ApplicantService;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
@Path("/applicants")
public class ApplicantController {
     @Inject
    ProducerTemplate producerTemplate;

    @Inject
    ApplicantService applicantService;

    @GET
    @Path("/exists")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean applicantExists(
            @QueryParam("name") String name,
            @QueryParam("dateOfBirth") String dateOfBirth,
            @QueryParam("identityDocumentNumber") String identityDocumentNumber,
            @QueryParam("identityDocumentCountry") String identityDocumentCountry) throws Exception {
        return applicantService.applicantExists(
                name, dateOfBirth,
                identityDocumentNumber, identityDocumentCountry);
    }

    @POST
    public void createApplicant(String body) {
        producerTemplate.sendBody(
            "direct:createApplicant",
            body
        );
    }

    @POST
    @Path("/renew")
    public void renewApplicant(String body) {
        producerTemplate.sendBody(
                "direct:renewApplicant",
                body);
    }
}
