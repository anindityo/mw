package com.syb.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "doInqueryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "reqInquery"})
public class InqueryResponse {
	
	@XmlElement(name="ReqInquery")
	private ReqInquery reqInquery;

	public ReqInquery getReqInquery() {
		return reqInquery;
	}

	public void setReqInquery(ReqInquery reqInquery) {
		this.reqInquery = reqInquery;
	}
	
	

}
