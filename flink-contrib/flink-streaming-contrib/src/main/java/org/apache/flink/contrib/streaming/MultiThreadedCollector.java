package org.apache.flink.contrib.streaming;

import org.apache.flink.util.Collector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by kewillia on 20/12/2016.
 */
@Public
public class ThreadSafeCollector<T> implements Collector<T> {

	private ExecutorService cachedPool;
	private Collector<T> collector;

	public ThreadSafeCollector(Collector<T> _collector)
	{
		cachedPool = Executors.newCachedThreadPool();
		collector = _collector;
	}

	/*
	 * Emits a record
	 */
	private void collectSync(T record)
	{
		synchronized (collector) {
			collector.collect(record);
		}
	}

	/**
	 * Creates a task to emit a record.
	 *
	 * @param record The record to collect.
	 */
	@Override
	public void collect(T record) {

		class CollectRunnable implements Runnable {
			private T rec;

			CollectRunnable(T r)
			{
				this.rec = r;
			}

			@Override
			public void run () {
				collectSync(this.rec);
			}
		}

		cachedPool.execute(new CollectRunnable(record));
	}

	/**
	 * Waits for all Runs to complete before releasing all threads and closing the collector.
	 */
	@Override
	public void close() {
		cachedPool.shutdown();
		try {
			cachedPool.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			collector.close();
		}
	}
}
