package com.zy.github.multiple.cache.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author z
 * @date 2020/11/27 15:16
 */
public class HostUtil {


    public static String getHostName() {
        if (System.getenv("COMPUTERNAME") != null) {
            return System.getenv("COMPUTERNAME");
        } else {
            return getHostNameForLiunx();
        }
    }


    private static String getHostNameForLiunx() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException unknown) {
            String host = unknown.getMessage();
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UNKNOWNHOST";
        }
    }
}
