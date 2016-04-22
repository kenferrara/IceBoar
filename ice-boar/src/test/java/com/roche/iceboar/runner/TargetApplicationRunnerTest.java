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

package com.roche.iceboar.runner;

import com.roche.iceboar.downloader.JREDownloader;
import com.roche.iceboar.downloader.JarDownloader;
import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventObserver;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.settings.GlobalSettings;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TargetApplicationRunnerTest {

	@Test
	public void shouldRegisterInQueueCorrectClasses() {
		// given
		TargetApplicationRunner runner = new TargetApplicationRunner();
		GlobalSettings settings = mock(GlobalSettings.class);
		ExecutableCommandFactory executableCommandFactory = mock(ExecutableCommandFactory.class);
		ProgressEventFactory progressEventFactory = mock(ProgressEventFactory.class);
		ProgressEventQueue progressEventQueue = mock(ProgressEventQueue.class);
		ArgumentCaptor<ProgressEventObserver> argument = ArgumentCaptor.forClass(ProgressEventObserver.class);

		when(progressEventFactory.getEventsToReply())
				.thenReturn(new ArrayList<ProgressEvent>(asList(new ProgressEvent("a", ""))));

		when(settings.getTargetJavaVersion()).thenReturn("1.6.0");
		when(settings.getCurrentJavaVersion()).thenReturn("1.5.0");

		// when
		runner.run(settings, executableCommandFactory, progressEventFactory, progressEventQueue);

		// then
		verify(progressEventQueue, atLeast(4)).registerObserver(argument.capture());
		List<ProgressEventObserver> allValues = argument.getAllValues();
		List<Class> allClass = convertToListOfClasses(allValues);
		assertThat(allClass)
				.containsAll(asList(TargetApplicationRunner.class, TargetJVMRunner.class, JREDownloader.class, JarDownloader.class));
	}

	private List<Class> convertToListOfClasses(List<ProgressEventObserver> allValues) {
		List<Class> allClass = new ArrayList<Class>();
		for (ProgressEventObserver value : allValues) {
			allClass.add(value.getClass());
		}
		return allClass;
	}

	@Test
	public void shouldReplayNextEventWhenJREDownloadedEvent() {
		// given
		List<String> jarURLs = new ArrayList<String>();
		jarURLs.add("jar1");
		jarURLs.add("jar2");
		GlobalSettings settings = GlobalSettings.builder()
				.targetJavaVersion("1.5.0")
				.currentJavaVersion("1.5.0")
				.jarURLs(jarURLs)
				.build();
		ProgressEventFactory progressEventFactory = new ProgressEventFactory();
		progressEventFactory.init(settings);
		ProgressEventQueue progressEventQueue = mock(ProgressEventQueue.class);
		TargetApplicationRunner runner = new TargetApplicationRunner();

		runner.run(settings, null, progressEventFactory, progressEventQueue);

		// when
		runner.update(progressEventFactory.getJREDownloadedEvent());

		// then
		verify(progressEventQueue).update(progressEventFactory.getJREUnzipEvent());
	}

	@Test
	public void shouldReplayNextEventWhenJREUnzippedEvent() {
		// given
		List<String> jarURLs = new ArrayList<String>();
		jarURLs.add("jar1");
		jarURLs.add("jar2");
		GlobalSettings settings = GlobalSettings.builder()
				.targetJavaVersion("1.5.0")
				.currentJavaVersion("1.5.0")
				.jarURLs(jarURLs)
				.build();
		ProgressEventFactory progressEventFactory = new ProgressEventFactory();
		progressEventFactory.init(settings);
		ProgressEventQueue progressEventQueue = mock(ProgressEventQueue.class);
		TargetApplicationRunner runner = new TargetApplicationRunner();

		ProgressEvent expectedNextEvent = progressEventFactory.getEventsToReply().get(0);

		runner.run(settings, null, progressEventFactory, progressEventQueue);

		// when
		runner.update(progressEventFactory.getJREUnzippedEvent());

		// then
		verify(progressEventQueue).update(expectedNextEvent);
	}

	@Test
	public void shouldReplayNextEventWhenDownloadJarFinishEvent() {
		// given
		List<String> jarURLs = new ArrayList<String>();
		jarURLs.add("jar1");
		jarURLs.add("jar2");
		GlobalSettings settings = GlobalSettings.builder()
				.targetJavaVersion("1.5.0")
				.currentJavaVersion("1.5.0")
				.jarURLs(jarURLs)
				.build();
		ProgressEventFactory progressEventFactory = new ProgressEventFactory();
		progressEventFactory.init(settings);
		ProgressEventQueue progressEventQueue = mock(ProgressEventQueue.class);
		TargetApplicationRunner runner = new TargetApplicationRunner();

		ProgressEvent expectedNextEvent = progressEventFactory.getEventsToReply().get(0);

		runner.run(settings, null, progressEventFactory, progressEventQueue);

		// when
		runner.update(progressEventFactory.getDownloadJarFinishEvent("jar1"));

		// then
		verify(progressEventQueue).update(expectedNextEvent);
	}
}