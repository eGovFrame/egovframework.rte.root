package egovframework.rte.fdl.xml;

import egovframework.rte.fdl.xml.exception.UnsupportedException;

/**
 * DOMValidator 생성 Factory Class 
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
public class EgovConcreteDOMFactory extends abstractXMLFactoryService {
        /** DOMValidator  **/
        private EgovDOMValidatorService domvalidator = null;
        /**
         * DOMValidatorService 생성자
         * @return EgovDOMValidatorService - DOMValidator
         */
        @Override
        public EgovDOMValidatorService CreateDOMValidator() 
        {
                // TODO Auto-generated method stub
                domvalidator = new EgovDOMValidatorService();
                return domvalidator;
        }
        /**
         * EgovSAXValidatorService 생성자
         * @return EgovSAXValidatorService - SAXValidator
         * @exception UnsupportedException
         */
        @Override
        public EgovSAXValidatorService CreateSAXValidator()
        {
                // TODO Auto-generated method stub
                try
                {
                 throw new UnsupportedException("지원되지 않는 방식입니다.");
                }catch(UnsupportedException e)
                {
                        e.printStackTrace();
                }
                return null;
        }

}
