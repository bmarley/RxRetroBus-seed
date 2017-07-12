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

    private final String generatedPrefix = "RxRetroBus_";
    private List<GeneratedClass> generatedClasses = new ArrayList<>();


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        generateEventFiles(roundEnv);
        generateClientsFile();
        return true;
    }


    private void generateEventFiles(RoundEnvironment roundEnv) {
        // Get the Retrofit interfaces annotated with GenerateEvents
        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateEvents.class)) {
            String generatedClassName = generatedPrefix + element.getSimpleName().toString();
            GenerateEvents generateEvents = element.getAnnotation(GenerateEvents.class);
            generatedClasses.add(new GeneratedClass(generatedClassName, generateEvents.retrofit()));
            writeEventFile(element);
        }
    }


    private void writeEventFile(Element element) {
        try {
            JavaFileObject source = processingEnv.getFiler()
                    .createSourceFile(
                            "com.blarley.rxretrobusseed.annotationprocessor.generated."
                                    + generatedPrefix + element.getSimpleName().toString());

            Writer writer = source.openWriter();
            writer.write(generateEventFilesHelper(element).toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private StringBuilder generateEventFilesHelper(Element element) {
        GenerateEvents generateEvents = element.getAnnotation(GenerateEvents.class);
        String baseUrl = generateEvents.baseUrl();
        String baseType = element.asType().toString();
        String baseClassName = element.getSimpleName().toString();
        String generatedClassName = generatedPrefix + baseClassName;

        return new StringBuilder()
                .append(addEventImports(generateEvents.retrofit()))
                .append(addEventClassHeader(generatedClassName))
                .append(addEventFields(baseType))
                .append(addEventConstructor(generateEvents.retrofit(), generatedClassName, baseType, baseUrl))
                .append(addEventMethods(element))
                .append("}\n");
    }


    private StringBuilder addEventImports(boolean includeRetrofit) {
        return new StringBuilder()
                .append("package com.blarley.rxretrobusseed.annotationprocessor.generated;\n\n")
                .append("import com.blarley.rxretrobusseed.library.bus.*;\n")
                .append("import com.blarley.rxretrobusseed.library.bus.RxRetroBus;\n")
                .append(includeRetrofit ? "import retrofit2.Retrofit;\n" : "");
    }


    private StringBuilder addEventClassHeader(String generatedClassName) {
        return new StringBuilder()
                .append("\npublic class " + generatedClassName + " {\n\n");
    }


    private StringBuilder addEventFields(String baseType) {
        return new StringBuilder()
                .append("\tprivate " + baseType + " client;\n")
                .append("\tprivate RxRetroBus bus;\n\n");
    }


    private StringBuilder addEventConstructor(boolean includeRetrofit, String generatedClassName,
                                              String baseType, String baseUrl) {
        StringBuilder sb = includeRetrofit
                ? new StringBuilder().append("\tpublic " + generatedClassName + "(Retrofit.Builder retrofitBuilder, RxRetroBus bus) { \n" +
                "\t\tthis.client = retrofitBuilder.baseUrl(\"" + baseUrl + "\")\n" +
                "\t\t\t\t.build()\n" +
                "\t\t\t\t.create(" + baseType + ".class);\n")
                : new StringBuilder().append("\tpublic " + generatedClassName + "(RxRetroBus bus) { \n" +
                "\t\tthis.client = new " + baseType + "();\n");
        return sb.append("\t\tthis.bus = bus;\n\t}\n\n");
    }


    private StringBuilder addEventMethods(Element element) {
        StringBuilder builder = new StringBuilder();
        for (Element subElement : element.getEnclosedElements()) {

            // ExecutableElements represent methods (among other things) - TODO: Figure out how this can break
            if (subElement instanceof ExecutableElement) {

                FireAndForgetEvent fireAndForgetEvent = subElement.getAnnotation(FireAndForgetEvent.class);
                CachedEvent cachedEvent = subElement.getAnnotation(CachedEvent.class);
                UncachedEvent uncachedEvent = subElement.getAnnotation(UncachedEvent.class);

                if (fireAndForgetEvent != null || cachedEvent != null || uncachedEvent != null) {
                    ExecutableElement method = (ExecutableElement) subElement;  // Cast to access Parameters
                    builder.append(addEventMethodHeader(method.getSimpleName().toString()))
                            .append(addParams(method))
                            .append(") {\n")
                            .append(addMethodCalls(method, addArgs(method)))
                            .append(addAnnotationModelInstantiation(fireAndForgetEvent, cachedEvent, uncachedEvent))
                            .append("\t}\n\n");
                }
            }
        }
        return builder;
    }


    // Append parameters to method definition - TODO: Figure out how this can break
    private StringBuilder addParams(ExecutableElement method) {
        String delim = "";
        StringBuilder params = new StringBuilder();
        for (VariableElement param : method.getParameters()) {
            params.append(delim)
                    .append(param.asType() + " ")
                    .append(param.getSimpleName().toString());
            delim = ", ";
        }
        return params;
    }


    // Append parameters to method definition - TODO: Figure out how this can break
    private StringBuilder addArgs(ExecutableElement method) {
        String delim = "";
        StringBuilder args = new StringBuilder();
        for (VariableElement arg : method.getParameters()) {
            args.append(delim)
                    .append(arg.getSimpleName().toString());
            delim = ", ";
        }
        return args;
    }


    private StringBuilder addMethodCalls(ExecutableElement method, StringBuilder args) {
        // Need to strip off the Observable and get parameterized class
        // TODO: Is this a better way to do this?
        String observable = method.getReturnType().toString();
        Pattern regex = Pattern.compile("<(.*?)>");
        Matcher matcher = regex.matcher(observable);
        String innerClass = "";
        while (matcher.find()) {
            innerClass += matcher.group(1);
        }
        return new StringBuilder().append("\t\tbus.addObservable(client." + method.getSimpleName().toString() + "(")
                .append(args)
                .append("), ")
                .append(innerClass + ".class, ");
    }


    private StringBuilder addAnnotationModelInstantiation(FireAndForgetEvent fireAndForgetEvent,
                                                          CachedEvent cachedEvent, UncachedEvent uncachedEvent) {
        StringBuilder sb = new StringBuilder();
        if (fireAndForgetEvent != null) {
            sb.append("new FireAndForgetEvent(")
                    .append("\"" + fireAndForgetEvent.tag() + "\", ")
                    .append(fireAndForgetEvent.debounce() + "));\n");
        } else if (cachedEvent != null) {
            sb.append("new CachedEvent(")
                    .append("\"" + cachedEvent.tag() + "\", ")
                    .append(cachedEvent.debounce() + "));\n");
        } else if (uncachedEvent != null) {
            sb.append("new UncachedEvent(")
                    .append("\"" + uncachedEvent.tag() + "\", ")
                    .append(uncachedEvent.debounce() + ", ")
                    .append(uncachedEvent.sticky() + "));\n");
        }
        return sb;
    }


    private StringBuilder addEventMethodHeader(String methodName) {
        return new StringBuilder().append("\tpublic void " + methodName + "(");
    }


    private void generateClientsFile() {
        try {
            JavaFileObject source = processingEnv.getFiler()
                    .createSourceFile(
                            "com.blarley.rxretrobusseed.annotationprocessor.generated.Clients");
            Writer writer = source.openWriter();
            writer.write(generateClientsFileHelper().toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private StringBuilder generateClientsFileHelper() {
        return addClientsImports()
                .append(addClientsClassHeader())
                .append(addClientsFields())
                .append(addClientsConstructorHeader())
                .append(addClientsConstructorBody())
                .append(addClientsFooter());
    }


    private StringBuilder addClientsImports() {
        return new StringBuilder()
                .append("package com.blarley.rxretrobusseed.annotationprocessor.generated;\n\n")
                .append("import retrofit2.Retrofit;\n\n")
                .append("import com.blarley.rxretrobusseed.library.bus.RxRetroBus;\n\n");
    }


    private StringBuilder addClientsClassHeader() {
        return new StringBuilder().append("public class Clients {\n");
    }


    private StringBuilder addClientsFields() {
        StringBuilder sb = new StringBuilder();
        for (GeneratedClass generatedClass : generatedClasses) {
            String[] str = generatedClass.getName().split("_");
            String baseClassName = str[1];
            sb.append("\tpublic ")
                    .append(generatedClass.getName())
                    .append(" ")
                    .append(baseClassName)
                    .append(";\n");
        }
        return sb;
    }


    private StringBuilder addClientsConstructorHeader() {
        return new StringBuilder()
                .append("\n\tpublic Clients(Retrofit.Builder retrofitBuilder, RxRetroBus bus) {\n");
    }


    private StringBuilder addClientsConstructorBody() {
        StringBuilder constructorDefinition = new StringBuilder();
        for (GeneratedClass generatedClass : generatedClasses) {
            String[] str = generatedClass.getName().split("_");
            String baseClassName = str[1];
            constructorDefinition.append("\t\tthis.")
                    .append(baseClassName)
                    .append(" = new ")
                    .append(generatedClass.getName())
                    .append(generatedClass.isRetrofitEnabled()
                            ? "(retrofitBuilder, bus);\n"
                            : "(bus);\n");
        }
        return constructorDefinition;
    }


    private StringBuilder addClientsFooter() {
        return new StringBuilder().append("\t}\n").append("}");
    }


    class GeneratedClass {

        private String name;
        private Boolean retrofitEnabled;


        public GeneratedClass(String name, Boolean retrofitEnabled) {
            this.name = name;
            this.retrofitEnabled = retrofitEnabled;
        }


        public String getName() {
            return name;
        }


        public Boolean isRetrofitEnabled() {
            return retrofitEnabled;
        }
    }
}
