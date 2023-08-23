package com.api.services;

import com.api.models.Image;
import com.api.models.ImageList;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class PictureService {

    private final ObjectMapper objectMapper;

    public PictureService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<String> getPictures(String word) {
        CompletableFuture<HttpResponse<String>> future = Unirest.get("https://pixabay.com/api/")
                .queryString("key", "<KEY>")
                .queryString("q", word)
                .queryString("image_type", "photo")
                .asStringAsync();

        return future.thenApplyAsync(HttpResponse::getBody)
                .thenApplyAsync(stringResult -> {
                    try {
                        ImageList imageList = objectMapper.readValue(stringResult, ImageList.class);

                        String result = "<html><body>";
                        for (Image img: imageList.hits) {
                            result += "<img src=\"" + img.webformatURL + "\"/>";
                        }
                        result += "</body></html>";

                        return result;

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return "<html><body><h1>ERROR on deserialization</h1></body></html>";
                });
    }
}
