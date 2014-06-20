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

package com.springsource.hq.plugin.tcserver.plugin;

import java.util.Arrays;

/**
 * A collection of methods useful for working with lists of data. (Inspired by Scala collections library)
 */
public final class ListUtils {

    /**
     * Select all elements except the last n ones
     */
    public static <T> T[] dropRight(T[] xs, int n) {
        return Arrays.copyOf(xs, xs.length - n);
    }

    /**
     * Take last item from a list
     */
    public static <T> T last(T[] xs) {
        return xs[xs.length - 1];
    }

}
