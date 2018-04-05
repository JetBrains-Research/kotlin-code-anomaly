package io.github.ksmirenko.kotlin.featureCalc.metrics

import org.jetbrains.kotlin.psi.*

class MethodNumDeclarationsMetric : MethodSpecificNodeCountMetric<KtDeclaration>(
        csvName = "numDeclarations", description = "Число KtDeclaratioin",
        desiredNodeType = KtDeclaration::class.java
)

class MethodNumBlocksMetric : MethodSpecificNodeCountMetric<KtBlockExpression>(
        csvName = "numBlocks", description = "Число блоков",
        desiredNodeType = KtBlockExpression::class.java
)

class MethodNumTryExpressionsMetric : MethodSpecificNodeCountMetric<KtTryExpression>(
        csvName = "numTry", description = "Число try-выражений",
        desiredNodeType = KtTryExpression::class.java
)

class MethodNumCatchClausesMetric : MethodSpecificNodeCountMetric<KtCatchClause>(
        csvName = "numCatch", description = "Число catch-выражений",
        desiredNodeType = KtCatchClause::class.java
)

class MethodNumFinallySectionsMetric : MethodSpecificNodeCountMetric<KtFinallySection>(
        csvName = "numFinally", description = "Число finally-секций",
        desiredNodeType = KtFinallySection::class.java
)

class MethodNumLambdasMetric : MethodSpecificNodeCountMetric<KtLambdaExpression>(
        csvName = "numLambdas",
        description = "Число лямбда-выражений",
        desiredNodeType = KtLambdaExpression::class.java
)

class MethodNumNestedClassesMetric : MethodSpecificNodeCountMetric<KtClassOrObject>(
        csvName = "numNestedClasses",
        description = "Число вложенных классов",
        desiredNodeType = KtClassOrObject::class.java
)
