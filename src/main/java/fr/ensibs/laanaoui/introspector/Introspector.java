package fr.ensibs.laanaoui.introspector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Introspector {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Introspector <path-to-java-project>");
            return;
        }

        String projectPath = args[0];
        List<CompilationUnit> compilationUnits = parseJavaFiles(projectPath);
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode classesArray = mapper.createArrayNode();

        for (CompilationUnit cu : compilationUnits) {
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                ObjectNode classNode = mapper.createObjectNode();
                classNode.put("name", clazz.getNameAsString());

                ArrayNode fieldsArray = mapper.createArrayNode();
                clazz.getFields().forEach(field -> {
                    ObjectNode fieldNode = mapper.createObjectNode();
                    fieldNode.put("name", field.getVariable(0).getNameAsString());
                    fieldNode.put("type", field.getVariable(0).getTypeAsString());
                    fieldsArray.add(fieldNode);
                });
                classNode.set("fields", fieldsArray);

                ArrayNode methodsArray = mapper.createArrayNode();
                clazz.getMethods().forEach(method -> {
                    ObjectNode methodNode = mapper.createObjectNode();
                    methodNode.put("name", method.getNameAsString());

                    ArrayNode paramsArray = mapper.createArrayNode();
                    method.getParameters().forEach(param -> {
                        ObjectNode paramNode = mapper.createObjectNode();
                        paramNode.put("name", param.getNameAsString());
                        paramNode.put("type", param.getTypeAsString());
                        paramsArray.add(paramNode);
                    });
                    methodNode.set("parameters", paramsArray);
                    methodsArray.add(methodNode);
                });
                classNode.set("methods", methodsArray);

                classesArray.add(classNode);
            });
        }

        ObjectNode root = mapper.createObjectNode();
        root.set("classes", classesArray);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("project_analysis.json"), root);
    }
    private static List<CompilationUnit> parseJavaFiles(String projectPath) throws IOException {
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        JavaParser javaParser = new JavaParser();
        Files.walk(Paths.get(projectPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        CompilationUnit cu = javaParser.parse(path).getResult().orElse(null);
                        if (cu != null) {
                            compilationUnits.add(cu);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return compilationUnits;
    }
}