package com.frenzy.sso.service;


import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.sso.cache.FzCacheCaptcha;
import org.springframework.stereotype.Service;

/**
 * @author yf
 * @since 2024-11-20
 */
@Service
public class FzCacheCaptchaService extends AbstractCosmosService<FzCacheCaptcha> {
    public FzCacheCaptchaService() {
        super(FzCacheCaptcha.class);
    }

    public void saveCache(String verifyKey,String code){
        FzCacheCaptcha fzCacheCaptcha = new FzCacheCaptcha();
        fzCacheCaptcha.setId(verifyKey);
        fzCacheCaptcha.setCode(code);
        this.save(fzCacheCaptcha);
    }

    public String getCode(String verifyKey){
        FzCacheCaptcha fzCacheCaptcha = this.getById(verifyKey);
        if (fzCacheCaptcha==null){
            return "";
        }
        return fzCacheCaptcha.getCode();
    }

}
