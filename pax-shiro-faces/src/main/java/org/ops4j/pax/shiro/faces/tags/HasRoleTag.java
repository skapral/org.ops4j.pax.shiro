/*
 * Copyright 2013 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.shiro.faces.tags;

import javax.faces.view.facelets.TagConfig;

public class HasRoleTag extends AuthorizationTagHandler {

    public HasRoleTag(TagConfig config) {
        super(config);
    }

    @Override 
    protected boolean showTagBody(String value) {
        return hasRole(value);
    }
    
    protected boolean hasRole(String roleName) {
        return getSubject() != null && getSubject().hasRole(roleName);
    }
}