/**
 * Copyright (C) FuseSource, Inc.
 * http://fusesource.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fusesource.fabric.api;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the current status of the Fabric in terms of requirements and current instances
 */
public class FabricStatus {
    private final FabricService service;
    private FabricRequirements requirements;
    Map<String,ProfileStatus> profileStatusMap = new TreeMap<String, ProfileStatus>();

    public FabricStatus(FabricService service) {
        this.service = service;
        requirements = service.getRequirements();
        List<ProfileRequirements> profileRequirements = requirements.getProfileRequirements();
        for (ProfileRequirements profileRequirement : profileRequirements) {
            String key = profileRequirement.getProfile();
            ProfileStatus status = profileStatusMap.get(key);
            if (status == null) {
                status = createStatus(key, profileRequirement);
                profileStatusMap.put(key, status);
            }
        }
        Container[] containers = service.getContainers();
        for (Container container : containers) {
            if (container.isAliveAndOK()) {
                Profile[] profiles = container.getProfiles();
                for (Profile profile : profiles) {
                    String key = profile.getId();
                    ProfileStatus status = profileStatusMap.get(key);
                    if (status == null) {
                        status = createStatus(key, null);
                        profileStatusMap.put(key, status);
                    }
                    status.incrementCount();
                }
            }
        }
    }

    public Map<String, ProfileStatus> getProfileStatusMap() {
        return profileStatusMap;
    }

    public FabricRequirements getRequirements() {
        return requirements;
    }

    public FabricService getService() {
        return service;
    }

    protected ProfileStatus createStatus(String profileId, ProfileRequirements profileRequirement) {
        if (profileRequirement == null) {
            profileRequirement = new ProfileRequirements(profileId);
            requirements.addOrUpdateProfileRequirements(profileRequirement);
        }
        return new ProfileStatus(profileId, profileRequirement);
    }

    @Override
    public String toString() {
        return "FabricStatus[" +
                "requirements=" + requirements.getProfileRequirements() +
                ", profileStatusMap=" + profileStatusMap +
                ']';
    }
}
