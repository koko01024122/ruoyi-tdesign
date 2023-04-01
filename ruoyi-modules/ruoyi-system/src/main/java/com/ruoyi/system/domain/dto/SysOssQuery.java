package com.ruoyi.system.domain.dto;

import com.ruoyi.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


/**
 * OSS对象存储业务对象 sys_oss
 *
 * @author ruoyi
 * @date 2023-04-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOssQuery extends BaseEntity {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上传人
     */
    private String createByName;

    /**
     * 服务商
     */
    private String service;

}
