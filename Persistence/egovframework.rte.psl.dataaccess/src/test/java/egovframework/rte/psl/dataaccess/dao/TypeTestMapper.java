package egovframework.rte.psl.dataaccess.dao;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import egovframework.rte.psl.dataaccess.vo.TypeTestVO;

@Repository("typeTestMapper")
public class TypeTestMapper extends EgovAbstractMapper {

    public void insertTypeTest(String queryId, TypeTestVO vo) {
    	insert(queryId, vo);
    }

    public TypeTestVO selectTypeTest(String queryId, TypeTestVO vo) {
        return (TypeTestVO) selectByPk(queryId, vo);
    }

}
