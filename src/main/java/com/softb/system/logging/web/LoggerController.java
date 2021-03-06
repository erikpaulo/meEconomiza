package com.softb.system.logging.web;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.softb.system.logging.web.resource.LoggerResource;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/api/admin/logs")
public class LoggerController {

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
//    @Timed
    public List<LoggerResource> getList() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        List<LoggerResource> loggers = new ArrayList<>();
        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
            loggers.add(new LoggerResource(logger));
        }
        return loggers;
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @Timed
    public void changeLevel(@RequestBody LoggerResource jsonLogger) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
    }
    
}
