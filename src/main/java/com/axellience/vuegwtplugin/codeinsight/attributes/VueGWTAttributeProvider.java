package com.axellience.vuegwtplugin.codeinsight.attributes;

import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlAttributeDescriptorsProvider;
import org.jetbrains.annotations.Nullable;

public class VueGWTAttributeProvider implements XmlAttributeDescriptorsProvider {

  @Override
  public XmlAttributeDescriptor[] getAttributeDescriptors(XmlTag context) {
    return new XmlAttributeDescriptor[0];
  }

  @Nullable
  @Override
  public XmlAttributeDescriptor getAttributeDescriptor(String attributeName, XmlTag context) {
    return null;
  }
}
