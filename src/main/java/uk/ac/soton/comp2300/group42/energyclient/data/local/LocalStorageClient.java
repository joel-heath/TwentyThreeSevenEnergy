package uk.ac.soton.comp2300.group42.energyclient.data.local;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.LocalStorageExecutor;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.LocalStoragePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;

@Singleton
public class LocalStorageClient {

    private final Path storagePath;
    private final LocalStorageData data;
    private final ExecutorService executor;
    private final JsonMapper mapper;

    @Inject
    public LocalStorageClient(@BackendMapper JsonMapper mapper,
                              @LocalStoragePath Path storagePath,
                              @LocalStorageExecutor ExecutorService executor) {
        this.mapper = mapper;
        this.storagePath = storagePath;
        this.executor = executor;
        this.data = new LocalStorageData();
        loadData();
    }

    private void loadData() {
        executor.submit(() -> {
            try {
                if (!Files.exists(storagePath) || Files.size(storagePath) == 0)
                    throw new IOException("Local storage file does not exist or is empty.");

                LocalStorageData data = mapper.readValue(storagePath.toFile(), LocalStorageData.class);
                this.data.updateFrom(data);
            }
            catch (IOException | JacksonException e) {
                System.out.println("I/O Error while reading from local storage, maybe the file is corrupt. Using default data.");
                System.out.println(e.getMessage());
                this.data.updateFrom(LocalStorageData.createDefault());
                saveData();
            }
        });
    }

    public void saveData() {
        executor.submit(() -> {
            try {
                Path tempPath = storagePath.resolveSibling(storagePath.getFileName() + ".tmp");

                mapper.writerWithDefaultPrettyPrinter().writeValue(tempPath.toFile(), data);
                Files.move(tempPath, storagePath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e) {
                System.out.println("I/O Error while writing to local storage. Changes may not be saved.");
                System.out.println(e.getMessage());
            }
        });
    }

    public LocalStorageData getData() {
        return data;
    }
}