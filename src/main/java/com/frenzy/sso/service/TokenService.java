package com.frenzy.sso.service;


import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.frenzy.core.constant.Constants;
import com.frenzy.sso.cache.FzCacheSsoUser;
import com.frenzy.sso.entity.po.SsoLoginUser;
import com.microsoft.azure.functions.HttpRequestMessage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
/**
 * token验证处理
 *
 * @author yf
 */
@Component
@Slf4j
public class TokenService
{
    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Autowired
    private FzCacheSsoUserService cacheSsoUserService;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public <T> SsoLoginUser getLoginUser(HttpRequestMessage<Optional<T>> request)
    {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StrUtil.isNotEmpty(token))
        {
            try
            {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                FzCacheSsoUser fzCache = cacheSsoUserService.getById(userKey);
                SsoLoginUser user = fzCache.getLoginUser();
                return user;
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(SsoLoginUser loginUser)
    {
        if (loginUser!=null && StrUtil.isNotEmpty(loginUser.getToken()))
        {
            refreshToken(loginUser);
        }
    }



    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(SsoLoginUser loginUser)
    {
        String token = UUID.fastUUID().toString();
        loginUser.setToken(token);
//        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

//    /**
//     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
//     *
//     * @param loginUser
//     * @return 令牌
//     */
//    public void verifyToken(SsoLoginUser loginUser)
//    {
//        long expireTime = loginUser.getExpireTime();
//        long currentTime = System.currentTimeMillis();
//        if (expireTime - currentTime <= MILLIS_MINUTE_TEN)
//        {
//            refreshToken(loginUser);
//        }
//    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(SsoLoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        if (StrUtil.isEmpty(userKey)){
            FzCacheSsoUser fzCache = new FzCacheSsoUser();
            fzCache.setId(userKey);
            fzCache.setLoginUser(loginUser);
            fzCache.setCreateTime(LocalDateTimeUtil.now().toString());
            fzCache.setTtl(expireTime);
            cacheSsoUserService.save(fzCache);
        }else{
            FzCacheSsoUser fzCacheSsoUser = cacheSsoUserService.getById(userKey);
            if (fzCacheSsoUser==null){
                FzCacheSsoUser fzCache = new FzCacheSsoUser();
                fzCache.setId(userKey);
                fzCache.setLoginUser(loginUser);
                fzCache.setCreateTime(LocalDateTimeUtil.now().toString());
                fzCache.setTtl(expireTime);
                cacheSsoUserService.save(fzCache);
            }else{
                fzCacheSsoUser.setId(userKey);
                fzCacheSsoUser.setLoginUser(loginUser);
                fzCacheSsoUser.setCreateTime(LocalDateTimeUtil.now().toString());
                fzCacheSsoUser.setTtl(expireTime);
                cacheSsoUserService.update(fzCacheSsoUser);
            }
        }



//        cosmosService.createItem(fzCache);
//        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

//    /**
//     * 设置用户代理信息
//     *
//     * @param loginUser 登录信息
//     */
//    public void setUserAgent(SsoLoginUser loginUser)
//    {
//        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
//        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
//        loginUser.setIpaddr(ip);
//        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
//        loginUser.setBrowser(userAgent.getBrowser().getName());
//        loginUser.setOs(userAgent.getOperatingSystem().getName());
//    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token)
    {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private <T> String getToken(HttpRequestMessage<Optional<T>> request)
    {
        String token =  request.getHeaders().get(header);
        if (StrUtil.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid)
    {
        return Constants.LOGIN_TOKEN_KEY + uuid;
    }
}
