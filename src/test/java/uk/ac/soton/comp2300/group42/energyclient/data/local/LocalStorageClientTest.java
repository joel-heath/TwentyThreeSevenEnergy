package uk.ac.soton.comp2300.group42.energyclient.data.local;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalStorageClientTest {

    @Mock private JsonMapper mapper;
    @Mock private ObjectWriter writer;

    @TempDir private Path tempDir;

    private Path storagePath;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        storagePath = tempDir.resolve("test_data.json");
        executor = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }

    @Test
    void loadDataAsync_whenFileMissing_usesDefaultDataAndAttemptsSave() {
        LocalStorageClient client = new LocalStorageClient(mapper, storagePath, executor);
        client.loadDataAsync().join();

        assertNotNull(client.getData());
        verify(mapper, never()).readValue(any(File.class), eq(LocalStorageData.class));
    }

    @Test
    void loadDataAsync_whenFileCorrupt_usesDefaultDataAndAttemptsSave() throws Exception {
        Files.writeString(storagePath, "{ bad json }");
        when(mapper.readValue(any(File.class), eq(LocalStorageData.class))).thenThrow(mock(JacksonException.class));
        when(mapper.writerWithDefaultPrettyPrinter()).thenReturn(writer);

        LocalStorageClient client = new LocalStorageClient(mapper, storagePath, executor);
        client.loadDataAsync().join();

        assertNotNull(client.getData());
        verify(mapper).readValue(any(File.class), eq(LocalStorageData.class));
        verify(mapper).writerWithDefaultPrettyPrinter();
    }

    @Test
    void loadDataAsync_whenFileValid_readsDataWithoutSaving() throws Exception {
        Files.writeString(storagePath, "{\"x\":1}");
        LocalStorageData loaded = new LocalStorageData();
        when(mapper.readValue(any(File.class), eq(LocalStorageData.class))).thenReturn(loaded);

        LocalStorageClient client = new LocalStorageClient(mapper, storagePath, executor);
        client.loadDataAsync().join();

        assertNotNull(client.getData());
        verify(mapper).readValue(any(File.class), eq(LocalStorageData.class));
        verify(mapper, never()).writerWithDefaultPrettyPrinter();
    }
}
