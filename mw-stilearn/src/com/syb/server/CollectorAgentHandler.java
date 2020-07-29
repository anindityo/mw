package com.syb.server;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.util.CharsetUtil;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import com.syb.processor.Processor;
import com.syb.util.Helper;
import com.syb.util.SessionManager;

public class CollectorAgentHandler extends SimpleChannelHandler {
	private static final Logger logger = Logger.getLogger(CollectorAgentHandler.class);

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		SessionManager sessionManager = SessionManager.getInstance();
		
		String localPort = ctx.getChannel().getLocalAddress().toString().substring(ctx.getChannel().getLocalAddress().toString().indexOf(":")+1);
		String remoteIpAddress = ctx.getChannel().getRemoteAddress().toString().substring(1);
		
		logger.info("Serving from IP: " + remoteIpAddress + " , accepting in port = " + localPort);
		
		ChannelBuffer buf = (ChannelBuffer) e.getMessage();			
		String origMessage = buf.toString(CharsetUtil.ISO_8859_1);
		
		Integer useHeader = sessionManager.getUseHeader();
		String trailer = sessionManager.getTrailer();
		
		if (useHeader == 1) {
			origMessage = origMessage.substring(2);
		} else if (useHeader == 2) {
			origMessage = origMessage.substring(4);
		}
		Helper messageHelper = new Helper();
		origMessage = messageHelper.removeTrailer(origMessage, trailer);
		
		ISOMsg msg = new ISOMsg();
		try {
			msg.setPackager(sessionManager.getIsoPackager());
			msg.unpack(origMessage.getBytes(CharsetUtil.ISO_8859_1));
			
			logger.info(Helper.printStreamToStream(msg, "Incoming CA", remoteIpAddress));		
		
			Processor processor = new Processor();
			processor.process(msg, ctx.getChannel());

		} catch (ISOException ex) {
			// BAD Message
			logger.error("Bad Message CA. from IP Address: " + remoteIpAddress + 
					" , accepted in Port : " + localPort + " with Message : " + origMessage + " ", ex);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		// Close the connection when an exception is raised.
		String msg = e.getCause().getMessage();
		if(msg != null) {
			if(msg.trim().equalsIgnoreCase("Connection reset by peer")){
				//if(logger.isDebugEnabled())
					logger.info("Connection reset by peer");
			} else {			
				logger.error("Unexpected exception.", e.getCause());
				if(e.getChannel().isOpen()){
					logger.info("Closing connection from "+ctx.getChannel().getRemoteAddress());
					e.getChannel().close();
				}
			}
		}
	}
	
	public static void sendResponseToFrontEnd(Channel channel, ISOMsg resp) {
		SessionManager sessionManager = SessionManager.getInstance();
		String remoteIpAddress = channel.getRemoteAddress().toString().substring(1);
		Integer useHeader = sessionManager.getUseHeader();
		Integer includeHeaderLength = sessionManager.getIncludeHeaderLength();
		String trailer = sessionManager.getTrailer();
		
		if(resp!=null){
			try {
				if(resp.isRequest()){
					resp.setResponseMTI();
				}

				Helper messageHelper = new Helper();
				String message = new String(resp.pack(), CharsetUtil.ISO_8859_1);
				ChannelBuffer isoResponse = messageHelper.putHeaderAndTrailerByte(
						message, useHeader, includeHeaderLength, trailer);
				channel.write(isoResponse);
				logger.info(Helper.printStreamToStream(resp, "Outgoing CA", remoteIpAddress));
			} catch (Exception e) {
				logger.error("Failed to send response to CA "+remoteIpAddress+": " + e.getMessage(), e);
			}
		} else {
			logger.info("Skip sending response to CA "+remoteIpAddress+" due to no response from biller!");
		}

	}

}
