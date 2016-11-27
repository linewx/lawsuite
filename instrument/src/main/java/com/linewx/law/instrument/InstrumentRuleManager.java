package com.linewx.law.instrument;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.springframework.stereotype.Component;

/**
 * Created by luganlin on 11/27/16.
 */
@Component
public class InstrumentRuleManager {
    MultiKeyMap rules = MultiKeyMap.decorate(new HashedMap());

    public void addRule(String instrumentType, String level) {

    };
}
