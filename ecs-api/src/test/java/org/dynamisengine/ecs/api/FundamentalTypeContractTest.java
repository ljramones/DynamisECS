package org.dynamisengine.ecs.api;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FundamentalTypeContractTest {

    private static final List<String> FORBIDDEN_FILES = List.of(
            "EntityId.java",
            "Vector3f.java",
            "Matrix4f.java",
            "Transformf.java"
    );

    @Test
    void repositoryMustNotRedefineFundamentalTypes() throws IOException {
        Path root = findRepoRoot();

        Set<String> discovered = Files.walk(root)
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toSet());

        List<String> found = FORBIDDEN_FILES.stream()
                .filter(discovered::contains)
                .toList();

        assertTrue(found.isEmpty(), "Forbidden fundamental types found in repo: " + found);
    }

    private static Path findRepoRoot() {
        Path current = Paths.get("").toAbsolutePath();
        while (current != null) {
            if (Files.exists(current.resolve(".git"))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Unable to locate repository root containing .git");
    }
}
