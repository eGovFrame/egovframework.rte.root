package egovframework.rte.fdl.crypto;

/**
 * 암복화 정보 설정,리턴하는 Class
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since 2009. 03.12
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) == 
 * 
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 *  2009.03.12    김종호        최초생성
 * 
 * </pre>
 */

public class CryptoConfig {
  /** 패스워드*/
  private String password;
  /** 알고리즘*/
  private String algorithm;
  /** 패스워드 알고리즘*/
  private String pwd_algorithm;
  /**
   * Password 설정
   * @param password - 패스워드
   */
  public void setPassword(String password)
  {
	  this.password = password;
  }
  /**
   * Password 리턴
   * @return Password - 패스워드
   */
  public String getPassword()
  {
	  return password;
  }
  /**
   * Algorithm 설정
   * @param algorithm - 알고리즘
   */
  public void setAlgorithm(String algorithm)
  {
	  this.algorithm = algorithm;
  }
  /**
   * Algorithm 리턴
   * @return Algorithm - 알고리즘
   */
  public String getAlgorithm()
  {
	  return algorithm;
  }
  
  /**
   * Password Algorithm 설정
   * @param pwd_algorithm - 알고리즘
   */
  public void setPasswordAlgorithm(String pwd_algorithm)
  {
	  this.pwd_algorithm = pwd_algorithm;
  }
  /**
   * Password Algorithm 리턴
   * @return Algorithm - 알고리즘
   */
  public String getPasswordAlgorithm()
  {
	  return pwd_algorithm;
  }
}
