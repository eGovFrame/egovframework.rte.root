package egovframework.rte.fdl.xml;

import java.io.Serializable;
/**
 * Object Wrap 구조체 Class
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
public class SharedObject implements Serializable {
   /** Object 조회 Key **/
   String key = null;
   /** 저장 Object **/
   Object value = null;
   /**
    * SharedObject 생성자
    * @param key - Object key
    * @param value - 저장 object
    */
   public SharedObject(String key,Object value)
   {
           this.key = key;
           this.value = value;
   }
   /**
    * 키 리턴
    * @return 키 
    */
   public String getKey()
   {
           return key;
   }
   /**
    * Object 리턴
    * @return Object
    */
   public Object getValue()
   {
           return value;
   }
}
