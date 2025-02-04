package org.superbiz.moviefun.blobs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public interface BlobStore {
    void put(Blob blob) throws IOException;

    Optional<Blob> get(String name) throws IOException, URISyntaxException;

    void deleteAll() throws IOException;
}
