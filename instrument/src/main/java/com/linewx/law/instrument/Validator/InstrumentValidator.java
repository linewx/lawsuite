package com.linewx.law.instrument.Validator;

import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.parser.ParseContext;

import java.util.List;
import java.util.Map;

/**
 * Created by luganlin on 11/22/16.
 */
public class InstrumentValidator implements Validator{
    @Override
    public ValidationResult validate(ParseContext context) {
        //validation
        Map<String, List<String>> results = context.getResults();

        //validate accuser:原告
        validateField(results, "accuser", true, null);
        //validate accuserLegalEntity:原告法人代表
        //validate accuserLawyer:原告律师
        //validate accuserLawyerOffice:原告律师的律师事务所
        //validate defendant:被告  at least one
        List<String> defendantResult = results.get("defendant");
        if (defendantResult == null || defendantResult.isEmpty()) {
            throw new InstrumentParserException("no defendant found", InstrumentParserException.ErrorCode.VALIDATION);
        }
        //validate defendantLegalEntity:被告法人代表
        //validate defendantLawyer:被告律师
        //validate defendantLawyerOffice:被告律师的律师事务所
        //validate accuserProperty:原告企业类别和行业属性
        //validate defendantProperty:被告企业类别和行业属性
        //validate judge:法官
        //validate mainJudge:主审法官
        //validate secondaryJudge:非主审法官
        //validate secondaryJudge1:非主审法官1
        //validate secondaryJudge2:非主审法官2
        List<String> judgeResult = results.get("judge");
        if (judgeResult == null || judgeResult.isEmpty()) {
            throw  new InstrumentParserException("no judge found", InstrumentParserException.ErrorCode.VALIDATION);
        }else if(judgeResult.size() > 3) {
            throw new InstrumentParserException("more than 3 judges have been found: " + judgeResult.toString(), InstrumentParserException.ErrorCode.VALIDATION);
        }
        
        //validate date:判决日期
        List<String> dateResult = results.get("date");
        if (dateResult == null || dateResult.isEmpty()) {
            throw new InstrumentParserException("no date found", InstrumentParserException.ErrorCode.VALIDATION);
        }else if(dateResult.size() > 1) {
            throw new InstrumentParserException("two or more than date have been found: " + dateResult.toString(), InstrumentParserException.ErrorCode.VALIDATION);
        }
        //validate clerk:书记员
        List<String> clerkResult = results.get("clerk");
        if (clerkResult == null || clerkResult.isEmpty()) {
            throw new InstrumentParserException("no clerk found", InstrumentParserException.ErrorCode.VALIDATION);
        }else if(clerkResult.size() > 1) {
            throw new InstrumentParserException("two or more than clerk have been found: " + clerkResult.toString(), InstrumentParserException.ErrorCode.VALIDATION);
        }

        //validate reason:案由
        List<String> reasonResult = results.get("reason");
        if (reasonResult == null || reasonResult.isEmpty()) {
            throw new InstrumentParserException("no reason found", InstrumentParserException.ErrorCode.VALIDATION);
        }else if(reasonResult.size() > 1) {
            throw new InstrumentParserException("two or more than reason have been found: " + clerkResult.toString(), InstrumentParserException.ErrorCode.VALIDATION);
        }

        //validate number:案号
        List<String> numberResult = results.get("number");
        if (numberResult == null || numberResult.isEmpty()) {
            throw new InstrumentParserException("no number found", InstrumentParserException.ErrorCode.VALIDATION);
        }else if(numberResult.size() > 1) {
            throw new InstrumentParserException("two or more than reason have been found: " + clerkResult.toString(), InstrumentParserException.ErrorCode.VALIDATION);
            
        }

        //validate caseType:案件类型
        List<String> caseTypeResult = results.get("caseType");
        if (caseTypeResult == null || caseTypeResult.isEmpty()) {
            throw new InstrumentParserException("no caseType found", InstrumentParserException.ErrorCode.VALIDATION);
            return new ValidationResult(false, "no case type found");
        }else if(caseTypeResult.size() > 1) {
            return new ValidationResult(false, "two or more case type have been found: " + caseTypeResult.toString());
        }

        //validate instrumentType:文书类型
        List<String> instrumentTypeResult = results.get("instrumentType");
        if (instrumentTypeResult == null || instrumentTypeResult.isEmpty()) {
            return new ValidationResult(false, "no instrument type found");
        }else if(instrumentTypeResult.size() > 1) {
            return new ValidationResult(false, "two or more instrument type have been found: " + instrumentTypeResult.toString());
        }

        //validate suiteDate:立案年份
        List<String> suiteDateResult = results.get("suiteDate");
        if (suiteDateResult == null || suiteDateResult.isEmpty()) {
            return new ValidationResult(false, "no instrument type found");
        }else if(suiteDateResult.size() > 1) {
            return new ValidationResult(false, "two or more instrument type have been found: " + suiteDateResult.toString());
        }

        //validate level:审级
        List<String> levelResult = results.get("level");
        if (levelResult == null || levelResult.isEmpty()) {
            return new ValidationResult(false, "no level found");
        }else if(suiteDateResult.size() > 1) {
            return new ValidationResult(false, "two or more level have been found: " + levelResult.toString());
        }

        //validate court:法院
        List<String> courtResult = results.get("court");
        if (courtResult == null || courtResult.isEmpty()) {
            return new ValidationResult(false, "no court found");
        }else if(courtResult.size() > 1) {
            return new ValidationResult(false, "two or more court have been found: " + courtResult.toString());
        }
        //validate cost:案件受理费
        List<String> costResult = results.get("cost");
        if (costResult == null || costResult.isEmpty()) {
            return new ValidationResult(false, "no cost found");
        }else if(costResult.size() > 1) {
            return new ValidationResult(false, "two or more court have been found: " + costResult.toString());
        }

        //validate amount:诉请金额
        //validate ignoreAmount:诉请金额忽略标志
        //validate discountHalf:受理费减半
        //validate costOnDefendant:被告负担受理费
        //validate costOnAccuser:原告负担受理费
        //validate accuserWinPer:本案原告胜率
        //validate defendantWinPer:本案被告胜率
        //validate accuserAmount:原告主要诉请获支持金额
        //validate accuserAmountPer:原告主要诉请获支持率
        //validate defendantAmountPer:被告抗辩获支持率
        //validate firstConciliation:一审调解结案

        return new ValidationResult(true, "");
    }

    private void validateField(Map<String, List<String>> results, String fieldName, Boolean required, Integer maxNumber) {
        List<String> oneResult = results.get(fieldName);
        if (required) {
            //validate required
            if (oneResult == null || oneResult.isEmpty()) {
                throw new InstrumentParserException("no number found", InstrumentParserException.ErrorCode.VALIDATION);
            }
        }

        if (maxNumber != null) {
            if(oneResult.size() > maxNumber) {
                throw new InstrumentParserException("two or more than reason have been found: " + oneResult.toString(), InstrumentParserException.ErrorCode.VALIDATION);
            }
        }
    }

}
