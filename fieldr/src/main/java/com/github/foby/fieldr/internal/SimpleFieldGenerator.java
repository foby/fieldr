package com.github.foby.fieldr.internal;

public class SimpleFieldGenerator implements FieldGenerator {

    private String fieldName;

    SimpleFieldGenerator(final String fieldName) {
        this.fieldName = fieldName;
    }

    @Override public String generateFieldSpec(final String propertyName) {
        return "public String " + propertyName + " = withContext(\"" + fieldName + "\");";
    }

    @Override public String generateFieldSpecReference(final String propertyName) {
        return "public String $" + propertyName + " = withContext(\"$" + fieldName + "\");";
    }

    @Override public String generateFieldConstant(final String propertyName) {
        return "public static final String " + FieldrUtils.toConstantName(propertyName) + " = \"" + fieldName + "\";";
    }

    @Override public String generateEntryPointFieldSpec(final String propertyName, final String generatedWrapperClassName) {
        return "public static String " + propertyName + " = new " + generatedWrapperClassName + "(null)." + propertyName + ";";
    }

    @Override public String generateEntryPointFieldSpecReference(final String propertyName, final String generatedWrapperClassName) {
        return "public static String $" + propertyName + " = new " + generatedWrapperClassName + "(null).$" + propertyName + ";";
    }

    @Override public String generateEntryPointFieldConstant(final String propertyName, final String generatedWrapperClassName) {
        final String constantName = FieldrUtils.toConstantName(propertyName);
        return "public static final String " + constantName + " = " + generatedWrapperClassName + "." + constantName + ";";
    }


}
