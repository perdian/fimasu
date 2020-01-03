package de.perdian.apps.fimasu.support.stockinfo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StockInfo implements Serializable {

    static final long serialVersionUID = 1L;

    private String wkn = null;
    private String isin = null;
    private String title = null;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getWkn() {
        return this.wkn;
    }
    public void setWkn(String wkn) {
        this.wkn = wkn;
    }

    public String getIsin() {
        return this.isin;
    }
    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

}
