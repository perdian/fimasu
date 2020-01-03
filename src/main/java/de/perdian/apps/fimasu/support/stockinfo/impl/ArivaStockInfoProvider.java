package de.perdian.apps.fimasu.support.stockinfo.impl;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.fimasu.support.stockinfo.StockInfo;
import de.perdian.apps.fimasu.support.stockinfo.StockInfoProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ArivaStockInfoProvider implements StockInfoProvider {

    private static final Logger log = LoggerFactory.getLogger(ArivaStockInfoProvider.class);

    @Override
    public StockInfo findStockInfoByWkn(String wkn) {
        if (wkn == null || wkn.length() != 6) {
            return null;
        } else {
            return this.findStockInfo(wkn);
        }
    }

    @Override
    public StockInfo findStockInfoByIsin(String isin) {
        if (isin == null || isin.length() != 12) {
            return null;
        } else {
            return this.findStockInfo(isin);
        }
    }

    private StockInfo findStockInfo(String query) {
        try {
            OkHttpClient httpClient = new OkHttpClient.Builder().build();
            Request httpRequest = new Request.Builder().get().url("https://www.ariva.de/search/search.m?searchname=" + query).build();
            try (Response httpResponse = httpClient.newCall(httpRequest).execute()) {
                Document htmlDocument = Jsoup.parse(httpResponse.body().string());
                Element titleElement = htmlDocument.selectFirst("#pageSnapshotHeader h1 span[itemprop='name']");
                String title = titleElement == null ? null : titleElement.text().strip();
                Element wknAndIsinDiv = htmlDocument.selectFirst("div[wkn]");
                String wkn = wknAndIsinDiv == null ? null : wknAndIsinDiv.attr("wkn");
                String isin = wknAndIsinDiv == null ? null : wknAndIsinDiv.attr("isin");
                if (StringUtils.isNotEmpty(wkn) && StringUtils.isNotEmpty(isin)) {
                    StockInfo stockInfo = new StockInfo();
                    stockInfo.setIsin(isin);
                    stockInfo.setWkn(wkn);
                    stockInfo.setTitle(title);
                    return stockInfo;
                }
            }
        } catch (Exception e) {
            log.debug("Error occured while trying to fetch StockInfo from Ariva", e);
        }
        return null;
    }

}
