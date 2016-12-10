package de.perdian.personal.stockimporter.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockModelFactory {

    private static final Logger log = LoggerFactory.getLogger(StockModelFactory.class);

    public static StockModel createStockModel() {
        StockModel stockModel = new StockModel();
        StockModelBean stockModelBean = StockModelFactory.loadStockModelBean();
        if (stockModelBean != null) {
            stockModel.buyingTimesProperty().addAll(stockModelBean.getBuyingTimes().stream().map(BuyingTimeBean::toBuyingTime).collect(Collectors.toList()));
        }
        stockModel.addChangeListener((x, oldValue, newValue) -> StockModelFactory.saveStockModel(stockModel));
        return stockModel;
    }

    private static StockModelBean loadStockModelBean() {
        File stockModelFile = StockModelFactory.resolveStockModelFile();
        if (stockModelFile.exists() && stockModelFile.length() > 0) {
            try {
                log.debug("Loading model from file at: {}", stockModelFile.getAbsolutePath());
                try (ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(stockModelFile)))) {
                    return (StockModelBean)objectStream.readObject();
                }
            } catch (Exception e) {
                log.warn("Cannot load model from file at: {}", stockModelFile.getAbsolutePath(), e);
            }
        }
        return null;
    }

    private static void saveStockModel(StockModel stockModel) {

        File targetFile = StockModelFactory.resolveStockModelFile();
        File targetDirectory = targetFile.getParentFile();
        if (!targetDirectory.exists()) {
            log.debug("Creating storage directory at: {}", targetDirectory.getAbsolutePath());
            targetDirectory.mkdirs();
        }

        log.debug("Writing model into file at: {}", targetFile.getAbsolutePath());
        try (ObjectOutputStream objectStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)))) {
            objectStream.writeObject(new StockModelBean(stockModel));
            objectStream.flush();
            log.debug("Completed writing model into file at: {}", targetFile.getAbsolutePath());
        } catch (Exception e) {
            log.warn("Cannot write model into file at: {}", targetFile.getAbsolutePath(), e);
        }

    }

    private static File resolveStockModelFile() {

        File userHomeDirectory = new File(System.getProperty("user.home"));
        File stockimporterDirectory = new File(userHomeDirectory, ".stockimporter/");
        File stockModelFile = new File(stockimporterDirectory, "model.object") ;

        return stockModelFile;

    }

    static class StockModelBean implements Serializable {

        static final long serialVersionUID = 1L;

        private List<BuyingTimeBean> buyingTimes = null;

        StockModelBean(StockModel stockModel) {
            this.setBuyingTimes(stockModel.buyingTimesProperty().stream().map(BuyingTimeBean::new).collect(Collectors.toList()));
        }

        List<BuyingTimeBean> getBuyingTimes() {
            return this.buyingTimes;
        }
        void setBuyingTimes(List<BuyingTimeBean> buyingTimes) {
            this.buyingTimes = buyingTimes;
        }

    }

    static class BuyingTimeBean implements Serializable {

        static final long serialVersionUID = 1L;

        private String title = null;
        private List<ShareBean> shares = null;

        BuyingTimeBean(BuyingTime buyingTime) {
            this.setTitle(buyingTime.titleProperty().getValue());
            this.setShares(buyingTime.sharesProperty().stream().map(ShareBean::new).collect(Collectors.toList()));
        }

        BuyingTime toBuyingTime() {
            BuyingTime buyingTime = new BuyingTime();
            buyingTime.titleProperty().setValue(this.getTitle());
            buyingTime.sharesProperty().addAll(this.getShares().stream().map(ShareBean::toShare).collect(Collectors.toList()));
            return buyingTime;
        }

        String getTitle() {
            return this.title;
        }
        void setTitle(String title) {
            this.title = title;
        }

        List<ShareBean> getShares() {
            return this.shares;
        }
        void setShares(List<ShareBean> shares) {
            this.shares = shares;
        }

    }

    static class ShareBean implements Serializable {

        static final long serialVersionUID = 1L;

        private String wkn = null;
        private String isin = null;
        private String title = null;
        private Double value = null;
        private Double discount = null;

        ShareBean(Share share) {
            this.setDiscount(share.discountProperty().getValue());
            this.setIsin(share.isinProperty().getValue());
            this.setTitle(share.titleProperty().getValue());
            this.setValue(share.valueProperty().getValue());
            this.setWkn(share.wknProperty().getValue());
        }

        Share toShare() {
            Share share = new Share();
            share.discountProperty().setValue(this.getDiscount());
            share.isinProperty().setValue(this.getIsin());
            share.titleProperty().setValue(this.getTitle());
            share.valueProperty().setValue(this.getValue());
            share.wknProperty().setValue(this.getWkn());
            return share;
        }

        String getWkn() {
            return this.wkn;
        }
        void setWkn(String wkn) {
            this.wkn = wkn;
        }

        String getIsin() {
            return this.isin;
        }
        void setIsin(String isin) {
            this.isin = isin;
        }

        String getTitle() {
            return this.title;
        }
        void setTitle(String title) {
            this.title = title;
        }

        Double getValue() {
            return this.value;
        }
        void setValue(Double value) {
            this.value = value;
        }

        Double getDiscount() {
            return this.discount;
        }
        void setDiscount(Double discount) {
            this.discount = discount;
        }

    }

}
