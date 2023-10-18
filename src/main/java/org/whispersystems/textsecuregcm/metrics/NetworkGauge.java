package org.whispersystems.textsecuregcm.metrics;


import com.codahale.metrics.Gauge;
import org.whispersystems.textsecuregcm.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class NetworkGauge implements Gauge<Double> {

  protected Pair<Long, Long> getSentReceived() throws IOException {
    BufferedReader reader=null;
    try {
      File proc= new File("/proc/net/dev");
      reader = new BufferedReader(new FileReader(proc));
      String header = reader.readLine();
      String header2 = reader.readLine();

      long bytesSent = 0;
      long bytesReceived = 0;

      String interfaceStats;

      while ((interfaceStats = reader.readLine()) != null) {
        String[] stats = interfaceStats.split("\\s+");

        if (!stats[1].equals("lo:")) {
          bytesReceived += Long.parseLong(stats[2]);
          bytesSent += Long.parseLong(stats[10]);
        }
      }

      return new Pair<>(bytesSent, bytesReceived);
    }catch (Exception e){
      if(reader!=null){
        reader.close();
      }
    }finally {
      if(reader!=null){
        reader.close();
      }
    }
    return new Pair<>(0L,0L);
  }
}
