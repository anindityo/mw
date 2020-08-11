package com.syb.server;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import com.syb.util.SessionManager;

public class DecoderIn extends FrameDecoder {

	private static final Logger logger = Logger.getLogger(DecoderIn.class);
	
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		SessionManager sessionManager = SessionManager.getInstance();
		logger.info("Panjang Stream dari CA = " + buffer.capacity());
		Integer useHeader = sessionManager.getUseHeader();
		Integer includeHeaderLength = sessionManager.getIncludeHeaderLength();
		String trailer = sessionManager.getTrailer();
		
		if (trailer != null) {
			ChannelBuffer channelBuffer = null;
			StringBuffer sb = new StringBuffer();
			while(buffer.readable()){
				byte tempByte = buffer.readByte();
				char ch = (char)tempByte;
				if(tempByte == -1) {
					buffer.markReaderIndex();
					channelBuffer = buffer.copy(0, sb.length()+1);
					break;
				} else {
					sb.append(ch);
				}
			}
			
			return channelBuffer;
		} else if (useHeader == 1) {
			if (buffer.readableBytes() < 2) {
				return null;
			}
			int dataLength = buffer.getShort(buffer.readerIndex());
			
			if (includeHeaderLength == 0) {
				dataLength = dataLength + 2;
			}
			
			if (buffer.readableBytes() < dataLength) {
				return null;
			} else {
				ChannelBuffer channelBuffer = buffer.readBytes(dataLength);
				return channelBuffer;
			}
		} else if (useHeader == 2) {
			if (buffer.readableBytes() < 4) {
				return null;
			}
			ChannelBuffer tempBufferHeader = buffer.copy(0, 4);
			String tempDataLength = new String(tempBufferHeader.array());
			int dataLength = Integer.parseInt(tempDataLength);
			
			if (includeHeaderLength == 0) {
				dataLength = dataLength + 4;
			}
			
			if (buffer.readableBytes() < dataLength) {
				return null;
			} else {
				ChannelBuffer channelBuffer = buffer.readBytes(dataLength);
				return channelBuffer;
			}
		} else {
			ByteBuffer byteBuffer = ByteBuffer.allocate(buffer.readableBytes());
			while(buffer.readable()) {
				byteBuffer.put(buffer.readByte());
			}
			buffer.markReaderIndex();
			Integer capacity = byteBuffer.capacity();
			if (capacity > 0) {
				return buffer;
			} else {
				return null;
			}
		}
	}
}
