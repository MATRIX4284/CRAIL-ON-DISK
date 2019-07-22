package org.apache.crail.yarn;


import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Utilities related to both Alluxio paths like {@link AlluxioURI} and local file paths.
 */
@ThreadSafe
public final class PathUtils {
    private static final String TEMPORARY_SUFFIX_FORMAT = ".alluxio.0x%016X.tmp";
    private static final int TEMPORARY_SUFFIX_LENGTH =
            String.format(TEMPORARY_SUFFIX_FORMAT, 0).length();
    public static final String SEPARATOR = "/";



    public static String concatPath(Object base, Object... paths) throws IllegalArgumentException {
        Preconditions.checkArgument(base != null, "Failed to concatPath: base is null");
        Preconditions.checkArgument(paths != null, "Failed to concatPath: a null set of paths");
        List<String> trimmedPathList = new ArrayList<>();
        String trimmedBase =
                CharMatcher.is(SEPARATOR.charAt(0)).trimTrailingFrom(base.toString());
        trimmedPathList.add(trimmedBase);
        for (Object path : paths) {
            if (path == null) {
                continue;
            }
            String trimmedPath =
                    CharMatcher.is(SEPARATOR.charAt(0)).trimFrom(path.toString());
            if (!trimmedPath.isEmpty()) {
                trimmedPathList.add(trimmedPath);
            }
        }
        if (trimmedPathList.size() == 1 && trimmedBase.isEmpty()) {
            // base must be "[/]+"
            return SEPARATOR;
        }
        return Joiner.on(SEPARATOR).join(trimmedPathList);

    }



    private PathUtils() {} // prevent instantiation
}
