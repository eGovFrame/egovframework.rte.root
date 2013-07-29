package egovframework.rte.psl.dataaccess.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import egovframework.rte.psl.dataaccess.vo.DeptVO;

@Repository("deptMapper")
public class DeptMapper extends EgovAbstractMapper {

    public void insertDept(String queryId, DeptVO vo) {
        insert(queryId, vo);
    }

    public int updateDept(String queryId, DeptVO vo) {
        return update(queryId, vo);
    }

    public int deleteDept(String queryId, DeptVO vo) {
        return delete(queryId, vo);
    }

    public DeptVO selectDept(String queryId, DeptVO vo) {
        return (DeptVO)selectByPk(queryId, vo);
    }

    @SuppressWarnings("unchecked")
    public List<DeptVO> selectDeptList(String queryId, DeptVO searchVO) {
        return list(queryId, searchVO);
    }
}
