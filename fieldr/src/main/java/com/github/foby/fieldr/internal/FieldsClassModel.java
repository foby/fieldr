package com.github.foby.fieldr.internal;

import java.util.Map;

import com.google.common.collect.Maps;

class FieldsClassModel {

    String packageName;

    String className;

    Map<String, FieldGenerator> fieldModel = Maps.newHashMap();
}
