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

import com.roche.iceboar.IceBoarException;
import com.roche.iceboar.progressevent.ProgressEventFactory;
import com.roche.iceboar.progressevent.ProgressEventQueue;
import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class JVMRunnerFactoryTest {

	@Test(expectedExceptions = IceBoarException.class)
	public void shouldThrowExceptionWhenTargetJVMIsNotDefined() {
		// given
		JVMRunnerFactory factory = new JVMRunnerFactory();

		// should fail
		factory.create(GlobalSettings.builder().currentJavaVersion("1.6")
						.build(), mock(ExecutableCommandFactory.class),
				mock(ProgressEventFactory.class), mock(ProgressEventQueue.class));

	}

	@Test
	public void shouldCreateTargetJVMRunnerWhenTargetVersionEqualsCurrentVersion() {
		// given
		JVMRunnerFactory factory = new JVMRunnerFactory();
		GlobalSettings settings = GlobalSettings.builder()
				.targetJavaVersion("1.6")
				.currentJavaVersion("1.6")
				.alwaysRunOnPreparedJVM(true)
				.build();
		// when
		JVMRunner runner = factory
				.create(settings, mock(ExecutableCommandFactory.class), mock(ProgressEventFactory.class),
						mock(ProgressEventQueue.class));

		// then
		assertThat(runner).isInstanceOf(TargetJVMRunner.class);
	}

	@Test
	public void shouldCreateCurrentJVMRunnerWhenTargetVersionEqualsCurrentVersion() {
		// given
		JVMRunnerFactory factory = new JVMRunnerFactory();
		GlobalSettings settings = GlobalSettings.builder()
				.targetJavaVersion("1.6")
				.currentJavaVersion("1.6")
				.alwaysRunOnPreparedJVM(false)
				.build();
		// when
		JVMRunner runner = factory
				.create(settings, mock(ExecutableCommandFactory.class), mock(ProgressEventFactory.class),
						mock(ProgressEventQueue.class));

		// then
		assertThat(runner).isInstanceOf(CurrentJVMRunner.class);
	}

	@Test
	public void shouldRunOnTargetJavaVersionWhenTargetVersionNotEqualsCurrentVersion() {
		// given
		JVMRunnerFactory factory = new JVMRunnerFactory();
		GlobalSettings settings = GlobalSettings.builder()
				.targetJavaVersion("1.6")
				.currentJavaVersion("1.7")
				.alwaysRunOnPreparedJVM(true)
				.build();
		// when
		JVMRunner runner = factory
				.create(settings, mock(ExecutableCommandFactory.class), mock(ProgressEventFactory.class),
						mock(ProgressEventQueue.class));

		// then
		assertThat(runner).isInstanceOf(TargetJVMRunner.class);
	}

	@Test
	public void shouldRunOnTargetJavaVersionWhenTargetVersionNotEqualsCurrentVersionWhenDownloadFlagIsFalse() {
		// given
		JVMRunnerFactory factory = new JVMRunnerFactory();
		GlobalSettings settings = GlobalSettings.builder()
				.targetJavaVersion("1.7")
				.currentJavaVersion("1.6")
				.alwaysRunOnPreparedJVM(false)
				.build();
		// when
		JVMRunner runner = factory
				.create(settings, mock(ExecutableCommandFactory.class), mock(ProgressEventFactory.class),
						mock(ProgressEventQueue.class));

		// then
		assertThat(runner).isInstanceOf(TargetJVMRunner.class);
	}

}