package com.github.foby.fieldr.internal;

public class ComplexFieldGenerator implements FieldGenerator {

    private String originalTypeName;

    private String fieldName;

    ComplexFieldGenerator(final String fieldName, final String originalTypeName) {
        this.fieldName = fieldName;
        this.originalTypeName = originalTypeName;
    }

    @Override public String generateFieldSpec(final String propertyName) {
        final String plainTypeName = getGeneratedFieldClassName();
        return "public " + plainTypeName + " " + propertyName + " = new " + plainTypeName + "(withContext(\"" + fieldName + "\"));";
    }

    @Override public String generateEntryPointFieldSpec(final String propertyName, final String generatedWrapperClassName) {
        final String plainTypeName = getGeneratedFieldClassName();
        return "public static " + plainTypeName +  " " + propertyName + " = new " + generatedWrapperClassName + "(null)." + propertyName + ";";
    }

    private String getGeneratedFieldClassName() {
        final String generatedFieldsTypeName = originalTypeName + "Fields";
        final int lastDotIndex = generatedFieldsTypeName.lastIndexOf('.') >= 0 ? generatedFieldsTypeName.lastIndexOf('.') + 1 : 0;
        final String originalPackageName = originalTypeName.substring(0, Math.max(lastDotIndex - 1, 0));
        final String generationPackageName = originalPackageName + ".generated";
        return generationPackageName + "." + generatedFieldsTypeName.substring(lastDotIndex);
    }
}
