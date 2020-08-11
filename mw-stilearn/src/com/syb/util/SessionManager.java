package com.syb.util;

//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.packager.ISO87APackager;

import com.syb.bean.InqueryResponse;
//import com.syb.bean.ReqInquery;
import com.syb.bean.Request;

public class SessionManager {
	
	private static final Logger logger = Logger.getLogger(SessionManager.class);
	
	private static SessionManager instance = new SessionManager();
	
	private ISOPackager isoPackager;
	private Integer useHeader = 0;
	private Integer includeHeaderLength = 0;
	private String trailer;
	
	private int readTimeout;
	private int connectTimeout;
	
	private String url;
	
	private String productcode;
	private String userId;
	private String pass;
	private String channel;
	
	private String rcHttpError;	
	private ExecutorService connectionExecutor = Executors.newCachedThreadPool();
	
	private JAXBContext inquiryContext = null;
	private JAXBContext inquiryResponseContext = null;

	
	protected SessionManager() {
		logger.info("Start load config");
		this.isoPackager = new ISO87APackager();
		
		String filepath = System.getProperty("user.dir") + "/settings/settings.properties";
		Properties props = FileHelper.getFile(filepath);
		
		String channelType = (String) props.getProperty("header");
		if (channelType.equalsIgnoreCase(Constant.HEADER_TYPE_SYBHE)) {
			this.useHeader = 1;
		} else if (channelType.equalsIgnoreCase(Constant.HEADER_TYPE_SYBHI)) {
			this.useHeader = 1;
			this.includeHeaderLength = 1;
		} else if (channelType.equalsIgnoreCase(Constant.HEADER_TYPE_ASCII)) {
			this.useHeader = 2;
		} else if (channelType.equalsIgnoreCase(Constant.HEADER_TYPE_DJBB)) {
			this.trailer = "-1";
		}
		
		this.connectTimeout = Integer.parseInt(props.getProperty("connect-timeout").trim());
		this.connectTimeout = Integer.parseInt(props.getProperty("read-timeout").trim());
		this.url = props.getProperty("url").trim();
		this.userId = props.getProperty("username").trim();
		this.pass  = props.getProperty("password").trim();
		
		try {
			this.inquiryContext = JAXBContext.newInstance(Request.class);
			this.inquiryResponseContext = JAXBContext.newInstance(InqueryResponse.class);
		
		} catch (JAXBException e) {
			logger.error("JAXBContext initialization error. ", e);
		}

		this.productcode = (String) props.getProperty("productcode").trim();
//		this.rcHttpError = props.getProperty("rc-http-error").trim();
		this.channel = props.getProperty("channel").trim();
		
	}
	
	public static SessionManager getInstance(){		
		return instance;
	}

	public ISOPackager getIsoPackager() {
		return isoPackager;
	}

	public Integer getUseHeader() {
		return useHeader;
	}

	public Integer getIncludeHeaderLength() {
		return includeHeaderLength;
	}

	public String getTrailer() {
		return trailer;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getUrl() {
		return url;
	}
	public String getProducCode() {
		return productcode;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public ExecutorService getConnectionExecutor() {
		return connectionExecutor;
	}

	public JAXBContext getInquiryContext() {
		return inquiryContext;
	}

	public void setInquiryContext(JAXBContext inquiryContext) {
		this.inquiryContext = inquiryContext;
	}

	public JAXBContext getInquiryResponseContext() {
		return inquiryResponseContext;
	}

	public void setInquiryResponseContext(JAXBContext inquiryResponseContext) {
		this.inquiryResponseContext = inquiryResponseContext;
	}

	public String getRcHttpError() {
		return rcHttpError;
	}

	public String getChannel() {
		return channel;
	}
}
