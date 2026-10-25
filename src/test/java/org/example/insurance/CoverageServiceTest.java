package org.example.insurance;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CoverageServiceTest {

    private final CoverageService service = new CoverageService();

    @Test
    void testCoveragePeriod() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);

        assertTrue(service.isWithinCoveragePeriod(
                start, end, LocalDate.of(2026, 6, 15)));

        assertFalse(service.isWithinCoveragePeriod(
                start, end, LocalDate.of(2025, 12, 31)));

        assertTrue(service.isWithinCoveragePeriod(
                start, end, start));

        assertTrue(service.isWithinCoveragePeriod(
                start, end, end));
    }
}