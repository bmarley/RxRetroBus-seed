package com.blarley.rxretrobusseed.annotationprocessor.processor;


import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.blarley.rxretrobusseed.annotationprocessor.processor.GenerateEvents")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RxRetroBusAnnotationProcessor extends AbstractProcessor{

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {


        // for each javax.lang.model.element.Element annotated with the CustomAnnotation
        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateEvents.class)) {

            String baseType = element.asType().toString();
            String baseClassName = element.getSimpleName().toString();
            String generatedClassName = "RxRetroBus" + baseClassName;

            String baseUrl = element.getAnnotation(GenerateEvents.class).baseUrl();

            //Package and imports
            StringBuilder builder = new StringBuilder()
                .append("package com.blarley.rxretrobusseed.annotationprocessor.generated;\n\n")
                .append("import retrofit2.Retrofit;\n" +
                        "import io.reactivex.android.schedulers.AndroidSchedulers;\n" +
                        "import io.reactivex.functions.Consumer;\n" +
                        "import io.reactivex.schedulers.Schedulers;\n");

            //Begin class definition
            builder.append("public class " + generatedClassName + " {\n\n");

            //Retrofit client impl
            builder.append("\tprivate " + baseType + " client;\n");

            //Constructor - builds Retrofit client
            builder.append("\tpublic " + generatedClassName + "(Retrofit.Builder retrofitBuilder) { \n" +
                            "\t\tthis.client = retrofitBuilder.baseUrl(\"" + baseUrl + "\")\n" +
                            "\t\t.build()\n" +
                            "\t\t.create(" + baseType + ".class);\n" +
                            "\t}\n\n");

            //Get Annotated methods within the class - the builds the method used to make calls
            for (Element subElement : roundEnv.getElementsAnnotatedWith(Publish.class)) {

                // ExecutableElements represent methods (among other things) - TODO: Figure out how this can break
                if (subElement instanceof ExecutableElement) {

                    //Cast to ExecutableElement in order to get Parameters
                    ExecutableElement method = (ExecutableElement) subElement;
                    String methodName = method.getSimpleName().toString();

                    // Begin definition of method
                    builder.append("\tpublic void " + methodName + "(");

                    // Append parameters to method definition - TODO: Figure out how this can break
                    String delim = " ";
                    StringBuilder parameters = new StringBuilder();
                    for (VariableElement param : method.getParameters()) {
                        parameters.append(delim)
                                .append(param.asType() + " ")
                                .append(param.getSimpleName().toString());
                        delim = ", ";
                    }

                    //Append the parameters to the method definition and open declaration
                    builder.append(parameters)
                            .append(") {\n");

                    //Begin body, this method should call the same method on the Retrofit client
                    builder.append("\t\tclient." + methodName + "(").append(parameters).append(")\n");

                    //Subscribe on New thread, observe on the main thread, and subscribe!
                    builder.append("\t\t\t.subscribeOn(Schedulers.newThread())\n" +
                                    "\t\t\t.observeOn(AndroidSchedulers.mainThread())\n" +
                                    "\t\t\t.subscribe(\n");

                    //Need to strip off the Observable and get parameterized class
                    //TODO: Is this a better way to do this?
                    String observable = method.getReturnType().toString();
                    Pattern regex = Pattern.compile("<(.*?)>");
                    Matcher matcher = regex.matcher(observable);
                    String innerClass = "";
                    while (matcher.find()) {
                        innerClass += matcher.group(1);
                    }

                    //Provide onNext and onError Consumers
                    //TODO: Figure out how the bus is going to work
                    //TODO: Go look at how Dagger generates classes, get rid of \t
                    builder.append("\t\t\t\tnew Consumer<").append(innerClass).append(">() {\n")
                            .append("\t\t\t\t\t@Override\n")
                            .append("\t\t\t\t\tpublic void accept(").append(innerClass).append(" model) throws Exception {\n")
                            .append("\t\t\t\t\t\tSystem.out.println(model.getExampleField());\n")
                            .append("\t\t\t\t\t}\n")
                            .append("\t\t\t\t},\n")
                            .append("\t\t\t\tnew Consumer<Throwable>() {\n")
                            .append("\t\t\t\t\t@Override\n")
                            .append("\t\t\t\t\tpublic void accept(Throwable throwable) throws Exception {\n")
                            .append("\t\t\t\t\t}\n")
                            .append("\t\t\t\t}\n")
                            .append("\t\t\t);\n");

                    //End method definition
                    builder.append("\t}");
                }
            }

            //End Class definition
            builder.append("}\n");

            try { // write the file
                JavaFileObject source = processingEnv.getFiler().createSourceFile("com.blarley.rxretrobusseed.annotationprocessor.generated." + generatedClassName);

                Writer writer = source.openWriter();
                writer.write(builder.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                // Note: calling e.printStackTrace() will print IO errors
                // that occur from the file already existing after its first run, this is normal
            }

        }

        return true;
    }
}
