package com.it.repository.h2;

import com.it.bean.CodeObserver;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CodeObserverMapper {

    List<CodeObserver> getCodeObserverList(CodeObserver codeObserver);

    void updateCodeObserver(CodeObserver codeObserver);
}