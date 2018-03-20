package io.github.ksmirenko.kotlin.featureCalc.features

import io.github.ksmirenko.kotlin.featureCalc.records.FeatureRecord
import org.jetbrains.kotlin.psi.*

class MethodNumDeclarationsFeature : MethodSpecificNodeCountFeature<KtDeclaration>(
        id = FeatureRecord.Type.MethodNumDeclarations,
        csvName = "numDeclarations", description = "Number of KtDeclaratioins",
        desiredNodeType = KtDeclaration::class.java
)

class MethodNumBlocksFeature : MethodSpecificNodeCountFeature<KtBlockExpression>(
        id = FeatureRecord.Type.MethodNumBlocks,
        csvName = "numBlocks", description = "Number of block expressions",
        desiredNodeType = KtBlockExpression::class.java
)

class MethodNumTryExpressionsFeature : MethodSpecificNodeCountFeature<KtTryExpression>(
        id = FeatureRecord.Type.MethodNumTryExpressions,
        csvName = "numTry", description = "Number of try expressions",
        desiredNodeType = KtTryExpression::class.java
)

class MethodNumCatchClausesFeature : MethodSpecificNodeCountFeature<KtCatchClause>(
        id = FeatureRecord.Type.MethodNumCatchClauses,
        csvName = "numCatch", description = "Number of catch clauses",
        desiredNodeType = KtCatchClause::class.java
)

class MethodNumFinallySectionsFeature : MethodSpecificNodeCountFeature<KtFinallySection>(
        id = FeatureRecord.Type.MethodNumFinallySections,
        csvName = "numFinally", description = "Number of finally sections",
        desiredNodeType = KtFinallySection::class.java
)
