package com.github.foby.fieldr.internal;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Base class for generated field name constant classes.
 */
@SuppressWarnings("unused")
public class FieldsBase {

    @SuppressWarnings("WeakerAccess") protected String context;

    protected FieldsBase(final String context) {
        this.context = context;
    }

    protected String withContext(final String propertyName) {
        return Optional.ofNullable(context)
                .map(s -> String.format("%s.%s", s, propertyName))
                .orElse(propertyName);
    }

    public Set<String> all() {
        return Sets.newHashSet();
    }
}
