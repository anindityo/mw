package com.syb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class FileHelper {
	private static final Logger logger = Logger.getLogger(FileHelper.class);

	/**
	 * Mengambil isi file properties
	 * @param filePath
	 * @return
	 */
	public static Properties getFile(String filePath) {
		Properties props = new Properties();
		String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
		if (!fileName.equalsIgnoreCase("null") && !fileName.trim().equals("")) {
			try {
				FileInputStream fis = new FileInputStream(new File(filePath));
				props.load(fis);
				fis.close();
			} catch (IOException e) {
				logger.error("getFile Error. ", e);
			}
		}
		return props;
	}

}
