package org.whispersystems.textsecuregcm.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.entities.GiveInteractResponse;

import java.util.*;

import static com.codahale.metrics.MetricRegistry.name;

public class InteractManager {

  private final Logger logger = LoggerFactory.getLogger(InteractManager.class);
  private MemCache memCache;
  private final InteractsTable interactsTable;
  private final AccountsManager accountsManager;

  public InteractManager(InteractsTable interactsTable, MemCache memCache,AccountsManager accountsManager){
    this.interactsTable=interactsTable;
    this.memCache=memCache;
    this.accountsManager=accountsManager;
  }

  public InteractMem store(Interact interact) {
    long xmax=interactsTable.insert(interact);
    boolean add=false;
    if(xmax==0) {
      add=true;
    }
    return updateMem(interact,add);
  }

  private InteractMem updateMem(Interact interact,boolean add){
    String interactKey=getAccountInteractKey(interact.getNumber(),interact.getType());
    InteractMem interactMem= (InteractMem) memCache.get(interactKey,InteractMem.class);
    if(interactMem==null) {
      long count = interactsTable.queryCountByNumber(interact.getNumber());
      if(count>0) {
        List<Interact> interacts = interactsTable.queryByNumber(interact.getNumber());
        LinkedList<InteractMem.InteractMemSource> lastSource = new LinkedList<>();
        for (Interact interactTemp : interacts) {
          Optional<Account> accountOptional = accountsManager.get(interactTemp.getSource());
          if (accountOptional.isPresent() && !accountOptional.get().isinValid()) {
            lastSource.add(new InteractMem.InteractMemSource(interactTemp.getSource(), accountOptional.get().getPublicName(accountOptional.get().getPlainName())));
          }
        }
        interactMem = new InteractMem(count, lastSource);
        memCache.remove(interactKey);
      }
    }else{
      if(interact!=null){
        Optional<Account> accountOptional=accountsManager.get(interact.getSource());
        if(accountOptional.isPresent()&&!accountOptional.get().isinValid()) {
          if(add) {
            interactMem.setThumbsUpCount(interactMem.thumbsUpCount + 1);
          }
          interactMem.addSource(new InteractMem.InteractMemSource(interact.getSource(), accountOptional.get().getPublicName(accountOptional.get().getPlainName())));
          memCache.remove(interactKey);
        }
      }
    }
    return interactMem;
  }


  public GiveInteractResponse get(String number,Integer type) {
    String interactKey=getAccountInteractKey(number,type);
    InteractMem interactMem= (InteractMem) memCache.get(interactKey,InteractMem.class);
    if(interactMem==null) {
      long count = interactsTable.queryCountByNumber(number);
      if(count>0) {
        List<Interact> interacts = interactsTable.queryByNumber(number);
        LinkedList<InteractMem.InteractMemSource> lastSource = new LinkedList<>();
        for (Interact interactTemp : interacts) {
          Optional<Account> accountOptional = accountsManager.get(interactTemp.getSource());
          if (accountOptional.isPresent() && !accountOptional.get().isinValid()) {
            lastSource.add(new InteractMem.InteractMemSource(interactTemp.getSource(), accountOptional.get().getPublicName(accountOptional.get().getPlainName())));
          }
        }
        interactMem = new InteractMem(count, lastSource);
        memCache.set(interactKey, interactMem);
      }
    }
    if(interactMem!=null){
      return new GiveInteractResponse(interactMem.getThumbsUpCount(),interactMem.getLastSource());
    }
    return null;
  }

  private String getAccountInteractCountKey(String number,Integer type) {
    return String.join("_", Interact.class.getSimpleName(),"Interact", String.valueOf(Interact.MEMCACHE_VERION),type+"",number,"count");
  }
  private String getAccountInteractKey(String number,Integer type) {
    return String.join("_", Interact.class.getSimpleName(),"Interact", String.valueOf(Interact.MEMCACHE_VERION),type+"",number);
  }

}
