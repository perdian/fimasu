package de.perdian.apps.fimasu.support.quicken;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.perdian.apps.fimasu.support.quicken.model.AccountRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.BookedAmountRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.BookingDateRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.BudgetAmountRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.CategoryRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.CommissionRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.ConversionFactorRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.CurrencyRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.IsinRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.MarketPriceRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.MemoRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.NumberOfSharesRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.SecurityRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.TotalAmountRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.TransactionTypeRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.ValutaDateRecordItem;
import de.perdian.apps.fimasu.support.quicken.model.WknRecordItem;

public class Record {

    private BookingDateRecordItem bookingDate = null;
    private ValutaDateRecordItem valutaDate = null;
    private TransactionTypeRecordItem transactionType = null;
    private CurrencyRecordItem currency = new CurrencyRecordItem("EUR");
    private ConversionFactorRecordItem conversionFactor = null;
    private MemoRecordItem memo = null;
    private SecurityRecordItem security = null;
    private WknRecordItem wkn = null;
    private IsinRecordItem isin = null;
    private CategoryRecordItem category = new CategoryRecordItem();
    private MarketPriceRecordItem marketPrice = null;
    private NumberOfSharesRecordItem numberOfShares = new NumberOfSharesRecordItem(0);
    private TotalAmountRecordItem totalAmount = null;
    private CommissionRecordItem commission = null;
    private AccountRecordItem account = null;
    private BookedAmountRecordItem bookedAmount = null;
    private BudgetAmountRecordItem budgetAmount = null;

    public String toQifString() {
        List<RecordItem> recordItems = Arrays.asList(this.getBookingDate(), this.getValutaDate(), this.getTransactionType(), this.getCurrency(), this.getConversionFactor(), this.getMemo(), this.getSecurity(), this.getWkn(), this.getIsin(), this.getCategory(), this.getMarketPrice(), this.getNumberOfShares(), this.getTotalAmount(), this.getCommission(), this.getAccount(), this.getBookedAmount(), this.getBudgetAmount());
        StringBuilder result = new StringBuilder();
        for (RecordItem recordItem : recordItems) {
            String recordItemString = recordItem == null ? null : recordItem.toQifString();
            if (StringUtils.isNotEmpty(recordItemString)) {
                result.append(recordItemString).append("\n");
            }
        }
        return result.toString();
    }

    public BookingDateRecordItem getBookingDate() {
        return this.bookingDate;
    }
    public void setBookingDate(BookingDateRecordItem bookingDate) {
        this.bookingDate = bookingDate;
    }

    public ValutaDateRecordItem getValutaDate() {
        return this.valutaDate;
    }
    public void setValutaDate(ValutaDateRecordItem valutaDate) {
        this.valutaDate = valutaDate;
    }

    public TransactionTypeRecordItem getTransactionType() {
        return this.transactionType;
    }
    public void setTransactionType(TransactionTypeRecordItem transactionType) {
        this.transactionType = transactionType;
    }

    public CurrencyRecordItem getCurrency() {
        return this.currency;
    }
    public void setCurrency(CurrencyRecordItem currency) {
        this.currency = currency;
    }

    public ConversionFactorRecordItem getConversionFactor() {
        return this.conversionFactor;
    }
    public void setConversionFactor(ConversionFactorRecordItem conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public MemoRecordItem getMemo() {
        return this.memo;
    }
    public void setMemo(MemoRecordItem memo) {
        this.memo = memo;
    }

    public SecurityRecordItem getSecurity() {
        return this.security;
    }
    public void setSecurity(SecurityRecordItem security) {
        this.security = security;
    }

    public WknRecordItem getWkn() {
        return this.wkn;
    }
    public void setWkn(WknRecordItem wkn) {
        this.wkn = wkn;
    }

    public IsinRecordItem getIsin() {
        return this.isin;
    }
    public void setIsin(IsinRecordItem isin) {
        this.isin = isin;
    }

    public CategoryRecordItem getCategory() {
        return this.category;
    }
    public void setCategory(CategoryRecordItem category) {
        this.category = category;
    }

    public MarketPriceRecordItem getMarketPrice() {
        return this.marketPrice;
    }
    public void setMarketPrice(MarketPriceRecordItem marketPrice) {
        this.marketPrice = marketPrice;
    }

    public NumberOfSharesRecordItem getNumberOfShares() {
        return this.numberOfShares;
    }
    public void setNumberOfShares(NumberOfSharesRecordItem numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public TotalAmountRecordItem getTotalAmount() {
        return this.totalAmount;
    }
    public void setTotalAmount(TotalAmountRecordItem totalAmount) {
        this.totalAmount = totalAmount;
    }

    public CommissionRecordItem getCommission() {
        return this.commission;
    }
    public void setCommission(CommissionRecordItem commission) {
        this.commission = commission;
    }

    public AccountRecordItem getAccount() {
        return this.account;
    }
    public void setAccount(AccountRecordItem account) {
        this.account = account;
    }

    public BookedAmountRecordItem getBookedAmount() {
        return this.bookedAmount;
    }
    public void setBookedAmount(BookedAmountRecordItem bookedAmount) {
        this.bookedAmount = bookedAmount;
    }

    public BudgetAmountRecordItem getBudgetAmount() {
        return this.budgetAmount;
    }
    public void setBudgetAmount(BudgetAmountRecordItem budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

}
