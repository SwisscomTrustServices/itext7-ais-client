/*
 * Copyright 2021 Swisscom (Schweiz) AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swisscom.ais.itext.client.config;

import com.swisscom.ais.itext.client.common.Loggers;
import com.swisscom.ais.itext.client.model.VerboseLevel;

import org.slf4j.LoggerFactory;

import java.util.Objects;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LogbackConfiguration {

    private static final String ORG_APACHE_HC = "org.apache.hc";

    private static void setLoggerLevel(String loggerName, Level level, LoggerContext loggerContext) {
        Logger logger = loggerContext.getLogger(loggerName);
        if (Objects.nonNull(logger)) {
            logger.setLevel(level);
        }
    }

    public void initialize(VerboseLevel verboseLevel) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        switch (verboseLevel) {
            case LOW: {
                setLoggerLevel(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME, Level.WARN, loggerContext);
                setLoggerLevel(ORG_APACHE_HC, Level.INFO, loggerContext);
                setLoggerLevel(Loggers.CLIENT, Level.INFO, loggerContext);
                setLoggerLevel(Loggers.CONFIG, Level.INFO, loggerContext);
                setLoggerLevel(Loggers.CLIENT_PROTOCOL, Level.INFO, loggerContext);
                setLoggerLevel(Loggers.REQUEST_RESPONSE, Level.WARN, loggerContext);
                setLoggerLevel(Loggers.FULL_REQUEST_RESPONSE, Level.WARN, loggerContext);
                setLoggerLevel(Loggers.PDF_PROCESSING, Level.WARN, loggerContext);
                break;
            }
            case BASIC: {
                setLoggerLevel(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME, Level.INFO, loggerContext);
                setLoggerLevel(ORG_APACHE_HC, Level.INFO, loggerContext);
                setLoggerLevel(Loggers.CLIENT, Level.INFO, loggerContext);
                setLoggerLevel(Loggers.CONFIG, Level.INFO, loggerContext);
                setLoggerLevel(Loggers.CLIENT_PROTOCOL, Level.INFO, loggerContext);
                setLoggerLevel(Loggers.REQUEST_RESPONSE, Level.DEBUG, loggerContext);
                setLoggerLevel(Loggers.FULL_REQUEST_RESPONSE, Level.WARN, loggerContext);
                setLoggerLevel(Loggers.PDF_PROCESSING, Level.DEBUG, loggerContext);
                break;
            }
            case MEDIUM: // falls through
            case HIGH: {
                setLoggerLevel(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME, Level.DEBUG, loggerContext);
                setLoggerLevel(Loggers.CLIENT, Level.DEBUG, loggerContext);
                setLoggerLevel(Loggers.CONFIG, Level.DEBUG, loggerContext);
                setLoggerLevel(Loggers.CLIENT_PROTOCOL, Level.DEBUG, loggerContext);
                setLoggerLevel(Loggers.REQUEST_RESPONSE, Level.WARN, loggerContext);
                setLoggerLevel(Loggers.FULL_REQUEST_RESPONSE, Level.DEBUG, loggerContext);
                setLoggerLevel(Loggers.PDF_PROCESSING, Level.DEBUG, loggerContext);
                setLoggerLevel(ORG_APACHE_HC, verboseLevel.equals(VerboseLevel.MEDIUM) ? Level.INFO : Level.TRACE, loggerContext);
                break;
            }
        }
    }
}
