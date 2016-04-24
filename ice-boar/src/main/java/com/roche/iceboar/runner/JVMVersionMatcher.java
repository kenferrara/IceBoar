package com.roche.iceboar.runner;

import com.roche.iceboar.IceBoarException;
import com.roche.iceboar.settings.GlobalSettings;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java has linux based versioning convention
 * <p>
 * eg. 1.4.2_04 we decode as:
 * <p>
 * 1 - Major version
 * 4 - Minor
 * 2 - Micro
 * 04 - release number
 * <p>
 * User: koziolek
 */
public class JVMVersionMatcher {

    public boolean match(GlobalSettings settings) {
        if(StringUtils.isBlank(settings.getTargetJavaVersion())) {
            return false;
        }
        Version currentJavaVersion = new Version(settings.getCurrentJavaVersion());
        List<Version> targetJavaVersions = parseTarget(settings.getTargetJavaVersion());

        for (Version target : targetJavaVersions) {
            if (currentJavaVersion.equals(target)) {
                return true;
            }
        }

        return false;
    }

    private List<Version> parseTarget(String targetDesc) {
        if (targetDesc == null || targetDesc.trim().length() == 0) {
            throw new IceBoarException("You should define target JVM version", null);
        }

        String[] byComa = targetDesc.split(",");
        List<Version> versions = new ArrayList<Version>();
        for (String elem : byComa) {
            String[] bySpace = elem.trim().split(" ");
            for (String ver : bySpace) {
                versions.add(new Version(ver));
            }
        }
        return versions;
    }


    private static class Version {
        private static final Pattern VERSION_PATTERN = Pattern
                .compile("(\\d)+\\.(\\d)+\\.?(\\d)?(_\\d)?|(\\d)+\\.(\\d)+\\+");
        String major, minor, micro, release;
        boolean minimum;

        public Version(String version) {
            if (version == null || version.trim().length() == 0) {
                throw new IceBoarException("Current Java Version number cannot be null", null);
            }
            Matcher matcher = VERSION_PATTERN.matcher(version);
            matcher.find();
            major = matcher.group(1);
            minor = matcher.group(2);
            micro = matcher.group(3);
            release = matcher.group(4);
            minimum = version.contains("+");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Version)) {
                return false;
            }

            Version version = (Version) o;

            if (major != null ? !major.equals(version.major) : version.major != null) {
                return false;
            }
            return isMinorEqualOrGreater(version) && isMicroEqual(version) && isReleaseEqual(version);

        }

        private boolean isMinorEqualOrGreater(Version that) {
            if (minor != null && that.minor != null) {
                return this.minor.compareTo(that.minor) >= 0;
            } else if (minor != null) {
                return true;
            } else if (that.minor == null) {
                return true;
            }
            return false;
        }

        private boolean isMicroEqual(Version that) {
            if (micro != null && that.micro != null) {
                return this.micro.equals(that.micro);
            } else if (micro != null) {
                return true;
            } else if (that.micro == null) {
                return true;
            }
            return false || minimum;
        }

        private boolean isReleaseEqual(Version that) {
            if (release != null && that.release != null) {
                return this.release.equals(that.release);
            } else if (release != null) {
                return true;
            } else if (that.release == null) {
                return true;
            }
            return false || minimum;
        }

        @Override
        public int hashCode() {
            int result = major != null ? major.hashCode() : 0;
            result = 31 * result + (minor != null ? minor.hashCode() : 0);
            result = 31 * result + (micro != null ? micro.hashCode() : 0);
            result = 31 * result + (release != null ? release.hashCode() : 0);
            return result;
        }
    }
}
