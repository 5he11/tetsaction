package com.frenzy.sso.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.frenzy.core.service.AbstractCosmosService;
import com.frenzy.core.constant.UserConstants;
import com.frenzy.core.utils.StringUtils;
import com.frenzy.core.utils.YFLocalDateTimeUtil;
import com.frenzy.core.utils.text.Convert;
import com.frenzy.sso.domain.SsoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


/**
 * 登录校验方法
 * 
 * @author yf
 */
@Component
public class SsoConfigService extends AbstractCosmosService<SsoConfig>
{

    public SsoConfigService() {
        super(SsoConfig.class);
    }


    @Autowired
    private SsoUserService userService;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {

    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    public SsoConfig selectConfigById(String configId)
    {
        return this.getById(configId);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    public String selectConfigByKey(String configKey)
    {
        SsoConfig ssoConfig = this.getOne("select TOP 1 * from c where c.configKey = '"+configKey+"' and c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
        if (ssoConfig != null){
            if (StrUtil.isNotEmpty(ssoConfig.getConfigValue())){
                return ssoConfig.getConfigValue();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    public boolean selectCaptchaOnOff()
    {
        String captchaOnOff = selectConfigByKey("sys.account.captchaOnOff");
        if (StringUtils.isEmpty(captchaOnOff))
        {
            return true;
        }
        return Convert.toBool(captchaOnOff);
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SsoConfig> selectConfigList(SsoConfig config)
    {
        String sql = "select * from c where c.partitionKey = '"+this.getPartitionKeyFromItem()+"'";
        if (StrUtil.isNotEmpty(config.getConfigName())){
            sql = sql + "AND c.configName like concat('%', '"+config.getConfigName()+"', '%')";
        }
//        if (StrUtil.isNotEmpty(config.getConfigType())){
//            sql = sql + "AND c.configType = '"+config.getConfigType()+"' ";
//        }
        if (StrUtil.isNotEmpty(config.getConfigKey())){
            sql = sql + "AND c.configKey like concat('%', '"+config.getConfigKey()+"', '%')";
        }
        return this.list(sql);
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    public SsoConfig insertConfig(SsoConfig config)
    {
        config.setKeyName(config.getKeyName());
        config.setId(UUID.fastUUID().toString());
        config.setCreateTime(YFLocalDateTimeUtil.now());
        this.save(config);
        return config;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    public SsoConfig updateConfig(SsoConfig config)
    {
        config.setUpdateTime(YFLocalDateTimeUtil.now());
        this.update(config);
        return config;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    public void deleteConfigByIds(String configIds)
    {
//        for (String configId : configIds)
//        {
//            SsoConfig config = selectConfigById(configIds);
//            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
//            {
//                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
//            }
            this.delete(configIds);
//        }
    }




    /**
     * 重置参数缓存数据
     */
    public void resetConfigCache()
    {

    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    public String checkConfigKeyUnique(SsoConfig config)
    {
        String configId = StringUtils.isNull(config.getId()) ? "-1" : config.getId();
        SsoConfig ssoConfig = this.getOne("select TOP 1 * from c where c.configKey = '"+config.getConfigKey()+"' and c.partitionKey = '"+this.getPartitionKeyFromItem()+"'");
        if (StringUtils.isNotNull(ssoConfig) && !StrUtil.equals(ssoConfig.getId(), configId))
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }


}
