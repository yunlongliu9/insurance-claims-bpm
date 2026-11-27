
package org.example.insurance.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Period;

public class MockApplicantClient {

    public static int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        send(client, "Test Standard", LocalDate.of(1996, 5, 20),
                false, 23.1);

        send(client, "Test Chronic", LocalDate.of(1995, 5, 20),
                true, 23.1);

        send(client, "Test Age", LocalDate.of(1970, 5, 20),
                false, 23.1);

        send(client, "Test BMI", LocalDate.of(1994, 5, 20),
                false, 32.0);
    }

    private static void send(
            HttpClient client,
            String name,
            LocalDate dateOfBirth,
            boolean chronicCondition,
            double bmi) throws Exception {

        int age = calculateAge(dateOfBirth);

        String json = """
                {
                  "messageName": "applicationSubmitted",
                  "processVariables": {
                    "name": {"value": "%s", "type": "String"},
                    "gender": {"value": "male", "type": "String"},
                    "phoneNumber": {"value": "+491701234567", "type": "String"},
                    "dateOfBirth": {"value": "%s", "type": "String"},
                    "residenceGermany": {"value": true, "type": "Boolean"},
                    "age": {"value": %d, "type": "Integer"},
                    "employmentStatus": {"value": "EMPLOYED", "type": "String"},
                    "grossAnnualIncome": {"value": 55000, "type": "Integer"},

                    "dentalTreatmentFlag": {"value": false, "type": "Boolean"},
                    "chronicConditionFlag": {"value": %b, "type": "Boolean"},
                    "inpatientTreatmentFlag": {"value": false, "type": "Boolean"},
                    "psychotherapyFlag": {"value": false, "type": "Boolean"},
                    "spinalHistory": {"value": false, "type": "Boolean"},
                    "allergyFlag": {"value": false, "type": "Boolean"},
                    "medicationOngoing": {"value": false, "type": "Boolean"},

                    "bmi": {"value": %s, "type": "Double"},
                    "tariffBenefitLevel": {"value": "STANDARD", "type": "String"},
                    "priorAdverseDecision": {"value": false, "type": "Boolean"},

                    "chronicCondition": {"value": %b, "type": "Boolean"},
                    "inpatientTreatment": {"value": false, "type": "Boolean"},
                    "psychotherapy": {"value": false, "type": "Boolean"},
                    "outpatientTreatment": {"value": false, "type": "Boolean"},
                    "dentalTreatment": {"value": false, "type": "Boolean"},
                    "deductible": {"value": 1000, "type": "Integer"},
                    "identityDocumentNumber": {"value": "TEST123456", "type": "String"},
        "identityDocumentCountry": {"value": "DE", "type": "String"},
                  }
                }
                """.formatted(
                name,
                dateOfBirth,
                age,
                chronicCondition,
                bmi,
                chronicCondition);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/engine-rest/message"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println("\n==============================");
        System.out.println("Applicant: " + name);
        System.out.println("Age: " + age);
        System.out.println("Chronic condition: " + chronicCondition);
        System.out.println("BMI: " + bmi);
        System.out.println("HTTP Status: " + response.statusCode());
        System.out.println("Response: " + response.body());
    }
}
