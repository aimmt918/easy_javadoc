package com.star.easydoc.kdoc.view.template

import com.google.common.collect.Maps
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.star.easydoc.common.config.EasyDocConfig
import com.star.easydoc.kdoc.config.EasyKdocConfig
import com.star.easydoc.kdoc.view.inner.CustomTemplateAddView
import java.awt.Dimension
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.table.DefaultTableModel

/**
 * @author [wangchao](mailto:wangchao.star@gmail.com)
 * @version 1.0.0
 * @since 2019-11-10 17:46:00
 */
class ClassConfigView(config: EasyKdocConfig) : AbstractTemplateConfigView(config) {
    private lateinit var panel: JPanel
    private lateinit var templateTextArea: JTextArea
    private lateinit var innerVariablePanel: JPanel
    private lateinit var customVariablePanel: JPanel
    private lateinit var templatePanel: JPanel
    private lateinit var defaultRadioButton: JRadioButton
    private lateinit var customRadioButton: JRadioButton
    private lateinit var innerTable: JTable
    private lateinit var innerScrollPane: JScrollPane
    private lateinit var customTable: JTable

    private fun createUIComponents() {
        // 初始化内置变量表格
        val innerData = Vector<Vector<String>>(innerMap.size)
        for ((key, value) in innerMap) {
            val row = Vector<String>(2)
            row.add(key)
            row.add(value)
            innerData.add(row)
        }
        val innerModel = DefaultTableModel(innerData, innerNames)
        innerTable = object : JBTable(innerModel) {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false
            }
        }
        innerScrollPane = JBScrollPane(innerTable)

        //设置表格显示的大小。
        innerTable.preferredScrollableViewportSize = Dimension(-1, innerTable.rowHeight * innerTable.rowCount)
        innerTable.fillsViewportHeight = true
        customTable = object : JBTable() {
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false
            }
        }
        refreshCustomTable()
        val toolbarDecorator = ToolbarDecorator.createDecorator(customTable)
        toolbarDecorator.setAddAction {
            val customTemplateAddView = CustomTemplateAddView()
            if (customTemplateAddView.showAndGet()) {
                config.classTemplateConfig.customMap[customTemplateAddView.entry.key] = customTemplateAddView.entry.value
                refreshCustomTable()
            }
        }
        toolbarDecorator.setRemoveAction {
            val customMap = config.classTemplateConfig.customMap
            customMap.remove(customTable.getValueAt(customTable.getSelectedRow(), 0).toString())
            refreshCustomTable()
        }
        customVariablePanel = toolbarDecorator.createPanel()
    }

    init {
        // 添加单选按钮事件
        defaultRadioButton.addChangeListener { e: ChangeEvent ->
            val button = e.source as JRadioButton
            if (button.isSelected) {
                customRadioButton.isSelected = false
                templateTextArea.isEnabled = false
                customTable.isEnabled = false
                templatePanel.isEnabled = false
                customVariablePanel.isEnabled = false
                innerTable.isEnabled = false
                innerScrollPane.isEnabled = false
                innerVariablePanel.isEnabled = false
            }
        }
        customRadioButton.addChangeListener { e: ChangeEvent ->
            val button = e.source as JRadioButton
            if (button.isSelected) {
                defaultRadioButton.isSelected = false
                templateTextArea.isEnabled = true
                customTable.isEnabled = true
                templatePanel.isEnabled = true
                customVariablePanel.isEnabled = true
                innerTable.isEnabled = true
                innerScrollPane.isEnabled = true
                innerVariablePanel.isEnabled = true
            }
        }
    }

    override val component: JComponent
        get() = panel

    private fun refreshCustomTable() {
        // 初始化自定义变量表格
        var customMap: Map<String?, EasyDocConfig.CustomValue> = Maps.newHashMap()
        if (config.classTemplateConfig != null && config.classTemplateConfig.customMap != null) {
            customMap = config.classTemplateConfig.customMap
        }
        val customData = Vector<Vector<String?>>(customMap.size)
        for ((key, value) in customMap) {
            val row = Vector<String?>(3)
            row.add(key)
            row.add(value.type.desc)
            row.add(value.value)
            customData.add(row)
        }
        val customModel = DefaultTableModel(customData, customNames)
        customTable.model = customModel
        customTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        customTable.columnModel.getColumn(0).preferredWidth = (customTable.width * 0.3).toInt()
    }

    var isDefault: Boolean
        get() = defaultRadioButton.isSelected
        set(isDefault) {
            if (isDefault) {
                defaultRadioButton.isSelected = true
                customRadioButton.isSelected = false
            } else {
                defaultRadioButton.isSelected = false
                customRadioButton.isSelected = true
            }
        }
    var template: String?
        get() = templateTextArea.text
        set(template) {
            templateTextArea.text = template
        }

    companion object {
        private var innerMap: MutableMap<String, String> = Maps.newHashMap()

        init {
            innerMap["\$DOC$"] = "注释信息"
            innerMap["\$AUTHOR$"] = "作者信息，可在通用配置里修改作者信息"
            innerMap["\$DATE$"] = "日期信息，格式可在通用配置中修改"
            innerMap["\$SINCE$"] = "支持的起始版本，默认1.0.0"
            innerMap["\$SEE$"] = "父类或者接口链接"
            innerMap["\$VERSION$"] = "默认：1.0.0"
        }
    }
}