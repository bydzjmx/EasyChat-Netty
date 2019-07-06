package com.jmx.utils;

import tk.mybatis.mapper.common.Mapper;

/**
 * 继承通用mapper的Mapper，作为基准Mapper
 */
public interface MyMapper<T> extends Mapper<T> {
}
