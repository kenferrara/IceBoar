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

import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.Test;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class ProgressEventQueueTest {

    @Test
    public void shouldInformAllObserversFirstAndProcessNextEvent() {
        // given
        final ProgressEventQueue queue = new ProgressEventQueue();

        ProgressEvent event1 = new ProgressEvent("Event 1", " ");
        final ProgressEvent event2 = new ProgressEvent("Event 2", " ");

        ProgressEventObserver mock1 = mock(ProgressEventObserver.class);
        queue.registerObserver(mock1);
        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                queue.update(event2);   // send second event to queue
                return null;
            }
        }).when(mock1).update(event1);
        ProgressEventObserver mock2 = mock(ProgressEventObserver.class);
        queue.registerObserver(mock2);


        // when
        queue.update(event1);

        // then
        InOrder inOrder = inOrder(mock1, mock2, mock1, mock2);

        inOrder.verify(mock1).update(event1);
        inOrder.verify(mock2).update(event1);
        inOrder.verify(mock1).update(event2);
        inOrder.verify(mock2).update(event2);

    }
}