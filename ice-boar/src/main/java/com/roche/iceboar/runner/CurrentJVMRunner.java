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

import com.roche.iceboar.progressevent.ProgressEvent;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.progressview.ProgressUpdater;
import com.roche.iceboar.settings.GlobalSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Runs a target user application on current installed Java version on user machine. For now this case isn't full
 * implemented.
 */
public class CurrentJVMRunner implements JVMRunner {

	private final ProgressEventFactory progressEventFactory;
	private ProgressUpdater progress;
	private GlobalSettings settings;
	private ExecutableCommandFactory executableCommandFactory;
	private ProgressEventQueue progressEventQueue;

	public CurrentJVMRunner(ProgressUpdater progress, GlobalSettings settings, ExecutableCommandFactory executableCommandFactory,
	                        ProgressEventFactory progressEventFactory, ProgressEventQueue progressEventQueue) {
		this.progress = progress;
		this.settings = settings;
		this.executableCommandFactory = executableCommandFactory;
		this.progressEventFactory = progressEventFactory;
		this.progressEventQueue = progressEventQueue;
	}

	public void runOnJVM() {
		runMainClass();
	}

	private void runMainClass() {
		ExecutableCommand command = executableCommandFactory.createRunTargetApplicationCommand(
				settings, settings.getCurrentJvmPath());

		Process process = command.exec();

		redirectProcessOutputsToDebugWindow(process);
		progressEventQueue.update(progressEventFactory.getAppStartedEvent());
	}

	private void redirectProcessOutputsToDebugWindow(final Process process) {
		startOutputReaderThread(process);
		startErrorReaderThread(process);
	}

	private void startOutputReaderThread(final Process process) {
		Thread thread1 = new Thread(new Runnable() {
			public void run() {
				BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while (true) {
					try {
						if (inputReader.ready()) {
							line = inputReader.readLine();
							if (line != null) {
								System.out.println("Process input: " + line);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread1.start();
	}

	private void startErrorReaderThread(final Process process) {
		Thread thread2 = new Thread(new Runnable() {
			public void run() {
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line;
				while (true) {
					try {
						if (errorReader.ready()) {
							line = errorReader.readLine();
							if (line != null) {
								System.out.println("Process error: " + line);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread2.start();
	}

	public void update(ProgressEvent event) {
		if (event.equals(progressEventFactory.getAppStartingEvent())) {
			runOnJVM();
		}
	}
}
