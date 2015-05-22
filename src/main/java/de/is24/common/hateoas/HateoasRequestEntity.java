package de.is24.common.hateoas;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Arrays;


public class HateoasRequestEntity {
  public static HttpEntity<Object> requestEntity() {
    return new org.springframework.http.HttpEntity<Object>(getHeaders());
  }

  public static HttpEntity<Object> requestEntity(Object object) {
    return new HttpEntity<Object>(object, getHeaders());
  }

  private static HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    MediaType mediaType = new MediaType("application", "hal+json");
    headers.setContentType(mediaType);
    headers.setAccept(Arrays.asList(mediaType));
    return headers;
  }
}
