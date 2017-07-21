package com.khorashadi.validation;

import com.uber.rave.BaseValidator;
import com.uber.rave.Validator;
import com.uber.rave.ValidatorFactory;

@Validator(mode = Validator.Mode.STRICT)
public class RaveFactory implements ValidatorFactory {
    @Override
    public BaseValidator generateValidator() {
        return new RaveFactory_Generated_Validator();
    }
}
