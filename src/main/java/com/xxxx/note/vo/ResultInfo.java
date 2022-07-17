package com.xxxx.note.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 封装返回结果的类
 *      状态码
 *          成功=1 ，失败=0
 *      提示信息
 *      返回对象（字符串，JavaBean，集合，Map等）
 */
@Getter
@Setter
public class ResultInfo<T> {

    private Integer code;//状态码
    private String msg;
    private T result;//返回对象
}
