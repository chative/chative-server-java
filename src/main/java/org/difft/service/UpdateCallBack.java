package org.difft.service;

import com.google.gson.Gson;
import com.microsoft.sqlserver.jdbc.StringUtils;
import org.difft.factory.EnforcerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

public class UpdateCallBack implements Consumer {

    private Logger logger = LoggerFactory.getLogger(UpdateCallBack.class);

    @Override
    public void accept(Object o) {
        logger.info("casbin ex info, {}", o.toString());
        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(o.toString(), Map.class);
        String type = map.get("type");
        String ptype = map.get("ptype");
        if(StringUtils.isEmpty(type)){
            return;
        }
        if(type.equals("add")){
            if(ptype.equals("p")){
                EnforcerFactory.enforcer.addMemoryPolicy(map.get("v0"), map.get("v1"), map.get("v2"), map.get("v3"));
            } else if (ptype.equals("g")){
                EnforcerFactory.enforcer.addGroupingMemoryPolicy(map.get("v0"), map.get("v1"), map.get("v2"));
            }
        } else if(type.equals("remove")){
            if(ptype.equals("p")) {
                String[] params = new String[]{map.get("v0"), map.get("v1"), map.get("v2"), map.get("v3")};
                EnforcerFactory.enforcer.removeMemoryPolicy(Arrays.asList(params));
            } else if (ptype.equals("g")){
                String[] params = new String[]{map.get("v0"), map.get("v1"), map.get("v2")};
                EnforcerFactory.enforcer.removeGroupingMemoryPolicy(Arrays.asList(params));
            }
        }
    }
}
