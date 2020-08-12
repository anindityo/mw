package com.syb.processor;

import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPFault;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

import com.syb.bean.ClientResponse;
import com.syb.bean.Detail;
import com.syb.bean.InqueryResponse;
import com.syb.bean.PaymentRequest;
import com.syb.bean.PaymentResponse;
import com.syb.bean.ReqInquery;
import com.syb.bean.ReqPayment;
import com.syb.bean.Request;
//import com.syb.bean.Request;
import com.syb.client.Client;
import com.syb.server.CollectorAgentHandler;
import com.syb.util.CommonUtil;
import com.syb.util.Constant;
import com.syb.util.IsoHelper;
import com.syb.util.SessionManager;

public class RunnableProcessor implements Runnable {

	private Logger logger = Logger.getLogger(RunnableProcessor.class);

	/*
	 * Perubahan parameter untuk class runnable proccessor String xml request
	 * dihapus.
	 */

	private Channel channel;
	private ISOMsg resp;

	public RunnableProcessor(ISOMsg resp, Channel channel) {
		this.resp = resp;
		this.channel = channel;
	}

	private String header = Constant.BUANA_HEADER;

	private void processResponse() throws Exception {

		SessionManager sessionManager = SessionManager.getInstance();
		Client client = new Client();
		Marshaller marshaller = sessionManager.getInquiryContext().createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		String messageId = "Inquery";
		Date dTrxDate = null;
		Request request = new Request();
		try {
			dTrxDate = (Date) IsoHelper.getTrxDate(resp);
			String timestamp = CommonUtil.generateTimestamp(dTrxDate);
			String trackRef = IsoHelper.getTrackingRef(resp);
			String storeId = IsoHelper.getStoreId(resp);
			String productId = IsoHelper.getProductId(resp);
			String noKontrak = IsoHelper.getPaymentCode(resp);

			request.setTimestamp(timestamp);
			request.setMessageId(messageId);
			request.setProductId(productId);
			request.setPaymentCode(noKontrak);
			request.setTrackingRef(trackRef);
			request.setStoreId(storeId);
		} catch (ISOException e1) {
			logger.error("doInquiry error. ", e1);
			try {
				resp.set(39, Constant.RC_ERROR);
			} catch (Exception ex) {
				logger.error("doInquiry error. ", ex);
			}
		}

		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(request, stringWriter);
		String requestString = CommonUtil.createEnvelope(stringWriter.toString());

		ClientResponse clientResponse = client.createSoapMessage(requestString, header);
		try {
			if (clientResponse != null && !clientResponse.isTimeout()) {
				String respContent = CommonUtil.getSoapContent(clientResponse.getSoapMessage());
				respContent = CommonUtil.formatStringXml(respContent);
				logger.info("Response: " + respContent);
				SOAPFault fault = clientResponse.getSoapMessage().getSOAPBody().getFault();
				String rCode = null;
				int code = 0;
				if (fault != null) {
					rCode = fault.getFaultCode();
					code = Integer.parseInt(rCode);
				} else {
					rCode = null;
				}
				String rc = null;
				if (!clientResponse.getSoapMessage().getSOAPBody().hasFault() && rCode == null) {
//					/StringReader reader = new StringReader(respContent);
					Unmarshaller unmarshaller = sessionManager.getInquiryResponseContext().createUnmarshaller();
					InqueryResponse inqueryResponse = unmarshaller
							.unmarshal(clientResponse.getSoapMessage().getSOAPBody().extractContentAsDocument(),
									InqueryResponse.class)
							.getValue();
					ReqInquery reqInquery = inqueryResponse.getReqInquery();
					Long amount = reqInquery.getTotalAmount();
					rc = reqInquery.getResponseCode();
					int responCode = Integer.parseInt(rc);
					if (responCode == Constant.STATUS_CODE_SUCCESS) {

						if (amount != null) {
							String modifiedbit48 = "";
							String bit48 = resp.getString(48);
							Long admin = reqInquery.getFee();
							Long billAmt = amount - admin;
							bit48 = bit48.substring(0, 71) + CommonUtil.zeropad(billAmt, 12) + bit48.substring(83);
							modifiedbit48 = bit48.substring(0, 81) + CommonUtil.strpad(reqInquery.getCustomer(), 30)
									+ CommonUtil.zeropad(billAmt, 12) + CommonUtil.zeropad(admin, 12)
									+ CommonUtil.strpad(reqInquery.getTrackingRef(), 32);
							resp.set(48, modifiedbit48);

						}
						String detailTagihan = "";

						if (reqInquery.getDetail().size() > 0) {

							for (Detail data : reqInquery.getDetail()) {
								detailTagihan = CommonUtil.strpad(data.getInstallmentPeriod(), 20)
										+ CommonUtil.zeropad(data.getInstallmentNumber(), 2)
										+ CommonUtil.zeropad(data.getInstallmentPenalty(), 12)
										+ CommonUtil.zeropad(data.getInstallmentPenalty(), 12);
							}

						} else {
							detailTagihan = "";
						}

						String bit62 = CommonUtil.strpad(reqInquery.getContractNo(), 20)
								+ CommonUtil.strpad(reqInquery.getCustomer(), 30)
								+ CommonUtil.strpad(reqInquery.getPoliceNum(), 20)
								+ CommonUtil.strpad(reqInquery.getType(), 50)
								+ ISOUtil.zeropad(reqInquery.getDetail().size(), 2) + detailTagihan;

						resp.set(4, CommonUtil.zeropad(amount, 12));
						resp.set(39, Constant.RC_SUCCESS);
						resp.set(62, bit62);

					} else {
						resp.set(39, Constant.RC_ERROR);
					}
				} else if (code == Constant.STATUS_UNAUTHORIZED) {
					resp.set(39, Constant.RC_INVALID_MANDATORY_FIELD);
				}
			} else {
				if (clientResponse == null) {
					logger.info("[Response] No Response ");
					resp = null;
				} else {
					resp.set(39, Constant.RC_ERROR);
					logger.info("[Response] HTTP Response Error " + clientResponse.getSoapMessage());
				}
			}
		} catch (Exception e) {
			logger.error("processResponse error. ", e);
			try {
				resp.set(39, Constant.RC_ERROR);
			} catch (Exception ex) {
				logger.error("processResponse error. ", ex);
			}
		} finally {
			CollectorAgentHandler.sendResponseToFrontEnd(channel, resp);
		}

	}

	private void processResponsePayment() throws Exception {

		SessionManager sessionManager = SessionManager.getInstance();
		Client client = new Client();
		Marshaller marshaller = sessionManager.getPaymentContext().createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		PaymentRequest paymentRequest = new PaymentRequest();
		try {
			String messageId = "Payment";
			Date dTrxDate = IsoHelper.getTrxDate(resp);
			String timestamp = CommonUtil.generateTimestamp(dTrxDate);
			String trackRef = IsoHelper.getTrackingRef(resp);
			String storeId = IsoHelper.getStoreId(resp);
			String productId = IsoHelper.getProductId(resp);
			String noKontrak = IsoHelper.getPaymentCode(resp);
			String amount = IsoHelper.getAmount(resp);

			paymentRequest.setTimestamp(timestamp);
			paymentRequest.setMessageId(messageId);
			paymentRequest.setTrackingRef(trackRef);
			paymentRequest.setStoreId(storeId);
			paymentRequest.setPaymentCode(noKontrak);
			paymentRequest.setProductId(productId);
			paymentRequest.setAmount(amount);

		} catch (Exception e) {
			logger.error("processPayment error. ", e);
			try {
				resp.set(39, Constant.RC_ERROR);
			} catch (Exception ex) {
				logger.error("processPayment error. ", ex);
			}

		}

		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(paymentRequest, stringWriter);
		String requestString = CommonUtil.createEnvelope(stringWriter.toString());
		ClientResponse clientResponse = client.createSoapMessage(requestString, header);
		try {
			if (clientResponse != null && !clientResponse.isTimeout()) {
				String respContent = CommonUtil.getSoapContent(clientResponse.getSoapMessage());
				respContent = CommonUtil.formatStringXml(respContent);
				logger.info("Response: " + respContent);
				SOAPFault fault = clientResponse.getSoapMessage().getSOAPBody().getFault();
				String rCode = null;
				int code = 0;
				if (fault != null) {
					rCode = fault.getFaultCode();
					code = Integer.parseInt(rCode);
				} else {
					rCode = null;
				}

				String rc = null;
				if (!clientResponse.getSoapMessage().getSOAPBody().hasFault() && rCode == null) {
					Unmarshaller unmarshaller = sessionManager.getPaymentResponseContext().createUnmarshaller();
					PaymentResponse paymentResponse = unmarshaller
							.unmarshal(clientResponse.getSoapMessage().getSOAPBody().extractContentAsDocument(),
									PaymentResponse.class)
							.getValue();
					ReqPayment reqPayment = paymentResponse.getReqPayment();
					rc = reqPayment.getResponseCode();
					int responseCode = Integer.parseInt(rc);
					if (responseCode == Constant.STATUS_CODE_SUCCESS) {

						if (rc != null) {
							String bit48 = resp.getString(48);
							bit48 = bit48.substring(0, 81) + CommonUtil.strpad(reqPayment.getMessage(), 100)
									+ CommonUtil.strpad(reqPayment.getTrackingRef(), 32)
									+ CommonUtil.strpad(reqPayment.getResponseCode(), 2);
							resp.set(48, bit48);

						}

						String bit62 = CommonUtil.strpad(reqPayment.getTrackingRef(), 100)
								+ CommonUtil.strpad(reqPayment.getBillInfo(), 100)
								+ CommonUtil.strpad(reqPayment.getMessage(), 100);

						resp.set(39, Constant.RC_SUCCESS);
						resp.set(62, bit62);
					} else if (code == Constant.STATUS_UNAUTHORIZED) {
						resp.set(39, Constant.RC_INVALID_MANDATORY_FIELD);
					} else if (code == Constant.STATUS_ERROR_PAYMENT) {
						resp.set(39, Constant.RC_NO_BILLING);
					}
				} else {
					resp.set(39, Constant.RC_ERROR);
				}
			} else {
				if (clientResponse == null) {
					logger.info("[Response] No Response ");
					resp = null;
				} else {
					resp.set(39, Constant.RC_ERROR);
					logger.info("[Response] HTTP Response Error " + clientResponse.getSoapMessage());
				}
			}
		} catch (Exception e) {
			logger.error("processResponsePayment error. ", e);
			try {
				resp.set(39, Constant.RC_ERROR);
			} catch (Exception ex) {
				logger.error("processResponsePayment error. ", ex);
			}
		} finally {
			CollectorAgentHandler.sendResponseToFrontEnd(channel, resp);
		}

	}

	@Override
	public void run() {
		try {
			String productCode = IsoHelper.getProductCode(resp);
			String bit3 = resp.getString(3).trim().substring(0, 1);

			SessionManager sessionManager = SessionManager.getInstance();
			if (sessionManager.getProducCode().equalsIgnoreCase(productCode)) {
				if (Constant.TRANSACTION_TYPE_INQUIRY.equalsIgnoreCase(bit3)) {
					processResponse();
				} else if (Constant.TRANSACTION_TYPE_PAYMENT.equalsIgnoreCase(bit3)) {
					processResponsePayment();
				}
			}

		} catch (Exception e) {
			logger.error("Runtime exception occured while executing request: " + e.getMessage(), e);
		}
	}

}
