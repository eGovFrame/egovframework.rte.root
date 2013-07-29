package egovframework.rte.psl.dataaccess.mapper;

import java.math.BigDecimal;
import java.util.List;

import egovframework.rte.psl.dataaccess.vo.EmpVO;

@Mapper("employerMapper")
public interface EmployerMapper  {
	
    public List<EmpVO> selectEmployerList(EmpVO vo);
    
    public EmpVO selectEmployer(BigDecimal empNo);

    public void insertEmployer(EmpVO vo);

    public int updateEmployer(EmpVO vo);
    
    public int deleteEmployer(BigDecimal empNo);
    
}
