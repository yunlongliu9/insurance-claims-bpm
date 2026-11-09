package org.example.insurance.server;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;

@ApplicationScoped
public class CoverageService {

    public boolean isWithinCoveragePeriod(
            LocalDate policyStart,
            LocalDate policyEnd,
            LocalDate treatmentDate) {

        if (policyStart == null || policyEnd == null ||
                treatmentDate == null || policyEnd.isBefore(policyStart)) {
            return false;
        }

        return !treatmentDate.isBefore(policyStart)
                && !treatmentDate.isAfter(policyEnd);
    }
}