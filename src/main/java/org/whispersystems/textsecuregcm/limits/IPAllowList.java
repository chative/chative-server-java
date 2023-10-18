package org.whispersystems.textsecuregcm.limits;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class IPAllowList {
    private Set<String> localAddresses = new HashSet<String>();

    public IPAllowList()  {
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements())
            {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//                System.out.println(netInterface.getName());
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements())
                {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address)
                    {
                        localAddresses.add(ip.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
//            throw new ServletException("Unable to lookup local addresses");
        }
    }
    public void LocalOnly(HttpServletRequest request) {
        if (!localAddresses.contains(request.getRemoteAddr())) {
            throw new WebApplicationException(Response.status(404).build());
        }
    }

    public static void main(String[] args) throws SocketException {
        IPAllowList ipAllowList=new IPAllowList();
        for(String str:ipAllowList.localAddresses){
            System.out.println(str);
        }
    }
}
