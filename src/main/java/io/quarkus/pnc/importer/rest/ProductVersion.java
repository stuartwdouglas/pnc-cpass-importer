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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

import java.util.Map;


/**
 * Product version represents one product stream like "6.3", "6,4", "7.1".
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */

@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = ProductVersion.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductVersion extends ProductVersionRef {

    /**
     * Product that this is a version of.
     */

    
    private final ProductRef product;

    /**
     * Current milestone of this product version that is being productized.
     */

    private final ProductMilestoneRef currentProductMilestone;

    /**
     * List of all milestones in this product version.
     */

    private final Map<String, ProductMilestoneRef> productMilestones;

    /**
     * List of all releases in this product version.
     */

    private final Map<String, ProductReleaseRef> productReleases;

    /**
     * List of all group configs linked to this product version.
     */

    private final Map<String, GroupConfigurationRef> groupConfigs;

    /**
     * List of all group configs linked to this product version.
     */

    private final Map<String, BuildConfigurationRef> buildConfigs;

    @lombok.Builder(builderClassName = "Builder", toBuilder = true)
    private ProductVersion(
            ProductRef product,
            ProductMilestoneRef currentProductMilestone,
            Map<String, ProductMilestoneRef> productMilestones,
            Map<String, ProductReleaseRef> productReleases,
            Map<String, GroupConfigurationRef> groupConfigs,
            Map<String, BuildConfigurationRef> buildConfigs,
            String id,
            String version,
            Map<String, String> attributes) {
        super(id, version, attributes);
        this.product = product;
        this.currentProductMilestone = currentProductMilestone;
        this.productMilestones = productMilestones;
        this.productReleases = productReleases;
        this.groupConfigs = groupConfigs;
        this.buildConfigs = buildConfigs;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
