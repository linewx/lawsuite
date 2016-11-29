package com.linewx.law.instrument.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by lugan on 11/29/2016.
 */
@Component
public class LookupService implements ApplicationContextAware{
    private static volatile LookupService lookupService;

    private ApplicationContext applicationContext;

    private LookupService() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @Qualifier("lookupService")
    public static LookupService getInstance() {
        if (lookupService == null) {
            synchronized (LookupService.class) {
                if (lookupService == null) {
                    lookupService = new LookupService();
                }
            }
        }
        return lookupService;
    }

    public <T> T lookup(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
