package com.axellience.vuegwtplugin.codeinsight.attributes

import com.axellience.vuegwtplugin.codeinsight.ATTR_DIRECTIVE_PREFIX
import com.axellience.vuegwtplugin.codeinsight.attributes.VueAttributeDescriptor.AttributePriority.LOW
import com.axellience.vuegwtplugin.codeinsight.attributes.VueAttributeDescriptor.AttributePriority.NONE
import com.axellience.vuegwtplugin.codeinsight.attributes.VueAttributeNameParser.VueAttributeKind
import com.axellience.vuegwtplugin.codeinsight.attributes.VueAttributeNameParser.VueDirectiveKind
import com.axellience.vuegwtplugin.codeinsight.tags.VueGWTElementDescriptor
import com.axellience.vuegwtplugin.model.DEFAULT_SLOT_NAME
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil.isVueContext
import com.intellij.openapi.project.DumbService
import com.intellij.psi.impl.source.html.dtd.HtmlNSDescriptorImpl
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider
import one.util.streamex.StreamEx

class VueGWTAttributeDescriptorProvider : XmlAttributeDescriptorsProvider {
    override fun getAttributeDescriptors(context: XmlTag?): Array<out XmlAttributeDescriptor> {
        if (context == null
                || DumbService.isDumb(context.project)
                || !isVueContext(context)) return emptyArray()
        val result = mutableListOf<XmlAttributeDescriptor>()

        StreamEx.of(*VueAttributeKind.values())
                .filter { it.attributeName != null && it.isValidIn(context) }
                .map {
                    VueAttributeDescriptor(context, it.attributeName!!, acceptsNoValue = !it.requiresValue, priority = if (it.deprecated) NONE else LOW)
                }
                .forEach { result.add(it) }

        StreamEx.of(*VueDirectiveKind.values())
                .filter {
                    // 'on' should not be proposed without colon. It is added separately in VueTagAttributeCompletionProvider
                    it !== VueDirectiveKind.ON
                            && it.directiveName != null
                }
                .map { VueAttributeDescriptor(context, ATTR_DIRECTIVE_PREFIX + it.directiveName!!, acceptsNoValue = !it.requiresValue, priority = LOW) }
                .forEach { result.add(it) }

        return result.toTypedArray()
    }

    override fun getAttributeDescriptor(attributeName: String?, context: XmlTag?): XmlAttributeDescriptor? {
        if (context == null
                || attributeName == null
                || DumbService.isDumb(context.project)
                || !isVueContext(context)) return null
        val info = VueAttributeNameParser.parse(attributeName, context)
        when {
            info.kind == VueAttributeKind.PLAIN -> {
                if (info.modifiers.isEmpty()) {
                    return null
                }
                return HtmlNSDescriptorImpl.getCommonAttributeDescriptor(info.name, context)
                        ?: VueAttributeDescriptor(context, info.name, acceptsNoValue = !info.requiresValue, priority = LOW)
            }
            info is VueAttributeNameParser.VueDirectiveInfo -> {
                return when {
                    info.isShorthand && info.arguments.isNullOrEmpty() -> return null

                    info.directiveKind == VueDirectiveKind.BIND ->
                        info.arguments?.let { HtmlNSDescriptorImpl.getCommonAttributeDescriptor(it, context) }

//                    info.directiveKind == VueDirectiveKind.ON -> {
//                        info.arguments?.let { eventName ->
//                            val event = (context.descriptor as? VueElementDescriptor)
//                                    ?.getEmitCalls()
//                                    ?.find { it.name == eventName}
//                                    ?.let { VueAttributeDescriptor(context, it.name, it.source, listOf(it), false) }
//                            event ?: HtmlNSDescriptorImpl.getCommonAttributeDescriptor("on$eventName", context)
//                        }
//                    }

//                    info.directiveKind == VueDirectiveKind.SLOT -> {
//                        val slotName = info.arguments ?: DEFAULT_SLOT_NAME
//                        getAvailableSlots(context, true)
//                                .find { it.name == slotName || it.pattern?.matches(slotName) == true }
//                                ?.let {
//                                    VueAttributeDescriptor(context, attributeName, it.source, listOf(it), true)
//                                }
//                    }

                    info.directiveKind == VueDirectiveKind.CUSTOM ->
                        return null

                    else -> null
                }
                        ?: return VueAttributeDescriptor(context, attributeName, acceptsNoValue = !info.requiresValue, priority = LOW)
            }
            else -> return VueAttributeDescriptor(context, attributeName, acceptsNoValue = !info.requiresValue, priority = LOW)
        }
    }
}