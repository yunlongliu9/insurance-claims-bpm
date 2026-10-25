package org.example.insurance;

import org.cibseven.bpm.engine.delegate.DelegateExecution;
import org.cibseven.bpm.engine.delegate.JavaDelegate;
import java.time.LocalDate;

public class CoverageCheckDelegate implements JavaDelegate {

    private final CoverageService coverageService = new CoverageService();

    @Override
    public void execute(DelegateExecution execution) {
        LocalDate start = LocalDate.parse((String) execution.getVariable("policyStart"));
        LocalDate end = LocalDate.parse((String) execution.getVariable("policyEnd"));
        LocalDate treatment = LocalDate.parse((String) execution.getVariable("treatmentDate"));

        boolean valid = coverageService.isWithinCoveragePeriod(start, end, treatment);
        execution.setVariable("withinCoverage", valid);
    }
}