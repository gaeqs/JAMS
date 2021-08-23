/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.event;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

@SupportedAnnotationTypes({"net.jamsimulator.jams.event.Listener"})
@SupportedSourceVersion(SourceVersion.RELEASE_16)
public class ListenerProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var element : roundEnv.getElementsAnnotatedWith(Listener.class)) {
            var annotation = element.getAnnotation(Listener.class);
            if (element.asType() instanceof ExecutableType te) {
                System.out.println("Method: " + element.getSimpleName());
                System.out.println("Parameters: " + te.getParameterTypes());
                for (var typeMirror : te.getParameterTypes()) {
                    if (typeMirror instanceof DeclaredType dclt) {
                        var data = dclt.getTypeArguments().stream().map(TypeMirror::toString)
                                .toArray(String[]::new);


                        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                            System.out.println("MIRROR: " + annotationMirror);
                            System.out.println("TYPE: " + annotationMirror.getAnnotationType());
                            System.out.println(annotationMirror.toString().equals(annotation.toString()));

                            if (Listener.class.getCanonicalName().equals(annotationMirror.getAnnotationType().toString())) {
                                System.out.println("HERE WE GOO");


                                processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror)
                                        .forEach((a, b) -> {
                                            System.out.println(a + " -> " + b);
                                        });

                            }

                        }
                    }
                }
            }
        }

        return true;
    }
}
