package com.syb.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ReqInquery")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "timestamp", "messageId", "contractNo", "customer", "policeNum", "type","detail","totalAmount", "fee", "responseCode", "responseDesc", "trackingRef", "storeId" })
public class ReqInquery  {

	@XmlElement(name = "TimeStamp")
	private String timestamp;
	
	@XmlElement(name = "MessageID")
	private String messageId;
	
	@XmlElement(name = "ContractNo")
	private String contractNo;
	
	@XmlElement(name = "Customer")
	private String customer;
	
	@XmlElement(name = "PoliceNum")
	private String policeNum;
	
	@XmlElement(name = "Type")
	private String type;
	
	@XmlElementWrapper(name = "Details")
	@XmlElement(name = "Detail")
	private List<Detail> detail;
	
	@XmlElement(name = "TotalAmount")
	private Long totalAmount;
	
	@XmlElement(name = "Fee")
	private Long fee;
	
	@XmlElement(name = "ResponseCode")
	private String responseCode;
	
	@XmlElement(name = "ResponseDesc")
	private String responseDesc;
	
	@XmlElement(name = "TrackingRef")
	private String trackingRef;
	
	@XmlElement(name = "StoreID")
	private String storeId;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getPoliceNum() {
		return policeNum;
	}

	public void setPoliceNum(String policeNum) {
		this.policeNum = policeNum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Detail> getDetail() {
		return detail;
	}

	public void setDetail(List<Detail> detail) {
		this.detail = detail;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getFee() {
		return fee;
	}

	public void setFee(Long fee) {
		this.fee = fee;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseDesc() {
		return responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	public String getTrackingRef() {
		return trackingRef;
	}

	public void setTrackingRef(String trackingRef) {
		this.trackingRef = trackingRef;
	}

}
