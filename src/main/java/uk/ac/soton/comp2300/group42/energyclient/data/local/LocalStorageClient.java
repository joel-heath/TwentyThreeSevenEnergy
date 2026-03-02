package uk.ac.soton.comp2300.group42.energyclient.data.local;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.BackendMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class LocalStorageClient {
    private static final String FILE_PATH = "local_data.json";
    private final ExecutorService executor;
    private final JsonMapper mapper;
    private final LocalStorageData data;

    @Inject
    public LocalStorageClient(@BackendMapper JsonMapper mapper) {
        this.mapper = mapper;
        this.executor = Executors.newSingleThreadExecutor();
        this.data = new LocalStorageData();
        loadData();
    }

    private void loadData() {
        executor.submit(() -> {
            File file = Paths.get(FILE_PATH).toFile();

            if (file.exists() && file.length() > 0) {
                try {
                    LocalStorageData data = mapper.readValue(file, LocalStorageData.class);
                    this.data.updateFrom(data);
                }
                catch (JacksonException e) {
                    System.out.println("I/O Error while reading from local storage, maybe the file is corrupt. Using default data.");
                    System.out.println(e.getMessage());
                    this.data.updateFrom(LocalStorageData.createDefault());
                    saveData();
                }
            }
            else {
                this.data.updateFrom(LocalStorageData.createDefault());
                saveData();
            }
        });
    }

    public void saveData() {
        executor.submit(() -> {
            try {
                Path targetPath = Paths.get(FILE_PATH);
                Path tempPath = Paths.get(FILE_PATH + ".tmp");

                mapper.writerWithDefaultPrettyPrinter().writeValue(tempPath.toFile(), data);
                Files.move(tempPath, targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
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