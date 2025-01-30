package fr.ensibs.laanaoui.introspector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Introspector {

    private static final Logger logger = LoggerFactory.getLogger(Introspector.class);

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

    private static List<CompilationUnit> parseJavaFiles(String projectPath) {
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        JavaParser javaParser = new JavaParser();
        try (Stream<Path> paths = Files.walk(Paths.get(projectPath))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".java"))
                 .forEach(path -> {
                     try {
                         javaParser.parse(path).getResult().ifPresent(compilationUnits::add);
                     } catch (IOException e) {
                         logger.error("Error parsing file: {}", path, e);
                     }
                 });
        } catch (IOException e) {
            logger.error("Error walking through project path: {}", projectPath, e);
        }
        return compilationUnits;
    }
}