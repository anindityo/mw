package com.syb.bean;

import javax.xml.soap.SOAPMessage;

public class ClientResponse {

	private SOAPMessage soapMessage;
	private boolean timeout;
	
	
	public SOAPMessage getSoapMessage() {
		return soapMessage;
	}
	public void setSoapMessage(SOAPMessage soapMessage) {
		this.soapMessage = soapMessage;
	}
	public boolean isTimeout() {
		return timeout;
	}
	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}
	
}
