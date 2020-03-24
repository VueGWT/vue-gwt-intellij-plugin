// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.axellience.vuegwtplugin.codeinsight.attributes

import com.axellience.vuegwtplugin.VueGWTIcons
import com.axellience.vuegwtplugin.codeinsight.attributes.VueAttributeNameParser.VueAttributeInfo
import com.axellience.vuegwtplugin.model.VueInputProperty
import com.intellij.openapi.util.NotNullLazyValue
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import com.intellij.psi.meta.PsiPresentableMetaData
import com.intellij.psi.xml.XmlElement
import com.intellij.psi.xml.XmlTag
import com.intellij.util.ArrayUtil
import com.intellij.xml.impl.BasicXmlAttributeDescriptor
import javax.swing.Icon

open class VueAttributeDescriptor(protected val tag: XmlTag,
                                  private val name: String,
                                  internal val element: PsiElement? = null,
                                  private val acceptsNoValue: Boolean = false,
                                  private val acceptsValue: Boolean = true,
                                  val priority: AttributePriority = AttributePriority.NORMAL,
                                  isRequired: Boolean = false)
    : BasicXmlAttributeDescriptor(), PsiPresentableMetaData {

    constructor(tag: XmlTag, name: String, prop: VueInputProperty) : this(
            tag, name, prop.source,
            isRequired = prop.required,
            acceptsNoValue = isBooleanProp(prop),
            priority = AttributePriority.HIGH)

    private val _isRequired: Boolean = isRequired
    private val info: NotNullLazyValue<VueAttributeInfo> = NotNullLazyValue.createValue {
        VueAttributeNameParser.parse(getName(), tag)
    }

    fun getInfo(): VueAttributeInfo = info.value

    override fun validateValue(context: XmlElement?, value: String?): String? {
        if (value != null && !acceptsValue) {
            return "$name does not accept any value."
        }
        return null
    }

    override fun isRequired(): Boolean = _isRequired
    override fun getName(): String = name
    override fun getDeclaration(): PsiElement? = element
    override fun init(element: PsiElement?) {}

    override fun isFixed(): Boolean = false
    override fun hasIdType(): Boolean = false
    override fun getEnumeratedValueDeclaration(xmlElement: XmlElement?, value: String?): PsiElement? {
        return if (isEnumerated)
            xmlElement
        else if (value == null || value.isEmpty())
            null
        else
            super.getEnumeratedValueDeclaration(xmlElement, value)
    }

    override fun hasIdRefType(): Boolean = false
    override fun getDefaultValue(): Nothing? = null
    override fun isEnumerated(): Boolean = acceptsNoValue

    override fun getEnumeratedValues(): Array<out String> {
        if (isEnumerated) {
            return arrayOf(name)
        }
        return ArrayUtil.EMPTY_STRING_ARRAY
    }

    override fun getTypeName(): String? = null
    override fun getIcon(): Icon = VueGWTIcons.VUE

    companion object {
        private fun isBooleanProp(prop: VueInputProperty): Boolean {
            return prop.type?.isAssignableFrom(PsiType.BOOLEAN) ?: false
        }
    }

    enum class AttributePriority(val value: Double) {
        NONE(0.0),
        LOW(25.0),
        NORMAL(50.0),
        HIGH(100.0);
    }
}
