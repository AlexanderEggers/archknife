package org.archknife.util

import org.archknife.MainProcessor
import javax.annotation.processing.RoundEnvironment

interface AnnotationProcessor {
    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment)
}