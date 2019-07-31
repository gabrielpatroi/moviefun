package org.superbiz.moviefun.blobs;

import org.apache.tika.Tika;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = getCoverFile(blob.getName());
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(blob.getInputStream(), outputStream);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException, URISyntaxException {
        File file = getExistingFile(name);
        String contentType = new Tika().detect(file.getCanonicalPath());
        Blob blob = new Blob(name, new FileInputStream(file), contentType);

        return Optional.ofNullable(blob);
    }

    @Override
    public void deleteAll() throws IOException {
        File parentDir = new File("covers");
        if (parentDir.isDirectory()) {
            FileUtils.cleanDirectory(parentDir);
        }
    }

    private File getCoverFile(String name) {
        String coverFileName = format("covers/%d", name);
        return new File(coverFileName);
    }

    private File getExistingFile(String name) throws URISyntaxException {
        File coverFile = getCoverFile(name);
        Path coverFilePath;

        if (coverFile.exists()) {
            return coverFile;
        } else {
            return new File(Paths.get(getSystemResource("default-cover.jpg").toURI()).toString());
        }
    }

}
