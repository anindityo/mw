package com.syb.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Details")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "installmentPeriod", "installmentNumber", "installmentAmount","installmentPenalty"})
public class Detail {

	@XmlElement(name="InstallmentPeriod")
	private String installmentPeriod;
	@XmlElement(name="InstallmentNumber")
	private Long installmentNumber;
	@XmlElement(name="InstallmentAmount")
	private Long installmentAmount;
	@XmlElement(name="InstallmentPenalty")
	private Long installmentPenalty;
	
	public Detail () {
		
	}

	public String getInstallmentPeriod() {
		return installmentPeriod;
	}

	public void setInstallmentPeriod(String installmentPeriod) {
		this.installmentPeriod = installmentPeriod;
	}

	public Long getInstallmentNumber() {
		return installmentNumber;
	}

	public void setInstallmentNumber(Long installmentNumber) {
		this.installmentNumber = installmentNumber;
	}

	public Long getInstallmentAmount() {
		return installmentAmount;
	}

	public void setInstallmentAmount(Long installmentAmount) {
		this.installmentAmount = installmentAmount;
	}

	public Long getInstallmentPenalty() {
		return installmentPenalty;
	}

	public void setInstallmentPenalty(Long installmentPenalty) {
		this.installmentPenalty = installmentPenalty;
	}

	
	
	
	
	
}
