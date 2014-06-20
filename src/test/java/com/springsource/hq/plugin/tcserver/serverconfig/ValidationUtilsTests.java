
package com.springsource.hq.plugin.tcserver.serverconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public final class ValidationUtilsTests {

    @Test
    public void validationOfCollection() {
        List<StubValidator> validators = new ArrayList<StubValidator>();
        validators.add(new StubValidator());
        validators.add(new StubValidator());
        validators.add(new StubValidator());

        ValidationUtils.validateCollection(validators, "theValidators", new BeanPropertyBindingResult(null, null));

        for (int i = 0; i < validators.size(); i++) {
            StubValidator validator = validators.get(i);
            Assert.assertSame(validator, validator.target);
            Assert.assertEquals("theValidators[" + i + "].", validator.nestedPath);
        }
    }

    private static final class StubValidator implements Validator {

        private volatile Object target;

        private volatile String nestedPath;

        @SuppressWarnings("rawtypes")
        public boolean supports(Class clazz) {
            return true;
        }

        public void validate(Object target, Errors errors) {
            this.target = target;
            this.nestedPath = errors.getNestedPath();
        }
    }
}
