package com.braids.burncoffeeman.common.transfer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Transfer {

	TransferType value();
}
