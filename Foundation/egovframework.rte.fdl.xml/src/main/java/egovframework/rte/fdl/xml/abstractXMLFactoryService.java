package egovframework.rte.fdl.xml;

/**
 * Factory Class
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since 2009. 03.10
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 *  2009.03.10    김종호        최초생성
 * 
 * </pre>
 */

public abstract class abstractXMLFactoryService {
        /**
         * DOM 파서를 사용할 수 있도록 Service 생성
         * @return EgovDOMValidatorService
         */
        public abstract EgovDOMValidatorService CreateDOMValidator();
         /**
         * SAX 파서를 사용할 수 있도록 Service 생성
         * @return EgovSAXValidatorService
         */
        public abstract EgovSAXValidatorService CreateSAXValidator();
}
