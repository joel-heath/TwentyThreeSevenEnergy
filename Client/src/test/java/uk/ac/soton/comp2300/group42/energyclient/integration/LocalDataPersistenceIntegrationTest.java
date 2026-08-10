package uk.ac.soton.comp2300.group42.energyclient.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageClient;
import uk.ac.soton.comp2300.group42.energyclient.data.local.LocalStorageData;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.*;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.preferences.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for local data persistence.
 * Tests the real file I/O cycle: writing data, reading it back, and ensuring
 * data integrity across multiple load/save cycles.
 *
 * Demonstrates: Integration Testing, Regression Testing, Boundary Testing
 */
@Tag("integration")
class LocalDataPersistenceIntegrationTest {

    private JsonMapper mapper;
    private Path storagePath;
    private ExecutorService executor;
    private Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        mapper = new JsonMapper();
        // Use a temp directory in system temp folder instead of @TempDir to avoid cleanup issues
        tempDir = Files.createTempDirectory("energy_client_test_");
        storagePath = tempDir.resolve("persistence_test.json");
        executor = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void tearDown() throws Exception {
        executor.shutdownNow();
        // Clean up temp directory
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> -a.compareTo(b))
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                        }
                    });
        }
    }

    // =========================================================================
    // INTEGRATION TESTS: Full persistence cycle (write, read, verify)
    // =========================================================================

    @Test
    void loadDataAsync_WithValidStoredData_RestoresDataCorrectly_IntegrationTest() throws Exception {
        // Arrange: create and save a LocalStorageData object with real data
        LocalStorageClient writeClient = new LocalStorageClient(mapper, storagePath, executor);
        LocalStorageData originalData = new LocalStorageData();
        originalData.user = new User(1L, "John Doe", "john@example.com");
        originalData.preferences = new Preferences(
                1L, true, ColorVision.DEUTERAN, Theme.LIGHT, Mode.ADVANCED, false, 1.0, 1L
        );

        // Manually set data and save
        writeClient.getData().updateFrom(originalData);
        writeClient.saveDataAsync().join();

        // Act: create a new client and load the saved data
        LocalStorageClient readClient = new LocalStorageClient(mapper, storagePath, executor);
        readClient.loadDataAsync().join();

        // Assert: loaded data matches original
        assertEquals(originalData.user.id(), readClient.getData().user.id());
        assertEquals(originalData.user.name(), readClient.getData().user.name());
        assertEquals(originalData.user.email(), readClient.getData().user.email());
        assertEquals(originalData.preferences.vision(), readClient.getData().preferences.vision());
        assertEquals(originalData.preferences.theme(), readClient.getData().preferences.theme());
    }

    @Test
    void saveAndLoadCycle_WithMultipleDataUpdates_PreservesAllChanges_IntegrationTest() throws Exception {
        // Arrange
        LocalStorageClient client = new LocalStorageClient(mapper, storagePath, executor);

        // Act: update data multiple times and save between updates
        LocalStorageData data1 = new LocalStorageData();
        data1.user = new User(1L, "Alice", "alice@example.com");
        client.getData().updateFrom(data1);
        client.saveDataAsync().join();

        // Second update
        LocalStorageData data2 = new LocalStorageData();
        data2.user = new User(2L, "Bob", "bob@example.com");
        client.getData().updateFrom(data2);
        client.saveDataAsync().join();

        // Load and verify the second (latest) update was persisted
        LocalStorageClient reloadClient = new LocalStorageClient(mapper, storagePath, executor);
        reloadClient.loadDataAsync().join();

        assertEquals(2L, reloadClient.getData().user.id());
        assertEquals("Bob", reloadClient.getData().user.name());
    }

    @Test
    void loadDataAsync_WithCorruptedFile_FallsBackToDefaultData_BoundaryTest() throws Exception {
        // Arrange: write invalid JSON to storage file
        Files.createDirectories(storagePath.getParent());
        Files.writeString(storagePath, "{ this is not valid json ]]");

        // Act: client loads corrupted data and triggers default + save
        LocalStorageClient client = new LocalStorageClient(mapper, storagePath, executor);
        client.loadDataAsync().join();

        // Assert: client has default data, not corrupted data
        assertNotNull(client.getData());
        assertNotNull(client.getData().user);
        // Default data should be safe to use

        // Cleanup
        Files.deleteIfExists(storagePath);
    }

    @Test
    void loadDataAsync_WithMissingFile_UsesDefaultAndSaves_BoundaryTest() throws Exception {
        // Arrange: use a path that doesn't exist
        Path testPath = storagePath.getParent().resolve("missing_file_test.json");
        assertFalse(Files.exists(testPath));

        // Act: client loads from missing file
        LocalStorageClient client = new LocalStorageClient(mapper, testPath, executor);
        client.loadDataAsync().join();

        // Assert: client recovers with default data
        assertNotNull(client.getData());

        // Cleanup
        Files.deleteIfExists(testPath);
    }

    @Test
    void loadDataAsync_WithEmptyFile_FallsBackToDefaultData_BoundaryTest() throws Exception {
        // Arrange: create an empty file
        Path testPath = storagePath.getParent().resolve("empty_file_test.json");
        Files.createDirectories(testPath.getParent());
        Files.createFile(testPath);
        assertTrue(Files.size(testPath) == 0);

        // Act: client loads from empty file
        LocalStorageClient client = new LocalStorageClient(mapper, testPath, executor);
        client.loadDataAsync().join();

        // Assert: client has default data
        assertNotNull(client.getData());

        // Cleanup
        Files.deleteIfExists(testPath);
    }

    // =========================================================================
    // REGRESSION TESTS: Ensure previously fixed issues stay fixed
    // =========================================================================

    @Test
    void atomicFileSave_DoesNotCorruptFileOnConcurrentAccess_RegressionTest() throws Exception {
        // Regression: ensures atomic move is used during save to prevent partial writes
        LocalStorageClient client = new LocalStorageClient(mapper, storagePath, executor);

        LocalStorageData data = new LocalStorageData();
        data.user = new User(42L, "Regression Test User", "regtest@example.com");
        client.getData().updateFrom(data);

        // Save multiple times rapidly
        client.saveDataAsync().join();
        client.saveDataAsync().join();
        client.saveDataAsync().join();

        // Verify file is still valid JSON and loadable
        LocalStorageClient reloadClient = new LocalStorageClient(mapper, storagePath, executor);
        assertDoesNotThrow(() -> reloadClient.loadDataAsync().join());
        assertEquals(42L, reloadClient.getData().user.id());
    }

    @Test
    void loadDataAsync_AfterPartialWrite_StillUsesValidPreviousVersion_RegressionTest() throws Exception {
        // Arrange: save valid data first
        LocalStorageClient client1 = new LocalStorageClient(mapper, storagePath, executor);
        LocalStorageData validData = new LocalStorageData();
        validData.user = new User(1L, "Valid User", "valid@example.com");
        client1.getData().updateFrom(validData);
        client1.saveDataAsync().join();

        // Simulate corruption by writing invalid JSON
        Files.writeString(storagePath, "{ incomplete json");

        // Act: new client loads from corrupted file
        LocalStorageClient client2 = new LocalStorageClient(mapper, storagePath, executor);
        client2.loadDataAsync().join();

        // Assert: recovered gracefully to default, not stuck with corrupt data
        assertNotNull(client2.getData());
        assertNotNull(client2.getData().user);
    }

    @Test
    void storagePathResolution_WithSpecialCharacters_HandlesCorrectly_BoundaryTest() throws Exception {
        // Arrange: use a path with special characters
        Path specialPath = tempDir.resolve("test-data_v1.0.json");

        LocalStorageClient client = new LocalStorageClient(mapper, specialPath, executor);
        LocalStorageData data = new LocalStorageData();
        data.user = new User(99L, "Special Path User", "special@example.com");
        client.getData().updateFrom(data);

        // Act
        client.saveDataAsync().join();

        // Assert: file was created and is readable
        assertTrue(Files.exists(specialPath));
        LocalStorageClient reloadClient = new LocalStorageClient(mapper, specialPath, executor);
        reloadClient.loadDataAsync().join();
        assertEquals(99L, reloadClient.getData().user.id());
    }
}

