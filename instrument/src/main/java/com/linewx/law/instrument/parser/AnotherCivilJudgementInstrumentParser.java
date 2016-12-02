package com.linewx.law.instrument.parser;

import com.linewx.law.instrument.model.Instrument;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.json.RuleJson;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lugan on 11/23/2016.
 */
public class AnotherCivilJudgementInstrumentParser extends BasicInstrumentParser implements InstrumentParser {
    //private static Logger logger = LoggerFactory.getLogger(FinalCivilJudgementInstrumentParser.class);

    public AnotherCivilJudgementInstrumentParser(RuleJson ruleJson) {
        super(ruleJson);
    }

    @Override
    void populateOtherField(ParseContext context, Instrument instrument) {
        Map<String, List<String>> results = context.getResults();
        //二审
        //relatedNumber关联案件组
        List<String> relatedNumberResults = results.get("relatedNumber");
        validateField(relatedNumberResults, "relatedNumberResults", true, null);
        Set<String> relatedNumberSet = new HashSet<>(relatedNumberResults);
        relatedNumberSet.removeIf(oneNumber -> oneNumber.equals(instrument.getNumber()));
        instrument.setRelatedNumber(String.join("|", relatedNumberSet));


       /* //appellantIsAccuser上诉人是否原审原告
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
        }
        instrument.setJudgeType(judgeType);

        //finalConciliation 二审调解结案
        instrument.setFinalConciliation(true);*/
        //上诉人委托代理人
        //被上诉人委托代理人
        //二审律师缺勤标记
        //二审缺勤率
    }
}
