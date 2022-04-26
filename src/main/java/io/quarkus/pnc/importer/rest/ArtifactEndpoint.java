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

import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Path("/artifacts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RestClient
public interface ArtifactEndpoint {
    static final String A_ID = "ID of the artifact";
    static final String A_REV = "Revision number of the artifact";

    static final String GET_ALL_DESC = "Gets all artifacts.";
    static final String FILTER_SHA256_DESC = "Filter by sha256 of the artifact.";
    static final String FILTER_SHA1_DESC = "Filter by sha1 of the artifact.";
    static final String FILTER_MD5_DESC = "Filter by md5 of the artifact.";

    /**
     * {@value GET_ALL_DESC}
     *
     * @param pageParams
     * @param sha256     {@value FILTER_SHA256_DESC}
     * @param md5        {@value FILTER_MD5_DESC}
     * @param sha1       {@value FILTER_SHA1_DESC}
     * @return
     */
    @GET
    Page<Artifact> getAll(@BeanParam PageParameters pageParams, @QueryParam("sha256") String sha256, @QueryParam("md5") String md5, @QueryParam("sha1") String sha1);

}
