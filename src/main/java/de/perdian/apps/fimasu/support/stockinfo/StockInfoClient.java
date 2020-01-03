package de.perdian.apps.fimasu.support.stockinfo;

import java.util.ServiceLoader;

public class StockInfoClient {

    public static StockInfo findStockInfoByWkn(String wkn) {
        return StockInfoClient.resolveStockInfoProvider().findStockInfoByWkn(wkn);
    }

    public static StockInfo findStockInfoByIsin(String isin) {
        return StockInfoClient.resolveStockInfoProvider().findStockInfoByIsin(isin);
    }

    private static StockInfoProvider resolveStockInfoProvider() {
        return ServiceLoader.load(StockInfoProvider.class)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Cannot find StockInfoProvider"));
    }

}
