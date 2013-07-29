package egovframework.rte.fdl.cryptography.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEBigDecimalEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.springframework.beans.factory.annotation.Required;

import egovframework.rte.fdl.cryptography.EgovGeneralCryptoService;
import egovframework.rte.fdl.cryptography.EgovPasswordEncoder;

public class EgovGeneralCryptoServiceImpl implements EgovGeneralCryptoService {
    private final Base64 base64 = new Base64();
    
    private Logger logger = Logger.getLogger(this.getClass());	// Logger 처리
    
    private String algorithm = "PBEWithSHA1AndDESede";	// default    
    private EgovPasswordEncoder passwordEncoder;
    private int blockSize = 1024;	// default

    public String getAlgorithm() {
	return algorithm;
    }

    public void setAlgorithm(String algorithm) {
	this.algorithm = algorithm;
	
	logger.debug("General Crypto Service's algorithm : " + algorithm);
    }
    
    @Required
    public void setPasswordEncoder(EgovPasswordEncoder passwordEncoder) {
	this.passwordEncoder = passwordEncoder;
	
	logger.debug("passwordEncoder's algorithm : " + passwordEncoder.getAlgorithm());
    }
    
    public void setBlockSize(int blockSize) {
	this.blockSize = blockSize;
    }
    
    public byte[] encrypt(byte[] data, String password) {
	if (passwordEncoder.checkPassword(password)) {
	    StandardPBEByteEncryptor cipher = new StandardPBEByteEncryptor();
	    
	    cipher.setAlgorithm(algorithm);
	    cipher.setPassword(password);
	    
	    return cipher.encrypt(data);
	} else {
	    logger.error("password not matched!!!");
	    throw new IllegalArgumentException("password not matched!!!");
	}
    }
    
    public BigDecimal encrypt(BigDecimal number, String password) {
	if (passwordEncoder.checkPassword(password)) {
	    StandardPBEBigDecimalEncryptor cipher = new StandardPBEBigDecimalEncryptor();
	    
	    cipher.setAlgorithm(algorithm);
	    cipher.setPassword(password);
	    
	    return cipher.encrypt(number);
	} else {
	    logger.error("password not matched!!!");
	    throw new IllegalArgumentException("password not matched!!!");
	}
    }
    
    public void encrypt(File srcFile, String password, File trgtFile) throws FileNotFoundException, IOException {
	FileInputStream fis = null;
	FileWriter fw = null;
	BufferedInputStream bis = null;
	BufferedWriter bw = null;

	byte[] buffer = null;

	if (passwordEncoder.checkPassword(password)) {
	    StandardPBEByteEncryptor cipher = new StandardPBEByteEncryptor();
	    
	    cipher.setAlgorithm(algorithm);
	    cipher.setPassword(password);
	    
	    buffer = new byte[blockSize];
	    
	    logger.debug("blockSize = " + blockSize);

	    try {
    	    	fis = new FileInputStream(srcFile);
		bis = new BufferedInputStream(fis);
		
		fw = new FileWriter(trgtFile);
		bw = new BufferedWriter(fw);
		
		byte[] encrypted = null;
		int length = 0;
		long size = 0L;
		while ((length = bis.read(buffer)) >= 0) {
		    if (length < blockSize) {
			byte[] tmp = new byte[length];
			System.arraycopy(buffer, 0, tmp, 0, length);
			encrypted = cipher.encrypt(tmp);
		    } else {
			encrypted = cipher.encrypt(buffer);
		    }
		    String line;
		    try {
			line = new String(base64.encode(encrypted), "US-ASCII");
		    } catch (Exception e) {
			throw new RuntimeException(e);
		    }
		    bw.write(line);
		    bw.newLine();
		    size += length;
		}
		bw.flush();
		logger.debug("processed bytes = " + size);
	    } catch (FileNotFoundException fnfe) {
		throw fnfe;
	    } catch (IOException ioe) {
		throw ioe;
	    } finally {
		if (fw != null) {
		    try {
			fw.close();
		    } catch (IOException ignore) {
			// no-op
		    }
		}
		if (bw != null) {
		    try {
			bw.close();
		    } catch (IOException ignore) {
			// no-op
		    }
		}
		if (fis != null) {
		    try {
			fis.close();
		    } catch (IOException ignore) {
			// no-op
		    }
		}
		if (bis != null) {
		    try {
			bis.close();
		    } catch (IOException ignore) {
			// no-op
		    }
		}
	    }
	    
	} else {
	    logger.error("password not matched!!!");
	    throw new IllegalArgumentException("password not matched!!!");
	}
    }

    public byte[] decrypt(byte[] encryptedData, String password) {
	if (passwordEncoder.checkPassword(password)) {
	    StandardPBEByteEncryptor cipher = new StandardPBEByteEncryptor();
	    
	    cipher.setAlgorithm(algorithm);
	    cipher.setPassword(password);
	    
	    return cipher.decrypt(encryptedData);
	} else {
	    logger.error("password not matched!!!");
	    throw new IllegalArgumentException("password not matched!!!");
	}
    }

    public BigDecimal decrypt(BigDecimal encryptedNumber, String password) {
	if (passwordEncoder.checkPassword(password)) {
	    StandardPBEBigDecimalEncryptor cipher = new StandardPBEBigDecimalEncryptor();
	    
	    cipher.setAlgorithm(algorithm);
	    cipher.setPassword(password);
	    
	    return cipher.decrypt(encryptedNumber);
	} else {
	    logger.error("password not matched!!!");
	    throw new IllegalArgumentException("password not matched!!!");
	}
    }

    public void decrypt(File encryptedFile, String password, File trgtFile) throws FileNotFoundException, IOException {
	FileReader fr = null;
	FileOutputStream fos = null;
	BufferedReader br = null;
	BufferedOutputStream bos = null;

	if (passwordEncoder.checkPassword(password)) {
	    StandardPBEByteEncryptor cipher = new StandardPBEByteEncryptor();
	    
	    cipher.setAlgorithm(algorithm);
	    cipher.setPassword(password);

	    try {
    	    	fr = new FileReader(encryptedFile);
		br = new BufferedReader(fr);
		
		fos = new FileOutputStream(trgtFile);
		bos = new BufferedOutputStream(fos);
		
		byte[] encrypted = null;
		byte[] decrypted = null;
		String line = null;
		
		while ((line = br.readLine()) != null) {
		    try {
			encrypted = base64.decode(line.getBytes("US-ASCII"));
		    } catch (Exception e) {
			throw new RuntimeException(e);
		    }
		    
		    decrypted = cipher.decrypt(encrypted);
		    
		    bos.write(decrypted);
		}
		bos.flush();
	    } catch (FileNotFoundException fnfe) {
		throw fnfe;
	    } catch (IOException ioe) {
		throw ioe;
	    } finally {
		if (fos != null) {
		    try {
			fos.close();
		    } catch (IOException ignore) {
			// no-op
		    }
		}
		if (bos != null) {
		    try {
			bos.close();
		    } catch (IOException ignore) {
			// no-op
		    }
		}
		if (fr != null) {
		    try {
			fr.close();
		    } catch (IOException ignore) {
			// no-op
		    }
		}
		if (br != null) {
		    try {
			br.close();
		    } catch (IOException ignore) {
			// no-op
		    }
		}
	    }
	    
	} else {
	    logger.error("password not matched!!!");
	    throw new IllegalArgumentException("password not matched!!!");
	}
    }
}
