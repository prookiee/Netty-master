package com.prookie.nettylibrary.litebean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DeviceInfo
 * Created by brin on 2018/7/10.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DeviceInfo extends LitePalSupport{

    @Column(unique = true, nullable = false)
    private long id;//数据库存储Id
    @Column(nullable = false)
    private String groupId;//所属groupId
    @Column(nullable = false)
    private String deviceToken;//设备编号

}
