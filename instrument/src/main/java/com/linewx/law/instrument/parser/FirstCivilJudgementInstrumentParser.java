package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.utils.AmountParserUtil;
import com.linewx.law.instrument.utils.AmountUtil;
import com.linewx.law.instrument.utils.ContentClearUtil;
import com.linewx.law.instrument.utils.ReasonUtil;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ParseStateMachine;
import com.linewx.law.parser.json.RuleJson;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lugan on 11/23/2016.
 */
public class FirstCivilJudgementInstrumentParser extends BasicInstrumentParser implements InstrumentParser {
    private static Logger logger = LoggerFactory.getLogger(FirstCivilJudgementInstrumentParser.class);

    private ParseStateMachine parseStateMachine;
    private RuleJson rule;

    private static class CostInformation {
        Long costOnAccuser;
        Long costOnDefendant;

        public CostInformation(Long costOnAccuser, Long costOnDefendant) {
            this.costOnAccuser = costOnAccuser;
            this.costOnDefendant = costOnDefendant;
        }

        public Long getCostOnAccuser() {
            return costOnAccuser;
        }

        public void setCostOnAccuser(Long costOnAccuser) {
            this.costOnAccuser = costOnAccuser;
        }

        public Long getCostOnDefendant() {
            return costOnDefendant;
        }

        public void setCostOnDefendant(Long costOnDefendant) {
            this.costOnDefendant = costOnDefendant;
        }
    }

    private static class CostPerInformation {
        private Long accuserWinPer;
        private Long defendantWinPer;

        public CostPerInformation(Long accuserWinPer, Long defendantWinPer) {
            this.accuserWinPer = accuserWinPer;
            this.defendantWinPer = defendantWinPer;
        }

        public Long getAccuserWinPer() {
            return accuserWinPer;
        }

        public void setAccuserWinPer(Long accuserWinPer) {
            this.accuserWinPer = accuserWinPer;
        }

        public Long getDefendantWinPer() {
            return defendantWinPer;
        }

        public void setDefendantWinPer(Long defendantWinPer) {
            this.defendantWinPer = defendantWinPer;
        }
    }

    public static class AmountPerInformation {
        private Long accuserAmountPer;
        private Long defendantAmountPer;

        public AmountPerInformation(Long accuserAmountPer, Long defendantAmountPer) {
            this.accuserAmountPer = accuserAmountPer;
            this.defendantAmountPer = defendantAmountPer;
        }

        public Long getAccuserAmountPer() {
            return accuserAmountPer;
        }

        public void setAccuserAmountPer(Long accuserAmountPer) {
            this.accuserAmountPer = accuserAmountPer;
        }

        public Long getDefendantAmountPer() {
            return defendantAmountPer;
        }

        public void setDefendantAmountPer(Long defendantAmountPer) {
            this.defendantAmountPer = defendantAmountPer;
        }
    }

    public FirstCivilJudgementInstrumentParser(RuleJson rule) {
        super(rule);
    }

    @Override
    void populateOtherField(ParseContext context, Instrument instrument) {

        Map<String, List<String>> results = context.getResults();

        //validate cost:案件受理费
        List<String> costResults = results.get("cost");
        validateField(costResults, "cost", true, 1);

        Long cost = AmountParserUtil.ParseLong(costResults.get(0));
        instrument.setCost(cost);

        //validate discountHalf:受理费减半
        List<String> discountHalfResults = results.get("discountHalf");
        validateField(discountHalfResults, "discountHalf", false, 1);
        Boolean discountHalf = false;
        if (discountHalfResults != null && !discountHalfResults.isEmpty()) {
            discountHalf = true;
        }
        instrument.setDiscountHalf(discountHalf);

        //validate costOnDefendant:被告负担受理费
        //validate costOnAccuser:原告负担受理费
        CostInformation costInformation = calculateCost(cost,
                discountHalf,
                context);
        Long costOnAccuser = costInformation.getCostOnAccuser();
        Long costOnDefendant = costInformation.getCostOnDefendant();
        instrument.setCostOnAccuser(costOnAccuser);
        instrument.setCostOnDefendant(costOnDefendant);

        //validate accuserWinPer:本案原告胜率
        //validate defendantWinPer:本案被告胜率

        CostPerInformation costPerInformation = calculateCostPer(cost, discountHalf, costOnAccuser, costOnDefendant);
        instrument.setAccuserWinPer(costPerInformation.getAccuserWinPer());
        instrument.setDefendantWinPer(costPerInformation.getDefendantWinPer());


        //validate ignoreAmount:诉请金额忽略标志
        Boolean ignoreAmount = !AmountUtil.isCharge(ReasonUtil.getReasonNumber(instrument.getReason()), cost);
        instrument.setIgnoreAmount(ignoreAmount);

        //validate amount:诉请金额
        //set amount:诉请金额
        Long amount = 0L;
        if (!ignoreAmount) {

            amount = AmountUtil.calculateAmount(cost);
        }
        instrument.setAmount(amount);


        //validate accuserAmount:原告主要诉请获支持金额
        Long accuserAmount = calcuateAccuserAmount(amount, results.get("accuserAmountLines"));

        //validate accuserAmountPer:原告主要诉请获支持率
        //validate defendantAmountPer:被告抗辩获支持率
        AmountPerInformation amountPerInformation = calculateAmountPer(amount, accuserAmount);
        instrument.setAccuserAmountPer(amountPerInformation.getAccuserAmountPer());
        instrument.setDefendantWinPer(amountPerInformation.getDefendantAmountPer());

        //validate firstConciliation:一审调解结案

        //validate accuserProperty:原告企业类别和行业属性
        //validate defendantProperty:被告企业类别和行业属性
    }

    private CostInformation calculateCost(Long cost,
                                          Boolean discountHalf,
                                          ParseContext context) {
        Map<String, List<String>> results = context.getResults();

        List<String> costsOnAccuser = results.get("costOnAccuser");
        List<String> costsOnDefendant = results.get("costOnDefendant");
        List<String> accuserResults = results.get("accuser");
        List<String> defendantResults = results.get("defendant");
        List<String> costUsers = results.get("costUser");
        List<String> costOnUsers = results.get("costOnUser");
        List<String> accuserAlias = results.get("accuserAlias");
        List<String> defendantAlias = results.get("defendantAlias");
        Long totalCost = cost;
        Long costOnAccuser = null;
        Long costOnDefendant = null;

        if (discountHalf != null && discountHalf) {
            totalCost = totalCost / 2;
        }

        if (totalCost == 0) {
            costOnDefendant = 0L;
            costOnAccuser = 0L;
            return new CostInformation(costOnAccuser, costOnDefendant);
        }

        // caculate cost on accuser
        if (costsOnAccuser != null && costsOnAccuser.size() == 1) {
            //有原告信息
            if (costsOnAccuser.get(0).isEmpty()) {
                //全部由原告承担
                costOnAccuser = totalCost;
                costOnDefendant = 0L;
            } else {
                //部分由原告承担
                costOnAccuser = Long.parseLong(costsOnAccuser.get(0));
                costOnDefendant = totalCost - costOnAccuser;
            }
        } else {
            //无原告信息
            if (costsOnDefendant != null) {
                //有被告信息
                if (costsOnDefendant.get(0).isEmpty()) {
                    costOnDefendant = totalCost;
                    costOnAccuser = 0L;
                } else {
                    costOnDefendant = Long.parseLong(costsOnDefendant.get(0));
                    costOnAccuser = totalCost - costOnDefendant;
                }
            } else {
                //无原告和被告信息

                if (costUsers != null && costOnUsers != null) {
                    //有负担费用人信息
                    Boolean found = false;

                    for (String oneAccuser : accuserResults) {
                        if (costUsers.get(0).contains(oneAccuser)) {
                            //原告人承担
                            if (costOnUsers.get(0).isEmpty()) {
                                costOnAccuser = totalCost;
                                costOnDefendant = 0L;

                            } else {
                                costOnAccuser = AmountParserUtil.ParseLong(costOnUsers.get(0));
                                costOnDefendant = totalCost - costOnAccuser;
                            }

                            found = true;
                            break;


                        }
                    }

                    if (!found) {
                        for (String oneDefendant : defendantResults) {
                            if (costUsers.get(0).contains(oneDefendant) || oneDefendant.contains(costUsers.get(0))) {
                                //被告人承担
                                if (costOnUsers.get(0).isEmpty()) {
                                    costOnDefendant = totalCost;
                                    costOnAccuser = 0L;

                                } else {
                                    costOnDefendant = AmountParserUtil.ParseLong(costOnUsers.get(0));
                                    costOnAccuser = totalCost - costOnDefendant;
                                }

                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found) {
                        if (accuserAlias != null) {
                            for (String oneAccuserAlias : accuserAlias) {
                                if (costUsers.get(0).contains(oneAccuserAlias) || oneAccuserAlias.contains(costUsers.get(0))) {
                                    //被告人承担
                                    costOnDefendant = 0L;
                                    costOnAccuser = totalCost;
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!found) {
                        if (defendantAlias != null) {
                            for (String oneDefendantAlias : defendantAlias) {
                                if (costUsers.get(0).contains(oneDefendantAlias) || oneDefendantAlias.contains(costUsers.get(0))) {
                                    //被告人承担
                                    costOnDefendant = totalCost;
                                    costOnAccuser = 0L;
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }


                }
            }
        }

        if (costOnAccuser == null || costOnDefendant == null) {
            throw new InstrumentParserException("can not identify the cost on accuser on defendant");
        }

        return new CostInformation(costOnAccuser, costOnDefendant);
    }

    private CostPerInformation calculateCostPer(Long cost, Boolean discountHalf, Long costOnAccuser, Long costOnDefendant) {
        Long totalCost = cost;
        if (discountHalf) {
            totalCost = totalCost / 2;
        }

        if (cost.equals(0L)) {
            return new CostPerInformation(50L, 50L);
        }

        Long costOnAccuserPer = costOnAccuser * 100 / totalCost;
        Long costOnDefendantPer = 100 - costOnAccuser;
        return new CostPerInformation(costOnAccuserPer, costOnDefendantPer);
    }

    private AmountPerInformation calculateAmountPer(Long amount, Long amountAccuser) {
        if (amount == 0) {
            return new AmountPerInformation(50L, 50L);
        } else if (amountAccuser > amount) {
            return new AmountPerInformation(100L, 0L);
        } else {
            Long amountOnAccuserPer = amountAccuser * 100 / amount;
            Long amountOnDefedantPer = 100 - amountOnAccuserPer;
            return new AmountPerInformation(amountOnAccuserPer, amountOnDefedantPer);
        }
    }

    private Long calcuateAccuserAmount(Long amount, List<String> accuserAmountLines) {
        if (amount == 0) {
            return 0L;
        } else {
            if (accuserAmountLines == null) {
                //todo: amount line can not be found
                //log warning...
                throw new InstrumentParserException("accuser amount not found");
                //return 0L;
            } else {
                Long accuserAmount = AmountParserUtil.getMainAmountSum(String.join("", accuserAmountLines));

                if (accuserAmount > amount) {
                    return accuserAmount;
                    //throw new InstrumentParserException(String.format("accuser amount is larger than total amount: accuser amount %s, total amount %s", accuserAmount.toString(), amount.toString()));
                } else {
                    return accuserAmount;
                }

            }
        }
    }
}
