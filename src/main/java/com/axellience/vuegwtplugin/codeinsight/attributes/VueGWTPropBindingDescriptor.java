package com.axellience.vuegwtplugin.codeinsight.attributes;

import com.axellience.vuegwtplugin.VueGWTIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.meta.PsiPresentableMetaData;
import com.intellij.util.ArrayUtil;
import com.intellij.xml.impl.BasicXmlAttributeDescriptor;
import javax.swing.Icon;
import org.jetbrains.annotations.Nullable;

public class VueGWTPropBindingDescriptor extends BasicXmlAttributeDescriptor implements
    PsiPresentableMetaData {

  private final PsiField field;
  private final String name;
  private boolean isRequired;

  public VueGWTPropBindingDescriptor(PsiField field) {
    this(field, field.getName());
  }

  public VueGWTPropBindingDescriptor(PsiField field, String name) {
    this.field = field;
    this.name = name;
    this.isRequired = false;
  }

  public boolean isAttributePropBinding(String attributeName) {
    return getName().equals(attributeName) ||
        (":" + getName()).equals(attributeName) ||
        ("v-bind:" + getName()).equals(attributeName);
  }

  @Override
  public boolean isRequired() {
    return isRequired;
  }

  @Override
  public PsiElement getDeclaration() {
    return field;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void init(PsiElement element) {

  }

  @Override
  public boolean hasIdType() {
    return false;
  }

  @Override
  public boolean hasIdRefType() {
    return false;
  }

  @Override
  public boolean isEnumerated() {
    return false;
  }

  @Override
  public boolean isFixed() {
    return false;
  }

  @Override
  public String getDefaultValue() {
    return null;
  }

  @Override
  public String[] getEnumeratedValues() {
    return ArrayUtil.EMPTY_STRING_ARRAY;
  }

  @Nullable
  @Override
  public String getTypeName() {
    return null;
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return VueGWTIcons.VUE;
  }
}
