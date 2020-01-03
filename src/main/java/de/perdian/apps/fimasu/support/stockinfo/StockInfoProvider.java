package de.perdian.apps.fimasu.support.stockinfo;

public interface StockInfoProvider {

    StockInfo findStockInfoByWkn(String wkn);
    StockInfo findStockInfoByIsin(String isin);

}
