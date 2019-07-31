package org.superbiz.moviefun.blobs;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private AmazonS3Client s3Client;
    private String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.getContentType());
        PutObjectRequest request = new PutObjectRequest(photoStorageBucket, blob.getName(), blob.getInputStream(), metadata);
        s3Client.putObject(request);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException, URISyntaxException {
        try {
            S3Object fullObject = s3Client.getObject(photoStorageBucket, name);
            Blob blob = new Blob(name, fullObject.getObjectContent(), fullObject.getObjectMetadata().getContentType());

            return Optional.ofNullable(blob);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll() throws IOException {
        s3Client.deleteObjects(new DeleteObjectsRequest(photoStorageBucket));
    }

    public AmazonS3Client getS3Client() {
        return s3Client;
    }

    public void setS3Client(AmazonS3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String getPhotoStorageBucket() {
        return photoStorageBucket;
    }

    public void setPhotoStorageBucket(String photoStorageBucket) {
        this.photoStorageBucket = photoStorageBucket;
    }
}
