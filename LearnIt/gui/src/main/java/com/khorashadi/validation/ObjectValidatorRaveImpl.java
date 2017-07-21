package com.khorashadi.validation;

import com.uber.rave.InvalidModelException;
import com.uber.rave.Rave;
import com.uber.rave.RaveException;

public class ObjectValidatorRaveImpl implements ObjectValidator {
    private final Rave instance = Rave.getInstance();

    @Override
    public boolean isValidObject(Object o) {
        try {
            instance.validateIgnoreUnsupported(o);
        } catch (InvalidModelException e) {
            return false;
        }
        return true;
    }
}
