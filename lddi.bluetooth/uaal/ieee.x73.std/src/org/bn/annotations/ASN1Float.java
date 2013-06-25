package org.bn.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target( ElementType.TYPE )
public @interface ASN1Float {
    String name();
}
