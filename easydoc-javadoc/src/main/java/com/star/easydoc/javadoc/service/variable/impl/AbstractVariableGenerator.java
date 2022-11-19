package com.star.easydoc.javadoc.service.variable.impl;

import com.intellij.openapi.components.ServiceManager;
import com.star.easydoc.javadoc.config.EasyJavadocConfig;
import com.star.easydoc.javadoc.config.EasyJavadocConfigComponent;
import com.star.easydoc.javadoc.service.variable.VariableGenerator;

/**
 * 变量生成器
 *
 * @author wangchao
 * @date 2022/10/01
 */
public abstract class AbstractVariableGenerator implements VariableGenerator {

    @Override
    public EasyJavadocConfig getConfig() {
        return ServiceManager.getService(EasyJavadocConfigComponent.class).getState();
    }
}
