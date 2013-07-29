package egovframework.rte.fdl.crypto;

import org.apache.log4j.Logger;

import egovframework.rte.fdl.crypto.impl.EgovEDcryptionBinServlceImpl;
import egovframework.rte.fdl.crypto.impl.EgovEDcryptionNumServiceImpl;
import egovframework.rte.fdl.crypto.impl.EgovEDcryptionPasswdServlceImpl;
import egovframework.rte.fdl.crypto.impl.EgovEDcryptionTxtServiceImpl;
import egovframework.rte.fdl.crypto.impl.EgovARIAEDcryptionTxtServiceImpl;
import egovframework.rte.fdl.crypto.impl.EgovARIAEDcryptionBinServiceImpl;
import egovframework.rte.fdl.crypto.impl.EgovARIAEDcryptionNumServiceImpl;
/**  
 * @Class Name : EgovCryptoset.java
 * @Description : Annotation 설정 Class
 * @Modifcation Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2009.03.10    김종호        최초생성
 * 
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since 2009. 03.10
 * @version 1.0
 * @see
 * 
 *  Copyright (C) by MOPAS All right reserved.
 */
public class EgovCryptoset {
	private static Logger logger  = CryptoLog.getLogger(EgovCryptoset.class);
	 /** 바이너리 암호화 Class */
	 private EgovEDcryptionBinServlceImpl cryptoBin; 
	 /** 바이너리 암호화 Class */
	 private EgovEDcryptionBinServlceImpl cryptoBin1; 
	 /** Text 암호화 Class */
	 private EgovEDcryptionTxtServiceImpl cryptoTxt;
	 /** Text 암호화 Class */
	 private EgovEDcryptionTxtServiceImpl cryptoTxt1; 
	 /** Password 암호화 Class */
	 private EgovEDcryptionPasswdServlceImpl cryptoPwd; 
	 /** Number 암호화 Class */
	 private EgovEDcryptionNumServiceImpl cryptoNum; 
	 /** Number 암호화 Class */
	 private EgovEDcryptionNumServiceImpl cryptoNum1; 
	 /** AriA Text 암호화 Class */
     private EgovARIAEDcryptionTxtServiceImpl cryptoAriaTxt;
     /** AriA Text 암호화 Class */
     private EgovARIAEDcryptionTxtServiceImpl cryptoAriaTxt1;
     /** AriA Bin 암호화 Class */
     private EgovARIAEDcryptionBinServiceImpl cryptoAriaBin;
     /** AriA Bin 암호화 Class */
     private EgovARIAEDcryptionBinServiceImpl cryptoAriaBin1;
     /** AriA Num 암호화 Class */
     private EgovARIAEDcryptionNumServiceImpl cryptoAriaNum;
     /** AriA Num 암호화 Class */
     private EgovARIAEDcryptionNumServiceImpl cryptoAriaNum1;
/**
 * Binary 암복화 모듈 설정
 * @param cryptoBin - Binary 암복화 구현 Class
 * @return null
 * @see 개발프레임워크 실행환경 개발팀
 */
	 public void setCryptoBin(EgovEDcryptionBinServlceImpl cryptoBin) {
	        this.cryptoBin = cryptoBin;
	        logger.debug(cryptoBin.toString());
	    }
/**
 * Binary 암복화 모듈 리턴
 * @param null	 
 * @return Binary 암복화 구현 Class
 * @see 개발프레임워크 실행환경 개발팀
 */
	 public EgovEDcryptionBinServlceImpl getCryptoBin()
	 {
		 return cryptoBin;
	 }
 
	 public void setCryptoBin1(EgovEDcryptionBinServlceImpl cryptoBin1) {
	        this.cryptoBin1 = cryptoBin1;
	        logger.debug(cryptoBin1.toString());
	    }
	 public EgovEDcryptionBinServlceImpl getCryptoBin1()
	 {
		 return cryptoBin1;
	 }
 /**
  * Text 암복화 모듈 설정
  * @param cryptoTxt - Text 암복화 구현 Class
  * @return null
  * @see 개발프레임워크 실행환경 개발팀
  */	 
	 public void setCryptoTxt(EgovEDcryptionTxtServiceImpl cryptoTxt) {
	        this.cryptoTxt = cryptoTxt;
	        logger.debug(cryptoTxt.toString());
	    }
 /**
  * Text 암복화 모듈 리턴
  * @param null	 
  * @return Text 암복화 구현 Class
  * @see 개발프레임워크 실행환경 개발팀
  */	 
	 public EgovEDcryptionTxtServiceImpl getCryptoTxt()
	 {
		 return cryptoTxt;
	 }
	 
	 public void setCryptoTxt1(EgovEDcryptionTxtServiceImpl cryptoTxt1) {
	        this.cryptoTxt1 = cryptoTxt1;
	        logger.debug(cryptoTxt1.toString());
	    }
	 public EgovEDcryptionTxtServiceImpl getCryptoTxt1()
	 {
		 return cryptoTxt1;
	 }
 /**
  * Password 암호화 모듈 설정
  * @param cryptoPwd - Password 암호화 구현 Class
  * @return null
  * @see 개발프레임워크 실행환경 개발팀
  */
	 public void setCryptoPwd(EgovEDcryptionPasswdServlceImpl cryptoPwd) {
	        this.cryptoPwd = cryptoPwd;
	        logger.debug(cryptoPwd.toString());
	    }
 /**
  * Password 암복화 모듈 리턴
  * @param null	 
  * @return Password 암복화 구현 Class
  * @see 개발프레임워크 실행환경 개발팀
  */
	 public EgovEDcryptionPasswdServlceImpl getCryptoPwd()
	 {
		 return cryptoPwd;
	 }
 /**
  * Number 암복화 모듈 설정
  * @param cryptoNum - Number 암복화 구현 Class
  * @return null
  * @see 개발프레임워크 실행환경 개발팀
  */	
	 public void setCryptoNum(EgovEDcryptionNumServiceImpl cryptoNum) {
	        this.cryptoNum = cryptoNum;
	        logger.debug(cryptoNum.toString());
	    }
 /**
  * Number 암복화 모듈 리턴
  * @param null	 
  * @return Number 암복화 구현 Class
  * @see 개발프레임워크 실행환경 개발팀
  */
	 public EgovEDcryptionNumServiceImpl getCryptoNum()
	 {
		 return cryptoNum;
	 }
	 
	 public void setCryptoNum1(EgovEDcryptionNumServiceImpl cryptoNum1) {
	        this.cryptoNum1 = cryptoNum1;
	        logger.debug(cryptoNum1.toString());
	    }
	 public EgovEDcryptionNumServiceImpl getCryptoNum1()
	 {
		 return cryptoNum1;
	 }
	 
	 /**
	  * Text 암복화 모듈 설정
	  * @param Aroia cryptoTxt - Text 암복화 구현 Class
	  * @return null
	  * @see 개발프레임워크 실행환경 개발팀
	  */     
	     public void setCryptoAriaTxt(EgovARIAEDcryptionTxtServiceImpl cryptoAriaTxt) {
	            this.cryptoAriaTxt = cryptoAriaTxt;
	            logger.debug(cryptoAriaTxt.toString());
	        }
	 /**
	  * Text 암복화 모듈 리턴
	  * @param null  
	  * @return Aria Text 암복화 구현 Class
	  * @see 개발프레임워크 실행환경 개발팀
	  */     
	     public EgovARIAEDcryptionTxtServiceImpl getCryptoAriaTxt()
	     {
	         return cryptoAriaTxt;
	     }
	     
	     public void setCryptoAriaTxt1(EgovARIAEDcryptionTxtServiceImpl cryptoAriaTxt1) {
             this.cryptoAriaTxt1 = cryptoAriaTxt1;
             logger.debug(cryptoAriaTxt.toString());
         }
	     public EgovARIAEDcryptionTxtServiceImpl getCryptoAriaTxt1()
         {
             return cryptoAriaTxt1;
         }
	     
	     /**
	      * Bin 암복화 모듈 설정
	      * @param Aroia cryptoBin - Bin 암복화 구현 Class
	      * @return null
	      * @see 개발프레임워크 실행환경 개발팀
	      */     
	         public void setCryptoAriaBin(EgovARIAEDcryptionBinServiceImpl cryptoAriaBin) {
	                this.cryptoAriaBin = cryptoAriaBin;
	            }
	     /**
	      * Bin 암복화 모듈 리턴
	      * @param null  
	      * @return Aria Bin 암복화 구현 Class
	      * @see 개발프레임워크 실행환경 개발팀
	      */     
	         public EgovARIAEDcryptionBinServiceImpl getCryptoAriaBin()
	         {
	             return cryptoAriaBin;
	         }
	         
	         public void setCryptoAriaBin1(EgovARIAEDcryptionBinServiceImpl cryptoAriaBin1) {
	             this.cryptoAriaBin1 = cryptoAriaBin1;
	        }
	         public EgovARIAEDcryptionBinServiceImpl getCryptoAriaBin1()
	         {
	             return cryptoAriaBin1;
	         }
	         
	         /**
	          * Num 암복화 모듈 설정
	          * @param Aroia cryptoNum - Num 암복화 구현 Class
	          * @return null
	          * @see 개발프레임워크 실행환경 개발팀
	          */     
	             public void setCryptoAriaNum(EgovARIAEDcryptionNumServiceImpl cryptoAriaNum) {
	                    this.cryptoAriaNum = cryptoAriaNum;
	                }
	         /**
	          * Num 암복화 모듈 리턴
	          * @param null  
	          * @return Aria Num 암복화 구현 Class
	          * @see 개발프레임워크 실행환경 개발팀
	          */     
	             public EgovARIAEDcryptionNumServiceImpl getCryptoAriaNum()
	             {
	                 return cryptoAriaNum;
	             }
	             
	             public void setCryptoAriaNum1(EgovARIAEDcryptionNumServiceImpl cryptoAriaNum1) {
	                 this.cryptoAriaNum1 = cryptoAriaNum1;
	            }
	             public EgovARIAEDcryptionNumServiceImpl getCryptoAriaNum1()
	             {
	                 return cryptoAriaNum1;
	             }
}
