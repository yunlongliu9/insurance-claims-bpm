package org.example.insurance;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;

import jakarta.inject.Inject;


public class ApplicantMessageRoute extends RouteBuilder {


    @Override
    public void configure() {

        // Producer: send to Artemis
        from("direct:createApplicant")
                .routeId("applicant-producer")
                .to("jms:queue:applicant.create");

        // Consumer: from Artemis receive applicant messages
        from("jms:queue:applicant.create")
                .routeId("applicant-consumer")
                .log("Received applicant: ${body}")
                .bean("applicantService", "saveApplicant");

        from("direct:renewApplicant")
                .routeId("applicant-renew-producer")
                .to("jms:queue:applicant.renew");

        from("jms:queue:applicant.renew")
                .routeId("applicant-renew-consumer")
                .bean("applicantService", "renewApplicant");
    }
}