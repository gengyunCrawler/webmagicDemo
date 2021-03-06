package com.gy.wm.service;

import com.gy.wm.dbpipeline.impl.MysqlPipeline;
import com.gy.wm.entry.ConfigLoader;
import com.gy.wm.model.CrawlData;
import com.gy.wm.parser.analysis.BaseTemplate;
import com.gy.wm.parser.analysis.TextAnalysis;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 网页抓取器。
 * @author yinlei
 * 2014-3-5 下午4:27:51
 */
public class ColumnPageProcessor implements PageProcessor {
    public List<String> unfetchCrawlQueue = new ArrayList<>();
    private List<CrawlData> crawlDataList = new ArrayList<>();
    List<BaseTemplate> baseTemplates = new ArrayList<>();
    public List<CrawlData> getCrawlDataList() {
        return crawlDataList;
    }

    public void setCrawlDataList(List<CrawlData> crawlDataList) {
        this.crawlDataList = crawlDataList;
    }

    private  Queue<String> linkQueue = new LinkedList<>();
    private TextAnalysis textAnalysis = new TextAnalysis(new ConfigLoader().loadTemplateConfig());

    private Site site = Site.me().setDomain("http://www.gog.cn/").setRetryTimes(3).setSleepTime(1000);

    public ColumnPageProcessor(List<CrawlData> crawlDataList,List<BaseTemplate> baseTemplates)   {
        this.crawlDataList = crawlDataList;
        this.baseTemplates = baseTemplates;
    }

    @Override
    public void process(Page page) {

        String url = page.getRequest().getUrl();
        String html = page.getHtml().toString();
        CrawlData beginCralwerData = initCrawlData(url,html);
        crawlDataList.add(beginCralwerData);

        List<CrawlData> perPageCrawlDateList = new TextAnalysis(baseTemplates).analysisHtml(beginCralwerData);
        for(CrawlData crawlData : perPageCrawlDateList)    {
            if(crawlData.isFetched() == false)  {
                page.addTargetRequest(crawlData.getUrl());
            }else {
                crawlDataList.add(crawlData);
            }
        }

        page.putField("crawlerDataList", crawlDataList);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static  CrawlData initCrawlData(String url,String html)    {
        CrawlData crawlData = new CrawlData();
        crawlData.setUrl(url);
        crawlData.setHtml(html);
        crawlData.setFetched(false);
        return crawlData;
    }


    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader();
        List<String> seedsList = configLoader.loadSeedConfig();
        List<BaseTemplate> baseTemplates = configLoader.loadTemplateConfig();
        List<CrawlData> crawlDataList = new ArrayList<>();
        for(String url : seedsList) {
            crawlDataList.add(initCrawlData(url,""));
        }
        for(String seed:seedsList) {
            Spider.create(new ColumnPageProcessor(crawlDataList,baseTemplates))
                    //从seed开始抓
                    .addUrl(seed).addPipeline(new MysqlPipeline("tb_crawler"))
                            //开启5个线程抓取
                    .thread(5)
                            //启动爬虫
                    .run();
        }


        for(CrawlData crawlData : crawlDataList)    {
            if(crawlData.isFetched())   {
//                System.out.println(crawlData.getUrl() + "\n" + "title:" + crawlData.getTitle());
            }
        }

    }
}