package egovframework.rte.fdl.xml;

import org.apache.log4j.Logger;

import egovframework.rte.fdl.xml.exception.UnsupportedException;
/**
 * SAXValidator 생성 Factory Class 
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
public class EgovConcreteSAXFactory extends abstractXMLFactoryService {
        private static Logger logger  = XmlLog.getLogger(EgovConcreteSAXFactory.class);
        /** SAXValidator **/
        private EgovSAXValidatorService saxValidator = null;
        /**
         * DOMValidatorService 생성자
         * @return EgovDOMValidatorService - DOMValidator
         * @exception UnsupportedException
         */
        @Override
        public EgovDOMValidatorService CreateDOMValidator()
        {
                try
                {
                 throw new UnsupportedException("지원되지 않는 방식입니다.");
                }catch(UnsupportedException e)
                {
                         logger.debug(e.getMessage());
                }
                return null;
        }
        /**
         * EgovSAXValidatorService 생성자
         * @return EgovSAXValidatorService - SAXValidator
         */
        @Override
        public EgovSAXValidatorService CreateSAXValidator()
        {
                // TODO Auto-generated method stub
                saxValidator =  new EgovSAXValidatorService();
                return saxValidator;
        }

}
