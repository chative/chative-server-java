package org.whispersystems.textsecuregcm.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.SharedMetricRegistries;
import io.dropwizard.lifecycle.Managed;
import org.whispersystems.textsecuregcm.directorynotify.DirectoryNotifyManager;
import org.whispersystems.textsecuregcm.util.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadPoolExecutor;

import static com.codahale.metrics.MetricRegistry.name;

public class LinuxDesk  {

    public static Desk getDeskUsage() {
        Desk desk = new Desk();
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("df -hl");// df -hl 查看硬盘空间
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String str = null;
                String[] strArray = null;
                int line = 0;
                while ((str = in.readLine()) != null) {
                    line++;
                    if (line != 2) {
                        continue;
                    }
                    int m = 0;
                    strArray = str.split(" ");
                    for (String para : strArray) {
                        if (para.trim().length() == 0)
                            continue;
                        ++m;
                        if (para.endsWith("G") || para.endsWith("Gi")) {
                            // 目前的服务器
                            if (m == 2) {
                                desk.setTotal(Double.parseDouble(para.replace("Gi","").replace("G","")));
                            }
                            if (m == 3) {
                                desk.setUsed(Double.parseDouble(para.replace("Gi","").replace("G","")));
                            }
                        }
                        if (para.endsWith("%")) {
                            if (m == 5) {
                                desk.setUseRate(Double.parseDouble(para.replace("%","")));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desk;
    }
 
    public static class Desk {
        private double total;
        private double used;
        private double useRate;
         
        public String toString(){
            return "总磁盘空间："+total+"，已使用："+used+"，使用率达："+useRate;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getUsed() {
            return used;
        }

        public void setUsed(double used) {
            this.used = used;
        }

        public double getUseRate() {
            return useRate;
        }

        public void setUseRate(double useRate) {
            this.useRate = useRate;
        }
    }

    public static void main(String[] args) {
        System.out.println(getDeskUsage());
//        打印结果，博主的电脑是mac，所以显示Gi
//        总磁盘空间：234Gi，已使用：100Gi，使用率达：44%
    }
}