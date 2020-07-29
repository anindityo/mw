package com.syb.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;

public class Helper {

private static final Logger logger = Logger.getLogger(Helper.class);
	
	public String removeTrailer(String message, String trailer) {
		if (trailer != null && !trailer.isEmpty()) {
			message = message.substring(0, message.length()-1);
		}
		return message;
	}
	
	public byte[] putHeaderAndTrailerByte(byte[] messageByte, Integer header,
				Integer includeHeaderLength, String trailer){
		int len = messageByte.length;		
		byte[] result = null;
		byte[] headerByte = new byte[2];
		byte[] headerBuffer = new byte[2];
		if (header == 1) {
			result = new byte[len+2];
			BigInteger msgLen = null;
			if (includeHeaderLength == 1)
				msgLen = new BigInteger(Integer.toString(len+2));
			else
				msgLen = new BigInteger(Integer.toString(len));	
			headerBuffer = msgLen.toByteArray();
			if(headerBuffer.length == 1){
				headerByte[1] = headerBuffer[0];
			} else {
				headerByte = headerBuffer;
			}
			System.arraycopy(headerByte, 0, result, 0, headerByte.length);
		    System.arraycopy(messageByte, 0, result, headerByte.length, messageByte.length);
		} else if (header == 2) {
			result = new byte[len+4];
			String headerCount = ISOUtil.zeropad(len, 4);
			byte[] headerCountByte = new byte[4];
			headerCountByte = headerCount.getBytes();
			System.arraycopy(headerCountByte, 0, result, 0, headerCountByte.length);
		    System.arraycopy(messageByte, 0, result, headerCountByte.length, messageByte.length);
		} else {
			result = new byte[len];
			result = messageByte;
		}
		
		if (trailer != null && !trailer.isEmpty()) {
			byte[] newResult = new byte[result.length+1];
			byte[] trailerByte = new byte[1];
			
			trailerByte[0] = new Byte(trailer);
			System.arraycopy(result, 0, newResult, 0, result.length);
		    System.arraycopy(trailerByte, 0, newResult, result.length, trailerByte.length);
		    
		    return newResult;
 		}
		return result;
	}
	
	public ChannelBuffer putHeaderAndTrailerByte(String message, Integer header,
			Integer includeHeaderLength, String trailer){
		int len = message.length();		
		ChannelBuffer result = null;
		byte[] headerByte = new byte[2];
		byte[] headerBuffer = new byte[2];
		if (header == 1) {
			BigInteger msgLen = null;
			if (includeHeaderLength == 1)
				msgLen = new BigInteger(Integer.toString(len+2));
			else
				msgLen = new BigInteger(Integer.toString(len));	
			headerBuffer = msgLen.toByteArray();
			if(headerBuffer.length == 1){
				headerByte[1] = headerBuffer[0];
			} else {
				headerByte = headerBuffer;
			}
			
			result = ChannelBuffers.copiedBuffer(new String(headerByte, CharsetUtil.ISO_8859_1) + message, CharsetUtil.ISO_8859_1);
		} else if (header == 2) {
			String headerCount = ISOUtil.zeropad(len, 4);
			result = ChannelBuffers.copiedBuffer(headerCount + message, CharsetUtil.ISO_8859_1);
		} else {
			result = ChannelBuffers.copiedBuffer(message, CharsetUtil.ISO_8859_1);
		}
		
		if (trailer != null && !trailer.isEmpty()) {
			byte[] trailerByte = new byte[1];
			trailerByte[0] = new Byte(trailer);
		    result = ChannelBuffers.copiedBuffer(message + new String(trailerByte, CharsetUtil.ISO_8859_1) , CharsetUtil.ISO_8859_1);
		}
		return result;
	}
	
	public static String printStreamToStream(ISOMsg iso, String dump, String ipAddress) {
		String result = null;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			iso.dump(ps, "");
			result = dump + " - IP Address: " + ipAddress
					+ "\r\n" + baos.toString("UTF-8"); // e.g. ISO-8859-1
		} catch (Exception e){
			logger.error("Exception in printStreamToStream.", e);
		}
		return result;
	}
	
	public static String setResponseMTI(String mti) {
		int resp = Integer.parseInt(mti.substring(2, 3))+1;
		if (mti.substring(0,1).equalsIgnoreCase("0"))
			return mti.substring(0, 2) + String.valueOf(resp) + "0";
		else
			return mti.substring(0, 2) + String.valueOf(resp) + mti.substring(3, 4);
	}
	
	
	public static boolean compareDate(Date date1, Date date2) {
	    Calendar calendar = Calendar.getInstance();
	    
	    calendar.setTime(date1);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    date1 = calendar.getTime();
	    
	    calendar.setTime(date2);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    date2 = calendar.getTime();
	    
	    if (date1.after(date2)) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
}
