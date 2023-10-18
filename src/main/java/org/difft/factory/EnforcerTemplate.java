package org.difft.factory;

import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.main.SyncedEnforcer;
import org.casbin.jcasbin.model.Model;
import org.difft.domain.Policy;
import org.difft.service.UpdateCallBack;
import org.springframework.util.StringUtils;
import org.watch.RedisWatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnforcerTemplate {

    public static SyncedEnforcer getEnforcer(EnforcerConfig enforcerConfig){
        return getEnforcer(enforcerConfig, null);
    }

    public static SyncedEnforcer getEnforcer(EnforcerConfig enforcerConfigProperties, String MODEL){
        RedisWatcher redisWatcher = new RedisWatcher(enforcerConfigProperties.getRedisHost(),
                enforcerConfigProperties.getRedisPort(),
                enforcerConfigProperties.getRedisTopic(),
                enforcerConfigProperties.getRedisTimeout(),
                StringUtils.isEmpty(enforcerConfigProperties.getRedisPassword())?(String)null: enforcerConfigProperties.getRedisPassword(),
                enforcerConfigProperties.getRedisDatabase(),
                enforcerConfigProperties.isRedisSsl());
        redisWatcher.setUpdateCallback(new UpdateCallBack());

        //从数据库读取策略
        JDBCAdapter jdbcAdapter = null;
        try {
            jdbcAdapter = new JDBCAdapter(enforcerConfigProperties.getDbDriverClassName(),
                    enforcerConfigProperties.getDbUrl(),
                    enforcerConfigProperties.getDbUsername(),
                    enforcerConfigProperties.getDbPassword());
        } catch (Exception e) {

        }
        Model model = new Model();
        if(StringUtils.isEmpty(MODEL)){
            MODEL = EnforcerFactory.MODEL;
        }
        model.loadModelFromText(MODEL);
        SyncedEnforcer enforcer = new SyncedEnforcer(model, jdbcAdapter);
        enforcer.setWatcherEx(redisWatcher);
        return enforcer;
    }

    public static boolean hasPermission(Enforcer enforcer, String user, String domain, String policyKey){
        if(!user.startsWith("account:")){
            user = "account:"+user;
        }
        if(!domain.startsWith("domain:")){
            domain = "domain:"+domain;
        }
        // 自身策略优先
        List<List<String>> permissionsForUserInDomain = enforcer.getPermissionsForUserInDomain(user, domain);
        if(!permissionsForUserInDomain.isEmpty()){
            for(List<String> policies : permissionsForUserInDomain){
                String key = policies.get(2);
                if(!StringUtils.isEmpty(key) && key.equals(policyKey)){
                    String value = policies.get(3);
                    switch (value) {
                        case "1":
                            return true;
                        default:
                            return false;
                    }
                }
            }
        }
        // 直接team策略
        List<String> teams = enforcer.getRolesForUserInDomain(user, domain);
        if(teams.isEmpty()){
            return false;
        }
        List<List<String>> teamPolicies = new ArrayList<>();
        for(String team : teams){
            teamPolicies.addAll(enforcer.getPermissionsForUserInDomain(team, domain));
        }

        // 过滤出key相同的policy
        // value - team1, team2...
        Map<String, List<String>> collect = teamPolicies.stream().filter((List<String> list) -> list.get(2).equals(policyKey)).
                collect(Collectors.groupingBy(t -> t.get(3), Collectors.mapping(t2 -> t2.get(0), Collectors.toList())));
        // 判断0和1的两组team，只要1里有一个是0的子team，就返回1
        List<String> teams1 = collect.get("1");
        List<String> teams0 = collect.get("0");
        if(teams1.isEmpty() && !teams0.isEmpty()){
            return false;
        }
        if(!teams1.isEmpty() && teams0.isEmpty()){
            return true;
        }
        if(!teams1.isEmpty() && !teams0.isEmpty()){
            List<String> parentTeams = new ArrayList<>();
            for(String team : teams1){
                parentTeams.addAll(enforcer.getRolesForUserInDomain(team, domain));
            }
            parentTeams.removeIf(s ->
                    !teams0.contains(s)
            );
            if(!parentTeams.isEmpty()){
                return true;
            }
            return false;
        }


        // 再父策略
        List<List<String>> allPermissions = enforcer.getImplicitPermissionsForUserInDomain(user, domain);
        List<List<String>> allPolicies = allPermissions.stream().filter((List<String> list) -> list.get(2).equals(policyKey)).
                collect(Collectors.toList());
        if(!allPolicies.isEmpty()){
            for(List<String> policies : allPolicies){
                String key = policies.get(2);
                if(!StringUtils.isEmpty(key) && key.equals(policyKey)){
                    String value = policies.get(3);
                    switch (value) {
                        case "1":
                            return true;
                        default:
                            return false;
                    }
                }
            }
        }
        return false;
    }

}
