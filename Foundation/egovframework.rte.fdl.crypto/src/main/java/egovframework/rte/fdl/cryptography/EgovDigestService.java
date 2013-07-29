package egovframework.rte.fdl.cryptography;

public interface EgovDigestService {
    /**
     * Digest 알고리즘.
     * 
     * @param algorithm
     */
    public void setAlgorithm(String algorithm);
    
    /**
     * Digest 알고리즘.
     * 
     * @return
     */
    public String getAlgorithm();
    
    /**
     * Plain digest.
     * 
     * @param isPlainDigest
     */
    public void setPlainDigest(boolean isPlainDigest);
    
    /**
     * Plain digest.
     * 
     * @return
     */
    public boolean isPlainDigest();
    
    /**
     * Digest 처리.
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public byte[] digest(byte[] data);
    
    /**
     * 확인 처리.
     * 
     * @param message
     * @param digest
     * @return
     */
    public boolean matches(byte[] messageByte, byte[] digestByte);
}
