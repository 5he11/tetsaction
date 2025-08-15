package com.frenzy.sso.service;

import cn.hutool.core.util.StrUtil;
import com.frenzy.FrenzyConfig;
import com.frenzy.core.utils.SecurityUtils;
import com.frenzy.core.utils.yftools;
import com.frenzy.sso.domain.SsoUser;
import com.frenzy.sso.entity.po.SsoLoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Component
public class SsoLoginService
{
    @Autowired
    private TokenService tokenService;
    @Autowired
    private FrenzyConfig frenzyConfig;
    @Autowired
    private FzCacheCaptchaService fzCacheCaptchaService;
    @Autowired
    private SsoUserService userService;



    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid)
    {

        boolean captchaOn = frenzyConfig.isCaptchaOn();
        // 验证码开关
        if (captchaOn)
        {
            validateCaptcha(username, code, uuid);
        }
        SsoUser ssoUser = userService.getUserByUserName(username);
        if (ssoUser == null){
            yftools.throwException("账号不存在");
        }

        if (!SecurityUtils.matchesPassword(password,ssoUser.getPassword())){
            yftools.throwException("密码不正确");
        }


//        if (!StrUtil.equals(SecurityUtils.encryptPassword(password),ssoUser.getPassword())){
//            yftools.throwException("密码不正确");
//        }

        SsoLoginUser ssoLoginUser = new SsoLoginUser();
        ssoLoginUser.setUserId(ssoUser.getId());
        ssoLoginUser.setUser(ssoUser);

        // 生成token
        return tokenService.createToken(ssoLoginUser);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = uuid;
        String captcha = fzCacheCaptchaService.getCode(verifyKey);
        fzCacheCaptchaService.delete(verifyKey);
        if (captcha == null)
        {
            yftools.throwException("Code err!");
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            yftools.throwException("Code err!");
        }
    }

}
