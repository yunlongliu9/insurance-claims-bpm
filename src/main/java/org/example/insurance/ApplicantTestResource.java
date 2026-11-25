package org.example.insurance;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.apache.camel.ProducerTemplate;

@Path("/test/applicant")
public class ApplicantTestResource {

    @Inject
    ProducerTemplate producerTemplate;

    @GET
    public String sendTestMessage() {

        producerTemplate.sendBody(
                "direct:createApplicant",
                "Test Applicant");

        return "Applicant message sent";
    }
}