package com.dc.im.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.echo.config.LoggerName;

/**
 * Unit test for simple App.
 */
public class LogTest {
    private static Logger LOG = LoggerFactory.getLogger(LoggerName.CONSOLE);
    private static Logger LOG_RECEIVE = LoggerFactory.getLogger(LoggerName.RECEIVE);
    public static void main(String[] args) {
        LOG.info("a");
        LOG_RECEIVE.info("b");
    }
}
