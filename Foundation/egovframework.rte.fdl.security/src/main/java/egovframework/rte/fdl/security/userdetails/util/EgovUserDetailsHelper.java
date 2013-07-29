/*
 * Copyright 2008-2009 MOPAS(Ministry of Public Administration and Security).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.rte.fdl.security.userdetails.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;

import egovframework.rte.fdl.security.userdetails.EgovUserDetails;
import egovframework.rte.fdl.string.EgovObjectUtil;

/**
 * 사용자 계정 정보를 처리하는 유틸 클래스
 * <p>
 * <b>NOTE:</b> 사용자 계정 정보와 권한정보를 조회할 수 있는 유틸 클래스
 * @author 실행환경 개발팀 윤성종
 * @since 2009.06.01
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.06.01  윤성종           최초 생성
 * 
 * </pre>
 */
public class EgovUserDetailsHelper {
    private static Log log = LogFactory.getLog(EgovUserDetailsHelper.class);

    /**
     * 인증된 사용자객체를 VO형식으로 가져온다.
     * @return 사용자 ValueObject
     */
    public static Object getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (EgovObjectUtil.isNull(authentication)) {
            log.debug("## authentication object is null!!");
            return null;
        }

        EgovUserDetails details =
            (EgovUserDetails) authentication.getPrincipal();

        log
            .debug("## EgovUserDetailsHelper.getAuthenticatedUser : AuthenticatedUser is "
                + details.getUsername());
        return details.getEgovUserVO();
    }

    /**
     * 인증된 사용자의 권한 정보를 가져온다. 예) [ROLE_ADMIN, ROLE_USER,
     * ROLE_A, ROLE_B, ROLE_RESTRICTED,
     * IS_AUTHENTICATED_FULLY,
     * IS_AUTHENTICATED_REMEMBERED,
     * IS_AUTHENTICATED_ANONYMOUSLY]
     * @return 사용자 권한정보 목록
     */
    public static List<String> getAuthorities() {
        List<String> listAuth = new ArrayList<String>();

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (EgovObjectUtil.isNull(authentication)) {
            log.debug("## authentication object is null!!");
            return null;
        }

        GrantedAuthority[] authorities = authentication.getAuthorities();

        for (int i = 0; i < authorities.length; i++) {
            listAuth.add(authorities[i].getAuthority());

            log.debug("## EgovUserDetailsHelper.getAuthorities : Authority is "
                + authorities[i].getAuthority());
        }

        return listAuth;
    }

    /**
     * 인증된 사용자 여부를 체크한다.
     * @return 인증된 사용자 여부(TRUE / FALSE)
     */
    public static Boolean isAuthenticated() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (EgovObjectUtil.isNull(authentication)) {
            log.debug("## authentication object is null!!");
            return Boolean.FALSE;
        }

        String username = authentication.getName();
        if (username.equals("roleAnonymous")) {
            log.debug("## username is " + username);
            return Boolean.FALSE;
        }

        Object principal = authentication.getPrincipal();

        return (Boolean.valueOf(!EgovObjectUtil.isNull(principal)));
    }
}
