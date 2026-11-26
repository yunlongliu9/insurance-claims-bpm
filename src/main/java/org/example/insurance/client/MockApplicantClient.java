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

        LocalDate dateOfBirth = LocalDate.of(1996, 5, 20);
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

        String json = """
                {
                  "messageName": "applicationSubmitted",
                  "processVariables": {
                    "name": {"value": "Max Mustermann", "type": "String"},
                    "gender": {"value": "male", "type": "String"},
                    "phoneNumber": {"value": "+491701234567", "type": "String"},
                    "dateOfBirth": {"value": "%s", "type": "String"},
                    "residenceGermany": {"value": true, "type": "Boolean"},
                    "age": {"value": %d, "type": "Integer"},
                    "employmentStatus": {"value": "EMPLOYED", "type": "String"},
                    "grossAnnualIncome": {"value": 55000, "type": "Integer"},
                    "dentalTreatmentFlag": {"value": false, "type": "Boolean"},
                    "chronicConditionFlag": {"value": false, "type": "Boolean"},
                    "inpatientTreatmentFlag": {"value": false, "type": "Boolean"},
                    "psychotherapyFlag": {"value": false, "type": "Boolean"},
                    "spinalHistory": {"value": false, "type": "Boolean"},
                    "allergyFlag": {"value": false, "type": "Boolean"},
                    "medicationOngoing": {"value": false, "type": "Boolean"},
                    "bmi": {"value": 23.1, "type": "Double"},
                    "tariffBenefitLevel": {"value": "STANDARD", "type": "String"},
                    "priorAdverseDecision": {"value": false, "type": "Boolean"}
                  }
                }
                """.formatted(dateOfBirth, age);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/engine-rest/message"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP Status: " + response.statusCode());
        System.out.println("Response: " + response.body());
    }
}