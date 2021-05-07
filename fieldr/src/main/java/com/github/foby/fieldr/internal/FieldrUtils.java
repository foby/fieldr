package com.github.foby.fieldr.internal;

import com.google.common.base.CaseFormat;

public final class FieldrUtils {

    private FieldrUtils() {
    }

    static String toConstantName(final String propertyName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, propertyName);
    }
}
