/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.serverconfig;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Utility methods for performing validation of domain objects
 * <p />
 * 
 * <strong>Concurrent Semantics</strong><br />
 * 
 * Thread-safe
 * 
 */
public final class ValidationUtils {

    private ValidationUtils() {

    }

    /**
     * Performs validation of the given <code>Collection</code> of <code>Validator</code> instances. Each
     * <code>Validator</code> is treated as a self-validating domain object, i.e. the following is called upon each
     * <code>Validator</code>: <code>validator.validate(validator, errors)</code>. The given <code>identifier</code> is
     * used when pushing the path on the given <code>errors</code>, e.g. an identifier of 'foo' will result in
     * <code>foo[i]</code> being pushed, where i is the index of the validator in the given collection. The given
     * <code>errors</code> will be used to record any errors that are found.
     * 
     * @see Validator#validate(Object, Errors)
     * 
     * @param selfValidatingItems The self-validating items to validate
     * @param identifier The identifier
     * @param errors Passed to each validator and to be used to record any errors
     */
    public static void validateCollection(Collection<? extends Validator> selfValidatingItems, String identifier, Errors errors) {
        Iterator<? extends Validator> iterator = selfValidatingItems.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            errors.pushNestedPath(String.format("%s[%d]", identifier, index++));
            Validator selfValidating = iterator.next();
            selfValidating.validate(selfValidating, errors);
            errors.popNestedPath();
        }
    }
}
