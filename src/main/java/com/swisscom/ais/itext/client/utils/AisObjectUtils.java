package com.swisscom.ais.itext.client.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;
import java.util.function.Function;

public class AisObjectUtils extends ObjectUtils {

    private static <R, C> boolean childOfNull(R root, Function<R, C> childGetter, boolean returnedValue) {
        if (Objects.isNull(root)) {
            return returnedValue;
        }
        C childResult = childGetter.apply(root);
        return Objects.isNull(childResult) == returnedValue;
    }

    public static <R, C> boolean firstChildNull(R root, Function<R, C> firstChildGetter) {
        return childOfNull(root, firstChildGetter, true);
    }

    public static <R, C1, C2> boolean firstChildNull(R root, Function<R, C1> firstChildGetter, Function<C1, C2> secondChildGetter) {
        return firstChildNull(root, firstChildGetter) || firstChildNull(firstChildGetter.apply(root), secondChildGetter);
    }

    public static <R, C1, C2, C3> boolean firstChildNull(R root, Function<R, C1> firstChildGetter, Function<C1, C2> secondChildGetter,
                                                         Function<C2, C3> thirdChildGetter) {
        return firstChildNull(root, firstChildGetter) || firstChildNull(firstChildGetter.apply(root), secondChildGetter, thirdChildGetter);
    }

    public static <R, C> boolean childNotNull(R root, Function<R, C> childGetter) {
        return childOfNull(root, childGetter, false);
    }

    public static <R, C1, C2> boolean allChildrenNotNull(R root, Function<R, C1> firstChildGetter, Function<C1, C2> secondChildGetter) {
        return childNotNull(root, firstChildGetter) && childNotNull(firstChildGetter.apply(root), secondChildGetter);
    }

    public static <R, C1, C2, C3> boolean allChildrenNotNull(R root, Function<R, C1> firstChildGetter, Function<C1, C2> secondChildGetter,
                                                             Function<C2, C3> thirdChildGetter) {
        return childNotNull(root, firstChildGetter) && allChildrenNotNull(firstChildGetter.apply(root), secondChildGetter, thirdChildGetter);
    }

    public static <R, C1, C2, C3, C4> boolean allChildrenNotNull(R root, Function<R, C1> firstChildGetter, Function<C1, C2> secondChildGetter,
                                                                 Function<C2, C3> thirdChildGetter, Function<C3, C4> fourthChildGetter) {
        return childNotNull(root, firstChildGetter) && allChildrenNotNull(firstChildGetter.apply(root), secondChildGetter, thirdChildGetter,
                                                                          fourthChildGetter);
    }

    public static <R, C1, C2, C3, C4, C5> boolean allChildrenNotNull(R root, Function<R, C1> firstChildGetter, Function<C1, C2> secondChildGetter,
                                                                     Function<C2, C3> thirdChildGetter, Function<C3, C4> fourthChildGetter,
                                                                     Function<C4, C5> fifthChildGetter) {
        return childNotNull(root, firstChildGetter) && allChildrenNotNull(firstChildGetter.apply(root), secondChildGetter, thirdChildGetter,
                                                                          fourthChildGetter, fifthChildGetter);
    }
}
