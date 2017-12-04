@file:Suppress("unused", "SENSELESS_COMPARISON", "MemberVisibilityCanPrivate", "LiftReturnOrAssignment",
        "SimplifyBooleanWithConstants", "ConstantConditionIf")

package testSrc

class Complexity {
    internal fun foo() {}

    internal fun bar(): Int {
        return 42
    }

    internal fun ifSimple(): Int {
        return if (4 > 0) {
            1
        } else {
            0
        }
    }

    internal fun ifWithCallCondition(): Int {
        return if (bar() > 0) {
            1
        } else {
            0
        }
    }

    internal fun ifWithCallBranch(): Int {
        if (4 > 0) {
            return 1
        } else {
            foo()
            return 0
        }
    }

    internal fun ifWithCallBoth(): Int {
        if (bar() > 0) {
            return 1
        } else {
            foo()
            return 0
        }
    }

    internal fun ifAnd(): Int {
        return if (1 > 0 && 4 % 2 == 0) {
            1
        } else {
            0
        }
    }

    internal fun ifAndWithCall(): Int {
        return if (1 > 0 && bar() % 2 == 0) {
            1
        } else {
            0
        }
    }

    internal fun ifAndWithNestedIf(): Int {
        return if (1 > 0 && bar() % 2 == 0) {
            if (bar() % 3 == 0) {
                1
            } else {
                0
            }
        } else {
            0
        }
    }

    internal fun ifAndWithNestedIfOr(): Int {
        return if (1 > 0 && bar() % 2 == 0) {
            if (bar() % 3 == 0 || 4 < 8) {
                1
            } else {
                0
            }
        } else {
            0
        }
    }
}
