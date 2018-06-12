package de.markus.statbot.config;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <b> This is a wrapper class for SLF4J. It is used when messages need to be
 * logged through SLF4J.</b>
 * </p>
 * <p>
 * To use SLF4j for them EclipseLink logs configured to propiedade
 * <code>eclipselink.logging.logger</code> with the value
 * <code>com.beamasset.useful.Slf4jSessionLogger</code>
 * </p>
 *
 * @author Dilnei Cunha
 * @author Nils Bauer
 *
 */
public class Slf4jSessionLogger extends AbstractSessionLog {

    public static final String ECLIPSELINK_NAMESPACE = "org.eclipse.persistence.logging";
    public static final String DEFAULT_CATEGORY = "DefaultLogger";
    public static final String DEFAULT_ECLIPSELINK_NAMESPACE = ECLIPSELINK_NAMESPACE + "." + DEFAULT_CATEGORY;

    private Map<Integer, LogLevel> mapLevels;
    private Map<String, Logger> categoryLoggers = new HashMap<String, Logger>();

    /**
     * Default constructor.
     */
    public Slf4jSessionLogger() {
        super();
        createCategoryLoggers();
        initMapLevels();
    }

    /**
     * Implementation of the log.
     */
    @Override
    public void log(SessionLogEntry entry) {
        if (!shouldLog(entry.getLevel(), entry.getNameSpace())) {
            return;
        }

        Logger logger = getLogger(entry.getNameSpace());
        LogLevel logLevel = getLogLevel(entry.getLevel());

        StringBuilder message = new StringBuilder();

        message.append(getSupplementDetailString(entry));
        message.append(formatMessage(entry));

        // Ignoriere leere Nachrichten
        if (message.toString().isEmpty())
            return;

        switch (logLevel) {
            case TRACE:
                logger.trace(message.toString());
                break;
            case DEBUG:
                logger.debug(message.toString());
                break;
            case INFO:
                logger.info(message.toString());
                break;
            case WARN:
                logger.warn(message.toString());
                break;
            case ERROR:
                logger.error(message.toString());
                break;
            default:
                break;
        }
    }

    /**
     * Log verify.
     */
    @Override
    public boolean shouldLog(int level, String category) {
        Logger logger = getLogger(category);
        boolean resp = false;
        LogLevel logLevel = getLogLevel(level);

        switch (logLevel) {
            case TRACE:
                resp = logger.isTraceEnabled();
                break;
            case DEBUG:
                resp = logger.isDebugEnabled();
                break;
            case INFO:
                resp = logger.isInfoEnabled();
                break;
            case WARN:
                resp = logger.isWarnEnabled();
                break;
            case ERROR:
                resp = logger.isErrorEnabled();
                break;
            default:
                break;
        }
        return resp;
    }

    /**
     * Should log.
     */
    @Override
    public boolean shouldLog(int level) {
        return shouldLog(level, DEFAULT_CATEGORY);
    }

    /**
     * Return true if SQL logging should log visible bind parameters. If the
     * shouldDisplayData is not set, return false.
     */
    @Override
    public boolean shouldDisplayData() {
        if (this.shouldDisplayData != null) {
            return shouldDisplayData.booleanValue();
        } else {
            return false;
        }
    }

    /**
     * Initialize loggers eagerly.
     */
    private void createCategoryLoggers() {
        for (String category : SessionLog.loggerCatagories) {
            addLogger(category, ECLIPSELINK_NAMESPACE + "." + category);
        }
        // Logger default when there is no category.
        addLogger(DEFAULT_CATEGORY, DEFAULT_ECLIPSELINK_NAMESPACE);
    }

    /**
     * INTERNAL: Add Logger to the categoryLoggers.
     */
    private void addLogger(String loggerCategory, String loggerNameSpace) {
        categoryLoggers.put(loggerCategory, LoggerFactory.getLogger(loggerNameSpace));
    }

    /**
     * INTERNAL: Return the Logger for the given category
     */
    private Logger getLogger(String category) {
        if (category != null && !category.isEmpty() || !this.categoryLoggers.containsKey(category)) {
            category = DEFAULT_CATEGORY;
        }
        return categoryLoggers.get(category);
    }

    /**
     * Return the corresponding Slf4j Level for a given EclipseLink level.
     */
    private LogLevel getLogLevel(Integer level) {
        LogLevel logLevel = mapLevels.get(level);
        if (logLevel == null)
            logLevel = LogLevel.OFF;
        return logLevel;
    }

    /**
     * SLF4J log levels.
     */
    enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR, OFF
    }

    /**
     * From to SLF4J e eclipselink
     */
    private void initMapLevels() {
        mapLevels = new HashMap<Integer, LogLevel>();
        mapLevels.put(SessionLog.ALL, LogLevel.TRACE);
        mapLevels.put(SessionLog.FINEST, LogLevel.TRACE);
        mapLevels.put(SessionLog.FINER, LogLevel.TRACE);
        mapLevels.put(SessionLog.FINE, LogLevel.DEBUG);
        mapLevels.put(SessionLog.CONFIG, LogLevel.INFO);
        mapLevels.put(SessionLog.INFO, LogLevel.INFO);
        mapLevels.put(SessionLog.WARNING, LogLevel.WARN);
        mapLevels.put(SessionLog.SEVERE, LogLevel.ERROR);
    }
}
