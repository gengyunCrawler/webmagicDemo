package com.gy.wm.dbpipeline.dbclient;

import com.gy.wm.model.CrawlData;
import com.gy.wm.util.ConfigUtils;
import com.gy.wm.util.CrawlerDataUtils;
import com.gy.wm.util.HbasePoolUtils;
import com.gy.wm.util.RandomUtils;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by TianyuanPan on 5/18/16.
 */
public class HbaseClient extends AbstractDBClient {


    private String tableName;
    private String columnFamilyName;
    private HConnection connection;
    private HTableInterface myTable;


    private List<CrawlData> dataList;

    public HbaseClient() {

        this.dataList = new ArrayList<>();
        this.tableName = ConfigUtils.getResourceBundle().getString("HBASE_TABLE_NAME");
        this.columnFamilyName = ConfigUtils.getResourceBundle().getString("HBASE_COLUMNFAMILY_NAME");
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnFamilyName() {
        return columnFamilyName;
    }

    @Override
    public Object getConnection() {
        return null;
    }

    @Override
    public void closeConnection() {

    }

    @Override
    public int doSetInsert() {
        int i = 0;

        for (CrawlData o : dataList) {

            try {
                i += this.insertRecord(tableName, RandomUtils.getRandomString(50) + "_" + new Date().getTime(), columnFamilyName, o);
            } catch (Exception ex) {
                logger.warn("HbaseClient doSetInsert Exception!!! Message: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        this.dataList.clear();

        return i;
    }

    @Override
    public boolean isConnOpen() {
        return false;
    }


    public void add(CrawlData data) {

        this.dataList.add(data);
    }


    public synchronized int insertRecord(String tableName, String rowKey, String columnFamilyName, CrawlData data) {

        String columnQualifier = null;
        String value = null;
        String type = null;


        try {
            this.connection = HbasePoolUtils.getHConnection();
            myTable = this.connection.getTable(tableName);
        } catch (Exception ex) {
            logger.error("Hbase get connection Error or get table Error!!, Message: " + ex.getMessage());
            ex.printStackTrace();
        }


        Put put = new Put(Bytes.toBytes(rowKey));// 设置rowkey

        CrawlerDataUtils utils = CrawlerDataUtils.getCrawlerDataUtils(data);

        List<Map<String, Object>> myDataList = utils.getAttributeInfoList();
        try {

            for (Map<String, Object> o : myDataList) {
                try {
                    columnQualifier = o.get("name").toString();
                    type = o.get("type").toString();

                    switch (type) {

                        case "int":
                            value = Integer.toString((int) o.get("value"));
                            break;
                        case "long":
                            value = Long.toString((long) o.get("value"));
                            break;
                        case "boolean":
                            value = Boolean.toString((boolean) o.get("value"));
                            break;
                        default:
                            value = (String) o.get("value");
                            break;
                    }

                } catch (Exception ex) {

                    logger.warn("get data Exception! columnQualifier = " + columnQualifier + ", value = " + value);
                    ex.printStackTrace();

                }

                if (value == null)
                    value = "null";

                if (columnQualifier != null)
                    put.add(Bytes.toBytes(columnFamilyName),
                            Bytes.toBytes(columnQualifier),
                            Bytes.toBytes(value));
            }

            myTable.put(put);

        } catch (Exception ex) {

            try {
                myTable.close();
                this.connection.close();
            } catch (Exception exc) {
                logger.warn("Hbase table.close() or connection,close() error!!! Message: " + exc.getMessage());
                exc.printStackTrace();
            }

            logger.warn("HBase Put data Exception!!! Message: " + ex.getMessage());
            ex.printStackTrace();
            return 0;
        }


        try {

            myTable.close();
            this.connection.close();

        } catch (Exception ex) {
            logger.warn("Hbase table.close() or connection,close() error!!! Message: " + ex.getMessage());
            ex.printStackTrace();
        }
//        System.out.println("add data Success!");
//        logger.debug("Insert data Success!");

        return 1;
    }

}
