package com.syb.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "doPayment")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "timestamp", "messageId", "productId", "paymentCode","amount", "trackingRef", "storeId" })
public class PaymentRequest {
	
	@XmlElement(name = "TimeStamp")
	private String timestamp;
	@XmlElement(name = "MessageId")
	private String messageId;
	@XmlElement(name = "ProductId")
	private String productId;
	@XmlElement(name = "PaymentCode")
	private String paymentCode;
	@XmlElement(name = "Amount")
	private String amount;
	@XmlElement(name = "TrackingRef")
	private String trackingRef;
	@XmlElement(name = "StoreId")
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
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getPaymentCode() {
		return paymentCode;
	}
	public void setPaymentCode(String paymentCode) {
		this.paymentCode = paymentCode;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTrackingRef() {
		return trackingRef;
	}
	public void setTrackingRef(String trackingRef) {
		this.trackingRef = trackingRef;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	

}
