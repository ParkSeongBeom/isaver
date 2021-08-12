package com.icent.isaver.critical.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupTask implements ApplicationListener<ApplicationReadyEvent> {
    @Value("${cnf.server.version}")
    private String version = null;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        StringBuilder loggerBuiler = new StringBuilder();
        loggerBuiler.append("\n==============================");
        loggerBuiler.append("\n= iSaver Critical Agent");
        loggerBuiler.append("\n= Version : ").append(version);
        loggerBuiler.append("\n==============================");
        log.info(loggerBuiler.toString());
        loggerBuiler.setLength(0);
    }
}