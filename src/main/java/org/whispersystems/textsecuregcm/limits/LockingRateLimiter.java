package org.whispersystems.textsecuregcm.limits;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import org.whispersystems.textsecuregcm.controllers.RateLimitExceededException;
import org.whispersystems.textsecuregcm.storage.MemCache;
import org.whispersystems.textsecuregcm.util.Constants;

import static com.codahale.metrics.MetricRegistry.name;

public class LockingRateLimiter extends RateLimiter {

  private final Meter meter;

  public LockingRateLimiter(MemCache cacheClient, String name, int bucketSize, double leakRatePerMinute) {
    super(cacheClient, name, bucketSize, leakRatePerMinute);

    MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
    this.meter = metricRegistry.meter(name(getClass(), name, "locked"));
  }

  @Override
  public void validate(String key, int amount) throws RateLimitExceededException {
    if (!acquireLock(key)) {
      meter.mark();
      throw new RateLimitExceededException("Locked");
    }

    try {
      super.validate(key, amount);
    } finally {
      releaseLock(key);
    }
  }

  @Override
  public void validate(String key) throws RateLimitExceededException {
    validate(key, 1);
  }

  private void releaseLock(String key) {
      cacheClient.remove(getLockName(key));
  }

  private boolean acquireLock(String key) {
    return cacheClient.set(getLockName(key), "L", "NX", "EX", 10) != null;
      // String ret = jedis.set(getLockName(key), "L", SetParams.setParams().nx().ex(10));
  }

  private String getLockName(String key) {
    return "leaky_lock::" + name + "::" + key;
  }


}
