package com.frenzy.core.config;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UploadConfig {

    public static final String Upload_Folder_name 		= "f";//云端上传的文件夹名称，适用于一个云端空间保存多个项目的静态文件
    public static final String accessKey                = "3Zvb-uMvQpxgj6JjISidzRGFFxGB0lu2BJH1yG7h";
    public static final String secretKey                = "b_QEyLAhLJok0kA2gmZoOLDH0ZesZIIAwEFiYjL2";
    public static final String bucket                   = "rongsheng2025";
    public static final String domain                   = "https://cimg.rongsheng.c8b.com.cn/";
    public static final String folder                   = "up";

}
