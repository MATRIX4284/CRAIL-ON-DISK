package org.apache.crail.yarn;

import org.apache.crail.yarn.OSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Common network address related utilities shared by all components in Alluxio.
 */
@ThreadSafe
public final class NetworkAddressUtils {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkAddressUtils.class);

    public static final String WILDCARD_ADDRESS = "0.0.0.0";

    /**
     * Checks if the underlying OS is Windows.
     */
    public static final boolean WINDOWS = OSUtils.isWindows();

    private static String sLocalHost;
    private static String sLocalHostMetricName;
    private static String sLocalIP;

    private NetworkAddressUtils() {}

    /**
     * Different types of services that client uses to connect. These types also indicate the service
     * bind address.
     */

    private static boolean isValidAddress(InetAddress address, int timeoutMs) throws IOException {
        return !address.isAnyLocalAddress() && !address.isLinkLocalAddress()
                && !address.isLoopbackAddress() && address.isReachable(timeoutMs)
                && (address instanceof Inet4Address);
    }

    public static synchronized String getLocalIpAddress(int timeoutMs) {
        if (sLocalIP != null) {
            return sLocalIP;
        }

        try {
            InetAddress address = InetAddress.getLocalHost();
            LOG.debug("address: {} isLoopbackAddress: {}, with host {} {}", address,
                    address.isLoopbackAddress(), address.getHostAddress(), address.getHostName());

            // Make sure that the address is actually reachable since in some network configurations
            // it is possible for the InetAddress.getLocalHost() call to return a non-reachable
            // address e.g. a broadcast address
            if (!isValidAddress(address, timeoutMs)) {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

                // Make getNetworkInterfaces have the same order of network interfaces as listed on
                // unix-like systems. This optimization can help avoid to get some special addresses, such
                // as loopback address"127.0.0.1", virtual bridge address "192.168.122.1" as far as
                // possible.
                if (!WINDOWS) {
                    List<NetworkInterface> netIFs = Collections.list(networkInterfaces);
                    Collections.reverse(netIFs);
                    networkInterfaces = Collections.enumeration(netIFs);
                }

                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface ni = networkInterfaces.nextElement();
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        address = addresses.nextElement();

                        // Address must not be link local or loopback. And it must be reachable
                        if (isValidAddress(address, timeoutMs)) {
                            sLocalIP = address.getHostAddress();
                            return sLocalIP;
                        }
                    }
                }

                LOG.warn("Your hostname, {} resolves to a loopback/non-reachable address: {}, "
                                +  "but we couldn't find any external IP address!",
                        InetAddress.getLocalHost().getHostName(), address.getHostAddress());
            }

            sLocalIP = address.getHostAddress();
            return sLocalIP;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * Gets a local hostname for the host this JVM is running on.
     *
     * @return the local host name, which is not based on a loopback ip address
     */
    public static synchronized String getLocalHostName() {
        if (sLocalHost != null) {
            return sLocalHost;
        }
        int hostResolutionTimeout =
                2000;
        return getLocalHostName(hostResolutionTimeout);
    }

    /**
     * Gets a local host name for the host this JVM is running on.
     *
     * @param timeoutMs Timeout in milliseconds to use for checking that a possible local host is
     *        reachable
     * @return the local host name, which is not based on a loopback ip address
     */
    public static synchronized String getLocalHostName(int timeoutMs) {
        if (sLocalHost != null) {
            return sLocalHost;
        }

        try {
            sLocalHost = InetAddress.getByName(getLocalIpAddress(timeoutMs)).getCanonicalHostName();
            return sLocalHost;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a local hostname for the host this JVM is running on with '.' replaced with '_' for
     * metrics usage.
     *
     * @return the metrics system friendly local host name
     */

}

