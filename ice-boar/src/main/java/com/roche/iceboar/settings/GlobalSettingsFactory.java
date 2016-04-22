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
import com.roche.iceboar.cachestorage.LocalCacheStorage;
import org.apache.commons.lang3.StringUtils;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.roche.iceboar.settings.GlobalSettings.JNLP_ALWAYS_RUN_ON_PREPARED_JVM;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_CLOSE_ON_END;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_FRAME_TITLE;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_ICONS_PREFIX;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_INITIAL_HEAP_SIZE;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_JARS_PREFIX;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_JAVA_VM_ARGS;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_MAIN_CLASS;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_MAX_HEAP_SIZE;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_SHOW_DEBUG;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_SPLASH_HIDE_FRAME_BORDER;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_SPLASH_SCREEN;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_TARGET_JAVA_URL;
import static com.roche.iceboar.settings.GlobalSettings.JNLP_TARGET_JAVA_VERSION;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This class create an instance of {@link GlobalSettings} based on arguments from main(String [] args) method and
 * System properties.
 */
public class GlobalSettingsFactory {

	/**
	 * <tt>{@value #DEFAULT_FRAME_TITLE}</tt><br>
	 * Default Frame Title.
	 */
	public static final String DEFAULT_FRAME_TITLE = "Ice Boar";

	private static final String JAVA_VERSION = "java.version";
	private static final String OS_NAME = "os.name";
	private static final String TEMP_DIRECTORY = "java.io.tmpdir";
	private static final String JNLP_ICE_BOAR_PREFIX = "jnlp.IceBoar.";
	private static final String PATH_SEPARATOR = "path.separator";

	/**
	 * Reads system properties (incl. those specified in JNLP file).
	 *
	 * @param args
	 */
	public static GlobalSettings getGlobalSettings(String[] args) {
		long jvmStartTime = ManagementFactory.getRuntimeMXBean()
				.getStartTime();

		String codeBase = getCodeBase();

		Properties properties = System.getProperties();
		boolean showDebug = getShowDebug(properties);
		String frameTitle = properties.getProperty(JNLP_FRAME_TITLE, DEFAULT_FRAME_TITLE);
		String currentJavaVersion = properties.getProperty(JAVA_VERSION);
		String targetJavaVersion = getTargetJavaVersion(currentJavaVersion, properties);
		String tempDirectory = properties.getProperty(TEMP_DIRECTORY);
		String mainClass = properties.getProperty(JNLP_MAIN_CLASS);
		String targetJavaURL = getTargetJavaUrl(codeBase, properties);
		List<String> jarURLs = getDependenciesJars(codeBase, properties);
		List<String> allPropertiesForTarget = getAllPropertiesForTarget(properties);
		String initialHeapSize = properties.getProperty(JNLP_INITIAL_HEAP_SIZE);
		String maxHeapSize = properties.getProperty(JNLP_MAX_HEAP_SIZE);
		String javaVmArgs = properties.getProperty(JNLP_JAVA_VM_ARGS);
		String operationSystemName = properties.getProperty(OS_NAME);
		String pathSeparator = properties.getProperty(PATH_SEPARATOR);
		boolean closeOnEnd = getCloseOnEnd(properties);
		String cachePath = tempDirectory + ".IceBoar.cache";
		CacheStatus cacheStatus = getCacheStatus(cachePath);
		List<String> icons = getIcons(codeBase, properties);
		String splash = getSplashScreen(codeBase, properties);
		boolean hideFrameBorder = getHideFrameBorder(properties);
		boolean alwaysRunOnPreparedJVM = getAlwaysRunOnPreparedJVM(properties);

		GlobalSettings settings = GlobalSettings.builder()
				.applicationArguments(args)
				.jvmStartTime(jvmStartTime)
				.showDebug(showDebug)
				.frameTitle(frameTitle)
				.currentJavaVersion(currentJavaVersion)
				.targetJavaVersion(targetJavaVersion)
				.tempDirectory(tempDirectory)
				.mainClass(mainClass)
				.targetJavaURL(targetJavaURL)
				.jarURLs(jarURLs)
				.allPropertiesForTarget(allPropertiesForTarget)
				.operationSystemName(operationSystemName)
				.pathSeparator(pathSeparator)
				.initialHeapSize(initialHeapSize)
				.maxHeapSize(maxHeapSize)
				.javaVmArgs(javaVmArgs)
				.closeOnEnd(closeOnEnd)
				.cachePath(cachePath)
				.cacheStatus(cacheStatus)
				.icons(icons)
				.customSplashImage(splash)
				.hideFrameBorder(hideFrameBorder)
				.alwaysRunOnPreparedJVM(alwaysRunOnPreparedJVM)
				.build();
		return settings;
	}

	private static String getCodeBase() {
		String codeBase = "";
		try {
			codeBase = ((BasicService) ServiceManager.lookup("javax.jnlp.BasicService")).getCodeBase()
					.toString();
		} catch (UnavailableServiceException e) {
			System.out.println("BasicService is uninitialized. Do you started it from JNLP?");
		}
		return codeBase;
	}

	private static boolean getShowDebug(Properties properties) {
		String showDebugProperty = properties.getProperty(JNLP_SHOW_DEBUG);
		System.out.println("showDebug: " + showDebugProperty);
		return isNotBlank(showDebugProperty) && showDebugProperty.equals("true");
	}

	private static boolean getCloseOnEnd(Properties properties) {
		String closeOnEnd = properties.getProperty(JNLP_CLOSE_ON_END);
		return !(isNotBlank(closeOnEnd) && closeOnEnd.equals("false"));
	}

	private static String getTargetJavaVersion(String currentJavaVersion, Properties properties) {
		String targetJavaVersion;
		targetJavaVersion = properties.getProperty(JNLP_TARGET_JAVA_VERSION);
		if (targetJavaVersion == null) {
			System.out.println("A property " + JNLP_TARGET_JAVA_VERSION
					+ " is not defined. It is set to current Java Version: " + currentJavaVersion);
			targetJavaVersion = currentJavaVersion;
		}
		return targetJavaVersion;
	}

	private static String getTargetJavaUrl(String codeBase, Properties properties) {
		String targetJavaURL;
		targetJavaURL = properties.getProperty(JNLP_TARGET_JAVA_URL);
		if (targetJavaURL == null) {
			throw new IceBoarException("A property " + JNLP_TARGET_JAVA_URL + " is not defined. Please specify it in JNLP file!", null);
		}
		if (!isAbsolutePath(targetJavaURL) && StringUtils.isNotBlank(codeBase)) {
			targetJavaURL = codeBase + targetJavaURL;
		}
		return targetJavaURL;
	}

	private static List<String> getDependenciesJars(String codeBase, Properties properties) {
		List<String> jarURLs = readRemoteResourcesByPrefix(codeBase, properties, JNLP_JARS_PREFIX);
		if (jarURLs.isEmpty()) {
			throw new IceBoarException("Please specify minimum a one JAR file in property: " + JNLP_JARS_PREFIX +
					"0", null);
		}
		return jarURLs;
	}

	private static List<String> readRemoteResourcesByPrefix(String codeBase, Properties properties, String prefix) {
		List<String> urls = new ArrayList<String>();
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			Object key = entry.getKey();
			if (key instanceof String && ((String) key).startsWith(prefix)) {
				String urlText = getAbsoluteUrl(codeBase, entry.getValue().toString());
				urls.add(urlText);
			}
		}
		return urls;
	}

	private static String getAbsoluteUrl(String codeBase, String urlText) {
		if (!isAbsolutePath(urlText) && StringUtils.isNotBlank(codeBase)) {
			urlText = codeBase + urlText;
		}
		return urlText;
	}

	private static List<String> getAllPropertiesForTarget(Properties properties) {
		List<String> allPropertiesForTarget = new ArrayList<String>();
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			Object key = entry.getKey();
			if (key instanceof String) {
				String keyText = (String) key;
				if (keyText.startsWith("jnlp.") && !keyText.startsWith(JNLP_ICE_BOAR_PREFIX)) {
					allPropertiesForTarget.add("-D" + key + "=\"" + entry.getValue() + "\"");
				}
			}
		}
		return allPropertiesForTarget;
	}

	private static boolean isAbsolutePath(String urlText) {
		return urlText.contains("://");
	}

	private static CacheStatus getCacheStatus(String cachePath) {
		LocalCacheStorage localCacheStorage = new LocalCacheStorage();
		CacheStatus cacheStatus = localCacheStorage.loadCacheStatus(cachePath);
		return cacheStatus;
	}

	private static List<String> getIcons(String codeBase, Properties properties) {
		List<String> icons = readRemoteResourcesByPrefix(codeBase, properties, JNLP_ICONS_PREFIX);
		return icons;
	}

	private static String getSplashScreen(String codeBase, Properties properties) {
		return getAbsoluteUrl(codeBase, defaultIfNull((String) properties.get(JNLP_SPLASH_SCREEN), ""));
	}

	private static boolean getHideFrameBorder(Properties properties) {
		String hideFrameBorder = properties.getProperty(JNLP_SPLASH_HIDE_FRAME_BORDER);
		return (isNotBlank(hideFrameBorder) && hideFrameBorder.equals("true"));
	}

	private static boolean getAlwaysRunOnPreparedJVM(Properties properties) {
		String hideFrameBorder = properties.getProperty(JNLP_ALWAYS_RUN_ON_PREPARED_JVM);
		return (isNotBlank(hideFrameBorder) && hideFrameBorder.equals("true"));
	}

}
