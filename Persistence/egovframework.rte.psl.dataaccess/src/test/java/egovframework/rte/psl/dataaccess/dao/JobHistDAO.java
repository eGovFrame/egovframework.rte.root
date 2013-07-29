package egovframework.rte.psl.dataaccess.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractDAO;
import egovframework.rte.psl.dataaccess.vo.JobHistVO;

@Repository("jobHistDAO")
public class JobHistDAO extends EgovAbstractDAO {

    public JobHistVO selectJobHist(String queryId, JobHistVO vo) {
        return (JobHistVO) getSqlMapClientTemplate()
            .queryForObject(queryId, vo);
    }

    @SuppressWarnings("unchecked")
    public List<JobHistVO> selectJobHistList(String queryId, JobHistVO vo) {
        return getSqlMapClientTemplate().queryForList(queryId, vo);
    }

}
