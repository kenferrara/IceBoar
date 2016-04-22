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

package com.roche.iceboar.settings;

import com.roche.iceboar.IceBoarException;
import com.roche.iceboar.cachestorage.CacheStatus;
import com.roche.iceboar.cachestorage.StatusInfo;
import com.roche.iceboar.downloader.FileUtilsFacade;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.roche.iceboar.downloader.FileUtilsFacade.extractFilenameFromURL;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * This class defines properties that can be set in JNLP file to configure behaviour of Ice Boar application,
 * ex. <br>
 * <pre class="code"><code class="xml">{@code <resources os="Windows" arch="x86">
 * <property name="jnlp.IceBoar.targetJavaVersion" value="1.6.0_15"/>
 * </resources>}</code></pre>
 */
public class GlobalSettings {

	/**
	 * <tt>{@value #JNLP_SHOW_DEBUG}</tt><br>
	 * If this property is set to <tt>true</tt> a debug JFrame is shown. It is not recommended to use it on
	 * production environment. Default set to <tt>false</tt>.
	 */
	public static final String JNLP_SHOW_DEBUG = "jnlp.IceBoar.showDebug";

	/**
	 * <tt>{@value #JNLP_FRAME_TITLE}</tt><br>
	 * A title of Progress frame. Default set to: {@link GlobalSettingsFactory#DEFAULT_FRAME_TITLE}.
	 */
	public static final String JNLP_FRAME_TITLE = "jnlp.IceBoar.frameTitle";

	/**
	 * <tt>{@value #JNLP_TARGET_JAVA_VERSION}</tt><br>
	 * This property defines a Java (JRE) Version. In plain JNLP (when you do not use ice-boar) this is
	 * configured by <tt>&lt;j2se&gt;</tt> tag, key: version, ex:<br>
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <resources os="Windows" arch="amd64">
	 * <j2se version="1.6+" ...>}
	 * </code></pre>
	 * It is highly recommend to set this to version the same like in &lt;j2se&gt; tag - if user doesn't have
	 * Java installed in defined or newest version, then  plain Java Web Start will download and install JRE in a
	 * version as specified (or newest).
	 */
	public static final String JNLP_TARGET_JAVA_VERSION = "jnlp.IceBoar.targetJavaVersion";

	/**
	 * <tt>{@value #JNLP_TARGET_JAVA_URL}</tt><br>
	 * This property defines a location of downloadable JRE ZIP file. The downloaded JRE must be in the same version
	 * as defined in {@link #JNLP_TARGET_JAVA_VERSION}.
	 */
	public static final String JNLP_TARGET_JAVA_URL = "jnlp.IceBoar.targetJavaURL";

	/**
	 * <tt>{@value #JNLP_JARS_PREFIX}</tt><br>
	 * This prefix is used to define properties that specify libraries needed. At least one dependency needs to be
	 * specified. In clear JNLP this properties correspond to:<br>
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <jar href="guava-XX.X.jar"/>
	 * <jar href="slf4j-api-X.X.X.jar"/>}
	 * </code></pre>
	 * You can't duplicate the property name. If you need to specify more than one entry, please create more
	 * properties that start from the same prefix e.g.
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <property name="jnlp.IceBoar.jar.0" value="guava-XX.X.jar"/>
	 * <property name="jnlp.IceBoar.jar.1" value="slf4j-api-X.X.X.jar"/>}
	 * </code></pre>
	 */
	public static final String JNLP_JARS_PREFIX = "jnlp.IceBoar.jar.";

	/**
	 * <tt>{@value #JNLP_MAIN_CLASS}</tt><br>
	 * This property defines the target main class. It is a actual code to be executed on a client machine and
	 * corresponds to the value of main-class property specified in <tt>&lt;application-desc&gt;</tt> tag, e.g.:<br>
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <application-desc main-class="com.roche.MainClass">}
	 * </code></pre>
	 */
	public static final String JNLP_MAIN_CLASS = "jnlp.IceBoar.main-class";

	/**
	 * <tt>{@value #JNLP_INITIAL_HEAP_SIZE}</tt>
	 * This property specifies an initial size of Java heap size to be used while executing the target application.
	 * It corresponds to initial-heap-size value in <tt>&lt;j2se&gt;</tt> tag, e.g.:<br>
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <j2se href="..." initial-heap-size="128m"/>}
	 * </code></pre>
	 */
	public static final String JNLP_INITIAL_HEAP_SIZE = "jnlp.IceBoar.initial-heap-size";

	/**
	 * <tt>{@value #JNLP_MAX_HEAP_SIZE}</tt>
	 * This property define a max size of Java heap for target application.
	 * It correspond to max-heap-size value in <tt>&lt;j2se&gt;</tt> tag, e.g.:<br>
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <j2se href="..." max-heap-size="1024m"/>}
	 * </code></pre>
	 */
	public static final String JNLP_MAX_HEAP_SIZE = "jnlp.IceBoar.max-heap-size";

	/**
	 * <tt>{@value #JNLP_JAVA_VM_ARGS}</tt>
	 * This property defines an additional set of standard and non-standard virtual machine arguments for the target
	 * application. It corresponds to java-vm-args value in <tt>&lt;j2se&gt;</tt> tag, e.g.:<br>
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <j2se href="..." java-vm-args="-XX:+UseParallelGC"/>}
	 * </code></pre>
	 */
	public static final String JNLP_JAVA_VM_ARGS = "jnlp.IceBoar.java-vm-args";

	/**
	 * <tt>{@value #JNLP_CLOSE_ON_END}</tt>
	 * This property defines a behaviour after finishing staring a target application. When set on true, all Ice Boar
	 * windows will be close, when false windows will be NOT close. The second approach is useful for debugging.
	 * Default is set to true.
	 */
	public static final String JNLP_CLOSE_ON_END = "jnlp.IceBoar.close-on-end";

	/**
	 * <tt>{@value #JNLP_ICONS_PREFIX}</tt><br>
	 * This prefix is used to define properties that specify icons for JFrame with progressbar. In clear JNLP this
	 * properties correspond to:<br>
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <icon href="images/image.jpg"/>}
	 * </code></pre>
	 * You can't duplicate the property name. If you need to specify more than one entry (e.g. for different sizes
	 * like 16x16, 32x32, 64x64, 128x128), please create more properties that start from the same prefix e.g.
	 * <pre class="code"><code class="xml">
	 * {@code
	 * <property name="jnlp.IceBoar.icon.0" value="images/icon-128x128.png"/>
	 * <property name="jnlp.IceBoar.icon.1" value="images/icon-64x64.png"/>}
	 * </code></pre>
	 *
	 * @since 0.6
	 */
	public static final String JNLP_ICONS_PREFIX = "jnlp.IceBoar.icon.";

	/**
	 * <tt>{@value #JNLP_SPLASH_SCREEN}</tt><br>
	 * Define your custom splash screen. If file will be not found a default frame will be showed.
	 *
	 * @since 0.7
	 */
	public static final String JNLP_SPLASH_SCREEN = "jnlp.IceBoar.splash";

	/**
	 * <tt>{@value #JNLP_SPLASH_HIDE_FRAME_BORDER}</tt><br>
	 * Hide a frame border. A JFrame will be undecorated. Default is set to false.
	 *
	 * @since 0.7
	 */
	public static final String JNLP_SPLASH_HIDE_FRAME_BORDER = "jnlp.IceBoar.hide-frame-border";

	/**
	 * <tt>{@value #JNLP_ALWAYS_RUN_ON_PREPARED_JVM}</tt><br>
	 * Enforce always download JVM even if current JVM match to required target version.
	 *
	 * @since 0.8
	 */
	public static final String JNLP_ALWAYS_RUN_ON_PREPARED_JVM = "jnlp.IceBoar.alwaysRunOnPreparedJVM";

	/**
	 * <tt>{@value #JNLPX_JVM}</tt><br>
	 * Location of java runtime that were used to run current JVM.
	 *
	 * @since 0.8
	 */
	public static final String JNLPX_JVM = "jnlpx.jvm";

	private List<String> applicationArguments;
	private long jvmStartTime;
	private boolean showDebug;
	private String frameTitle;
	private String targetJavaVersion;
	private String currentJavaVersion;
	private String targetJavaURL;
	private String tempDirectory;
	private List<String> jarURLs;
	private String mainClass;
	private List<String> allPropertiesForTarget;
	private String operationSystemName;
	private String pathSeparator;
	private String initialHeapSize;
	private String maxHeapSize;
	private String javaVmArgs;
	private String cachePath;
	private CacheStatus cacheStatus;
	private boolean closeOnEnd;
	private List<String> icons;
	private String customSplashImage;
	private boolean hideFrameBorder;
	private boolean alwaysRunOnPreparedJVM;
	private String currentJvmPath;


	/**
	 * Arguments that come from JNLP file and should be propagated to the destination JAR.
	 */
	private GlobalSettings(String[] applicationArguments) {
		if (applicationArguments == null || applicationArguments.length == 0) {
			this.applicationArguments = new ArrayList<String>();
		} else {
			this.applicationArguments = Arrays.asList(applicationArguments);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public boolean isShowDebug() {
		return showDebug;
	}

	public String getFrameTitle() {
		return frameTitle;
	}

	public String getTargetJavaVersion() {
		return targetJavaVersion;
	}

	public String getCurrentJavaVersion() {
		return currentJavaVersion;
	}

	public String getTargetJavaURL() {
		return targetJavaURL;
	}

	public String getTempDirectory() {
		if (tempDirectory == null) {
			throw new IceBoarException("User temp directory is not defined!", null);
		}
		return tempDirectory;
	}

	public String getDestinationJREPath() {
		String fileName = targetJavaURL.substring(targetJavaURL.lastIndexOf('/') + 1);
		return getTempDirectory() + fileName;
	}

	public String getDestinationJARsPath() {
		return getTempDirectory() + "IceBoar_" + jvmStartTime;
	}

	public String getUnzipPath() {
		String tempDirectory = getTempDirectory();
		String fileNameWithoutExtension = targetJavaURL
				.substring(targetJavaURL.lastIndexOf('/') + 1, targetJavaURL.lastIndexOf('.'));
		return tempDirectory + fileNameWithoutExtension + "_" + jvmStartTime;
	}

	public String getUnzipJavaCommandPath() {
		return FileUtilsFacade.addJavaCommandPathToPath(getUnzipPath());
	}

	public List<String> getApplicationArguments() {
		return applicationArguments;
	}

	public String getApplicationArgumentsAsString() {
		return join(applicationArguments, " ");
	}

	public List<String> getJarURLs() {
		return jarURLs;
	}


	public String getMainClass() {
		return mainClass;
	}

	public String getClassPathAsText() {
		List<String> jars = new ArrayList<String>();
		for (String jarURL : jarURLs) {
			jars.add(getDestinationPathForJar(jarURL));
		}
		return join(jars, getPathSeparator());
	}

	public String getDestinationPathForJar(String jarUrl) {
		return getDestinationJARsPath() + File.separator + extractFilenameFromURL(jarUrl);
	}

	public List<String> getAllPropertiesForTarget() {
		return allPropertiesForTarget;
	}

	public String getAllPropertiesForTargetAsText() {
		return join(allPropertiesForTarget, " ");
	}

	public String getOperationSystemName() {
		return operationSystemName;
	}

	public boolean isOperationSystemMacOSX() {
		return getOperationSystemName().equals("Mac OS X");
	}

	public String getPathSeparator() {
		return pathSeparator;
	}

	public String getInitialHeapSize() {
		if (isNotBlank(initialHeapSize)) {
			return "-Xms" + initialHeapSize;
		}
		return "";
	}

	public String getMaxHeapSize() {
		if (isNotBlank(maxHeapSize)) {
			return "-Xmx" + maxHeapSize;
		}
		return "";
	}

	public List<String> getJavaVmArgs() {
		if (javaVmArgs == null || javaVmArgs.isEmpty()) {
			return new ArrayList<String>();
		}
		List<String> result = Arrays.asList(javaVmArgs.split(" "));
		return result;
	}

	public String getJavaVmArgsAsText() {
		if (isNotBlank(javaVmArgs)) {
			return javaVmArgs;
		}
		return "";
	}

	public String getCachePath() {
		return cachePath;
	}

	public CacheStatus getCacheStatus() {
		return cacheStatus;
	}

	public String getDestinationJreZipPathFromCache() {
		CacheStatus cacheStatus = getCacheStatus();
		StatusInfo statusInfo = cacheStatus.getJreDownloadedStatusInfo(getTargetJavaVersion());
		if (statusInfo == null || statusInfo.getPath() == null) {
			return "";
		}
		return statusInfo.getPath();
	}

	public String getUnzippedJrePathFromCache() {
		CacheStatus cacheStatus = getCacheStatus();
		StatusInfo statusInfo = cacheStatus.getJreUnzippedStatusInfo(getTargetJavaVersion());
		if (statusInfo == null || statusInfo.getPath() == null) {
			return "";
		}
		return statusInfo.getPath();
	}

	public boolean isCloseOnEnd() {
		return closeOnEnd;
	}

	public List<String> getIcons() {
		return icons;
	}

	public String getCustomSplashImage() {
		return customSplashImage;
	}

	public boolean isHideFrameBorder() {
		return hideFrameBorder;
	}

	public boolean isAlwaysRunOnPreparedJVM() {
		return alwaysRunOnPreparedJVM;
	}

	public String getCurrentJvmPath() {
		return currentJvmPath;
	}

	public static class Builder {
		private String[] applicationArguments = new String[]{};
		private long jvmStartTime;
		private boolean showDebug;
		private String frameTitle;
		private String targetJavaVersion;
		private String currentJavaVersion;
		private String targetJavaURL;
		private String tempDirectory;
		private List<String> jarURLs;
		private String mainClass;
		private List<String> allPropertiesForTarget;
		private String operationSystemName;
		private String pathSeparator;
		private String initialHeapSize;
		private String maxHeapSize;
		private String javaVmArgs;
		private String cachePath;
		private CacheStatus cacheStatus;
		private boolean closeOnEnd;
		private List<String> icons;
		private String splashScreen;
		private boolean hideFrameBorder;
		private boolean alwaysRunOnPreparedJVM;
		private String currentJvmPath;

		public Builder applicationArguments(String[] applicationArguments) {
			if (applicationArguments != null) {
				this.applicationArguments = applicationArguments.clone();
			}
			return this;
		}

		public Builder jvmStartTime(long jvmStartTime) {
			this.jvmStartTime = jvmStartTime;
			return this;
		}

		public Builder frameTitle(String frameTitle) {
			this.frameTitle = frameTitle;
			return this;
		}

		public Builder showDebug(boolean showDebug) {
			this.showDebug = showDebug;
			return this;
		}

		public Builder targetJavaVersion(String targetJavaVersion) {
			this.targetJavaVersion = targetJavaVersion;
			return this;
		}

		public Builder currentJavaVersion(String currentJavaVersion) {
			this.currentJavaVersion = currentJavaVersion;
			return this;
		}

		public Builder targetJavaURL(String targetJavaURL) {
			this.targetJavaURL = targetJavaURL;
			return this;
		}

		public Builder tempDirectory(String tempDirectory) {
			if (tempDirectory.endsWith(File.separator)) {
				this.tempDirectory = tempDirectory;
			} else {
				this.tempDirectory = tempDirectory + File.separator;
			}
			return this;
		}

		public Builder jarURLs(List<String> jarURLs) {
			this.jarURLs = jarURLs;
			return this;
		}

		public Builder mainClass(String mainClass) {
			this.mainClass = mainClass;
			return this;
		}

		public Builder allPropertiesForTarget(List<String> allPropertiesForTarget) {
			this.allPropertiesForTarget = allPropertiesForTarget;
			return this;
		}

		public Builder operationSystemName(String operationSystemName) {
			this.operationSystemName = operationSystemName;
			return this;
		}

		public Builder pathSeparator(String pathSeparator) {
			this.pathSeparator = pathSeparator;
			return this;
		}

		public Builder initialHeapSize(String initialHeapSize) {
			this.initialHeapSize = initialHeapSize;
			return this;
		}

		public Builder maxHeapSize(String maxHeapSize) {
			this.maxHeapSize = maxHeapSize;
			return this;
		}

		public Builder javaVmArgs(String javaVmArgs) {
			this.javaVmArgs = javaVmArgs;
			return this;
		}

		public Builder closeOnEnd(boolean closeOnEnd) {
			this.closeOnEnd = closeOnEnd;
			return this;
		}

		public Builder cachePath(String cachePath) {
			this.cachePath = cachePath;
			return this;
		}

		public Builder cacheStatus(CacheStatus cacheStatus) {
			this.cacheStatus = cacheStatus;
			return this;
		}

		public Builder icons(List<String> icons) {
			this.icons = icons;
			return this;
		}

		public Builder customSplashImage(String image) {
			this.splashScreen = image;
			return this;
		}

		public Builder hideFrameBorder(boolean hideFrameBorder) {
			this.hideFrameBorder = hideFrameBorder;
			return this;
		}

		public Builder alwaysRunOnPreparedJVM(boolean alwaysRunOnPreparedJVM) {
			this.alwaysRunOnPreparedJVM = alwaysRunOnPreparedJVM;
			return this;
		}

		public Builder currentJvmPath(String currentJvmPath) {
			this.currentJvmPath = currentJvmPath;
			return this;
		}

		public GlobalSettings build() {
			GlobalSettings settings = new GlobalSettings(applicationArguments);
			settings.jvmStartTime = jvmStartTime;
			settings.showDebug = showDebug;
			settings.frameTitle = frameTitle;
			settings.targetJavaVersion = targetJavaVersion;
			settings.currentJavaVersion = currentJavaVersion;
			settings.targetJavaURL = targetJavaURL;
			settings.tempDirectory = tempDirectory;
			settings.jarURLs = jarURLs;
			settings.mainClass = mainClass;
			settings.allPropertiesForTarget = allPropertiesForTarget;
			settings.operationSystemName = operationSystemName;
			settings.pathSeparator = pathSeparator;
			settings.initialHeapSize = initialHeapSize;
			settings.maxHeapSize = maxHeapSize;
			settings.javaVmArgs = javaVmArgs;
			settings.closeOnEnd = closeOnEnd;
			settings.cachePath = cachePath;
			settings.cacheStatus = cacheStatus;
			settings.icons = icons;
			settings.customSplashImage = splashScreen;
			settings.hideFrameBorder = hideFrameBorder;
			settings.alwaysRunOnPreparedJVM = alwaysRunOnPreparedJVM;
			settings.currentJvmPath = currentJvmPath;
			return settings;
		}
	}
}
