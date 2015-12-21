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

package com.roche.iceboar.progressevent;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Class for representing Progress Events.
 */
public class ProgressEvent {
    private final String eventName;
    private String message;
    private ProgressEventDetailInfo detailInfo;

    public ProgressEvent(String eventName, String message) {
        this.eventName = eventName;
        this.message = message;
    }

    public String getEventName() {
        return eventName;
    }

    public String getMessage() {
        return message;
    }

    public void addDetailInfo(ProgressEventDetailInfo detailInfo) {
        this.detailInfo = detailInfo;
    }

    public ProgressEventDetailInfo getDetailInfo() {
        return detailInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProgressEvent that = (ProgressEvent) o;

        return new EqualsBuilder()
                .append(eventName, that.eventName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(eventName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ProgressEvent{" +
                "eventName='" + eventName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
