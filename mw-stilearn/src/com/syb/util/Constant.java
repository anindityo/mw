package com.syb.util;

public class Constant {
	public static final String HEADER_TYPE_SYBHE = "SYBHE";
	public static final String HEADER_TYPE_SYBHI = "SYBHI";
	public static final String HEADER_TYPE_ASCII = "ASCII";
	public static final String HEADER_TYPE_DJBB = "DJBB";
	
	public static final String MTI_TRANSACTION = "02";
	public static final String SUBMTI_TRANSACTION = "0";
	public static final String MTI_REVERSAL = "04";
	public static final String TRANSACTION_TYPE_INQUIRY = "3";
	public static final String TRANSACTION_TYPE_PAYMENT = "1";
	
	public static final String RC_SUCCESS = "00";
	public static final String RC_LINK_SERVICEPROVIDER_DOWN = "02";
	//public static final String RC_TIMEOUT= "03";
	public static final String RC_INVALID_MANDATORY_FIELD = "59";
	public static final String RC_INVALID_MTI= "08";
	public static final String RC_INVALID_PROCESSING_CODE = "24";
	public static final String RC_INVALID_PRODUCT_CODE = "30";
	public static final String RC_NO_BILLING = "55";
	public static final String RC_ERROR = "98";
	public static final String RC_INVALID_BANKCODE = "26";
	public static final String USER_CREATED_SYSTEM = "SYSTEM";
	
	public static final Integer STATUS_CODE_SUCCESS = 200;
	public static final Integer STATUS_LINK_DOWN = 404;
	public static final Integer STATUS_UNAUTHORIZED = 401;
	public static final Integer STATUS_ERROR_PAYMENT = 406;
	
	public static final String ACTION_GETBILLSBYCONTRACTNUMBER = "getBillsByContractNumber";
	public static final String ACTION_SETBILLSPAYMENTBYCONTRACTNUMBER = "setBillsPaymentByContractNumber";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_FORMAT_BIT48 = "ddMMyyyyHHmmss";
	public static final String PAYMENT_COST_COLLECTION = "PAYMENT_COST_COLLECTION";
	public static final String PAYMENTCOST = "0";
	public static final String APP_FEE = "0";
	public static final String REF_ID = "1234";
	public static final String INSTALLMENTROWS = "1234";
	
	public static final String BUANA_HEADER_INQUIRY = "http://122.129.117.54/wsdl_buana/SYBServices.php/doInquery";
	
	public static final String TRXDATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
//	public static final String METHOD_PAY_TOPUP = "recharge";
//	public static final String METHOD_INQ_POSTPAID = "bi";
//	public static final String METHOD_PAY_POSTPAID = "bp";
	
	public static final Integer LENGTH_LABEL = 50;
	public static final Integer LENGTH_NAME = 30;
	public static final Integer LENGTH_PRICE = 11;
	public static final Integer LENGTH_BIT4 = 12;
	public static final Integer LENGTH_BIT48_AMOUNT = 12;
	public static final Integer LENGTH_BIT48_ADMIN = 12;
	public static final Integer LENGTH_BIT48_REFF = 32;
	public static final Integer LENGTH_BIT48_CUSTOMERNAME = 30;
//	public static final Integer LENGTH_BIT62_BILLNAME = 50;
//	public static final Integer LENGTH_BIT62_TOPUPNAME = 30;
//	public static final Integer LENGTH_BIT62_BILLDESC = 50;
//	public static final Integer LENGTH_BIT62_AMOUNT = 11;
	public static final Integer LENGTH_BIT62_REFFNUMBER = 30;
	
	public static final String CLIENT_SEND_FAILED = "Message send failed";

	
	public static Long MULTIPLICATION = 50000l;
	public static String DCS = "";

}
