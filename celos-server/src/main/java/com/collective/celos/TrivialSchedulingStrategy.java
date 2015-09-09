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

import java.util.LinkedList;
import java.util.List;

/**
 * Trivial scheduling strategy that submits as many ready jobs as possible in parallel. 
 */
public class TrivialSchedulingStrategy implements SchedulingStrategy {

    public TrivialSchedulingStrategy() {
    }
    
    public List<SlotState> getSchedulingCandidates(List<SlotState> states) {
        List<SlotState> candidates = new LinkedList<SlotState>();
        for (SlotState slotState : states) {
            if (slotState.getStatus() == SlotState.Status.READY) {
                candidates.add(slotState);
            }
        }
        return candidates;
    }

}
