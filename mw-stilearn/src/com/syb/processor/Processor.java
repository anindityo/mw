package com.syb.processor;

//import java.io.StringWriter;
//import java.util.Date;

//import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.syb.bean.ClientResponse;
//import com.syb.bean.PaymentRequest;
//import com.syb.bean.Request;
import com.syb.server.CollectorAgentHandler;
//import com.syb.util.CommonUtil;
import com.syb.util.Constant;
import com.syb.util.IsoHelper;
import com.syb.util.SessionManager;

public class Processor {

	private static final Logger logger = Logger.getLogger(Processor.class);
	private ClientResponse clientResponse;

	public ClientResponse getClientResponse() {
		return clientResponse;
	}

	public void setClientResponse(ClientResponse clientResponse) {
		this.clientResponse = clientResponse;
	}

	public void process(ISOMsg msg, Channel channel) {
		ISOMsg resp = (ISOMsg) msg.clone();
		try {
			if (msg.hasField(32)) {
				if (msg.getMTI().substring(0, 2).equals(Constant.MTI_TRANSACTION)) {
					if (msg.hasField(3)) {
						String transactionType = msg.getString(3).substring(0, 1);
						String productCode = IsoHelper.getProductCode(msg).trim();
						SessionManager sessionManager = SessionManager.getInstance();
						if (sessionManager.getProducCode().equalsIgnoreCase(productCode)) {
							if (transactionType.equals(Constant.TRANSACTION_TYPE_INQUIRY)) {
								resp = doInquery(msg, channel);
							} else if (transactionType.equals(Constant.TRANSACTION_TYPE_PAYMENT)) {
								resp = doPayment(msg, channel);
							} else if (transactionType.equals(Constant.TRANSACTION_TYPE_REVERSE)){
								resp = doReverse(msg, channel);
							} else {
							
								logger.info("bit 3 (" + transactionType + ") is invalid");
								resp.set(39, Constant.RC_INVALID_PROCESSING_CODE);
							}
						} else {
							logger.info("Invalid product code " + productCode);
							resp.set(39, Constant.RC_INVALID_PRODUCT_CODE);
						}
					} else {
						logger.info("bit 3 is required");
						resp.set(39, Constant.RC_INVALID_MANDATORY_FIELD);
					}
				} else {
					resp.set(39, Constant.RC_INVALID_MTI);
				}
			} else {
				resp.set(39, Constant.RC_INVALID_MANDATORY_FIELD);
			}
		} catch (Exception e) {
			logger.error("process error. ", e);
			try {
				resp.set(39, Constant.RC_ERROR);
			} catch (ISOException e1) {
				logger.error("process error setting RC error. ", e);
			}
		} finally {
			if (resp != null) {
				if (resp.hasField(39)) {
					if (!Constant.RC_SUCCESS.equalsIgnoreCase(resp.getString(39))) {
						CollectorAgentHandler.sendResponseToFrontEnd(channel, resp);
					}
				}
			}
		}
	}

	/*
	 * Perubahan marshaller di runnable processor sebelumnya di class processor
	 * 
	 */
	public ISOMsg doInquery(ISOMsg req, Channel channel) {
		ISOMsg resp = (ISOMsg) req.clone();
//		try {

		SessionManager sessionManager = SessionManager.getInstance();

//			String messageId = "Inquery";
//			Date dTrxDate = IsoHelper.getTrxDate(req);
//			String timestamp = CommonUtil.generateTimestamp(dTrxDate);
//			String trackRef = IsoHelper.getTrackingRef(req);
//			String storeId = IsoHelper.getStoreId(req);
//			String productId = IsoHelper.getProductId(req);
//			String noKontrak = IsoHelper.getPaymentCode(req);
//
//			Request request = new Request();
//			request.setTimestamp(timestamp);
//			request.setMessageId(messageId);
//			request.setProductId(productId);
//			request.setPaymentCode(noKontrak);
//			request.setTrackingRef(trackRef);
//			request.setStoreId(storeId);
//			
//			String xmlReq = request.toString();

		RunnableProcessor runnableProcessor = new RunnableProcessor(resp, channel);
		sessionManager.getConnectionExecutor().execute(runnableProcessor);

//		} catch (Exception e) {
//			logger.error("doInquiry error. ", e);
//			try {
//				resp.set(39, Constant.RC_ERROR);
//			} catch (Exception ex) {
//				logger.error("doInquiryPostpaid error. ", ex);
//			}
//
//		}
		return resp;
	}

	public ISOMsg doPayment(ISOMsg req, Channel channel) {
		ISOMsg resp = (ISOMsg) req.clone();
//		try {
//
		SessionManager sessionManager = SessionManager.getInstance();
//			String messageId = "Payment";
//			Date dTrxDate = IsoHelper.getTrxDate(req);
//			String timestamp = CommonUtil.generateTimestamp(dTrxDate);
//			String trackRef = IsoHelper.getTrackingRef(req);
//			String storeId = IsoHelper.getStoreId(req);
//			String productId = IsoHelper.getProductId(req);
//			String noKontrak = IsoHelper.getPaymentCode(req);
//			String amount = IsoHelper.getAmount(req);
//
//			PaymentRequest paymentRequest = new PaymentRequest();
//			paymentRequest.setTimestamp(timestamp);
//			paymentRequest.setMessageId(messageId);
//			paymentRequest.setTrackingRef(trackRef);
//			paymentRequest.setStoreId(storeId);
//			paymentRequest.setPaymentCode(noKontrak);
//			paymentRequest.setProductId(productId);
//			paymentRequest.setAmount(amount);
//			
////			String payReq = paymentRequest.toString();
//
////			String url = sessionManager.getUrl();
		RunnableProcessor runnableProcessor = new RunnableProcessor(resp, channel);
		sessionManager.getConnectionExecutor().execute(runnableProcessor);

//		} catch (Exception e) {
//			logger.error("doInquiry error. ", e);
//			try {
//				resp.set(39, Constant.RC_ERROR);
//			} catch (Exception ex) {
//				logger.error("doInquiryPostpaid error. ", ex);
//			}
//
//		}
		return resp;
	}
	
	public ISOMsg doReverse(ISOMsg req, Channel channel) {
		ISOMsg resp = (ISOMsg) req.clone();
		SessionManager sessionManager = SessionManager.getInstance();
		RunnableProcessor runnableProcessor = new RunnableProcessor(resp, channel);
		sessionManager.getConnectionExecutor().execute(runnableProcessor);
		return resp;
	}

}
