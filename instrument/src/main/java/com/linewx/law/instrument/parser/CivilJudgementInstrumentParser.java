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

import java.util.List;
import java.util.Map;

/**
 * Created by lugan on 11/23/2016.
 */
public class CivilJudgementInstrumentParser implements InstrumentParser {
    private static Logger logger = LoggerFactory.getLogger(CivilJudgementInstrumentParser.class);

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

    public CivilJudgementInstrumentParser(RuleJson rule) {
        this.rule = rule;
        parseStateMachine = new ParseStateMachine(rule);
    }

    @Override
    public Instrument parse(List<String> statements) {

        ParseContext context = new ParseContext();
        context.setCurrentState("start");
        ParseStateMachine stateMachine = new ParseStateMachine(rule);
        stateMachine.run(context, statements);

        return parseContext(context);
    }

    private Instrument parseContext(ParseContext context) {
        Instrument instrument = new Instrument();
        
        Map<String, List<String>> results = context.getResults();

        /******* basic field ***********/

        //validate instrumentType:文书类型
        List<String> instrumentTypeResults = results.get("instrumentType");
        validateField(instrumentTypeResults, "instrumentType", true, 1);
        String instrumentType = instrumentTypeResults.get(0);
        instrument.setInstrumentType(instrumentType);

        //validate court:法院
        List<String> courtResults = results.get("court");
        validateField(courtResults, "court", true, 1);
        String court = courtResults.get(0);
        instrument.setCourt(court);


        //validate date:判决日期
        String date = "";
        List<String> abstractDateResults = results.get("abstractDate");
        if (abstractDateResults != null && !abstractDateResults.isEmpty()) {
            date = abstractDateResults.get(0);
            instrument.setDate(date);
        }

        if (date.isEmpty()) {
            List<String> dateResults = results.get("date");
            validateField(dateResults, "date", true, 1);
            date = dateResults.get(0);
            instrument.setDate(date);
        }

        //validate clerk:书记员
        List<String> clerkResults = results.get("clerk");
        validateField(clerkResults, "clerk", true, 1);
        String clerk = clerkResults.get(0);
        clerk = ContentClearUtil.clearAbstract(clerk);
        instrument.setClerk(clerk);

        //validate judge:法官
        //validate mainJudge:主审法官
        //validate secondaryJudge:非主审法官
        //validate secondaryJudge1:非主审法官1
        //validate secondaryJudge2:非主审法官2
        List<String> judgeResults = results.get("judge");
        validateField(judgeResults, "judge", true, 3);
        for (int i = 0; i < judgeResults.size(); i++) {
            if (i == 0) {
                instrument.setMainJudge(judgeResults.get(0));
            }

            if (i == 1) {
                instrument.setSecondaryJudge(judgeResults.get(1));
            }

            if (i == 2) {
                instrument.setSecondaryJudge2(judgeResults.get(2));
            }
        }

        //validate number:案号
        List<String> numberResults = results.get("number");
        validateField(numberResults, "number", true, 1);
        String number = numberResults.get(0);
        instrument.setNumber(number);

        //validate caseType:案件类型
        List<String> caseTypeResults = results.get("caseType");
        validateField(caseTypeResults, "caseType", true, 1);
        String caseType = caseTypeResults.get(0);
        instrument.setCaseType(caseType);


        //validate suiteDate:立案年份
        List<String> suiteDateResults = results.get("suiteDate");
        validateField(suiteDateResults, "suiteDate", true, 1);
        String suiteDate = suiteDateResults.get(0);
        instrument.setSuiteDate(suiteDate);

        //validate level:审级
        //todo: level detail
        List<String> levelResults = results.get("level");
        validateField(levelResults, "level", true, 1);
        String level = levelResults.get(0);
        instrument.setLevel(level);

        /********* end basic field ************/

        //validate accuser:原告
        List<String> accuserResults = results.get("accuser");
        validateField(accuserResults, "accuser", true, null);
        String accusers = String.join("|", accuserResults);
        instrument.setAccuser(accusers);

        //validate accuserLegalEntity:原告法人代表
        List<String> accuserLegalEntityResults = results.get("accuserLegalEntity");
        if (accuserLegalEntityResults != null && !accuserLegalEntityResults.isEmpty()) {
            String accuserLegalEntity = String.join("|", accuserLegalEntityResults);
            instrument.setAccuserLegalEntity(accuserLegalEntity);
        }

        //validate accuserLawyer:原告律师
        List<String> accuserLawyerResults = results.get("accuserLawyer");
        if (accuserLawyerResults != null && !accuserLawyerResults.isEmpty()) {
            String accuserLawyer = String.join("|", accuserLawyerResults);
            instrument.setAccuserLawyer(accuserLawyer);
        }

        //validate accuserLawyerOffice:原告律师的律师事务所
        List<String> accuserLawyerOfficeResults = results.get("accuserLawyerOffice");
        if (accuserLawyerOfficeResults != null && !accuserLawyerOfficeResults.isEmpty()) {
            String accuserLawyerOffice = String.join("|", accuserLawyerOfficeResults);
            instrument.setAccuserLawyerOffice(accuserLawyerOffice);
        }

        //validate lawyer and lawyer office
        if (accuserLawyerResults != null && accuserLawyerOfficeResults != null) {
            if (accuserLawyerOfficeResults.size() != accuserLawyerResults.size()) {
                throw new InstrumentParserException("mismatch accuserLawyer and accuserLawyerOffice");
            }
        } else if (accuserLawyerResults == null && accuserLawyerOfficeResults == null) {
            //both are null
        } else {
            throw new InstrumentParserException("mismatch accuserLawyer and accuserLawyerOffice");
        }

        //validate defendant:被告
        List<String> defendantResults = results.get("defendant");
        validateField(defendantResults, "defendant", true, null);
        String defendants = String.join("|", defendantResults);
        instrument.setDefendant(defendants);

        //validate defendantLegalEntity:被告法人代表
        List<String> defendantLegalEntityResults = results.get("defendantLegalEntity");
        if (defendantLegalEntityResults != null && !defendantLegalEntityResults.isEmpty()) {
            String defendantLegalEntity = String.join("|", defendantLegalEntityResults);
            instrument.setDefendantLegalEntity(defendantLegalEntity);
        }

        //validate defendantLawyer:被告律师
        List<String> defendantLawyerResults = results.get("defendantLawyer");
        if (defendantLawyerResults != null && !defendantLawyerResults.isEmpty()) {
            String defendantLawyer = String.join("|", defendantLawyerResults);
            instrument.setDefendantLawyer(defendantLawyer);
        }

        //validate defendantLawyerOffice:被告律师的律师事务所
        List<String> defendantLawyerOfficeResults = results.get("defendantLawyerOffice");
        if (defendantLawyerOfficeResults != null && !defendantLawyerOfficeResults.isEmpty()) {
            String defendantLawyerOffice = String.join("|", defendantLawyerOfficeResults);
            instrument.setDefendantLawyerOffice(defendantLawyerOffice);
        }

        //validate lawyer and lawyer office
        if (defendantLawyerResults != null && defendantLawyerOfficeResults != null) {
            if (defendantLawyerOfficeResults.size() != defendantLawyerResults.size()) {
                throw new InstrumentParserException("mismatch defendantLawyer and defendantLawyerOffice");
            }
        } else if (defendantLawyerResults == null && defendantLawyerOfficeResults == null) {
            //both are null
        } else {
            throw new InstrumentParserException("mismatch defendantLawyer and defendantLawyerOffice");
        }







        //validate reason:案由
        String reason = "";

        List<String> abstractReasonResults = results.get("abstractReason");
        if (abstractDateResults != null && !abstractDateResults.isEmpty()) {
            reason = abstractReasonResults.get(0);
            instrument.setReason(reason);
        }

        if (reason.isEmpty()) {
            List<String> reasonResults = results.get("reason");
            validateField(reasonResults, "reason", true, 1);
            reason = ReasonUtil.getReason(reasonResults.get(0));

            if (reason != null) {
                instrument.setReason(reason);
            }else {
                throw new InstrumentParserException("can not identify reason", InstrumentErrorCode.INPROPER_REASON);
            }

        }

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
                results.get("costOnAccuser"),
                results.get("costOnDefendant"),
                accuserResults,
                defendantResults,
                results.get("costUser"),
                results.get("costOnUser"),
                results.get("accuserAlias"),
                results.get("defendantAlias"));
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
        Boolean ignoreAmount = !AmountUtil.isCharge(ReasonUtil.getReasonNumber(reason), cost);
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


        return instrument;
    }

    private void validateField(List<String> fieldValues, String fieldName, Boolean required, Integer maxNumber) {
        if (required) {
            //validate required
            if (fieldValues == null || fieldValues.isEmpty()) {
                throw new InstrumentParserException("no " + fieldName + " found", InstrumentErrorCode.FIELD_MISSING);
            }
        }

        if (maxNumber != null) {
            if (fieldValues != null && fieldValues.size() > maxNumber) {
                throw new InstrumentParserException(maxNumber + " or more than " + fieldName + " have been found: " + fieldValues.toString(), InstrumentErrorCode.FILED_EXCEED);
            }
        }
    }

    private CostInformation calculateCost(Long cost,
                                          Boolean discountHalf,
                                          List<String> costsOnAccuser,
                                          List<String> costsOnDefendant,
                                          List<String> accuserResults,
                                          List<String> defendantResults,
                                          List<String> costUsers,
                                          List<String> costOnUsers,
                                          List<String> accuserAlias,
                                          List<String> defendantAlias) {
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
