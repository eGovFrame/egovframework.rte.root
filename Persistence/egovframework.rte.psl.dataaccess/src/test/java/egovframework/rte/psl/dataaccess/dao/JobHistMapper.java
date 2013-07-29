package egovframework.rte.psl.dataaccess.dao;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import egovframework.rte.psl.dataaccess.vo.JobHistVO;

@Repository("jobHistMapper")
public class JobHistMapper extends EgovAbstractMapper {

	@Resource(name = "batchSqlSessionTemplate")
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		super.setSqlSessionTemplate(sqlSessionTemplate);
	}
	
    public JobHistVO selectJobHist(String queryId, JobHistVO vo) {
        return (JobHistVO) selectByPk(queryId, vo);
    }

    @SuppressWarnings("unchecked")
    public List<JobHistVO> selectJobHistList(String queryId, JobHistVO vo) {
        return list(queryId, vo);
    }

}
