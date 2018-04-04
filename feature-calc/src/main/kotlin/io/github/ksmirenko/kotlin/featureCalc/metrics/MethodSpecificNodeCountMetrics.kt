package io.github.ksmirenko.kotlin.featureCalc.metrics

import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.lexer.KtKeywordToken
import org.jetbrains.kotlin.psi.*

class MethodNumDeclarationsMetric : MethodSpecificNodeCountMetric<KtDeclaration>(
        id = FeatureRecord.Type.MethodNumDeclarations,
        csvName = "numDeclarations", description = "Number of KtDeclaratioins",
        desiredNodeType = KtDeclaration::class.java
)

class MethodNumBlocksMetric : MethodSpecificNodeCountMetric<KtBlockExpression>(
        id = FeatureRecord.Type.MethodNumBlocks,
        csvName = "numBlocks", description = "Number of block expressions",
        desiredNodeType = KtBlockExpression::class.java
)

class MethodNumTryExpressionsMetric : MethodSpecificNodeCountMetric<KtTryExpression>(
        id = FeatureRecord.Type.MethodNumTryExpressions,
        csvName = "numTry", description = "Number of try expressions",
        desiredNodeType = KtTryExpression::class.java
)

class MethodNumCatchClausesMetric : MethodSpecificNodeCountMetric<KtCatchClause>(
        id = FeatureRecord.Type.MethodNumCatchClauses,
        csvName = "numCatch", description = "Number of catch clauses",
        desiredNodeType = KtCatchClause::class.java
)

class MethodNumFinallySectionsMetric : MethodSpecificNodeCountMetric<KtFinallySection>(
        id = FeatureRecord.Type.MethodNumFinallySections,
        csvName = "numFinally", description = "Number of finally sections",
        desiredNodeType = KtFinallySection::class.java
)

class MethodNumLambdasMetric : MethodSpecificNodeCountMetric<KtLambdaExpression>(
        id = FeatureRecord.Type.MethodNumLambdas,
        csvName = "numLambdas",
        description = "Number of lambda expressions",
        desiredNodeType = KtLambdaExpression::class.java
)

class MethodNumNestedClassesMetric : MethodSpecificNodeCountMetric<KtClassOrObject>(
        id = FeatureRecord.Type.MethodNumNestedClasses,
        csvName = "numNestedClasses",
        description = "Number of nested classes, including indirectly nested (through a function too)",
        desiredNodeType = KtClassOrObject::class.java
)
