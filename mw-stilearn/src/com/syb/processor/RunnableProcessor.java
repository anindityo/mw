package com.syb.processor;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

import com.syb.bean.ClientResponse;
import com.syb.bean.Detail;
import com.syb.bean.ReqInquery;
import com.syb.bean.Request;
import com.syb.client.Client;
import com.syb.server.CollectorAgentHandler;
import com.syb.util.CommonUtil;
import com.syb.util.Constant;
import com.syb.util.IsoHelper;
import com.syb.util.SessionManager;

public class RunnableProcessor implements Runnable {

	private Logger logger = Logger.getLogger(RunnableProcessor.class);

	private Request xmlRequest;
	private String method;
	private Channel channel;
	private String header;
	private ISOMsg resp;


	public RunnableProcessor(Request xmlRequest, String method, String header, ISOMsg resp, Channel channel) {
		this.xmlRequest = xmlRequest;
		this.method = method;
		this.header = header;
		this.resp = resp;
		this.channel = channel;
	}

	private void processResponse() throws JAXBException {

		SessionManager sessionManager = SessionManager.getInstance();
		Client client = new Client();
		Marshaller marshaller = sessionManager.getInquiryContext().createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(xmlRequest, stringWriter);
		String requestString = CommonUtil.createEnvelope(stringWriter.toString());

		ClientResponse clientResponse = client.createSoapMessage(requestString, header);
		try {
			if (clientResponse != null && !clientResponse.isTimeout()) {
				String respContent = CommonUtil.getSoapContent(clientResponse.getSoapMessage());
				respContent = CommonUtil.formatStringXml(respContent);
				logger.info("Response: " + respContent);
				String rc = null;
				if (!clientResponse.getSoapMessage().getSOAPBody().hasFault()) {
					StringReader reader = new StringReader(respContent);
					Unmarshaller unmarshaller = sessionManager.getInquiryResponseContext().createUnmarshaller();
					ReqInquery reqInquery = unmarshaller.unmarshal(new StreamSource(reader), ReqInquery.class).getValue();
					Long amount = reqInquery.getTotalAmount();
					rc= reqInquery.getResponseCode();

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
							+ CommonUtil.strpad(reqInquery.getPoliceNum(), 20) + CommonUtil.strpad(reqInquery.getType(), 50)
							+ ISOUtil.zeropad(reqInquery.getDetail().size(), 2) + detailTagihan;

					resp.set(4, CommonUtil.zeropad(amount, 12));
					resp.set(39, Constant.RC_SUCCESS);
					resp.set(62, bit62);
				} else if (rc.equals(Constant.STATUS_LINK_DOWN)) {
					resp.set(39, Constant.RC_ERROR);
				} else if (rc.equals(Constant.STATUS_UNAUTHORIZED)) {
					resp.set(39, Constant.RC_INVALID_MANDATORY_FIELD);
				} else if (rc.equals(Constant.STATUS_ERROR_PAYMENT)) {
					resp.set(39, Constant.RC_NO_BILLING);
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

	@Override
	public void run() {
		try {
			String productCode = IsoHelper.getProductCode(resp);

			SessionManager sessionManager = SessionManager.getInstance();
			if (sessionManager.getProducCode().equalsIgnoreCase(productCode)) {
				processResponse();
			}

		} catch (Exception e) {
			logger.error("Runtime exception occured while executing request: " + e.getMessage(), e);
		}
	}

}
