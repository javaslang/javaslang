/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2019 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.match.generator;

import io.vavr.match.annotation.Unapply;
import io.vavr.match.model.ClassModel;
import io.vavr.match.model.MethodModel;
import io.vavr.match.model.TypeParameterModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Code generator for structural pattern matching patterns.
 *
 * @author Daniel Dietrich
 */
public class Generator {

    private Generator() {
    }

    // ENTRY POINT: Expands one @Patterns class
    public static String generate(String derivedClassName, ClassModel classModel) {
        final List<MethodModel> methodModels = classModel.getMethods().stream()
                .filter(method -> method.isAnnotatedWith(Unapply.class))
                .collect(toList());
        final String _package = classModel.getPackageName();
        final ImportManager im = ImportManager.forClass(classModel, "io.vavr.API.Match");
        final String methods = generate(im, classModel, methodModels);
        return "// @formatter:off\n" +
                "// CHECKSTYLE:OFF\n" +
                (_package.isEmpty() ? "" : "package " + _package + ";\n\n") +
                im.getImports() +
                "\n\n// GENERATED BY VAVR <<>> derived from " + classModel.getFullQualifiedName() + "\n\n" +
                "@SuppressWarnings(\"deprecation\")\n" +
                "public final class " + derivedClassName + " {\n\n" +
                "    private " + derivedClassName + "() {\n" +
                "    }\n\n" +
                methods +
                "}\n" +
                "// CHECKSTYLE:ON\n" +
                "// @formatter:on";
    }

    // Expands the @Unapply methods of a @Patterns class
    private static String generate(ImportManager im, ClassModel classModel, List<MethodModel> methodModels) {
        final StringBuilder builder = new StringBuilder();
        for (MethodModel methodModel : methodModels) {
            generate(im, classModel, methodModel, builder);
            builder.append("\n");
        }
        return builder.toString();
    }

    // Expands one @Unapply method
    private static void generate(ImportManager im, ClassModel classModel, MethodModel methodModel, StringBuilder builder) {
        final String paramTypeName = im.getType(methodModel.getParameter(0).getType());
        final String definedName = methodModel.getName();
        final String generatedName = "$" + definedName;
        final int arity = Integer.parseInt(methodModel.getReturnType().getClassName().substring("Tuple".length()));
        final String body;
        if (arity == 0) {
            body = pattern(im, 0) + ".of(" + paramTypeName + ".class)";
        } else {
            final String args = IntStream.rangeClosed(1, arity).mapToObj(i -> "p" + i).collect(joining(", "));
            final String unapplyRef = classModel.getFullQualifiedName() + "::" + definedName;
            body = String.format("%s.of(%s, %s, %s)", pattern(im, arity), paramTypeName + ".class", args, unapplyRef);
        }
        final List<String> typeArgs = methodModel.getTypeParameters().stream()
                .map(typeParameterModel -> mapToName(im, typeParameterModel))
                .collect(toList());
        final List<String> upperBoundArgs = deriveUpperBounds(typeArgs, methodModel.getReturnType().getTypeParameters().size());
        final String returnType = genReturnType(im, methodModel, upperBoundArgs, arity);
        final String method;
        if (arity == 0 && methodModel.getTypeParameters().size() == 0) {
            method = String.format("final %s %s = %s;", returnType, generatedName, body);
        } else {
            final String generics = genGenerics(im, methodModel, typeArgs, upperBoundArgs);
            final String params = genParams(im, upperBoundArgs, arity);
            method = String.format("%s %s %s(%s) {\n        return %s;\n    }", generics, returnType, generatedName, params, body);
        }
        builder.append("    public static ").append(method).append("\n");
    }

    // Introduces new upper generic type bounds for decomposed object parts
    private static List<String> deriveUpperBounds(List<String> typeArgs, int count) {
        final List<String> result = new ArrayList<>();
        final Set<String> knownTypeArgs = new HashSet<>(typeArgs);
        for (int i = 0; i < count; i++) {
            String typeArg = "_" + (i + 1);
            while (knownTypeArgs.contains(typeArg)) {
                typeArg = "_" + typeArg;
            }
            result.add(typeArg);
            knownTypeArgs.add(typeArg);
        }
        return result;
    }

    // Expands the generics part of a method declaration
    private static String genGenerics(ImportManager im, MethodModel methodModel, List<String> typeParameters, List<String> upperBoundArgs) {
        final List<TypeParameterModel> returnTypeArgs = methodModel.getReturnType().getTypeParameters();
        if (typeParameters.size() + returnTypeArgs.size() == 0) {
            return "";
        } else {
            final List<String> result = new ArrayList<>(typeParameters);
            for (int i = 0; i < returnTypeArgs.size(); i++) {
                final String returnTypeArg = mapToName(im, returnTypeArgs.get(i));
                result.add(upperBoundArgs.get(i) + " extends " + returnTypeArg);
            }
            return result.stream().collect(joining(", ", "<", ">"));
        }
    }

    // Expands the return type of a method declaration
    private static String genReturnType(ImportManager im, MethodModel methodModel, List<String> upperBoundArgs, int arity) {
        final List<String> resultTypes = new ArrayList<>();
        final String type = mapToName(im, methodModel.getParameter(0).getType());
        resultTypes.add(type);
        resultTypes.addAll(upperBoundArgs);
        return pattern(im, arity) + resultTypes.stream().collect(joining(", ", "<", ">"));
    }

    // Expands the parameters of a method declaration
    private static String genParams(ImportManager im, List<String> upperBoundArgs, int arity) {
        final String patternType = im.getType("io.vavr", "API.Match.Pattern");
        return IntStream.range(0, arity)
                .mapToObj(i -> patternType + "<" + upperBoundArgs.get(i) + ", ?> p" + (i + 1))
                .collect(joining(", "));
    }

    // Recursively maps generic type parameters to names according to their kind
    private static String mapToName(ImportManager im, TypeParameterModel typeParameterModel) {
        if (typeParameterModel.isType()) {
            return mapToName(im, typeParameterModel.asType());
        } else if (typeParameterModel.isTypeVar()) {
            return typeParameterModel.asTypeVar();
        } else {
            throw new IllegalStateException("Unhandled type parameter: " + typeParameterModel.toString());
        }
    }

    // Recursively maps class generics to names
    private static String mapToName(ImportManager im, ClassModel classModel) {
        final List<TypeParameterModel> typeParameters = classModel.getTypeParameters();
        final String simpleName = im.getType(classModel);
        if (typeParameters.size() == 0) {
            return simpleName;
        } else {
            return simpleName + classModel.getTypeParameters().stream()
                    .map(typeParam -> mapToName(im, typeParam))
                    .collect(joining(", ", "<", ">"));
        }
    }

    private static String pattern(ImportManager im, int arity) {
        return im.getType("io.vavr", "API.Match.Pattern" + arity);
    }
}
