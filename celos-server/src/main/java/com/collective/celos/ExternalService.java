/*
 * Copyright 2015 Collective, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.collective.celos;

/**
 * Execution engine for workflows.
 */
public interface ExternalService {

    /**
     * Submits slot to external service.
     * 
     * Returns external ID of submitted workflow, or throws an exception.
     */
    public String submit(SlotID id) throws ExternalServiceException;

    /**
     * Starts workflow with the given external ID.
     * 
     * The slot ID is the same that previously passed to submit.
     */
    public void start(SlotID id, String externalID) throws ExternalServiceException;
    
    /**
     * Gets the status of the externally running workflow with the given ID.
     */
    public ExternalStatus getStatus(SlotID id, String externalID) throws ExternalServiceException;

}