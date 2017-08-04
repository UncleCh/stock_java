package com.it.service;

import com.it.bean.SelectStrategyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SelectStrategyManager {

    private final ApplicationContext ctx;

    @Autowired
    public SelectStrategyManager(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }

    public SelectStrategy getSelectStrategy(SelectStrategyType strategyType) {
        switch (strategyType) {
            case CONTINUE_GROWTH:
                return ctx.getBean(ContinueGrowth.class);
            case CONTINUE_GROWTH_MAX:
                return ctx.getBean(ContinueGrowthMax.class);
            case CONTINUE_FALL_MAX:
                return ctx.getBean(ContinueFallMax.class);
            default:
                throw new RuntimeException("策略" + strategyType.getDesc() + "支不支持");
        }
    }

}
