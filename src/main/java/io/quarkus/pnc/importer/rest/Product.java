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
 * A product is a deliverable package composed of multiple project.
 *
 * @author Honza Brázdil &lt;jbrazdil@redhat.com&gt;
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonDeserialize(builder = Product.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product extends ProductRef {

    /**
     * List of this product's versions.
     */

    private final Map<String, ProductVersionRef> productVersions;

    @lombok.Builder(builderClassName = "Builder", toBuilder = true)
    private Product(
            Map<String, ProductVersionRef> productVersions,
            String id,
            String name,
            String description,
            String abbreviation,
            String productManagers,
            String productPagesCode) {
        super(id, name, description, abbreviation, productManagers, productPagesCode);
        this.productVersions = productVersions;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
