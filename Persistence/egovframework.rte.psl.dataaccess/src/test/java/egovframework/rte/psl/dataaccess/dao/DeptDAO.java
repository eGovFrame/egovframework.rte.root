package egovframework.rte.psl.dataaccess.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractDAO;
import egovframework.rte.psl.dataaccess.vo.DeptVO;

@Repository("deptDAO")
public class DeptDAO extends EgovAbstractDAO {

    public void insertDept(String queryId, DeptVO vo) {
        getSqlMapClientTemplate().insert(queryId, vo);
    }

    public int updateDept(String queryId, DeptVO vo) {
        return getSqlMapClientTemplate().update(queryId, vo);
    }

    public int deleteDept(String queryId, DeptVO vo) {
        return getSqlMapClientTemplate().delete(queryId, vo);
    }

    public DeptVO selectDept(String queryId, DeptVO vo) {
        return (DeptVO) getSqlMapClientTemplate().queryForObject(queryId, vo);
    }

    @SuppressWarnings("unchecked")
    public List<DeptVO> selectDeptList(String queryId, DeptVO searchVO) {
        return getSqlMapClientTemplate().queryForList(queryId, searchVO);
    }
}
