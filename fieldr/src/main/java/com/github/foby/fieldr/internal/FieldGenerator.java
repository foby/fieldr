package com.github.foby.fieldr.internal;

public interface FieldGenerator {

    String generateFieldSpec(String propertyName);

    String generateEntryPointFieldSpec(final String propertyName, final String generatedWrapperClassName);
}
