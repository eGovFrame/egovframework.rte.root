package egovframework.rte.fdl.cryptography;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

public interface EgovCryptoService {
    /**
     * 패스워드 암호화 지정.
     * 
     * @param passwordEncoder
     */
    public void setPasswordEncoder(EgovPasswordEncoder passwordEncoder);
    
    /**
     * 파일처리시 사용되는 blockSize 지정.
     * 
     * @param blockSize
     */
    public void setBlockSize(int blockSize);
    
    /**
     * 암호화 처리.
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] encrypt(byte[] data, String password);
    
    /**
     * BigDecimal 암호화 처리.
     * 
     * @param number
     * @return
     * @throws Exception
     */
    public BigDecimal encrypt(BigDecimal number, String password);
    
    /**
     * 파일 암호화 처리.
     * 
     * @param srcFile
     * @param trgtFile
     * @param password
     * @throws Exception
     */
    public void encrypt(File srcFile, String password, File trgtFile) throws FileNotFoundException, IOException;
    
    /**
     * 복호화 처리.
     * 
     * @param encryptedData
     * @param password
     * @return
     * @throws Exception
     */
    public byte[] decrypt(byte[] encryptedData, String password);
    
    /**
     * BigDecimal 복호화 처리.
     * 
     * @param encryptedNumber
     * @param password
     * @return
     * @throws Exception
     */
    public BigDecimal decrypt(BigDecimal encryptedNumber, String password);
    
    /**
     * 파일 복호화 처리.
     * 
     * @param encryptedFile
     * @param password
     * @param trgtFile
     * @throws Exception
     */
    public void decrypt(File encryptedFile, String password, File trgtFile) throws FileNotFoundException, IOException;
}
