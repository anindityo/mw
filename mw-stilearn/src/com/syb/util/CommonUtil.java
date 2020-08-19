package com.syb.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.jpos.iso.ISOUtil;

public class CommonUtil {

	private static Logger logger = Logger.getLogger(CommonUtil.class);

	private static TransformerFactory factory = TransformerFactory.newInstance();

	public static String getSoapContent(SOAPMessage soapMessage) {
		String hasil = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			soapMessage.writeTo(stream);
			hasil = stream.toString();
		} catch (SOAPException exception) {
			logger.error("getSoapContent Error. ", exception);
		} catch (IOException e2) {
			logger.error("getSoapContent Error. ", e2);
		}
		return hasil;
	}

	public static String getSoapContent(String header, SOAPMessage message) {
		String hasil = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			message.writeTo(stream);
			hasil = stream.toString();
		} catch (SOAPException exception) {
			logger.error("getSoapContent Error. ", exception);
		} catch (IOException e2) {
			logger.error("getSoapContent Error. ", e2);
		}
		return hasil;
	}

	public static String createEnvelope(String message) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
				+ "xmlns:syb=\"http://10.10.10.40/wsdl_buana/SYBServices.php\">\r\n"
				+ "<soapenv:Header/>\r\n<soapenv:Body>\r\n" + message + "\r\n</soapenv:Body>\r\n</soapenv:Envelope>";
	}

	public static String formatStringXml(String xml) {
		Source xmlInput = new StreamSource(new StringReader(xml));
		StringWriter stringW = new StringWriter();
		StreamResult xmlOutput = new StreamResult(stringW);
		try {
			//factory.setAttribute("indent_number", 4);
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// remove xml header
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(xmlInput, xmlOutput);
		} catch (TransformerException e) {
			logger.error("formatStringXml Error. " + e);
		}

		return xmlOutput.getWriter().toString();
	}

	public static String stripSOAPReplyMessage(String messageFragment) {
		String strippedMessage = messageFragment;
		if (strippedMessage.contains("<s:Fault")) {
			int start = strippedMessage.indexOf("<s:Fault");
			int end = strippedMessage.indexOf("</s:Body");
			strippedMessage = strippedMessage.substring(start, end);
		} else {
			int start = strippedMessage.indexOf("<s:Body");
			int end = strippedMessage.indexOf("</s:Body");
			strippedMessage = strippedMessage.substring(start+8, end);

		} 
		return strippedMessage;
	}

	public static String generateTrxDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constant.TRXDATE_FORMAT);
		String trxDate = simpleDateFormat.format(date);
		return trxDate;
	}

	public static String generateTimestamp(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	// public static String generateTrxId(Date currentDate, String customerId,
	// String bankId, String channelId) {
//		String data = currentDate.getTime() + customerId + bankId + channelId;
//		String trxId = DigestUtils.md5Hex(data);
//		trxId = trxId.substring(0,9).toUpperCase();
//		return trxId;
//	}

	public static String generateSignature(String params[]) {
		String data = "";
		for (int i = 0; i < params.length; i++) {
			String param = params[i];
			if (data.length() > 0) {
				data += ";";
			}
			data += param;
		}

		String signature = DigestUtils.md5Hex(data);
		return signature;
	}

	public static String zeropad(Long value, int length) {
		String text = null;
		if (value == null) {
			text = "";
		} else {
			text = String.valueOf(value);
		}
		return zeropad(String.valueOf(text), length);
	}

	public static String zeropad(String value, int length) {
		String text = null;
		if (value == null) {
			text = "";
		} else {
			text = String.valueOf(value);
		}
		try {
			text = ISOUtil.zeropad(truncate(text, length), length);
		} catch (Exception e) {
			logger.error("Failed to zeropad " + text);
		}
		return text;
	}

	public static String strpad(String text, int length) {
		if (text == null) {
			text = "";
		}
		text = ISOUtil.strpad(truncate(text, length), length);
		return text;
	}

	public static String truncate(String str, int length) {
		if (str != null && str.length() > length) {
			String msg = "[VALUE TRUNCATED] Value " + str + " (" + str.length() + ") is exceeding padlength (" + length
					+ "). ";
			str = str.substring(0, length);
			msg += "Value altered to " + str;
			logger.info(msg);
		}
		return str;
	}

}
