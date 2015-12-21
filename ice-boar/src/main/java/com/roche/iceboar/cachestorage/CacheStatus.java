/*
 * ****************************************************************************
 *  Copyright © 2015 Hoffmann-La Roche
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package com.roche.iceboar.cachestorage;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains information about cache records read from file from disk.
 */
public class CacheStatus implements Serializable {

    private static final long serialVersionUID = 3338720274125187874L;

    private Set<StatusInfo> statusSet = new HashSet<StatusInfo>();

    public CacheStatus() {
    }

    public void add(StatusInfo newStatus) {
        // remove old a store with maybe new path
        statusSet.remove(newStatus);
        statusSet.add(newStatus);
    }

    public StatusInfo getJreDownloadedStatusInfo(String javaVersion) {
        return getStatusInfoForVersionAndStatus(javaVersion, StatusInfo.Status.JRE_DOWNLOADED);
    }

    public StatusInfo getJreUnzippedStatusInfo(String javaVersion) {
        return getStatusInfoForVersionAndStatus(javaVersion, StatusInfo.Status.JRE_UNZIPPED);
    }

    private StatusInfo getStatusInfoForVersionAndStatus(String javaVersion, StatusInfo.Status status) {
        for (StatusInfo s : statusSet) {
            if (s.getVersion().equals(javaVersion) && s.getStatus().equals(status)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return StringUtils.join(statusSet, "\n");
    }
}
