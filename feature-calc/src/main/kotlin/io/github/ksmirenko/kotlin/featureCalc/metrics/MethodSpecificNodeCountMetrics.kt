package io.github.ksmirenko.kotlin.featureCalc.metrics

import org.jetbrains.kotlin.psi.*

class MethodNumDeclarationsMetric : MethodSpecificNodeCountMetric<KtDeclaration>(
        csvName = "numDeclarations", description = "Number of KtDeclaratioins",
        desiredNodeType = KtDeclaration::class.java
)

class MethodNumBlocksMetric : MethodSpecificNodeCountMetric<KtBlockExpression>(
        csvName = "numBlocks", description = "Number of block expressions",
        desiredNodeType = KtBlockExpression::class.java
)

class MethodNumTryExpressionsMetric : MethodSpecificNodeCountMetric<KtTryExpression>(
        csvName = "numTry", description = "Number of try expressions",
        desiredNodeType = KtTryExpression::class.java
)

class MethodNumCatchClausesMetric : MethodSpecificNodeCountMetric<KtCatchClause>(
        csvName = "numCatch", description = "Number of catch clauses",
        desiredNodeType = KtCatchClause::class.java
)

class MethodNumFinallySectionsMetric : MethodSpecificNodeCountMetric<KtFinallySection>(
        csvName = "numFinally", description = "Number of finally sections",
        desiredNodeType = KtFinallySection::class.java
)

class MethodNumLambdasMetric : MethodSpecificNodeCountMetric<KtLambdaExpression>(
        csvName = "numLambdas",
        description = "Number of lambda expressions",
        desiredNodeType = KtLambdaExpression::class.java
)

class MethodNumNestedClassesMetric : MethodSpecificNodeCountMetric<KtClassOrObject>(
        csvName = "numNestedClasses",
        description = "Number of nested classes, including indirectly nested (through a function too)",
        desiredNodeType = KtClassOrObject::class.java
)
