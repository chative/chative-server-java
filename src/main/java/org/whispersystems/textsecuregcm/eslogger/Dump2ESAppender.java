package org.whispersystems.textsecuregcm.eslogger;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.github.difftim.eslogger.ESLogger;
import io.dropwizard.logging.AbstractAppenderFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import io.dropwizard.logging.layout.LayoutFactory;
import io.dropwizard.logging.filter.LevelFilterFactory;
import io.dropwizard.logging.async.AsyncAppenderFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import ch.qos.logback.core.spi.DeferredProcessingAware;


@JsonTypeName("dump2es")
public class Dump2ESAppender<E extends DeferredProcessingAware> extends AbstractAppenderFactory<E> {

    private String appenderName = "dump2es-appender";
    private boolean includeContextName = false;

    @JsonProperty
    public String getName() {
        return this.appenderName;
    }

    @JsonProperty
    public void setName(String name) {
        this.appenderName = name;
    }

    @JsonProperty
    public boolean getIncludeContextName() {
        return this.includeContextName;
    }

    @JsonProperty
    public void setIncludeContextName(boolean includeContextName) {
        this.includeContextName = includeContextName;
    }

    @Override
    public Appender<E> build(LoggerContext context, String applicationName, LayoutFactory<E> layoutFactory,
                             LevelFilterFactory<E> levelFilterFactory,
                             AsyncAppenderFactory<E> asyncAppenderFactory) {
        ESAppender<E> appender = new ESAppender<>();
        appender.setName(appenderName);
        appender.setContext(context);
        appender.addFilter(levelFilterFactory.build(threshold));
        getFilterFactories().forEach(f -> appender.addFilter(f.build()));
        appender.start();

        return wrapAsync(appender, asyncAppenderFactory);
    }
}

class ESAppender<E> extends UnsynchronizedAppenderBase<E> {

    @Override
    protected void append(E eventObject) {
        // 只支持ILoggingEvent
        if (!(eventObject instanceof LoggingEvent)) return;

        LoggingEvent logEvent = (LoggingEvent) eventObject;
        ESLogger log = new ESLogger("filelogs");

        switch (logEvent.getLevel().toString()) {
//        case "TRACE":
//        case "DEBUG":
//            default:
//                return;

            case "INFO":
                log.withLevel(ESLogger.LOG_LEVEL_INFO);
                break;
            case "WARN":
                log.withLevel(ESLogger.LOG_LEVEL_WARN);
                break;
            case "ERROR":
                log.withLevel(ESLogger.LOG_LEVEL_ERROR);
                break;
            case "FATAL":
                log.withLevel(ESLogger.LOG_LEVEL_FATAL);
                break;
        }
        final IThrowableProxy throwableProxy = logEvent.getThrowableProxy();
        if (throwableProxy != null) {
            StringBuilder builder = new StringBuilder();
            for (StackTraceElementProxy step : throwableProxy.getStackTraceElementProxyArray()) {
                String string = step.toString();
                builder.append(CoreConstants.TAB).append(string);
                ThrowableProxyUtil.subjoinPackagingData(builder, step);
                builder.append(CoreConstants.LINE_SEPARATOR);
            }
            log.withCustom("exception", builder.toString());
        }
        log.withCustom("levelStr", logEvent.getLevel().toString());
        log.withCustom("threadName", logEvent.getThreadName()).withCustom("loggerName", logEvent.getLoggerName()).
                withCustom("message", logEvent.getFormattedMessage()).send();
    }
}