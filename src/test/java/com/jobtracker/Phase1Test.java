package com.jobtracker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;

/**
 * Phase 1 Verification Script (JUnit Version)
 * Checks project structure.
 */
public class Phase1Test {

    @Test
    public void verifyPhase1() {
        System.out.println("=== Smart Job Tracker: Phase 1 Java Verification ===");

        boolean structureOk = checkStructure();
        assertTrue(structureOk, "Project structure is incomplete!");
        System.out.println("\n[OK] Project structure verified.");
        System.out.println(
                "\n[INFO] Compilation check: PASSED (The test is running, so the project compiled successfully).");
        System.out.println("\n=== Phase 1 Verification PASSED ===");
    }

    private boolean checkStructure() {
        String basePath = "src/main/java/com/jobtracker";
        String[] requiredPaths = {
                basePath + "/config",
                basePath + "/controller",
                basePath + "/service/impl",
                basePath + "/repository",
                basePath + "/entity",
                basePath + "/dto",
                basePath + "/security",
                basePath + "/exception",
                basePath + "/util",
                basePath + "/service/PlaceholderService.java",
                basePath + "/JobTrackerApplication.java",
                basePath + "/exception/GlobalExceptionHandler.java",
                basePath + "/exception/ResourceNotFoundException.java",
                basePath + "/dto/ErrorResponseDTO.java",
                "src/main/resources/application.properties"
        };

        boolean allFound = true;
        for (String path : requiredPaths) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("[FOUND] " + path);
            } else {
                System.err.println("[MISSING] " + path);
                allFound = false;
            }
        }
        return allFound;
    }
}
