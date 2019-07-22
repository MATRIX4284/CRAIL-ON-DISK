package org.apache.crail.yarn;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Common utilities shared by all components in Alluxio.
 */
@ThreadSafe
public final class CommonUtils {
    private static final Logger LOG = LoggerFactory.getLogger(CommonUtils.class);

    private static final String ALPHANUM =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static void sleepMs(long timeMs) {
        sleepMs(null, timeMs);
    }

    /**
     * Sleeps for the given number of milliseconds, reporting interruptions using the given logger.
     *
     * Unlike Thread.sleep(), this method responds to interrupts by setting the thread interrupt
     * status. This means that callers must check the interrupt status if they need to handle
     * interrupts.
     *
     * @param logger logger for reporting interruptions; no reporting is done if the logger is null
     * @param timeMs sleep duration in milliseconds
     */
    public static void sleepMs(Logger logger, long timeMs) {
        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
            if (logger != null) {
                logger.warn(e.getMessage(), e);
            }
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Common empty loop utility that serves the purpose of warming up the JVM before performance
     * microbenchmarks.
     */


    private CommonUtils() {} // prevent instantiation
}

