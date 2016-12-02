package com.linewx.law.instrument.model.rawdata;

import org.omg.CORBA.DoubleHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
//import java.time.Instant;

@SuppressWarnings("serial")
@Entity
@Table(name = "Find_List_Zaisheng")
public class Rawdata implements java.io.Serializable {

	@Column(name = "ID")
	@Id
	private long id;

	@Column(name = "Find_List_id")
	private long listId;

	@Column(name= "Find_List_No", length = 240)
	private String no;

	@Column(name="Find_List_ajjzh", length = 200)
	private String ajjzh;

	@Column(name="Find_List_mc", length = 240)
	private String mc;

	@Column(name="Find_List_mk")
	private double mk;

	@Column(name="Find_List_pjly")
	private String pjly;

	@Column(name="Find_List_fy", length = 240)
	private String fy;

	@Column(name="Find_List_rq", length = 51)
	private String rq;

	@Column(name="Find_List_PN")
	private Double pn;

	@Column(name="Find_List_Tj", length = 255)
	private String tj;

	@Column(name="Find_List_LY", length = 255)
	private String ly;

	@Column(name="Find_List_Nr",  length = 10000)
	private String nr;

	@Column(name="Find_List_dq", length = 100)
	private String dq;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getAjjzh() {
		return ajjzh;
	}

	public void setAjjzh(String ajjzh) {
		this.ajjzh = ajjzh;
	}

	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public double getMk() {
		return mk;
	}

	public void setMk(double mk) {
		this.mk = mk;
	}

	public String getPjly() {
		return pjly;
	}

	public void setPjly(String pjly) {
		this.pjly = pjly;
	}

	public String getFy() {
		return fy;
	}

	public void setFy(String fy) {
		this.fy = fy;
	}

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public Double getPn() {
		return pn;
	}

	public void setPn(Double pn) {
		this.pn = pn;
	}

	public String getTj() {
		return tj;
	}

	public void setTj(String tj) {
		this.tj = tj;
	}

	public String getLy() {
		return ly;
	}

	public void setLy(String ly) {
		this.ly = ly;
	}

	public String getNr() {
		return nr;
	}

	public void setNr(String nr) {
		this.nr = nr;
	}

	public String getDq() {
		return dq;
	}

	public void setDq(String dq) {
		this.dq = dq;
	}

	public long getListId() {
		return listId;
	}

	public void setListId(long listId) {
		this.listId = listId;
	}
}
