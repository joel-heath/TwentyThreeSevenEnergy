package uk.ac.soton.comp2300.group42.energyclient.data.local;

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
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalStorageClientTest {

    @Mock JsonMapper mockMapper;
    @Mock ExecutorService mockExecutor;
    @Mock ObjectWriter mockWriter;

    @TempDir Path tempDir;
    Path testFilePath;

    @BeforeEach
    void setUp() {
        testFilePath = tempDir.resolve("test_data.json");

        // Force the executor to run tasks synchronously on the main thread
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return mock(Future.class);
        }).when(mockExecutor).submit(any(Runnable.class));
    }

    @Test
    void init_WhenFileDoesNotExist_LoadsDefaultDataAndSaves() {
        when(mockMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);

        LocalStorageClient client = new LocalStorageClient(mockMapper, testFilePath, mockExecutor);

        assertNotNull(client.getData());

        verify(mockMapper).writerWithDefaultPrettyPrinter();
    }

    @Test
    void init_WhenFileIsCorrupt_CatchesExceptionAndLoadsDefaultData() throws Exception {
        when(mockMapper.writerWithDefaultPrettyPrinter()).thenReturn(mockWriter);
        when(mockMapper.readValue(any(File.class), eq(LocalStorageData.class))).thenThrow(mock(JacksonException.class));

        Files.writeString(testFilePath, "{ bad json }");

        LocalStorageClient client = new LocalStorageClient(mockMapper, testFilePath, mockExecutor);

        assertNotNull(client.getData());
        verify(mockMapper).writerWithDefaultPrettyPrinter();
    }

    @Test
    void init_WhenFileIsValid_LoadsDataSuccessfully() throws Exception {
        Files.writeString(testFilePath, "{\"some\": \"data\"}");

        LocalStorageData mockData = new LocalStorageData();
        when(mockMapper.readValue(any(File.class), eq(LocalStorageData.class))).thenReturn(mockData);

        LocalStorageClient client = new LocalStorageClient(mockMapper, testFilePath, mockExecutor);

        assertNotNull(client.getData());
        verify(mockMapper, never()).writerWithDefaultPrettyPrinter();
    }
}