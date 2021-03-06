package com.syb.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ReqReverse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "timestamp", "messageId","responseCode", "responseDesc", "trackingRef", "storeId" })
public class ReqReverse {

	@XmlElement(name = "TimeStamp")
	private String timestamp;
	
	@XmlElement(name = "MessageID")
	private String messageId;
	
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

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}


	
}
