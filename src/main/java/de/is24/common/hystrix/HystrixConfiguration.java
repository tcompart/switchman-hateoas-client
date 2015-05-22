package de.is24.common.hystrix;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.springframework.beans.factory.DisposableBean;


public class HystrixConfiguration implements DisposableBean {
  private final int timeoutInMilliseconds;
  private boolean fallbackEnabled;

  public HystrixConfiguration(boolean fallbackEnabled, int timeoutInMilliseconds) {
    this.fallbackEnabled = fallbackEnabled;
    this.timeoutInMilliseconds = timeoutInMilliseconds;
  }

  public HystrixCommand.Setter getConfiguration(String commandGroup) {
    HystrixCommand.Setter setter = HystrixCommand.Setter.withGroupKey(
      HystrixCommandGroupKey.Factory.asKey(commandGroup))
      .andCommandPropertiesDefaults(
        HystrixCommandProperties.Setter()
        .withExecutionIsolationThreadTimeoutInMilliseconds(timeoutInMilliseconds)
        .withFallbackEnabled(fallbackEnabled));

    return setter;
  }

  public void setFallbackEnabled(boolean fallbackEnabled) {
    this.fallbackEnabled = fallbackEnabled;
  }

  @Override
  public void destroy() throws Exception {
    // cleanup hystrix thread pool
    Hystrix.reset();
  }
}
