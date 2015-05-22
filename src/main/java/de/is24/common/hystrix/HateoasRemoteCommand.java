package de.is24.common.hystrix;

import com.netflix.hystrix.HystrixCommand;
import de.is24.common.hateoas.HateoasLinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;


public abstract class HateoasRemoteCommand<T> extends HystrixCommand<T> {
  protected final RestOperations restOperations;
  protected final HateoasLinkProvider hateoasLinkProvider;

  private static final Logger LOGGER = LoggerFactory.getLogger(HateoasRemoteCommand.class);

  public HateoasRemoteCommand(Setter setter, RestOperations restOperations,
                              HateoasLinkProvider hateoasLinkProvider) {
    super(setter);
    this.restOperations = restOperations;
    this.hateoasLinkProvider = hateoasLinkProvider;
  }

  protected Link getLinkByName(String href, String linkName) {
    return hateoasLinkProvider.getLinkByName(href, linkName);
  }

  protected abstract T runCommand() throws Exception;

  @Override
  protected final T run() throws Exception {
    try {
      return runCommand();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
        LOGGER.warn("Cannot fetch remote data with known links. Cleaning HATEOAS link cache.");
        hateoasLinkProvider.resetLinkCache();
        return runCommand();
      }
      throw e;
    }
  }
}
