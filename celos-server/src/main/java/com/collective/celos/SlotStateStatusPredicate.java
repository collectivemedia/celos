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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.Predicate;

import com.collective.celos.SlotState.Status;

/**
 * Ivor who's a Scala wonk did this.
 */
public class SlotStateStatusPredicate implements Predicate {
    
    private Set<Status> targetValues;

    public SlotStateStatusPredicate(Status... targetValues) {
        this.targetValues = new HashSet<Status>(Arrays.asList(targetValues));
        if (this.targetValues.isEmpty()) {
            throw new IllegalArgumentException("please specify some status values");
        }
    }

    @Override
    public boolean evaluate(Object object) {
        SlotState state = (SlotState) object;
        return targetValues.contains(state.status);
    }

}
