package com.syb.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.syb.util.FileHelper;
import com.syb.util.SessionManager;






public class Server {
	
	private static final Logger logger = Logger.getLogger(Server.class);

	public static void main(String[] args) throws FileNotFoundException, IOException {		
		String loggerProperties = "./settings/log4j.properties";
		PropertyConfigurator.configure(loggerProperties);

		logger.info("----------- MW START -----------");
		System.out.println("----------- MW START -----------");
		
		SessionManager.getInstance();
		
		// Configure the server.
	    ServerBootstrap serverBootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("Decoder", new DecoderIn());
				pipeline.addLast("Handler", new CollectorAgentHandler());
				return pipeline;
			}
		});
		
		String filePath = System.getProperty("user.dir") + "/settings/settings.properties";
		Properties props = FileHelper.getFile(filePath);
		
		String localhost = props.getProperty("ipaddress");
		String port = props.getProperty("port").trim();
		serverBootstrap.bind(new InetSocketAddress(localhost, Integer.parseInt(port)));
		logger.info("LISTEN TO : " + localhost + ":" + port);
		System.out.println("LISTEN TO : " + localhost + ":" + port);
		
	}
}
