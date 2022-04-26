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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;


/**
 * Configuration for group of build configs to run together.
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = GroupConfiguration.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupConfiguration extends GroupConfigurationRef {

    /**
     * Product version this group config is linked to.
     */
    private final ProductVersionRef productVersion;

    /**
     * List of the build configs in the group.
     */
    private final Map<String, BuildConfigurationRef> buildConfigs;

    @lombok.Builder(builderClassName = "Builder", toBuilder = true)
    GroupConfiguration(
            ProductVersionRef productVersion,
            Map<String, BuildConfigurationRef> buildConfigs,
            String id,
            String name) {
        super(id, name);
        this.productVersion = productVersion;
        this.buildConfigs = buildConfigs;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
