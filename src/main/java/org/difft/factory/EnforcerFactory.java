package org.difft.factory;

import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.exception.CasbinNameNotExistException;
import org.casbin.jcasbin.main.SyncedEnforcer;
import org.casbin.jcasbin.model.Model;
import org.difft.service.UpdateCallBack;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.watch.RedisWatcher;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class EnforcerFactory implements InitializingBean {

    private final static ReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();

    public final static String MODEL = "[request_definition]\n" +
            "r = sub, dom, obj, act\n" +
            "\n" +
            "[policy_definition]\n" +
            "p = sub, dom, obj, act\n" +
            "\n" +
            "[role_definition]\n" +
            "g = _, _, _\n" +
            "\n" +
            "[policy_effect]\n" +
            "e = some(where (p.eft == allow))\n" +
            "\n" +
            "[matchers]\n" +
            "m = g(r.sub, p.sub, r.dom) && r.dom == p.dom && (r.obj == p.obj || keyMatch(r.obj, p.obj)) && r.act == p.act";


    public static SyncedEnforcer enforcer;

    private EnforcerConfig enforcerConfigProperties;

    static JDBCAdapter jdbcAdapter = null;

    static Model model = new Model();

    static RedisWatcher redisWatcher = null;

    public static SyncedEnforcer newEnforcer(){
        SyncedEnforcer newEnforcer =  new SyncedEnforcer(model, jdbcAdapter);
        newEnforcer.setWatcherEx(redisWatcher);
        return newEnforcer;
    }

    @Autowired
    public EnforcerFactory(EnforcerConfig enforcerConfigProperties) {
        this.enforcerConfigProperties = enforcerConfigProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Support for connecting to redis with timeout and password
        redisWatcher = new RedisWatcher(enforcerConfigProperties.getRedisHost(),
                enforcerConfigProperties.getRedisPort(),
                enforcerConfigProperties.getRedisTopic(),
                enforcerConfigProperties.getRedisTimeout(),
                StringUtils.isEmpty(enforcerConfigProperties.getRedisPassword())?(String)null: enforcerConfigProperties.getRedisPassword(),
                enforcerConfigProperties.getRedisDatabase(),
                enforcerConfigProperties.isRedisSsl());
        redisWatcher.setUpdateCallback(new UpdateCallBack());

        //从数据库读取策略
        jdbcAdapter = new JDBCAdapter(enforcerConfigProperties.getDbDriverClassName(),
                enforcerConfigProperties.getDbUrl(),
                enforcerConfigProperties.getDbUsername(),
                enforcerConfigProperties.getDbPassword());
        model.loadModelFromText(MODEL);
        enforcer = new SyncedEnforcer(model, jdbcAdapter);
        enforcer.setWatcherEx(redisWatcher);
//        enforcer.enableLog(false);
    }

    public static synchronized boolean enforceSync(boolean defaultVal, Object... rvals){
        try {
            return enforcer.enforce(rvals);
        } catch (Exception e){
            return defaultVal;
        }
    }

    /**
     * getUsersForRoleInDomain gets the users that a role has inside a domain.
     *
     * @param name the role.
     * @param domain the domain.
     * @return the users that the role has in the domain.
     */
    public static List<String> getUsersForRoleInDomain(String name, String domain){
        try {
            READ_WRITE_LOCK.readLock().lock();
            return enforcer.getModel().model.get("g").get("g").rm.getUsers(name, domain);
        } catch (CasbinNameNotExistException ignored) {

        } finally {
            READ_WRITE_LOCK.readLock().unlock();
        }
        return Collections.emptyList();
    }

    /**
     * getUsersForRoleInDomain gets the users that a role has inside a domain.
     *
     * @param name the role.
     * @param domain the domain.
     * @return the users that the role has in the domain.
     */
    public static List<List<String>> getAllPolicy(String name, String domain){
        try {
            READ_WRITE_LOCK.readLock().lock();
            return enforcer.getPolicy();
        } catch (CasbinNameNotExistException ignored) {

        } finally {
            READ_WRITE_LOCK.readLock().unlock();
        }
        return Collections.emptyList();
    }
//
//    /**
//     * 权限校验
//     *
//     * @param rvals 校验参数
//     * @return
//     */
//    public boolean enforce(Object... rvals) {
//        return enforcer.enforce(rvals);
//    }
//
//    /**
//     * 添加权限策略
//     *
//     * @param policy 策略参数
//     * @return 是否添加成功
//     */
//    public boolean addPolicy(Policy policy) {
//        return enforcer.addPolicy(policy.getSub(), policy.getObj(), policy.getAct());
//    }
//
//    /**
//     * 删除权限策略
//     *
//     * @param policy
//     * @return
//     */
//    public boolean removePolicy(Policy policy) {
//        return enforcer.removePolicy(policy.getSub(), policy.getObj(), policy.getAct());
//    }
//
//    /**
//     * 权限是否存在
//     *
//     * @param policy 权限参数
//     * @return 是否存在当前权限
//     */
//    public boolean hasPolicy(Policy policy) {
//        return enforcer.hasPolicy(policy.getSub(), policy.getObj(), policy.getAct());
//    }
//
//
//    /**
//     * 添加角色继承
//     *
//     * @param rSub 角色名
//     * @param pSub 权限名
//     * @return 是否继承
//     */
//    public boolean addRoleForUser(String rSub, String pSub) {
//        return enforcer.addRoleForUser(rSub,pSub);
//    }
//
//
//    /**
//     * 获取角色下的权限
//     * @param sub
//     * @return
//     */
//    public List<String> getRolesForPolicy(String sub) {
//        return enforcer.getRolesForUser(sub);
//    }
//
//    public List<String> getUsersForRole(String name){
//        return enforcer.getUsersForRole(name);
//    }
//
//    /**
//     * 移除角色权限
//     * @param rSub 角色名
//     * @param pSub 权限名
//     * @return 是否继承
//     */
//    public boolean deleteRoleForUser(String rSub,String pSub){
//        return enforcer.deleteRoleForUser(rSub, pSub);
//    }
//
//    public List<String> getAllSubjects(){
//        return enforcer.getAllSubjects();
//    }
//
//    public List<String> getAllObjects(){
//        return enforcer.getAllObjects();
//    }
//
//    public List<String> getAllRoles(){
//        return enforcer.getAllRoles();
//    }
//
//    public List<List<String>> getGroupingPolicy(){
//        return enforcer.getGroupingPolicy();
//    }
//
//
//    public List<List<String>> getPolicy(){
//        return enforcer.getPolicy();
//    }
//
//    /**
//     * 通过文件流取文件路径
//     * @param source
//     * @return
//     * @throws Exception
//     */
//    private static String loadKeyByResource(String source) throws Exception {
//        try {
//            Resource resource = new ClassPathResource(source);
//            InputStream in = resource.getInputStream();
//            InputStreamReader isReader= new InputStreamReader(in);
//            BufferedReader br = new BufferedReader(isReader);
//            String readLine = null;
//            StringBuilder sb = new StringBuilder();
//            while ((readLine = br.readLine()) != null) {
//                sb.append(readLine);
//                sb.append('\r');
//            }
//            br.close();
//            String ret=sb.toString();
//            return ret;
//        } catch (IOException e) {
//            throw new Exception("公、私钥数据读取错误");
//        } catch (NullPointerException e) {
//            throw new Exception("公、私钥输入流为空");
//        }
//    }
}
