package de.is24.common.hateoas;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;


public class HateoasLinkProvider {
  private final RestOperations restOperations;

  public HateoasLinkProvider(RestOperations restOperations) {
    this.restOperations = restOperations;
  }

  @Cacheable("linksByName")
  public Link getLinkByName(String href, String linkName) {
    ResponseEntity<ResourceSupport> remoteResource = restOperations.exchange(href,
      HttpMethod.GET,
      HateoasRequestEntity.requestEntity(),
      ResourceSupport.class);

    return remoteResource.getBody().getLink(linkName);
  }

  @CacheEvict(value = "linksByName", allEntries = true)
  public void resetLinkCache() {
    // Intentionally blank
  }
}
