package com.github.difftim.eslogger;

import java.lang.management.ManagementFactory;

public class ServerInfo {
    public static String getServerIP() {
        return ServerIP;
    }

    public static String getProcessID() {
        return ProcessID;
    }

    public static String getServiceName() {
        return ServiceName;
    }

    private static String ServerIP;
    private static String ProcessID;
    private static String ServiceName;

    static public void Init(String serverIP, String serviceName) {
        String name = ManagementFactory.getRuntimeMXBean().getName();
//        System.out.println(name);
        ProcessID = name.split("@")[0];
        ServerIP = serverIP;
        ServiceName = serviceName;
    }

}
