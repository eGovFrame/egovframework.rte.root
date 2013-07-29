package egovframework.rte.fdl.xml;

import java.util.Set;
import org.apache.xerces.parsers.DOMParser;
import egovframework.rte.fdl.xml.error.ErrorChecker;
import egovframework.rte.fdl.xml.exception.ValidatorException;
/**
 * DOMValidator Class 
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
public class EgovDOMValidatorService extends AbstractXMLUtility {
    /**
     *  EgovDOMValidatorService 생성자
     */
        public EgovDOMValidatorService() {
                super();
        }
        /**
         * XML Parsing
         * @param isValid - Validation 검사여부
         * @return 파싱결과
         * @throws ValidatorException
         */
        @Override
        public boolean parse(boolean isValid) throws ValidatorException {
                // TODO Auto-generated method stub
                try {
                        if ( (getXML() == null) && (getXMLFile() == null) ) {
                                String message = null;
                                if ( isValid ) {
                                        message = "XML Validation을 체크하기 위한 XML이 필요합니다.";
                                } else {
                                        message = "Well-Formed를 체크하기 위한 XML이 필요합니다.";
                                }

                        }
                        
                        //파서를 생성한다. DOM 파서는 파서의 직접 생성이 가능하다.
                        DOMParser parser = new DOMParser();
                        
                        //Validation의 실행 유무를 결정한다. isValid가 false일 경우 XML 문서가
                        //Well-Formed인지의 여부만 체크한다. 
                        //true로 설정할 경우 XML 문서의 Validation을 체크한다.
                        
                         // Well-formed : XML문서가 갖추어야 할 기본 조건을 잘지키는 문서
                         // Valid : Well-formed+DTD 조건을 추가로 지키고 있는 문
                         
                        parser.setFeature("http://xml.org/sax/features/validation",     isValid);
                        if(getSCHEMAFile() != null)
                    {
                        parser.setFeature("http://apache.org/xml/features/validation/schema",true);
                            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking",   true);
                        parser.setProperty("http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",   getSCHEMAFile() );
                    }

                        //에러 메시지를 저장할 ErrorHandler를 세팅한다.
                        ErrorChecker errors = new ErrorChecker();
                        //파서에 ErrorHandler를 전달한다.
                        parser.setErrorHandler(errors);
            
                        //XML 문서를 파싱한다.
                        if (getXMLFile() != null) {
                            parser.parse(getXMLFile());
               } else {
                                 parser.parse(stringToInputSource());
                        }

                        Set errorReport = errors.getErrorReport();

                        //XML 문서 파싱시 발생된 에러가 있다면 XMLValidatorException을 
                        //이용해서 에러 메시지를 사용자에게 전달한다.
                        if (errorReport.size() > 0) {
                                makeErrorMessage(errorReport);

                                return false;                           
                        } else {
                                return true;
                        }
                } catch (Exception e) {
                        throw new ValidatorException(e.getMessage());
                }
        }
}
