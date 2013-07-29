package egovframework.rte.fdl.cryptography.impl;

import org.apache.log4j.Logger;
import org.jasypt.digest.StandardByteDigester;

import egovframework.rte.fdl.cryptography.EgovDigestService;

public class EgovDigestServiceImpl implements EgovDigestService {
    private Logger logger = Logger.getLogger(this.getClass());	// Logger 처리
    
    private String algorithm = "SHA-256";	// default
    private boolean plainDigest = false;	// default
    
    private int strongIterations = 1000;	// default
    private int strongSaltSizeBytes = 8;	// default
	
    public void setAlgorithm(String algorithm) {
	this.algorithm = algorithm;
    }
    
    public String getAlgorithm() {
	return algorithm;
    }
    
    public void setPlainDigest(boolean plainDigest) {
	this.plainDigest = plainDigest;
    }
    
    public boolean isPlainDigest() {
	return plainDigest;
    }
    
    public byte[] digest(byte[] data) {
	StandardByteDigester digester = new StandardByteDigester();
	
	digester.setAlgorithm(algorithm);
	
	logger.debug("Digest's algorithm : " + algorithm);
	
        if (plainDigest) {
            digester.setIterations(1);
            digester.setSaltSizeBytes(0);
        } else {
            digester.setIterations(strongIterations);
            digester.setSaltSizeBytes(strongSaltSizeBytes);
        }
	
	return digester.digest(data);
    }
    
    public boolean matches(byte[] messageByte, byte[] digestByte) {
	StandardByteDigester digester = new StandardByteDigester();
	
	digester.setAlgorithm(algorithm);
	
	logger.debug("Digest's algorithm : " + algorithm);
	
        if (plainDigest) {
            digester.setIterations(1);
            digester.setSaltSizeBytes(0);
        } else {
            digester.setIterations(strongIterations);
            digester.setSaltSizeBytes(strongSaltSizeBytes);
        }
	
	return digester.matches(messageByte, digestByte);
    }
}
