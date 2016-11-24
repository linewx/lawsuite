package com.linewx.law.instrument.Validator;

import com.linewx.law.instrument.InstrumentTypeEnum;
import com.linewx.law.instrument.exception.InstrumentParserException;
import com.linewx.law.parser.NameMapping;
import com.linewx.law.parser.ParseContext;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by luganlin on 11/22/16.
 */
public class CivilJudgementValidator implements Validator{
    public static final Logger logger = LoggerFactory.getLogger(CivilJudgementValidator.class);

    @Override
    public String getType() {
        return InstrumentTypeEnum.CIVIL_JUDGMENT.getInstrumentType();
    }

    @Override
    public Boolean validate(ParseContext context) {
        //validation
        Map<String, List<String>> results = context.getResults();

        //validate instrumentType:文书类型
        validateField(results, "instrumentType", true, 1);
        if (!this.getType().equals(results.get("instrumentType").get(0))) {
            throw new InstrumentParserException(results.get("instrumentType").get(0), InstrumentParserException.ErrorCode.UNSUPPORTED_TYPE);
        }

        //validate accuser:原告
        validateField(results, "accuser", true, null);
        //validate accuserLegalEntity:原告法人代表

        //validate accuserLawyer:原告律师
        //validate accuserLawyerOffice:原告律师的律师事务所
        List<String> accuserLawyerResults = results.get("accuserLawyer");
        List<String> accuserLawyerOfficeResults = results.get("accuserLawyer");
        if (accuserLawyerResults != null && accuserLawyerOfficeResults != null) {
            if (accuserLawyerOfficeResults.size() != accuserLawyerResults.size()) {
                throw new InstrumentParserException("mismatch accuserLawyer and accuserLawyerOffice");
            }
        }

        //validate defendant:被告  at least one
        validateField(results, "defendant", true, null);

        //validate defendantLegalEntity:被告法人代表

        //validate defendantLawyer:被告律师
        //validate defendantLawyerOffice:被告律师的律师事务所
        List<String> defendantLawyerResult = results.get("defendantLawyer");
        List<String> defendantLawyerOfficeResult = results.get("defendantLawyerOffice");
        if (defendantLawyerResult != null && defendantLawyerOfficeResult != null) {
            if (defendantLawyerResult.size() != defendantLawyerOfficeResult.size()) {

                throw new InstrumentParserException("mismatch defendantLawyer and defendantLawyerOffice:" + defendantLawyerResult.toString() + defendantLawyerOfficeResult.toString());
            }
        }else if (defendantLawyerOfficeResult == null && defendantLawyerResult != null) {
            throw new InstrumentParserException("on related lawyer office found");
        }else if (defendantLawyerOfficeResult != null && defendantLawyerResult == null) {
            throw new InstrumentParserException("found lawyer office, no related lawyer found");
        }

        //validate accuserProperty:原告企业类别和行业属性
        //validate defendantProperty:被告企业类别和行业属性

        //validate judge:法官
        //validate mainJudge:主审法官
        //validate secondaryJudge:非主审法官
        //validate secondaryJudge1:非主审法官1
        //validate secondaryJudge2:非主审法官2
        validateField(results, "judge", true, 3);

        //validate date:判决日期
        validateField(results, "date", true, 1);

        //validate clerk:书记员
        validateField(results, "clerk", true, null);

        //validate reason:案由
        validateField(results, "reason", true, 1);

        //validate number:案号
        validateField(results, "number", true, 1);

        //validate caseType:案件类型
        validateField(results, "caseType", true, 1);



        //validate suiteDate:立案年份
        validateField(results, "suiteDate", true, 1);

        //validate level:审级
        validateField(results, "level", true, 1);

        //validate court:法院
        validateField(results, "court", true, 1);

        //validate cost:案件受理费
        validateField(results, "cost", true, 1);

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

        return true;

    }

    private void validateField(Map<String, List<String>> results, String fieldName, Boolean required, Integer maxNumber) {
        List<String> oneResult = results.get(fieldName);
        if (required) {
            //validate required
            if (oneResult == null || oneResult.isEmpty()) {
                for(Map.Entry<String, String> name: NameMapping.names.entrySet()) {
                    logger.error(name.getValue() + ":" + (results.get(name.getKey()) == null ? "null" : results.get(name.getKey()).toString()));
                }
                throw new InstrumentParserException("no " + fieldName + " found", InstrumentParserException.ErrorCode.VALIDATION);


            }
        }

        if (maxNumber != null) {
            if(oneResult.size() > maxNumber) {
                for(Map.Entry<String, String> name: NameMapping.names.entrySet()) {
                    logger.error(name.getValue() + ":" + (results.get(name.getKey()) == null ? "null" : results.get(name.getKey()).toString()));
                }
                throw new InstrumentParserException(maxNumber + " or more than "+ fieldName +" have been found: " + oneResult.toString(), InstrumentParserException.ErrorCode.VALIDATION);
            }
        }
    }

}
