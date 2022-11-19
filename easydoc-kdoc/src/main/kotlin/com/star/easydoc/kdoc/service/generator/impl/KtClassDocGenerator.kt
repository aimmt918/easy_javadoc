package com.star.easydoc.kdoc.service.generator.impl

import com.intellij.openapi.components.ServiceManager
import com.intellij.psi.PsiElement
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.kdoc.config.EasyKdocConfig
import com.star.easydoc.kdoc.config.EasyKdocConfigComponent
import com.star.easydoc.kdoc.service.generator.DocGenerator
import com.star.easydoc.kdoc.service.variable.VariableGeneratorService
import org.apache.commons.lang3.StringUtils
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.uast.getContainingClass

/**
 * @constructor
 */
class KtClassDocGenerator : DocGenerator {
    private val config: EasyKdocConfig = ServiceManager.getService(EasyKdocConfigComponent::class.java).state
    private val variableGeneratorService = ServiceManager.getService(VariableGeneratorService::class.java)

    override fun generate(psiElement: PsiElement): String {
        if (psiElement !is KtClass) {
            return StringUtils.EMPTY
        }
        return if (config.classTemplateConfig != null && true == config.classTemplateConfig.isDefault
        ) {
            defaultGenerate(psiElement)
        } else {
            customGenerate(psiElement)
        }
    }

    /**
     * 默认的生成
     *
     * @param psi 当前类
     * @return [java.lang.String]
     */
    private fun defaultGenerate(psi: KtClass): String {
        return variableGeneratorService.generate(
            psi, "/**\n" +
                    " * \$DOC\$\n" +
                    " *\n" +
                    " * @author \$AUTHOR\$\n" +
                    " * @date \$DATE\$\n" +
                    " * @constructor \$CONSTRUCTOR\$\n" +
                    " * \$PARAMS\$\n" +
                    " */".trimIndent(),
            config.classTemplateConfig.customMap, getClassInnerVariable(psi)
        )
    }

    /**
     * 自定义生成
     *
     * @param psi 当前类
     * @return [java.lang.String]
     */
    private fun customGenerate(psi: KtClass): String {
        return variableGeneratorService.generate(
            psi, config.classTemplateConfig.template,
            config.classTemplateConfig.customMap, getClassInnerVariable(psi)
        )
    }

    /**
     * 获取类内部变量
     *
     * @param psiClass psi类
     * @return [,][<]
     */
    private fun getClassInnerVariable(psiClass: KtClass): Map<String?, Any?> {
        val map: MutableMap<String?, Any?> = mutableMapOf()
        map["author"] = config.author
        map["className"] = psiClass.fqName
        map["simpleClassName"] = psiClass.name
        return map
    }

}