package io.github.ksmirenko.kotlin.featureCalc

import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import java.io.File

object PsiGenerator {
    private val env = prepareEnvironment()

    fun generate(ktFile: File): PsiFile {
        return PsiFileFactory.getInstance(env.project)
                .createFileFromText(ktFile.name, KotlinLanguage.INSTANCE, ktFile.readText())
    }

    private fun prepareEnvironment(): KotlinCoreEnvironment {
        val rootDisposable = Disposer.newDisposable()
        val configuration = CompilerConfiguration()
        return KotlinCoreEnvironment
                .createForProduction(rootDisposable, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)
    }
}
