package io.prometheus.client.dropwizard;

import com.codahale.metrics.Timer;
import com.codahale.metrics.*;
import io.prometheus.client.dropwizard.samplebuilder.DefaultSampleBuilder;
import io.prometheus.client.dropwizard.samplebuilder.SampleBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collect Dropwizard metrics from a MetricRegistry.
 */
public class DropwizardExportsDiscard extends io.prometheus.client.Collector implements io.prometheus.client.Collector.Describable {
    private static final Logger LOGGER = Logger.getLogger(DropwizardExportsDiscard.class.getName());
    private MetricRegistry registry;
    private MetricFilter metricFilter;
    private SampleBuilder sampleBuilder;
    private  long rateFactor;
    private  TimeUnit rateUnit=TimeUnit.SECONDS;
    private  long durationFactor;
    private  TimeUnit durationUnit=TimeUnit.MILLISECONDS;



    /**
     * Creates a new io.prometheus.client.dropwizard.DropwizardExports with a {@link DefaultSampleBuilder} and {@link MetricFilter#ALL}.
     *
     * @param registry a metric registry to export in prometheus.
     */
    public DropwizardExportsDiscard(MetricRegistry registry) {
        this.rateFactor = rateUnit.toSeconds(1);
        this.durationFactor=durationUnit.toNanos(1);
        this.registry = registry;
        this.metricFilter = MetricFilter.ALL;
        this.sampleBuilder = new DefaultSampleBuilder();
    }

    /**
     * Creates a new io.prometheus.client.dropwizard.DropwizardExports with a {@link DefaultSampleBuilder} and custom {@link MetricFilter}.
     *
     * @param registry     a metric registry to export in prometheus.
     * @param metricFilter a custom metric filter.
     */
    public DropwizardExportsDiscard(MetricRegistry registry, MetricFilter metricFilter) {
        this.registry = registry;
        this.metricFilter = metricFilter;
        this.sampleBuilder = new DefaultSampleBuilder();
    }

    /**
     * @param registry      a metric registry to export in prometheus.
     * @param sampleBuilder sampleBuilder to use to create prometheus samples.
     */
    public DropwizardExportsDiscard(MetricRegistry registry, SampleBuilder sampleBuilder) {
        this.registry = registry;
        this.metricFilter = MetricFilter.ALL;
        this.sampleBuilder = sampleBuilder;
    }

    /**
     * @param registry      a metric registry to export in prometheus.
     * @param metricFilter  a custom metric filter.
     * @param sampleBuilder sampleBuilder to use to create prometheus samples.
     */
    public DropwizardExportsDiscard(MetricRegistry registry, MetricFilter metricFilter, SampleBuilder sampleBuilder) {
        this.registry = registry;
        this.metricFilter = metricFilter;
        this.sampleBuilder = sampleBuilder;
    }

    private static String getHelpMessage(String metricName, Metric metric) {
        return String.format("Generated from Dropwizard metric import (metric=%s, type=%s)",
                metricName, metric.getClass().getName());
    }

    /**
     * Export counter as Prometheus <a href="https://prometheus.io/docs/concepts/metric_types/#gauge">Gauge</a>.
     */
    MetricFamilySamples fromCounter(String dropwizardName, Counter counter) {
        MetricFamilySamples.Sample sample = sampleBuilder.createSample(dropwizardName, "", new ArrayList<String>(), new ArrayList<String>(),
                new Long(counter.getCount()).doubleValue());
        return new MetricFamilySamples(sample.name, Type.GAUGE, getHelpMessage(dropwizardName, counter), Arrays.asList(sample));
    }

    /**
     * Export gauge as a prometheus gauge.
     */
    MetricFamilySamples fromGauge(String dropwizardName, Gauge gauge) {
        Object obj = gauge.getValue();
        double value;
        if (obj instanceof Number) {
            value = ((Number) obj).doubleValue();
        } else if (obj instanceof Boolean) {
            value = ((Boolean) obj) ? 1 : 0;
        } else {
            LOGGER.log(Level.FINE, String.format("Invalid type for Gauge %s: %s", sanitizeMetricName(dropwizardName),
                    obj == null ? "null" : obj.getClass().getName()));
            return null;
        }
        MetricFamilySamples.Sample sample = sampleBuilder.createSample(dropwizardName, "",
                new ArrayList<String>(), new ArrayList<String>(), value);
        return new MetricFamilySamples(sample.name, Type.GAUGE, getHelpMessage(dropwizardName, gauge), Arrays.asList(sample));
    }

    protected double convertRate(double rate) {
        return rate * rateFactor;
    }
    protected double convertDuration(double duration) {
        return duration / durationFactor;
    }
    protected String getRateUnit() {
        final String s = rateUnit.toString().toLowerCase(Locale.US);
        return s.substring(0, s.length() - 1);
    }
    protected String getDurationUnit() {
        return durationUnit.toString().toLowerCase(Locale.US);
    }

    MetricFamilySamples fromSnapshotAndCountM(String dropwizardName, long count, String helpMessage,Meter meter) {
        List<MetricFamilySamples.Sample> samples = Arrays.asList(
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("mean rate (calls/"+getRateUnit()+")"), Arrays.asList(""), convertRate(meter.getMeanRate())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("minute rate (calls/"+getRateUnit()+")"), Arrays.asList("1"), convertRate(meter.getOneMinuteRate())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("minute rate (calls/"+getRateUnit()+")"), Arrays.asList("5"), convertRate(meter.getFiveMinuteRate())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("minute rate (calls/"+getRateUnit()+")"), Arrays.asList("15"), convertRate(meter.getFifteenMinuteRate())),
                sampleBuilder.createSample(dropwizardName, "_count", new ArrayList<String>(), new ArrayList<String>(), count)
        );
        return new MetricFamilySamples(samples.get(0).name, Type.SUMMARY, helpMessage, samples);
    }
    /**
     * Export a histogram snapshot as a prometheus SUMMARY.
     *
     * @param dropwizardName metric name.
     * @param snapshot       the histogram snapshot.
     * @param count          the total sample count for this snapshot.
     * @param factor         a factor to apply to histogram values.
     */
    MetricFamilySamples fromSnapshotAndCount(String dropwizardName, Snapshot snapshot, long count, double factor, String helpMessage) {
        List<MetricFamilySamples.Sample> samples = Arrays.asList(
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("min ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getMin())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("max rate ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getMax())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("mean rate ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getMean())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("stddev rate ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getStdDev())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.5"), convertDuration(snapshot.getMedian() )),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.75"), convertDuration(snapshot.get75thPercentile() )),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.95"), convertDuration(snapshot.get95thPercentile())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.98"), convertDuration(snapshot.get98thPercentile() )),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.99"), convertDuration(snapshot.get99thPercentile() )),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.999"), convertDuration(snapshot.get999thPercentile() )),
                sampleBuilder.createSample(dropwizardName, "_count", new ArrayList<String>(), new ArrayList<String>(), count)
        );
        return new MetricFamilySamples(samples.get(0).name, Type.SUMMARY, helpMessage, samples);
    }

    MetricFamilySamples fromSnapshotAndCountT(String dropwizardName, Snapshot snapshot, long count, double factor, String helpMessage, Timer timer) {
        List<MetricFamilySamples.Sample> samples = Arrays.asList(
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("mean rate (calls/"+getRateUnit()+")"), Arrays.asList(""), convertRate(timer.getMeanRate())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("minute rate (calls/"+getRateUnit()+")"), Arrays.asList("1"), convertRate(timer.getOneMinuteRate())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("minute rate (calls/"+getRateUnit()+")"), Arrays.asList("5"), convertRate(timer.getFiveMinuteRate())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("minute rate (calls/"+getRateUnit()+")"), Arrays.asList("15"), convertRate(timer.getFifteenMinuteRate())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("min ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getMin())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("max rate ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getMax())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("mean rate ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getMean())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("stddev rate ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getStdDev())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("median rate ("+getDurationUnit()+")"), Arrays.asList(""), convertDuration(snapshot.getMedian())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.5"), convertDuration(snapshot.getMedian() )),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.75"), convertDuration(snapshot.get75thPercentile() )),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.95"), convertDuration(snapshot.get95thPercentile())),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.98"), convertDuration(snapshot.get98thPercentile() )),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.99"), convertDuration(snapshot.get99thPercentile() )),
                sampleBuilder.createSample(dropwizardName, "", Arrays.asList("quantile ("+getDurationUnit()+")"), Arrays.asList("0.999"), convertDuration(snapshot.get999thPercentile() )),
                sampleBuilder.createSample(dropwizardName, "_count", new ArrayList<String>(), new ArrayList<String>(), count)
        );
        return new MetricFamilySamples(samples.get(0).name, Type.SUMMARY, helpMessage, samples);
    }

    /**
     * Convert histogram snapshot.
     */
    MetricFamilySamples fromHistogram(String dropwizardName, Histogram histogram) {
        return fromSnapshotAndCount(dropwizardName, histogram.getSnapshot(), histogram.getCount(), 1.0,
                getHelpMessage(dropwizardName, histogram));
    }

    /**
     * Export Dropwizard Timer as a histogram. Use TIME_UNIT as time unit.
     */
    MetricFamilySamples fromTimer(String dropwizardName, Timer timer) {
        return fromSnapshotAndCountT(dropwizardName, timer.getSnapshot(), timer.getCount(),
                1.0D / TimeUnit.MILLISECONDS.toNanos(1L), getHelpMessage(dropwizardName, timer),timer);
    }

    /**
     * Export a Meter as as prometheus COUNTER.
     */
    MetricFamilySamples fromMeter(String dropwizardName, Meter meter) {
        final MetricFamilySamples.Sample sample = sampleBuilder.createSample(dropwizardName, "_total",
                new ArrayList<String>(),
                new ArrayList<String>(),
                meter.getCount());
        return  fromSnapshotAndCountM(dropwizardName,meter.getCount(),getHelpMessage(dropwizardName,meter),meter);
    }

    @Override
    public List<MetricFamilySamples> collect() {
        Map<String, MetricFamilySamples> mfSamplesMap = new HashMap<String, MetricFamilySamples>();

        for (SortedMap.Entry<String, Gauge> entry : registry.getGauges(metricFilter).entrySet()) {
            addToMap(mfSamplesMap, fromGauge(entry.getKey(), entry.getValue()));
        }
        for (SortedMap.Entry<String, Counter> entry : registry.getCounters(metricFilter).entrySet()) {
            addToMap(mfSamplesMap, fromCounter(entry.getKey(), entry.getValue()));
        }
        for (SortedMap.Entry<String, Histogram> entry : registry.getHistograms(metricFilter).entrySet()) {
            addToMap(mfSamplesMap, fromHistogram(entry.getKey(), entry.getValue()));
        }
        for (SortedMap.Entry<String, Timer> entry : registry.getTimers(metricFilter).entrySet()) {
            addToMap(mfSamplesMap, fromTimer(entry.getKey(), entry.getValue()));
        }
        for (SortedMap.Entry<String, Meter> entry : registry.getMeters(metricFilter).entrySet()) {
            addToMap(mfSamplesMap, fromMeter(entry.getKey(), entry.getValue()));
        }
        return new ArrayList<MetricFamilySamples>(mfSamplesMap.values());
    }

    private void addToMap(Map<String, MetricFamilySamples> mfSamplesMap, MetricFamilySamples newMfSamples)
    {
        if (newMfSamples != null) {
            MetricFamilySamples currentMfSamples = mfSamplesMap.get(newMfSamples.name);
            if (currentMfSamples == null) {
                mfSamplesMap.put(newMfSamples.name, newMfSamples);
            } else {
                List<MetricFamilySamples.Sample> samples = new ArrayList<MetricFamilySamples.Sample>(currentMfSamples.samples);
                samples.addAll(newMfSamples.samples);
                mfSamplesMap.put(newMfSamples.name, new MetricFamilySamples(newMfSamples.name, currentMfSamples.type, currentMfSamples.help, samples));
            }
        }
    }

    @Override
    public List<MetricFamilySamples> describe() {
        return new ArrayList<MetricFamilySamples>();
    }

    public static void main(String[] args) {
        double d=1.0D / TimeUnit.SECONDS.toNanos(1L);
        System.out.println(TimeUnit.SECONDS.toNanos(1L));
        System.out.println(d);
    }
}