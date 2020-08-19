package com.syb.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Base64;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.syb.bean.ClientResponse;
import com.syb.util.CommonUtil;
import com.syb.util.Constant;
import com.syb.util.SessionManager;

public class Client {

	private static final Logger logger = Logger.getLogger(Client.class);

	SessionManager sessionManager = SessionManager.getInstance();

	public ClientResponse createSoapMessage(String message, String header) {
			try {
				StreamSource content = new StreamSource(new ByteArrayInputStream(message.getBytes()));
				MessageFactory messageFactory = MessageFactory.newInstance();
				SOAPMessage soapMessage = messageFactory.createMessage();
				
				MimeHeaders headers = soapMessage.getMimeHeaders();
				headers.setHeader("SOAPAction", header);
				headers.setHeader("Content-Type", "text/html");
				
				SOAPPart soapPart = soapMessage.getSOAPPart();
				soapPart.setContent(content);
				soapMessage.saveChanges();
				logger.info(CommonUtil.getSoapContent("Outgoing Biller", soapMessage));
				
				ClientResponse clientResponse = null;
				String encodingHeader = Base64.getEncoder().
		        		encodeToString((sessionManager.getUserId()+":"+sessionManager.getPass()).getBytes());
				if(sessionManager.getUrl() != null) {
					clientResponse = sendMessage(soapMessage, sessionManager.getUrl(),encodingHeader);
				}else {
					clientResponse = null;
				}
				return clientResponse;
			}catch(Exception exception) {
				logger.error("crateSoapMessage Error.", exception);
				return null;
			}
		}

	private ClientResponse sendMessage(SOAPMessage message, String endPoint, String auth)
			throws MalformedURLException, SOAPException {
		ClientResponse resp = null;
		SOAPMessage response = null;
		if (endPoint != null && message != null) {
			URL url = new URL(null, endPoint, new URLStreamHandler() {
				@Override
				protected URLConnection openConnection(URL url) throws IOException {
					URL target = new URL(url.toString());
					URLConnection connection = target.openConnection();
					connection.setConnectTimeout(sessionManager.getConnectTimeout());
					connection.setReadTimeout(sessionManager.getReadTimeout());
					connection.setRequestProperty("Authorization","Basic "+ auth);
					return (connection);
				}
			});
			SOAPConnection connection = null;
			try {
				connection = SOAPConnectionFactory.newInstance().createConnection();
				response = connection.call(message, url);
				resp = new ClientResponse();
				resp.setSoapMessage(response);
			} catch (Exception e) {
				logger.error("sendMessage Error. ", e);
				if (e.getCause().getMessage().equalsIgnoreCase(Constant.CLIENT_SEND_FAILED)) {
					resp = new ClientResponse();
					resp.setTimeout(true);
				}
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SOAPException e) {
						logger.error("Can't close SOAPConnection. ", e);
					}
				}
			}
		}
		return resp;
	}
}
