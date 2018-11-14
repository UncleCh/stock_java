package com.it.repository.h2;

import com.it.bean.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 */
@Mapper
public interface ConfigMapper {
    void saveConfig(Config config);

    Config getConfig(String key);
}
