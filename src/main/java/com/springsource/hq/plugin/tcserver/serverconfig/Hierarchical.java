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

/**
 * Allows domain classes to track its containing parent. This enables walking the tree from any node in the tree.
 * 
 * @author Scott Andrews
 * @param <P> the parent's type
 * @since 2.0
 */
public interface Hierarchical<P extends Hierarchical<?>> {

    /**
     * @return this node's parent
     */
    public P parent();

    /**
     * @param parent the node's parent
     */
    public void setParent(P parent);

    /**
     * Apply the parent node to all children that implement {@link Hierarchical}. This method should invoke
     * {@link Hierarchical#setParent(Hierarchical)} and {@link Hierarchical#applyParentToChildren()} for each child node
     * to ensure the tree is properly constructed.
     */
    public void applyParentToChildren();

}
