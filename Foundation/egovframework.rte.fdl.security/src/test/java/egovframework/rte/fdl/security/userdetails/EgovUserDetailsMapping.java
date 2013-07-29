package egovframework.rte.fdl.security.userdetails;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import egovframework.rte.fdl.security.userdetails.jdbc.EgovUsersByUsernameMapping;

/**
 * @author sjyoon
 *
 */
public class EgovUserDetailsMapping extends EgovUsersByUsernameMapping {

	/**
	 * EgovUserDetailsMapping 생성자
	 * @param ds
	 * @param usersByUsernameQuery
	 */
	public EgovUserDetailsMapping(DataSource ds, String usersByUsernameQuery) {
        super(ds, usersByUsernameQuery);
    }

	/* (non-Javadoc)
	 * @see egovframework.rte.fdl.security.userdetails.jdbc.EgovUsersByUsernameMapping#mapRow(java.sql.ResultSet, int)
	 */
	/**
	 * EgovUsersByUsernameMapping 클래스를 상속받아
	 * jdbc-user-service 에서 지정된 users-by-username-query 의 쿼리문을 조회하여
	 * ResultSet에 매핑된다.
	 */
	@Override
    protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
    	logger.debug("## EgovUsersByUsernameMapping mapRow ##");

        String userid = rs.getString("user_id");
        String password = rs.getString("password");
        boolean enabled = rs.getBoolean("enabled");

        String username = rs.getString("user_name");
        String birthDay = rs.getString("birth_day");
        String ssn = rs.getString("ssn");

        EgovUserDetailsVO userVO = new EgovUserDetailsVO();
        userVO.setUserId(userid);
        userVO.setPassWord(password);
        userVO.setUserName(username);
        userVO.setBirthDay(birthDay);
        userVO.setSsn(ssn);

        logger.debug("###### userVO is " + userVO);

        return new EgovUserDetails(userid, password, enabled, userVO);
    }

}
