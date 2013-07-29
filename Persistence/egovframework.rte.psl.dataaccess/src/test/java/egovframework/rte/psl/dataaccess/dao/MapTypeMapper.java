package egovframework.rte.psl.dataaccess.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractMapper;

@Repository("mapTypeMapper")
public class MapTypeMapper extends EgovAbstractMapper {

    public void insertDept(String queryId, Map<String, Object> map) {
        insert(queryId, map);
    }

    public int updateDept(String queryId, Map<String, Object> map) {
        return update(queryId, map);
    }

    public int deleteDept(String queryId, Map<String, Object> map) {
        return delete(queryId, map);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> selectDept(String queryId, Map<String, Object> map) {
        return (Map<String, Object>) selectByPk(queryId, map);
    }

    @SuppressWarnings("unchecked")
    public List<Map> selectDeptList(String queryId, Map searchMap) {
        return list(queryId, searchMap);
    }
}
