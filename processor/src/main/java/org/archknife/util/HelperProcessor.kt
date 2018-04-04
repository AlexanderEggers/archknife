package org.archknife.util

import javax.annotation.processing.Filer

interface HelperProcessor {
    fun process(filer: Filer)
}
