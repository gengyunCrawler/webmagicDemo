package com.gy.wm.dbpipeline.dbclient;

/**
 * Created by TianyuanPan on 5/4/16.
 */
public interface DBClient {

    public  Object  getConnection();
    public void closeConnection();
    public int  doSetInsert();
    public  boolean isConnOpen();
}
