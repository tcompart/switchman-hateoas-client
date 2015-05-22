package de.is24.common.hystrix;

import de.is24.common.hateoas.HateoasLinkProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class HateoasRemoteCommandTest {
  @Mock
  HateoasLinkProvider hateoasLinkProvider;

  @Mock
  RestOperations restOperations;

  CountingAbTestRemoteCommand countingAbTestRemoteCommand;

  private Integer callCount;

  @Before
  public void setup() {
    countingAbTestRemoteCommand = new CountingAbTestRemoteCommand(restOperations,
      hateoasLinkProvider);
  }

  @Test
  public void retriesRunCommandWhen404() throws Exception {
    givenCommandThatThrows404();
    whenRunIsCalled();
    thenCacheIsCleared();
    thenRunCommandIsCalledOnceAgain();
  }

  private void thenCacheIsCleared() {
    verify(hateoasLinkProvider).resetLinkCache();
  }

  private void thenRunCommandIsCalledOnceAgain() {
    assertThat(callCount, is(2));
  }

  private void whenRunIsCalled() throws Exception {
    callCount = countingAbTestRemoteCommand.run();
  }

  private void givenCommandThatThrows404() {
    when(restOperations.getForEntity(anyString(), any(Class.class))).thenThrow(
      new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }

  private class CountingAbTestRemoteCommand extends HateoasRemoteCommand<Integer> {
    private int counter = 0;

    public CountingAbTestRemoteCommand(RestOperations restOperations,
                                       HateoasLinkProvider hateoasLinkProvider) {
      super(new HystrixConfiguration(true, 100000).getConfiguration("TEST"), restOperations, hateoasLinkProvider);
    }

    @Override
    protected Integer runCommand() throws Exception {
      try {
        restOperations.getForEntity("url", String.class);
      } catch (HttpClientErrorException e) {
        if (counter == 0) {
          ++counter;
          throw e;
        }
      }
      return ++counter;
    }
  }
}
