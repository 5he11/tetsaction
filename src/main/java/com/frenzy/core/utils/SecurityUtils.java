package com.frenzy.core.utils;

import com.frenzy.core.constant.HttpStatus;
import com.frenzy.core.exception.ServiceException;
import com.frenzy.sso.entity.po.SsoLoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全服务工具类
 *
 * @author ruoyi
 */
public class SecurityUtils
{

//    // 静态方法获取 Request
//    public static HttpServletRequest getCurrentRequest() {
//        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
//                .getRequest();
//    }
//
//    /**
//     * 用户ID
//     **/
//    public static String getUserId()
//    {
////        try
////        {
//            HttpServletRequest request = getCurrentRequest();
//            Authentication authentication = (Authentication) request.getUserPrincipal();
////            return (SsoLoginUser) authentication.getPrincipal();
//
//            SsoLoginUser loginuser = (SsoLoginUser)authentication.getPrincipal();
//            return loginuser.getUserId();
////            return getLoginUser().getUserId();
////        }
////        catch (Exception e)
////        {
////            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
////        }
//    }
//
//    /**
//     * 获取部门ID
//     **/
//    public static Long getDeptId()
//    {
//        try
//        {
//            return getLoginUser().getDeptId();
//        }
//        catch (Exception e)
//        {
//            throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    /**
//     * 获取用户账户
//     **/
//    public static String getUsername()
//    {
//        try
//        {
//
//            return getLoginUser().getUser().getUserName();
//        }
//        catch (Exception e)
//        {
//            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    /**
//     * 获取用户
//     **/
//    public static SsoLoginUser getLoginUser2()
//    {
//        try
//        {
//            Authentication authentication = getAuthentication();
//            return (SsoLoginUser) authentication.getPrincipal();
//        }
//        catch (Exception e)
//        {
//            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
//        }
//    }
//
//    /**
//     * 获取Authentication
//     */
//    public static Authentication getAuthentication()
//    {
//
//        return SecurityContextHolder.getContext().getAuthentication();
//    }
//
//    public static SsoLoginUser getLoginUser() {
//        HttpServletRequest request =
//                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//                        .getRequest();
//        Authentication authentication = (Authentication) request.getUserPrincipal();
//        return (SsoLoginUser) authentication.getPrincipal();
//    }
//
//    public HttpServletRequest getRequestInfo() {
//        HttpServletRequest request =
//                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
//                        .getRequest();
//        return request;
//    }
    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }
}
