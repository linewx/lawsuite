package com.linewx.law.parser.Processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lugan on 11/18/2016.
 */
public class AmountProcessor implements Processor {
    private Map<String, Long> amountStandard = new HashMap<>();
    private List<AmountLevel> amountLevels = new ArrayList<>();


    public AmountProcessor(){
        init();
        initAmountLevels();


    }

    @Override
    public String getName() {
        return "amount";
    }

    @Override
    public String transform(String source) {
        try {
            Long cost = Long.parseLong(source);
            return calculateAmount(cost).toString();
        }catch (Exception e) {
            return "0";
        }

    }

    public Long calculateAmount(Long cost) {
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

    public Boolean isCharge(String reasonNumber, Long cost) {
        if (reasonNumber != null && reasonNumber.length() >= 5) {
            String reasonCategory = reasonNumber.substring(0, 5);
            return cost >= amountStandard.getOrDefault(reasonCategory, 0L);

        } else {
            System.out.println("不明确的案由");
            return false;
        }
    }

    public void init() {
        amountStandard.put("00202", 2000L); //婚姻家庭、继承纠纷
        amountStandard.put("00201", 1000L); //人格权纠纷
        amountStandard.put("00201", Long.MAX_VALUE); //劳动争议、人事纠纷
        amountStandard.put("00302", 2500L); //著作权合同纠纷
    }

    public void initAmountLevels() {
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
