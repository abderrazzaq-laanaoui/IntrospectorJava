package fr.ensibs.laanaoui.introspector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
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
        ArrayNode relationsArray = mapper.createArrayNode();

        for (CompilationUnit cu : compilationUnits) {
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                if ("MavenWrapperDownloader".equals(clazz.getNameAsString())) {
                    return; // Skip MavenWrapperDownloader class
                }

                ObjectNode classNode = mapper.createObjectNode();
                classNode.put("name", clazz.getNameAsString());
                classNode.put("package", cu.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse(""));
                classNode.put("type", clazz.isInterface() ? "interface" : "class");
                String modifiers = clazz.getModifiers().toString().replace("[", "").replace("]", "");
                classNode.put("modifiers", modifiers);

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

                    // Calculate lines of code
                    int linesOfCode = calculateLinesOfCode(method);
                    methodNode.put("linesOfCode", linesOfCode);

                    methodsArray.add(methodNode);
                });
                classNode.set("methods", methodsArray);

                classesArray.add(classNode);

                // Add relations
                clazz.getExtendedTypes().forEach(extendedType -> {
                    ObjectNode relationNode = mapper.createObjectNode();
                    relationNode.put("source", clazz.getNameAsString());
                    relationNode.put("target", extendedType.getNameAsString());
                    relationNode.put("type", "extends");
                    relationsArray.add(relationNode);
                });

                clazz.getImplementedTypes().forEach(implementedType -> {
                    ObjectNode relationNode = mapper.createObjectNode();
                    relationNode.put("source", clazz.getNameAsString());
                    relationNode.put("target", implementedType.getNameAsString());
                    relationNode.put("type", "implements");
                    relationsArray.add(relationNode);
                });

                clazz.getMethods().forEach(method -> method.getParameters().forEach(param -> {
                    ObjectNode relationNode = mapper.createObjectNode();
                    relationNode.put("source", clazz.getNameAsString());
                    relationNode.put("target", param.getTypeAsString());
                    relationNode.put("type", "uses");
                    relationsArray.add(relationNode);
                }));
            });
        }

        ObjectNode root = mapper.createObjectNode();
        root.set("classes", classesArray);
        root.set("relations", relationsArray);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("project_analysis.json"), root);
    }

    private static int calculateLinesOfCode(MethodDeclaration method) {
        return method.getEnd().map(end -> end.line).orElse(0) - method.getBegin().map(begin -> begin.line).orElse(0) + 1;
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