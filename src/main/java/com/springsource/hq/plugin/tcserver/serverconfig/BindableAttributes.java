/*
        Copyright (C) 2010-2014 Pivotal Software, Inc.


        All rights reserved. This program and the accompanying materials
        are made available under the terms of the under the Apache License,
        Version 2.0 (the "License”); you may not use this file except in compliance
        with the License. You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
 */

package com.springsource.hq.plugin.tcserver.serverconfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used to mark classes for white listing on Spring MVC controllers. Is is used by BinderUtil to
 * generate a list of attributes needed by Spring MVC.
 * 
 * @author Greg Turnquist
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BindableAttributes {

}
