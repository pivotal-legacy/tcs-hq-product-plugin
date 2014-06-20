// Copyright (c) 2009-2010 VMware, Inc.  All rights reserved.

package com.springsource.hq.plugin.tcserver.serverconfig.resources.jdbc;

import org.springframework.validation.Errors;

/**
 * Mock implementation of {@link DataSource} abstract class
 * 
 * @author Scott Andrews
 */
public class MockDataSource extends DataSource {

    public boolean supports(Class<?> clazz) {
        return false;
    }

    public void validate(Object target, Errors errors) {
        // no-op
    }

    public void applyParentToChildren() {
        // no-op
    }

}
