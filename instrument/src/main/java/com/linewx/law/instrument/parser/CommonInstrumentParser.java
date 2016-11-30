package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.exception.InstrumentErrorCode;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.instrument.utils.AmountParserUtil;
import com.linewx.law.instrument.utils.ContentClearUtil;
import com.linewx.law.instrument.utils.ReasonUtil;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.ParseStateMachine;
import com.linewx.law.parser.json.RuleJson;

import java.util.List;
import java.util.Map;

/**
 * Created by lugan on 11/30/2016.
 */
public class CommonInstrumentParser implements InstrumentParser{
    protected  RuleJson rule;
    protected ParseStateMachine parseStateMachine;

    public CommonInstrumentParser(RuleJson rule) {
        this.rule = rule;
        parseStateMachine = new ParseStateMachine(rule);
    }
    @Override
    public Instrument parse(List<String> statements) {
        return null;
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

        //二审
        //relatedNumber关联案件组
        List<String> relatedNumberResults = results.get("relatedNumber");
        validateField(relatedNumberResults, "relatedNumberResults", true, 1);
        instrument.setRelatedNumber(relatedNumberResults.get(0));


        //appellantIsAccuser上诉人是否原审原告
        List<String> appellantIsAccuserResults = results.get("appellantIsAccuser");
        validateField(appellantIsAccuserResults, "appellantIsAccuser", true, null);
        Boolean appellantIsAccuser = false;
        String appellantInfo = appellantIsAccuserResults.get(0);
        if (appellantInfo.contains("原告")) {
            appellantIsAccuser = true;
        }
        instrument.setAppellantIsAccuser(appellantIsAccuser);

        //judgeType 审判类型
        String judgeType = null;
        List<String> judgeTypeResults1 = results.get("judgeType1");
        if (judgeTypeResults1 != null && !judgeTypeResults1.isEmpty()) {
            judgeType = "1";
        }

        if (judgeType != null) {
            List<String> judgeTypeResults2 = results.get("judgeType2");
            if (judgeTypeResults2 != null && !judgeTypeResults2.isEmpty()) {
                judgeType = "2";
            }
        }

        if (judgeType != null) {
            List<String> judgeTypeResults3 = results.get("judgeType3");
            if (judgeTypeResults3 != null && !judgeTypeResults3.isEmpty()) {
                String judgeTypeContent3 = judgeTypeResults3.get(0);
                if (judgeTypeContent3.contains("撤销")) {
                    judgeType = "3";
                }
            }
        }

        if (judgeType == null) {
            judgeType = "0";
        }else {
            instrument.setJudgeType(judgeType);
        }

        //finalConciliation 二审调解结案
        //上诉人委托代理人
        //被上诉人委托代理人
        //二审律师缺勤标记
        //二审缺勤率

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
}
