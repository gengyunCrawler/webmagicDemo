package com.gy.wm.dbpipeline;

import com.gy.wm.dbpipeline.dbclient.DBClient;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by TianyuanPan on 5/4/16.
 */
public interface DatabasePipeline<T> extends Pipeline {

    public int insertRecord(T obj);
}
