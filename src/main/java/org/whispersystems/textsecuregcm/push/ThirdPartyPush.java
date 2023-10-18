package org.whispersystems.textsecuregcm.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.MemCache;
import org.whispersystems.textsecuregcm.util.RandomString;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public class ThirdPartyPush {
    public final static String CMD_REFRESHDIRECTORY = "VThOAi8UNzV9S6nUeVZE5vujW5i3XA3k";

    private static ThirdPartyPush instance = null;

    private final Logger logger = LoggerFactory.getLogger(PushSender.class);
    private final AccountsManager accountsManager;

    private RandomString mRandomString = new RandomString(32);

    private LinkedBlockingQueue<ArrayList<String>> mQueue = new LinkedBlockingQueue<>();
    private ThirdPartyPushThread mThreadPush = null;

    public ThirdPartyPush(
            AccountsManager accountsManager,
            MemCache memCache
    ) {
        this.accountsManager = accountsManager;
        this.mThreadPush = new ThirdPartyPushThread("threadPush", mQueue, memCache);
        this.mThreadPush.start();
    }

    public static ThirdPartyPush getInstance(
            AccountsManager accountsManager,
            MemCache memCache
    ) {
        if (null == instance) {
            instance = new ThirdPartyPush(
                accountsManager,
                    memCache
            );
        }
        return instance;
    }

    public String getType(Account account) {
        if (account!=null) {
            return account.getPushType();
        }

        return null;
    }

    public String getToken(Account account) {
        if (account!=null) {
            return account.getPushToken();
        }

        return null;
    }


    public String getType(String addr) {
        Optional<Account> account = accountsManager.get(addr);
        if (account.isPresent()) {
            return account.get().getPushType();
        }

        return null;
    }

    public String getToken(String addr) {
        Optional<Account> account = accountsManager.get(addr);
        if (account.isPresent()) {
            return account.get().getPushToken();
        }

        return null;
    }

    public void setToken(String addr, String type, String token) {
        Optional<Account> account = accountsManager.get(addr);
        if (account.isPresent()) {
            account.get().setPushType(type);
            account.get().setPushToken(token);
            accountsManager.update(account.get(),null,false);
        }
    }

    public String newToken(String addr, String type) {
        String token;
        if (type.equals("xiaomi")) {
            token = mRandomString.nextString();
        } else {
            return null;
        }
        setToken(addr, type, token);
        return token;
    }

    public void push(String addr, String title, String content) {
        String type = getType(addr);
        String token = getToken(addr);
        if (null == type || null == token || null == title || null == content) {
            return;
        }
        pushToToken(type, token, title, content);
    }

    public void push(Account account, String title, String content) {
        String type = getType(account);
        String token = getToken(account);
        if (null == type || null == token || null == title || null == content) {
            return;
        }
        pushToToken(type, token, title, content);
    }

    private void pushToToken(String type, String token, String title, String content) {
        if (null == type || null == token || null == title || null == content) {
            return;
        }

        ArrayList<String> task = new ArrayList<>();
        task.add(type);
        task.add(token);
        task.add(title);
        task.add(content);
        logger.debug("third party push: type: " + type + ", token: " + token + ", title: " + title + ", content: " + content);
        try {
            mQueue.put(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
