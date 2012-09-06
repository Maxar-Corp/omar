package org.ossim.omar.stager;

import java.util.concurrent.*;
import java.lang.Runnable;
import java.lang.Throwable;
/**
 * Created with IntelliJ IDEA.
 * User: gpotts
 * Date: 9/5/12
 * Time: 9:51 PM
 * To change this template use File | Settings | File Templates.
 */
class StagerThreadPoolExecutor extends ThreadPoolExecutor {
    ConcurrentHashMap concurrentMap;

    class StagerRunnable implements Runnable{
        private Runnable wrappedRunnable;
        private String id;
        public StagerRunnable(String id, Runnable wrappedRunnable)
        {
            this.id = id;
            this.wrappedRunnable = wrappedRunnable;
        }
        public void run()
        {

            wrappedRunnable.run();
        }
        String getId(){return id;}
    }
    public StagerThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
                                    long keepAliveTime,
                                    TimeUnit unit,
                                    BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        concurrentMap = new ConcurrentHashMap();
    }
    protected void afterExecute(Runnable r, Throwable t){
        if(r instanceof FutureTask)
        {
            def task = (FutureTask)r;
            concurrentMap.remove(task.get());
        }
    }
    public Future submit(String id, Runnable runnable){
        if(concurrentMap.containsKey(id))
        {
            return null;
        }
        concurrentMap.put(id,id);
        return super.submit(new StagerRunnable(id, runnable), id);
    }
}
