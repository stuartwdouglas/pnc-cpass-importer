/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014-2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkus.pnc.importer.rest;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 * @deprecated use pnc-api
 */
@Deprecated
public enum RebuildMode {

    /**
     * Check automatically captured dependencies on {@link org.jboss.pnc.model.BuildRecord}.
     */
    IMPLICIT_DEPENDENCY_CHECK,

    /**
     * Check the user defined dependencies on {@link org.jboss.pnc.model.BuildConfiguration}.
     */
    EXPLICIT_DEPENDENCY_CHECK,

    /**
     * Don't check anything and run the build anyway.
     */
    FORCE
}