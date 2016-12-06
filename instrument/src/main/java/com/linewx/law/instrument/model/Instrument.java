package com.linewx.law.instrument.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
//import java.time.Instant;

@SuppressWarnings("serial")
@Entity
@Table(name = "Instrument")
public class Instrument implements java.io.Serializable {

	private static Logger logger = LoggerFactory.getLogger(Instrument.class);

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 1000)
	private String accuser;  //原告
	private String accuserAlias; //原告别名
	private String accuserLegalEntity;  //原告法人代表
	private String accuserLawyer;  //原告律师
	private String accuserLawyerOffice;  //原告律师的律师事务所

	@Column(length = 1000)
	private String defendant;  //被告
	private String defendantAlias; //被告别名
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
	@Column(name="startDate")
	private String date;  //判决日期
	private String clerk;  //书记员
	private String reason;  //案由

	@Column(name="suiteNumber")
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
	private Long accuserAmountPer;  //原告主要诉请获支持率
	private Long defendantAmountPer;  //被告抗辩获支持率
	private Boolean firstConciliation;  //一审调解结案

	//二审
	private String relatedNumber; //关联案件组
	private Boolean appellantIsAccuser; //上诉人是否原审原告
	private String judgeType; //审判类型
	private Boolean finalConciliation; //二审调解结案
	//上诉人委托代理人
	//被上诉人委托代理人
	//二审律师缺勤标记
	//二审缺勤率


	//metadata
	private String sourceType;
	private Long sourceId;
	private String sourceName;
	private int errorCode;
	private String errorMessage;

	@Column(columnDefinition = "TEXT")
	private String rawdata;




	public Instrument() {

	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static void setLogger(Logger logger) {
		Instrument.logger = logger;
	}

	public String getAccuser() {
		return accuser;
	}

	public void setAccuser(String accuser) {
		this.accuser = accuser;
	}

	public String getAccuserAlias() {
		return accuserAlias;
	}

	public void setAccuserAlias(String accuserAlias) {
		this.accuserAlias = accuserAlias;
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

	public String getDefendantAlias() {
		return defendantAlias;
	}

	public void setDefendantAlias(String defendantAlias) {
		this.defendantAlias = defendantAlias;
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

	public Boolean getFirstConciliation() {
		return firstConciliation;
	}

	public void setFirstConciliation(Boolean firstConciliation) {
		this.firstConciliation = firstConciliation;
	}

	public String getRawdata() {
		return rawdata;
	}

	public void setRawdata(String rawdata) {
		this.rawdata = rawdata;
	}

	public String getRelatedNumber() {
		return relatedNumber;
	}

	public void setRelatedNumber(String relatedNumber) {
		this.relatedNumber = relatedNumber;
	}

	public Boolean getAppellantIsAccuser() {
		return appellantIsAccuser;
	}

	public void setAppellantIsAccuser(Boolean appellantIsAccuser) {
		this.appellantIsAccuser = appellantIsAccuser;
	}

	public String getJudgeType() {
		return judgeType;
	}

	public void setJudgeType(String judgeType) {
		this.judgeType = judgeType;
	}

	public Boolean getFinalConciliation() {
		return finalConciliation;
	}

	public void setFinalConciliation(Boolean finalConciliation) {
		this.finalConciliation = finalConciliation;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		if (errorMessage != null && errorMessage.length() > 200) {
			this.errorMessage = errorMessage.substring(0, 200);
		}else {
			this.errorMessage = errorMessage;
		}
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public Long getSourceId() {
		return sourceId;
	}

	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}


}
