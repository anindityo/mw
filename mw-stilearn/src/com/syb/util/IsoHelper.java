package com.syb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

public class IsoHelper {

	private static Logger logger = Logger.getLogger(IsoHelper.class);

	public static Date getTrxDate(ISOMsg m) throws ISOException {
		Date trxDate = null;
		if (m.hasField(48)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.DATE_FORMAT_BIT48);
			String sTrxDate = m.getString(48).substring(67, 81).trim();
			try {
				trxDate = dateFormat.parse(sTrxDate);
			} catch (Exception e) {
				logger.error("Failed to parse bit 48 date (" + sTrxDate + ")");
			}
		}
		return trxDate;
	}

	public static String getYear(ISOMsg m) throws ISOException {
		if (m.hasField(47)) {
			return m.getString(47).substring(0, 4).trim();
		} else {
			return "";
		}

	}

	public static String getMonth(ISOMsg m) throws ISOException {
		if (m.hasField(47)) {
			return m.getString(47).substring(4, 6).trim();
		} else {
			return "";
		}

	}

	public static String getProductCode(ISOMsg m) throws ISOException {
		if (m.hasField(48)) {
			return m.getString(48).substring(0, 6).trim();
		} else {
			return "";
		}

	}
	
	public static String getMessageId(ISOMsg m) throws ISOException {
		if(m.hasField(48)){
			return m.getString(48).substring(7, 27).trim();
		} else {
			return "";
		} 
		
	}


	public static String getChannelId(ISOMsg m) throws ISOException {
		if (m.hasField(18)) {
			return m.getString(18).trim();
		} else {
			return "";
		}
	}

	public static String getProductId(ISOMsg m) throws ISOException {
		if (m.hasField(48) && m.getString(48).length() >= 47) {
			return m.getString(48).substring(26,46).trim();
		} else {
			return "";
		}
	}

	public static String getTrackingRef(ISOMsg m) throws ISOException {
		if (m.hasField(48) && m.getString(48).length() >= 153) {
			return m.getString(48).substring(120,153).trim();
		} else {
			return "";
		}
	}

	public static String getStoreId(ISOMsg m) throws ISOException {
		if (m.hasField(48) && m.getString(48).length() >= 66) {
			return m.getString(48).substring(45, 65).trim();
		} else {
			return "";
		}
	}

	public static String getAmount(ISOMsg m) throws ISOException {
		if (m.hasField(48)) {
			return m.getString(48).substring(96, 108).trim();
		} else {
			return "";
		}

	}

	public static String getAdmin(ISOMsg m) throws ISOException {
		if (m.hasField(48)) {
			return m.getString(48).substring(108,120).trim();
		} else {
			return "";
		}

	}

	public static String getTotalAmount(ISOMsg m) throws ISOException {
		if (m.hasField(4)) {
			return m.getString(4).trim();
		} else {
			return "";
		}

	}

	public static String getCustomerName(ISOMsg m) throws ISOException {
		if (m.hasField(48)) {
			return m.getString(48).substring(66, 96).trim();
		} else {
			return "";
		}

	}
	
	public static String getPaymentCode(ISOMsg m) throws ISOException {
		if (m.hasField(48)) {
			return m.getString(48).substring(7, 25).trim();
		} else {
			return "";
		}

	}
}
