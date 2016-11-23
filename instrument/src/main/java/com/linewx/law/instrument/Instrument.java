package com.linewx.law.instrument;

import com.linewx.law.instrument.Validator.InstrumentValidator;
import com.linewx.law.instrument.Validator.ValidationResult;
import com.linewx.law.instrument.Validator.Validator;
import com.linewx.law.instrument.utils.AmountParserUtil;
import com.linewx.law.instrument.utils.AmountUtil;
import com.linewx.law.instrument.utils.ReasonUtil;
import com.linewx.law.parser.NameMapping;
import com.linewx.law.parser.ParseContext;
import com.sun.deploy.util.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by luganlin on 11/22/16.
 */
public class Instrument {
    private String accuser;  //原告
    private String accuserLegalEntity;  //原告法人代表
    private String accuserLawyer;  //原告律师
    private String accuserLawyerOffice;  //原告律师的律师事务所
    private String defendant;  //被告
    private String defendantLegalEntity;  //被告法人代表
    private String defendantLawyer;  //被告律师
    private String defendantLawyerOffice;  //被告律师的律师事务所
    private String accuserProperty;  //原告企业类别和行业属性
    private String defendantProperty;  //被告企业类别和行业属性
    //private String judge;  //法官
    private String mainJudge;  //主审法官
    private String secondaryJudge;  //非主审法官
    private String secondaryJudge2;  //非主审法官1
    //private String secondaryJudge2;  //非主审法官2
    private String date;  //判决日期
    private String clerk;  //书记员
    private String reason;  //案由
    private String number;  //案号
    private String caseType;  //案件类型
    private String instrumentType;  //文书类型
    private String suiteDate;  //立案年份
    private String level;  //审级
    private String court;  //法院
    private Long cost;  //案件受理费
    private Long amount;  //诉请金额
    private Boolean ignoreAmount;  //诉请金额忽略标志
    private Boolean discountHalf;  //受理费减半
    private Long costOnDefendant;  //被告负担受理费
    private Long costOnAccuser;  //原告负担受理费
    private Long accuserWinPer;  //本案原告胜率
    private Long defendantWinPer;  //本案被告胜率
    private Long accuserAmount;  //原告主要诉请获支持金额
    private float accuserAmountPer;  //原告主要诉请获支持率
    private float defendantAmountPer;  //被告抗辩获支持率
    private Boolean firstConciliation;  //一审调解结案
    private List<String> errorMessage = new ArrayList<>();

    private Validator validator;
    private ParseContext context;


    public String getAccuser() {
        return accuser;
    }

    public void setAccuser(String accuser) {
        this.accuser = accuser;
    }

    public String getAccuserLegalEntity() {
        return accuserLegalEntity;
    }

    public void setAccuserLegalEntity(String accuserLegalEntity) {
        this.accuserLegalEntity = accuserLegalEntity;
    }

    public String getAccuserLawyer() {
        return accuserLawyer;
    }

    public void setAccuserLawyer(String accuserLawyer) {
        this.accuserLawyer = accuserLawyer;
    }

    public String getAccuserLawyerOffice() {
        return accuserLawyerOffice;
    }

    public void setAccuserLawyerOffice(String accuserLawyerOffice) {
        this.accuserLawyerOffice = accuserLawyerOffice;
    }

    public String getDefendant() {
        return defendant;
    }

    public void setDefendant(String defendant) {
        this.defendant = defendant;
    }

    public String getDefendantLegalEntity() {
        return defendantLegalEntity;
    }

    public void setDefendantLegalEntity(String defendantLegalEntity) {
        this.defendantLegalEntity = defendantLegalEntity;
    }

    public String getDefendantLawyer() {
        return defendantLawyer;
    }

    public void setDefendantLawyer(String defendantLawyer) {
        this.defendantLawyer = defendantLawyer;
    }

    public String getDefendantLawyerOffice() {
        return defendantLawyerOffice;
    }

    public void setDefendantLawyerOffice(String defendantLawyerOffice) {
        this.defendantLawyerOffice = defendantLawyerOffice;
    }

    public String getAccuserProperty() {
        return accuserProperty;
    }

    public void setAccuserProperty(String accuserProperty) {
        this.accuserProperty = accuserProperty;
    }

    public String getDefendantProperty() {
        return defendantProperty;
    }

    public void setDefendantProperty(String defendantProperty) {
        this.defendantProperty = defendantProperty;
    }

    public String getMainJudge() {
        return mainJudge;
    }

    public void setMainJudge(String mainJudge) {
        this.mainJudge = mainJudge;
    }

    public String getSecondaryJudge() {
        return secondaryJudge;
    }

    public void setSecondaryJudge(String secondaryJudge) {
        this.secondaryJudge = secondaryJudge;
    }

    public String getSecondaryJudge2() {
        return secondaryJudge2;
    }

    public void setSecondaryJudge2(String secondaryJudge2) {
        this.secondaryJudge2 = secondaryJudge2;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClerk() {
        return clerk;
    }

    public void setClerk(String clerk) {
        this.clerk = clerk;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getSuiteDate() {
        return suiteDate;
    }

    public void setSuiteDate(String suiteDate) {
        this.suiteDate = suiteDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Boolean getIgnoreAmount() {
        return ignoreAmount;
    }

    public void setIgnoreAmount(Boolean ignoreAmount) {
        this.ignoreAmount = ignoreAmount;
    }

    public Boolean getDiscountHalf() {
        return discountHalf;
    }

    public void setDiscountHalf(Boolean discountHalf) {
        this.discountHalf = discountHalf;
    }

    public Long getCostOnDefendant() {
        return costOnDefendant;
    }

    public void setCostOnDefendant(Long costOnDefendant) {
        this.costOnDefendant = costOnDefendant;
    }

    public Long getCostOnAccuser() {
        return costOnAccuser;
    }

    public void setCostOnAccuser(Long costOnAccuser) {
        this.costOnAccuser = costOnAccuser;
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

    public Long getAccuserAmount() {
        return accuserAmount;
    }

    public void setAccuserAmount(Long accuserAmount) {
        this.accuserAmount = accuserAmount;
    }

    public float getAccuserAmountPer() {
        return accuserAmountPer;
    }

    public void setAccuserAmountPer(float accuserAmountPer) {
        this.accuserAmountPer = accuserAmountPer;
    }

    public float getDefendantAmountPer() {
        return defendantAmountPer;
    }

    public void setDefendantAmountPer(float defendantAmountPer) {
        this.defendantAmountPer = defendantAmountPer;
    }

    public Boolean getFirstConciliation() {
        return firstConciliation;
    }

    public void setFirstConciliation(Boolean firstConciliation) {
        this.firstConciliation = firstConciliation;
    }



    public Instrument(ParseContext context) {
        this.context = context;
        this.validator = new InstrumentValidator();
    }

    public void loadContent() {
        ValidationResult validationResult = validator.validate(context);
        if (validationResult.getResult()) {
            setContent(context);
            printContent();
        }else {
            printError(validationResult.getMessage());
            //System.out.println("error:" + validationResult.getMessage());
        }

    }

    public void setContent(ParseContext context) {

        Map<String, List<String>> results = context.getResults();

        //set accuser:原告
        accuser = String.join("|", results.get("accuser"));

        //set accuserLegalEntity:原告法人代表
        List<String> accuserLegalEntityResult = results.get("accuserLegalEntity");
        if (accuserLegalEntityResult != null) {
            accuserLegalEntity = String.join("|", accuserLegalEntityResult);
        }

        //set accuserLawyer:原告律师
        List<String> accuserLawyerResult = results.get("accuserLawyer");
        if (accuserLawyerResult != null) {
            accuserLawyer = String.join("|", accuserLawyerResult);
        }


        //set accuserLawyerOffice:原告律师的律师事务所
        List<String> accuserLawyerOfficeResult = results.get("accuserLawyerOffice");
        if (accuserLawyerOfficeResult != null) {
            accuserLawyerOffice = String.join("|", accuserLawyerOfficeResult);
        }


        //set defendant:被告
        defendant = String.join("|", results.get("defendant"));

        //set defendantLegalEntity:被告法人代表
        List<String> defendantLegalEntityResult = results.get("defendantLegalEntity");
        if (defendantLegalEntityResult != null) {
            accuserLegalEntity = String.join("|", defendantLegalEntityResult);
        }

        //set defendantLawyer:被告律师
        List<String> defendantLawyerResult = results.get("defendantLawyer");
        if (defendantLawyerResult != null) {
            defendantLawyer = String.join("|", defendantLawyerResult);
        }

        //set defendantLawyerOffice:被告律师的律师事务所
        List<String> defendantLawyerOfficeResult = results.get("defendantLawyerOffice");
        if (defendantLawyerOfficeResult != null) {
            defendantLawyerOffice = String.join("|", defendantLawyerOfficeResult);
        }


        //set accuserProperty:原告企业类别和行业属性
        //set defendantProperty:被告企业类别和行业属性


        //set judge:法官
        //set mainJudge:主审法官
        //set secondaryJudge:非主审法官
        //set secondaryJudge1:非主审法官1
        //set secondaryJudge2:非主审法官2
        List<String> judgeResult = results.get("judge");
        for(int i=0; i<judgeResult.size(); i++) {
            if (i == 0) {
                mainJudge = judgeResult.get(0);
            }

            if (i == 1) {
                secondaryJudge =  judgeResult.get(1);
            }

            if (i== 2) {
                secondaryJudge2 = judgeResult.get(2);
            }
        }


        //set date:判决日期
        date = results.get("date").get(0);

        //set clerk:书记员
        clerk = results.get("clerk").get(0);

        //set reason:案由
        //todo: exception handle
        reason = ReasonUtil.getReason(results.get("reason").get(0));

        //set number:案号
        number = results.get("number").get(0);

        //set caseType:案件类型
        caseType = results.get("caseType").get(0);

        //set instrumentType:文书类型
        instrumentType = results.get("instrumentType").get(0);

        //set suiteDate:立案年份
        suiteDate = results.get("suiteDate").get(0);

        //set level:审级
        level = results.get("level").get(0);

        //set court:法院
        court = results.get("court").get(0);

        //set cost:案件受理费
        cost = AmountParserUtil.ParseLong(results.get("cost").get(0));

        //set ignoreAmount:诉请金额忽略标志
        //todo: exception handle
        ignoreAmount = !AmountUtil.isCharge(ReasonUtil.getReasonNumber(reason), cost);

        //set amount:诉请金额
        if (!ignoreAmount) {
            amount = AmountUtil.calculateAmount(cost);
        }else {
            amount = 0L;
        }

        //set discountHalf:受理费减半
        if (results.get("discountHalf") != null) {
            discountHalf = true;
        }


        //set costOnDefendant:被告负担受理费
        //set costOnAccuser:原告负担受理费
        //set accuserWinPer:本案原告胜率
        //set defendantWinPer:本案被告胜率
        calculateCost();

        //set accuserAmount:原告主要诉请获支持金额
        //set accuserAmountPer:原告主要诉请获支持率
        //set defendantAmountPer:被告抗辩获支持率
        calculateAmount();


        //set firstConciliation:一审调解结案
    }


    private void calculateCost() {
        Long totalCost = cost/2;
        if (totalCost == 0) {
            costOnDefendant = 0L;
            costOnAccuser = 0L;
            accuserWinPer = 50L;
            defendantAmountPer = 50L;
            return;
        }

        // caculate cost on accuser
        List<String> costsOnAccuser = context.getResults().get("costOnAccuser");
        if (costsOnAccuser != null && costsOnAccuser.size() == 1) {
            //有原告信息
            if (costsOnAccuser.get(0).isEmpty()) {
                //全部由原告承担
                costOnAccuser = totalCost;
                costOnDefendant = 0L;
            }else {
                //部分由原告承担
                costOnAccuser = Long.parseLong(costsOnAccuser.get(0));
                costOnDefendant = totalCost - costOnAccuser;
            }
        }else {
            //无原告信息
            List<String> costsOnDefendant = context.getResults().get("costOnDefendant");
            if (costsOnDefendant != null && costsOnDefendant.size() == 1) {
                //有被告信息
                if (costsOnDefendant.get(0).isEmpty()) {
                    costOnDefendant = totalCost;
                    costOnAccuser = 0L;
                }else {
                    costOnDefendant = Long.parseLong(costsOnDefendant.get(0));
                    costOnAccuser = totalCost - costOnDefendant;
                }
            }else {
                //无原告和被告信息
                List<String> costUsers = context.getResults().get("costUser");
                if (costUsers != null) {
                    //有负担费用人信息
                    for (String oneAccuser : context.getResults().get("accuser")) {
                        if (costUsers.get(0).contains(oneAccuser)) {
                            //原告人承担
                            costOnAccuser = totalCost;
                            costOnDefendant = 0L;
                            break;
                        }
                    }

                    for (String oneDefendant : context.getResults().get("defendant")) {
                        if (costUsers.get(0).contains(oneDefendant)) {
                            //被告人承担
                            costOnDefendant = totalCost;
                            costOnAccuser = 0L;
                        }
                    }

                }
            }
        }

        accuserWinPer = (totalCost - costOnAccuser) * 100/totalCost;
        defendantWinPer = 100 - accuserWinPer;
    }

    private void calculateAmount() {
        if (ignoreAmount || amount == 0) {
            accuserAmount = 0L;
            accuserAmountPer = 50L;
            defendantAmountPer = 50L;
        }else {
            List<String> accuserAmountLines = this.context.getResults().get("accuserAmountLines");
            if (accuserAmountLines == null) {
                //todo: amount line can not be found
                //log warning...
            }else {
                accuserAmount = AmountParserUtil.getMainAmountSum(String.join("", accuserAmountLines));
            }

            accuserAmountPer = accuserAmount * 100 / amount;
            defendantAmountPer = 100 - accuserAmountPer;
        }
    }


    public void printError(String error) {
        System.out.println("############### start parse ##########");
        System.out.println(String.join("\n", context.getResults().get("rawdata")));
        System.out.println("------ result ------");
        System.out.println("状态:" + context.getCurrentState());
        System.out.println("文件名称:" + context.getResults().get("filename"));
        System.out.println("error: " + error);
        System.out.println("############### end parse ##########");
    }

    public void printContent() {
        Class<?> c = this.getClass();
        System.out.println("############### start parse ##########");
        System.out.println(String.join("\n", context.getResults().get("rawdata")));
        System.out.println("------ result ------");
        System.out.println("状态:" + context.getCurrentState());
        System.out.println("文件名称:" + context.getResults().get("filename"));
        //System.out.println("原告金额:" + this.getResults().get("accuserAmountLines"));

        for(Map.Entry<String,String> oneName: NameMapping.names.entrySet()) {
            try {
                Method method = c.getDeclaredMethod("get" + WordUtils.capitalize(oneName.getKey()));
                System.out.println(oneName.getValue() + ":" + method.invoke(this));
            }catch(Exception e){

            }
        }
        System.out.println("############### end parse ##########");
    }

}
