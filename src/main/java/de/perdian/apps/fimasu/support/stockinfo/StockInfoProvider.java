package de.perdian.apps.fimasu.support.stockinfo;

import java.util.ServiceLoader;

public interface StockInfoProvider {

    public static StockInfoProvider resolveStockInfoProvider() {
        return ServiceLoader.load(StockInfoProvider.class)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find StockInfoProvider"));
    }

    StockInfo findStockInfoByWkn(String wkn);
    StockInfo findStockInfoByIsin(String isin);

}
