package de.team33.cmd.fsync.main.business;

import java.io.IOException;

@SuppressWarnings({"ClassNamePrefixedWithPackageName", "WeakerAccess"})
public class BusinessException extends RuntimeException {

    BusinessException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
