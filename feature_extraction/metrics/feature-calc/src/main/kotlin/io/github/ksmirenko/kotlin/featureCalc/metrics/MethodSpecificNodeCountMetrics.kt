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

class MethodNumConstants : MethodSpecificNodeCountMetric<KtConstantExpression>(
        csvName = "numConstExpr",
        description = "Число константных выражений",
        desiredNodeType = KtConstantExpression::class.java
)

class MethodNumStringTemplates : MethodSpecificNodeCountMetric<KtStringTemplateExpression>(
        csvName = "numStringTemplates",
        description = "Число строковых шаблонов",
        desiredNodeType = KtStringTemplateExpression::class.java
)

class MethodNumStringLiteralTemplates : MethodSpecificNodeCountMetric<KtLiteralStringTemplateEntry>(
        csvName = "numStringLiteralTemplates",
        description = "Число строковых шаблонов-литералов",
        desiredNodeType = KtLiteralStringTemplateEntry::class.java
)

class MethodNumBlockStringTemplates : MethodSpecificNodeCountMetric<KtBlockStringTemplateEntry>(
        csvName = "numBlockStringTemplates",
        description = "Число блочных строковых шаблонов",
        desiredNodeType = KtBlockStringTemplateEntry::class.java
)

class MethodNumReferences : MethodSpecificNodeCountMetric<KtReferenceExpression>(
        csvName = "numReferences",
        description = "Число ссылок",
        desiredNodeType = KtReferenceExpression::class.java
)

class MethodNumOperations : MethodSpecificNodeCountMetric<KtOperationReferenceExpression>(
        csvName = "numOperationReferences",
        description = "Число операций",
        desiredNodeType = KtOperationReferenceExpression::class.java
)

class MethodNumThrows : MethodSpecificNodeCountMetric<KtThrowExpression>(
        csvName = "numThrows",
        description = "Число throw-операторов",
        desiredNodeType = KtThrowExpression::class.java
)

class MethodNumSafeQualifiedExpressions : MethodSpecificNodeCountMetric<KtSafeQualifiedExpression>(
        csvName = "numSafeExpressions",
        description = "Число null-безопасных обращений по ссылкам",
        desiredNodeType = KtSafeQualifiedExpression::class.java
)

class MethodNumClassLiterals : MethodSpecificNodeCountMetric<KtClassLiteralExpression>(
        csvName = "numClassLiterals",
        description = "Число выражений-ссылок на классы",
        desiredNodeType = KtClassLiteralExpression::class.java
)

class MethodNumCollectionLiterals : MethodSpecificNodeCountMetric<KtCollectionLiteralExpression>(
        csvName = "numCollectionLiterals",
        description = "Число коллекций-литералов",
        desiredNodeType = KtCollectionLiteralExpression::class.java
)
