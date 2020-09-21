package com.github.foby.fieldr.internal;

public class SimpleFieldGenerator implements FieldGenerator {

    @Override public String generateFieldSpec(final String propertyName) {
        return "public String " + propertyName + " = withContext(\"" + propertyName + "\");";
    }

    @Override public String generateEntryPointFieldSpec(final String propertyName, final String generatedWrapperClassName) {
        return "public static String " + propertyName + " = new " + generatedWrapperClassName + "(null)." + propertyName + ";";
    }
}
