package testSrc.java;

@SuppressWarnings("ALL")
public class ComplexityJava {
    void foo() {
    }

    int bar() {
        return 42;
    }

    int ifSimple() {
        if (4 > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    int ifWithCallCondition() {
        if (bar() > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    int ifWithCallBranch() {
        if (4 > 0) {
            return 1;
        } else {
            foo();
            return 0;
        }
    }

    int ifWithCallBoth() {
        if (bar() > 0) {
            return 1;
        } else {
            foo();
            return 0;
        }
    }

    int ifAnd() {
        if (1 > 0 && 4 % 2 == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    int ifAndWithCall() {
        if (1 > 0 && bar() % 2 == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    int ifAndWithNestedIf() {
        if (1 > 0 && bar() % 2 == 0) {
            if (bar() % 3 == 0) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    int ifAndWithNestedIfOr() {
        if (1 > 0 && bar() % 2 == 0) {
            if (bar() % 3 == 0 || 4 < 8) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
