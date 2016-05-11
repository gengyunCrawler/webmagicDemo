package com.gy.wm.dbpipeline.impl;

import com.gy.wm.dbpipeline.DatabasePipeline;
import com.gy.wm.dbpipeline.dbclient.EsClient;
import com.gy.wm.model.CrawlData;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * Created by TianyuanPan on 5/9/16.
 */
public class EsPipeline implements DatabasePipeline {

    private EsClient esClient;


    public EsPipeline() {

        this.esClient = new EsClient();

    }


    @Override
    public int insertRecord(Object obj) {
        return 0;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {


        System.out.println("EsPipeline resultItems size: " + resultItems.getAll().size() +
                "\n\tTask uuid: " + task.getUUID());

        CrawlData crawlerData = resultItems.get("crawlerData");


        if (crawlerData != null) {

            this.esClient.add(crawlerData);
            int i = this.esClient.doSetInsert();
            System.out.println("doSetInser return code: " + i);
            return;
        }

        System.out.println("crawler data IS NULL !!!");

    }

}
