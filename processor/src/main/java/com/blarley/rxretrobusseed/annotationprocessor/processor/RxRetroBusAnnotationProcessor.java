package com.blarley.rxretrobusseed.annotationprocessor.processor;


import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.blarley.rxretrobusseed.annotationprocessor.processor.GenerateEvents")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RxRetroBusAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        List<String> generatedClasses = new ArrayList<>();
        String generatedPrefix = "RxRetroBus_";

        // Get the Retrofit interfaces annotated with GenerateEvents
        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateEvents.class)) {

            String baseType = element.asType().toString();
            String baseClassName = element.getSimpleName().toString();
            String generatedClassName = generatedPrefix + baseClassName;

            // Add generated Class to create Clients file
            generatedClasses.add(generatedClassName);

            String baseUrl = element.getAnnotation(GenerateEvents.class).baseUrl();

            // Package and imports
            StringBuilder builder = new StringBuilder()
                .append("package com.blarley.rxretrobusseed.annotationprocessor.generated;\n\n")
                .append("import retrofit2.Retrofit;\n" +
                        "import com.blarley.rxretrobusseed.library.bus.Publish;\n" +
                        "import com.blarley.rxretrobusseed.library.bus.RxRetroBus;\n");

            // Begin class definition
            builder.append("public class " + generatedClassName + " {\n\n");

            // Retrofit client impl
            builder.append("\tprivate " + baseType + " client;\n");
            builder.append("\tprivate RxRetroBus bus;\n\n");

            // Constructor - builds Retrofit client
            builder.append("\tpublic " + generatedClassName + "(Retrofit.Builder retrofitBuilder, RxRetroBus bus) { \n" +
                            "\t\tthis.client = retrofitBuilder.baseUrl(\"" + baseUrl + "\")\n" +
                            "\t\t\t\t.build()\n" +
                            "\t\t\t\t.create(" + baseType + ".class);\n" +
                            "\t\tthis.bus = bus;\n" +
                            "\t}\n\n");

            // Get Annotated methods within the class - the builds the method used to make calls
            for (Element subElement : element.getEnclosedElements()) {

                // ExecutableElements represent methods (among other things) - TODO: Figure out how this can break
                if (subElement instanceof ExecutableElement && subElement.getAnnotation(Publish.class) != null) {

                    // Cast to ExecutableElement in order to get Parameters
                    ExecutableElement method = (ExecutableElement) subElement;
                    String methodName = method.getSimpleName().toString();

                    // Begin definition of method
                    builder.append("\tpublic void " + methodName + "(");

                    // Append parameters to method definition - TODO: Figure out how this can break
                    String delim = "";
                    StringBuilder params = new StringBuilder();
                    StringBuilder args = new StringBuilder();
                    for (VariableElement param : method.getParameters()) {
                        params.append(delim)
                                .append(param.asType() + " ")
                                .append(param.getSimpleName().toString());

                        args.append(delim)
                                .append(param.getSimpleName().toString());
                        delim = ", ";
                    }

                    // Append the parameters to the method definition and open declaration
                    builder.append(params)
                            .append(") {\n");

                    // Need to strip off the Observable and get parameterized class
                    // TODO: Is this a better way to do this?
                    String observable = method.getReturnType().toString();
                    Pattern regex = Pattern.compile("<(.*?)>");
                    Matcher matcher = regex.matcher(observable);
                    String innerClass = "";
                    while (matcher.find()) {
                        innerClass += matcher.group(1);
                    }

                    Publish annotation = method.getAnnotation(Publish.class); // TODO: Build a model for this and pass that into the method

                    builder.append("\t\tbus.addObservable(client." + methodName + "(")
                            .append(args)
                            .append("), ")
                            .append(innerClass + ".class, ")
                            .append("new Publish(")
                            .append("\"" + annotation.tag() + "\", ")
                            .append(annotation.cache() + ", ")
                            .append(annotation.debounce() + ", ")
                            .append(annotation.sticky() + "));\n");

                    // End method definition
                    builder.append("\t}\n\n");
                }
            }

            // End Class definition
            builder.append("}\n");

            // Write the file
            try {
                JavaFileObject source = processingEnv.getFiler()
                        .createSourceFile(
                                "com.blarley.rxretrobusseed.annotationprocessor.generated."
                                        + generatedClassName);

                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
            }
        }

        // Clients String builder
        StringBuilder clientsFile = new StringBuilder()
                .append("package com.blarley.rxretrobusseed.annotationprocessor.generated;\n\n")
                .append("import retrofit2.Retrofit;\n\n")
                .append("import com.blarley.rxretrobusseed.library.bus.RxRetroBus;\n\n")
                .append("public class Clients {\n");

        StringBuilder constructorDefinition = new StringBuilder();

        // Append the instance fields
        for (String generatedClass: generatedClasses) {
            String[] str = generatedClass.split("_");
            String baseClassName = str[1];
            clientsFile.append(
                    "\tpublic "
                            + generatedClass
                            + " "
                            + baseClassName
                            + ";\n");

            constructorDefinition.append(
                    "\t\tthis."
                            + baseClassName
                            + " = new "
                            + generatedClass
                            + "(retrofitBuilder, bus);\n");
        }

        // Append the constructor declaration
        clientsFile.append("\n\tpublic Clients(Retrofit.Builder retrofitBuilder, RxRetroBus bus) {\n");

        // Append the constructor definition
        clientsFile.append(constructorDefinition);

        // Close the constructor and class
        clientsFile.append("\t}\n")
                .append("}");

        // Write the file
        try {
            JavaFileObject source = processingEnv.getFiler()
                    .createSourceFile(
                            "com.blarley.rxretrobusseed.annotationprocessor.generated.Clients");
            Writer writer = source.openWriter();
            writer.write(clientsFile.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
        }

        return true;
    }
}
