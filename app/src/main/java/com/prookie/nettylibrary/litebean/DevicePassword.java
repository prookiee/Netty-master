package com.prookie.nettylibrary.litebean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DevicePassword
 * Created by brin on 2018/7/10.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DevicePassword extends LitePalSupport {

    //    @Column(unique = true, nullable = false)
    private long id;//id，插入时自动生成
    @Column(unique = true, nullable = false)
    private String password;
    @Column(nullable = false)
    private String type;//密码类型:1.长期密码；2.短期密码；3.次数密码
    private String date;//密码持续日期
    private String count;//密码使用次数
    @Column(nullable = false)
    private String deviceToken;//设备编号

}
