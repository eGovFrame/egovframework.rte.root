package egovframework.rte.fdl.xml;

/**
 * XML 설정 정보관리 Class
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since 2009.03.18 
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 * 2009.03.18    김종호        최초생성
 * 
 * </pre>
 */
public class XmlConfig {
  /** XML 파일 생성 경로 */
  String path;
  
  /**
   * path 설정
   * @param path - 패스워드
   */
  public void setXmlpath(String path)
  {
          this.path = path;
  }
  /**
   * path 리턴
   * @return path - 패스워드
   */
  public String getXmlpath()
  {
          return path;
  }
}
