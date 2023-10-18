//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.nurkiewicz.asyncretry;

import com.google.common.util.concurrent.*;
import com.nurkiewicz.asyncretry.function.RetryCallable;

public class AsyncRetryJob<V> extends RetryJob<V> {
    private final RetryCallable<ListenableFuture<V>> userTask;

    public AsyncRetryJob(RetryCallable<ListenableFuture<V>> userTask, AsyncRetryExecutor parent) {
        this(userTask, parent, new AsyncRetryContext(parent.getRetryPolicy()), SettableFuture.create());
    }

    public AsyncRetryJob(RetryCallable<ListenableFuture<V>> userTask, AsyncRetryExecutor parent, AsyncRetryContext context, SettableFuture<V> future) {
        super(context, parent, future);
        this.userTask = userTask;
    }

    public void run(final long startTime) {
        try {
            Futures.addCallback((ListenableFuture)this.userTask.call(this.context), new FutureCallback<V>() {
                public void onSuccess(V result) {
                    AsyncRetryJob.this.complete(result, System.currentTimeMillis() - startTime);
                }

                public void onFailure(Throwable throwable) {
                    AsyncRetryJob.this.handleThrowable(throwable, System.currentTimeMillis() - startTime);
                }
            }, MoreExecutors.directExecutor());
        } catch (Throwable var4) {
            this.handleThrowable(var4, System.currentTimeMillis() - startTime);
        }

    }

    protected RetryJob<V> nextTask(AsyncRetryContext nextRetryContext) {
        return new AsyncRetryJob(this.userTask, this.parent, nextRetryContext, this.future);
    }
}
