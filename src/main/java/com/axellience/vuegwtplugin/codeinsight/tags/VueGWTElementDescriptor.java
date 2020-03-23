package com.axellience.vuegwtplugin.codeinsight.tags;

import com.axellience.vuegwtplugin.codeinsight.attributes.VueGWTAttributeDescriptor;
import com.axellience.vuegwtplugin.util.VueGWTPluginUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.html.dtd.HtmlNSDescriptorImpl;
import com.intellij.psi.impl.source.xml.XmlDescriptorUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlElementsGroup;
import com.intellij.xml.XmlNSDescriptor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

public class VueGWTElementDescriptor implements XmlElementDescriptor {

  private final PsiClass componentClass;

  public VueGWTElementDescriptor(PsiClass componentClass) {
    this.componentClass = componentClass;
  }

  @Override
  public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag context) {
    List<XmlAttributeDescriptor> result = Arrays
        .stream(HtmlNSDescriptorImpl.getCommonAttributeDescriptors(context))
        .collect(Collectors.toCollection(LinkedList::new));
    result.addAll(getProps());
    return result.toArray(new XmlAttributeDescriptor[0]);
  }

  @Nullable
  @Override
  public XmlAttributeDescriptor getAttributeDescriptor(String attributeName,
      @Nullable XmlTag context) {
    return Arrays.stream(componentClass.getAllFields())
        .filter(
            field -> field.getName().equals(attributeName) ||
                (":" + field.getName()).equals(attributeName) ||
                ("v-bind:" + field.getName()).equals(attributeName)
        )
        .findFirst()
        .map(field -> (XmlAttributeDescriptor) new VueGWTAttributeDescriptor(field))
        .orElse(HtmlNSDescriptorImpl.getCommonAttributeDescriptor(attributeName, context));
  }

  @Nullable
  @Override
  public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
    return getAttributeDescriptor(attribute.getName(), attribute.getParent());
  }

  public List<VueGWTAttributeDescriptor> getProps() {
    return Arrays.stream(componentClass.getAllFields())
        .map(VueGWTAttributeDescriptor::new)
        .collect(Collectors.toList());
  }

  public Optional<PsiFile> getHtmlTemplate() {
    return VueGWTPluginUtil.findHtmlTemplate(componentClass.getContainingFile());
  }

  @Override
  public int getContentType() {
    return XmlElementDescriptor.CONTENT_TYPE_ANY;
  }

  @Override
  public PsiElement getDeclaration() {
    return componentClass;
  }

  @Nullable
  @Override
  public XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
    return XmlDescriptorUtil.getElementDescriptor(childTag, contextTag);
  }

  @Override
  public XmlElementDescriptor[] getElementsDescriptors(XmlTag context) {
    return XmlDescriptorUtil.getElementsDescriptors(context);
  }

  @Override
  public String getDefaultName() {
    return this.getName();
  }

  @Override
  public String getName(PsiElement context) {
    if (context instanceof XmlTag) {
      return ((XmlTag) context).getName();
    }

    return this.getName();
  }

  @Override
  public String getName() {
    return VueGWTPluginUtil.componentToTagName(componentClass);
  }

  @Override
  public String getQualifiedName() {
    return this.getName();
  }

  @Override
  public void init(PsiElement element) {

  }

  @Nullable
  @Override
  public String getDefaultValue() {
    return null;
  }

  @Nullable
  @Override
  public XmlElementsGroup getTopGroup() {
    return null;
  }

  @Nullable
  @Override
  public XmlNSDescriptor getNSDescriptor() {
    return null;
  }
}
