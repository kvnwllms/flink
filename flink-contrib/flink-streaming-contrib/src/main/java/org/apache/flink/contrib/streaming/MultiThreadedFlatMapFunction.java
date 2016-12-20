package org.apache.flink.contrib.streaming;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * Created by kewillia on 20/12/2016.
 */
public interface MultiThreadedFlatMapFunction<IN, OUT> extends FlatMapFunction<IN, OUT> {

	void flatMap(IN value, MultiThreadedCollector<OUT> out) throws Exception;
}
