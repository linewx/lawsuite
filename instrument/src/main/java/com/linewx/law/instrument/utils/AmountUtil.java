package com.linewx.law.instrument.utils;

import com.linewx.law.parser.Processor.AmountLevel;
import com.linewx.law.parser.Processor.Processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lugan on 11/18/2016.
 */
public class AmountUtil {
    private static Map<String, Long> amountStandard = new HashMap<>();
    private static List<AmountLevel> amountLevels = new ArrayList<>();


    static {
        init();
        initAmountLevels();
    }

    public static Long calulate(String source) {
        try {
            Long cost = Long.parseLong(source);
            return calculateAmount(cost);
        }catch (Exception e) {
            return 0L;
        }

    }

    public static Long calculateAmount(Long cost) {
        if (cost == 0){
            return 0L;
        }
        if (cost == 50) {
            return 10000L;
        }

        cost = cost - 50;
        AmountLevel lastAmountLevel = null;
        for(AmountLevel amountLevel : amountLevels) {
            if (cost < amountLevel.getCostLevel()) {
                if(lastAmountLevel == null) {
                    throw new RuntimeException("invalid cost");
                }else {
                    return lastAmountLevel.getAmountLevel() + ((Double)((cost - lastAmountLevel.getCostLevel())/amountLevel.getPercentage())).longValue();
                }
            }
            lastAmountLevel = amountLevel;
        }

        throw new RuntimeException("too large amount to calculate");
    }

    public static Boolean isCharge(String reasonNumber, Long cost) {
        List<String> matchedReasons = amountStandard.keySet().stream().filter(reasonNumber::startsWith).collect(Collectors.toList());
        if (matchedReasons != null && !matchedReasons.isEmpty()) {
            return cost >= amountStandard.getOrDefault(matchedReasons.get(0), 0L);
        } else {
            return true;
        }
    }

    public static void init() {
        initAmountStandard();
        initAmountLevels();
    }

    public static void initAmountStandard() {
        amountStandard.put("0020201", 2000L); //婚姻家庭、继承纠纷
        amountStandard.put("0020101", 1000L); //人格权纠纷

        amountStandard.put("0020601", Long.MAX_VALUE); //劳动争议、人事纠纷
        amountStandard.put("0020602", Long.MAX_VALUE); //劳动争议、人事纠纷

        amountStandard.put("00302", 2500L); //著作权合同纠纷
        amountStandard.put("00301", 2500L); //著作权合同纠纷
    }

    public static void initAmountLevels() {
        amountLevels.add(new AmountLevel(0L, 0d));
        amountLevels.add(new AmountLevel(10000L, 0d));
        amountLevels.add(new AmountLevel(10000L*10, 2.5d/100));
        amountLevels.add(new AmountLevel(10000L*20, 2.0d/100));
        amountLevels.add(new AmountLevel(10000L*50, 1.5d/100));
        amountLevels.add(new AmountLevel(10000L*100, 1.0d/100));
        amountLevels.add(new AmountLevel(10000L*200, 0.9d/100));
        amountLevels.add(new AmountLevel(10000L*500, 0.8d/100));
        amountLevels.add(new AmountLevel(10000L*1000, 0.7d/100));
        amountLevels.add(new AmountLevel(10000L*2000, 0.6d/100));
        amountLevels.add(new AmountLevel(Long.MAX_VALUE, 0.5/100));

        AmountLevel lastMountLevel = new AmountLevel(0L, 0d);
        lastMountLevel.setCostLevel(0L);
        for(AmountLevel amountLevel: amountLevels) {
            Long rangeAmount = ((Double)((amountLevel.getAmountLevel() - lastMountLevel.getAmountLevel()) * amountLevel.getPercentage())).longValue();
            amountLevel.setCostLevel(lastMountLevel.getCostLevel() + rangeAmount);
            lastMountLevel = amountLevel;
        }
    }
}
