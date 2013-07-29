package egovframework.rte.fdl.cryptography;

public interface EgovGeneralCryptoService extends EgovCryptoService {
    /**
     * 암복호화 알고리즘.
     * 
     * @param algorithm
     */
    public void setAlgorithm(String algorithm);
    
    /**
     * 암복호화 알고리즘.
     * 
     * @return
     */
    public String getAlgorithm();

}
