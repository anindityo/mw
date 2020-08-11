package com.syb.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "doPaymentResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "reqPayment"})
public class PaymentResponse {

	

	@XmlElement(name="ReqPayment")
	private ReqPayment reqPayment;

	public ReqPayment getReqPayment() {
		return reqPayment;
	}

	public void setReqPayment(ReqPayment reqPayment) {
		this.reqPayment = reqPayment;
	}
	
	
	

	
}
