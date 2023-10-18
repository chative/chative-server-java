package org.whispersystems.textsecuregcm.controllers;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.textsecuregcm.distributedlock.DistributedLock;
import org.whispersystems.textsecuregcm.distributedlock.FairLock;
import org.whispersystems.textsecuregcm.storage.CountDemoRow;
import org.whispersystems.textsecuregcm.storage.CountDemoTable;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

@Path("/v1/count")
@Produces(MediaType.APPLICATION_JSON)
public class CountDemoController {
    static private final Logger logger = LoggerFactory.getLogger(DistributedLock.class);

    private final CountDemoTable mCntTable;
    public final static AtomicInteger mIntCount = new AtomicInteger(0);

    public CountDemoController() { // 必须要一个无参数的构造函数
        mCntTable = null;
    }

    ;

    public CountDemoController(CountDemoTable mCntTable) {
        this.mCntTable = mCntTable;
//        mCntTable.init();
    }


    @Timed
    @PUT
    @Path("/anntest/{v}")
    @FairLock(names = {"locker-name"})
    public CountDemoRow increaseAnnTest(@PathParam("v") int v) {
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
        mIntCount.addAndGet(v); //

        CountDemoRow old = get();
        mCntTable.update(old.getCount() + v);
        CountDemoRow row = get(); // 返回实际的
        if (row.getCount() != mIntCount.get()) {
            logger.error("atomic mIntCount {},not equals {} count in db ",mIntCount.get(), row.getCount());
        }
        return row;
    }

    @Timed
    @PUT
    @Path("/{v}")
    public CountDemoRow increase(@PathParam("v") int v) {
        // 根据业务获取锁
        Lock locker = DistributedLock.getLocker(new String[]{"locker-name"});
        try {
            // 对临界区加锁
            locker.lock();
//            try {
//                Thread.sleep(15000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            CountDemoRow old = get();
            mCntTable.update(old.getCount() + v);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { //
                locker.unlock();
            } catch (Exception e) {
//                e.printStackTrace();
            }

        }
        return get(); // 返回实际的
    }

    @GET
    public CountDemoRow get() {
        List<CountDemoRow> res = mCntTable.get();
        if (res.isEmpty()) {
            return new CountDemoRow();
        }
        return res.get(0);
    }

    @GET
    @Path("/atomic")
    public int getAtomic() {
     return mIntCount.get();
    }

}
