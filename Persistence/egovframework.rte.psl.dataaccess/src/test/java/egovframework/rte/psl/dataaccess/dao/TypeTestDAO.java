package egovframework.rte.psl.dataaccess.dao;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractDAO;
import egovframework.rte.psl.dataaccess.vo.TypeTestVO;

@Repository("typeTestDAO")
public class TypeTestDAO extends EgovAbstractDAO {

    public void insertTypeTest(String queryId, TypeTestVO vo) {
        getSqlMapClientTemplate().insert(queryId, vo);
    }

    public TypeTestVO selectTypeTest(String queryId, TypeTestVO vo) {
        return (TypeTestVO) getSqlMapClientTemplate().queryForObject(queryId,
            vo);
    }

}
