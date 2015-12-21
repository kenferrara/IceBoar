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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Contains information about downloaded / unzipped JRE: where a given version is downloaded / unzipped.
 * This class can be serialized and stored in a file (via {@linkplain CacheStatus}) to be run in the future by IceBoar.
 */
public class StatusInfo implements Serializable {

    private static final long serialVersionUID = 2022825810952620431L;

    private Status status;
    private String version;
    private String path;

    public StatusInfo() {
    }

    public StatusInfo(Status status, String version, String path) {
        this.status = status;
        this.version = version;
        this.path = path;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StatusInfo that = (StatusInfo) o;

        return new EqualsBuilder()
                .append(status, that.status)
                .append(version, that.version)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(status)
                .append(version)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "StatusInfo{" +
                "status='" + status + '\'' +
                ", version='" + version + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    enum Status {
        JRE_DOWNLOADED, JRE_UNZIPPED;
    }
}
