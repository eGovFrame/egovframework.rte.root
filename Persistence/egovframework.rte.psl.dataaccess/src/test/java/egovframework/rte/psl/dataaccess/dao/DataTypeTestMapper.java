package egovframework.rte.psl.dataaccess.dao;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;
import egovframework.rte.psl.dataaccess.vo.TypeTestVO;

@Repository("dataTypeTestMapper")
public class DataTypeTestMapper extends EgovAbstractMapper {

    public void insertTypeTest(String queryId, TypeTestVO vo) {
        insert(queryId, vo);
    }

    public TypeTestVO selectTypeTest(String queryId, TypeTestVO vo) {
        return (TypeTestVO) selectByPk(queryId, vo);
    }

}
