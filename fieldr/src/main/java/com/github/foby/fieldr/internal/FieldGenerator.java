package com.github.foby.fieldr.internal;

public interface FieldGenerator {

    String generateFieldSpec(String propertyName);

    String generateFieldSpecReference(final String propertyName);

    String generateEntryPointFieldSpec(final String propertyName, final String generatedWrapperClassName);

    String generateEntryPointFieldSpecReference(final String propertyName, final String generatedWrapperClassName);
}
