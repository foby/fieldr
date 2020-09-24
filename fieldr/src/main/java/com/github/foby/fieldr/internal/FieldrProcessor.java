package com.github.foby.fieldr.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

@SupportedAnnotationTypes("com.github.foby.fieldr.GenerateFieldNames")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class FieldrProcessor extends AbstractProcessor {

    @Override public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final TypeElement annotation : annotations) {

            final List<String> generatedTypes = roundEnv.getElementsAnnotatedWith(annotation).stream()
                    .map(element -> element.asType().toString())
                    .collect(Collectors.toList());

            final List<FieldsClassModel> fieldsClassModels =
                    roundEnv.getElementsAnnotatedWith(annotation)
                            .stream()
                            .filter(element -> ElementKind.CLASS == element.getKind())
                            .map(element -> buildGeneratorInput(generatedTypes, element))
                            .collect(Collectors.toList());

            fieldsClassModels.forEach(this::generateFieldsSourceFile);
            fieldsClassModels.forEach(this::generateFieldsEntryPointSourceFile);
        }
        return true;
    }

    private FieldsClassModel buildGeneratorInput(final List<String> generatedTypes, final Element element) {
        final Map<String, FieldGenerator> fieldModel = Maps.newHashMap();

        processingEnv.getElementUtils().getAllMembers((TypeElement) element)
                .stream()
                .filter(enclosing -> ElementKind.FIELD == enclosing.getKind())
                .filter(field -> !field.getModifiers().contains(Modifier.STATIC))
                .filter(field -> !field.getModifiers().contains(Modifier.FINAL))
                .forEach(field -> {

                    final ImmutablePair<String,String> propertyAndFieldName = getFieldName(field);
                    final String type = field.asType().toString();

                    if (generatedTypes.contains(type)) {
                        fieldModel.put(propertyAndFieldName.getLeft(), new ComplexFieldGenerator(propertyAndFieldName.getRight(), type));
                    } else {
                        fieldModel.put(propertyAndFieldName.getLeft(), new SimpleFieldGenerator(propertyAndFieldName.getRight()));
                    }
                });
        final String packageName = Optional.ofNullable(element.getEnclosingElement())
                .filter(e -> ElementKind.PACKAGE == e.getKind())
                .map(Object::toString)
                .orElse("");

        final FieldsClassModel fieldsClassModel = new FieldsClassModel();
        fieldsClassModel.packageName = packageName;
        fieldsClassModel.className = element.getSimpleName().toString();
        fieldsClassModel.fieldModel = fieldModel;

        return fieldsClassModel;
    }

    private static ImmutablePair<String,String> getFieldName(final Element field) {
        final String defaultFieldName = field.getSimpleName().toString();
        final Field fieldAnnotation = field.getAnnotation(Field.class);
        final Id idAnnotation = field.getAnnotation(Id.class);

        if (idAnnotation != null && "id".equals(defaultFieldName)) {
            // FIXME: this is mongodb specific. make configurable in @GenerateFieldNames.
            return ImmutablePair.of("id","_id");
        }

        final String fieldNameByMongoAnnotation = Optional.ofNullable(fieldAnnotation)
                .map(fAnnotation -> {
                    if (!Strings.isNullOrEmpty(fAnnotation.value())) {
                        return fAnnotation.value();
                    } else {
                        return fAnnotation.name();
                    }
                })
                .filter(s -> !Strings.isNullOrEmpty(s))
                .orElse(null);

        return Optional.ofNullable(fieldNameByMongoAnnotation)
                .map(s -> ImmutablePair.of(defaultFieldName, s))
                .orElse(ImmutablePair.of(defaultFieldName, defaultFieldName));
    }

    private void generateFieldsSourceFile(final FieldsClassModel fieldsClassModel) {
        final String simpleClassName = fieldsClassModel.className;
        final String targetPackageName = fieldsClassModel.packageName + ".generated";

        try {
            final String generatedFieldsClassName = simpleClassName + "Fields";
            final JavaFileObject builderFile =
                    processingEnv.getFiler().createSourceFile(targetPackageName + "." + generatedFieldsClassName);
            try (final PrintWriter out = new PrintWriter(builderFile.openWriter())) {

                out.println("package " + targetPackageName + ";");

                out.print("public class ");
                out.print(generatedFieldsClassName);
                out.println(" extends com.github.foby.fieldr.internal.FieldsBase {");
                out.println();
                out.println("public " + generatedFieldsClassName + "(final String context) { super(context); }");
                out.println();

                fieldsClassModel.fieldModel.forEach((propName, fieldGenerator) -> out.println(fieldGenerator.generateFieldSpec(propName)));

                out.println("  public String toString() { return java.util.Optional.ofNullable(this.context).orElse(\"\"); }");

                out.println("}");
            }
        } catch (final IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Unable to generate field name constants for class " + fieldsClassModel.packageName + "." + simpleClassName + ":" + e
                            .getMessage());
        }
    }

    private void generateFieldsEntryPointSourceFile(final FieldsClassModel fieldsClassModel) {
        final String simpleClassName = fieldsClassModel.className;
        final String targetPackageName = fieldsClassModel.packageName + ".generated";

        try {
            final String generatedWrapperFieldsClassName = simpleClassName + "_";
            final JavaFileObject builderFile =
                    processingEnv.getFiler().createSourceFile(targetPackageName + "." + generatedWrapperFieldsClassName);
            try (final PrintWriter out = new PrintWriter(builderFile.openWriter())) {

                out.println("package " + targetPackageName + ";");

                out.print("public class ");
                out.print(generatedWrapperFieldsClassName);
                out.println(" {");
                out.println();

                final String generatedFieldsClassName = simpleClassName + "Fields";
                fieldsClassModel.fieldModel
                        .forEach((propName, fieldGenerator) -> out.println(
                                fieldGenerator.generateEntryPointFieldSpec(propName, generatedFieldsClassName)));

                out.println("}");
            }
        } catch (final IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "Unable to generate entry point field name constants for class " + fieldsClassModel.packageName + "." + simpleClassName + ":"
                            + e
                            .getMessage());
        }
    }
}
