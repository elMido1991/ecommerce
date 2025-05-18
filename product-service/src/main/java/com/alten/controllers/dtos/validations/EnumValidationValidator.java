package com.alten.controllers.dtos.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidationValidator implements ConstraintValidator<EnumValidation, Enum<?>> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValidation constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        
        try {
            Enum.valueOf((Class<? extends Enum>) enumClass, value.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}