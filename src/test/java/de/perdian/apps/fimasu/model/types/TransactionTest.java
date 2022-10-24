package de.perdian.apps.fimasu.model.types;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    @Test
    public void bookingDateUpdated_expectWednesdayFromMonday() {
        Transaction transaction = new Transaction();
        Assertions.assertNull(transaction.getValutaDate().getValue());

        transaction.getBookingDate().setValue(LocalDate.of(2022, 10, 24)); // Monday
        Assertions.assertEquals(LocalDate.of(2022, 10, 26), transaction.getValutaDate().getValue()); // Wednesday
    }

    @Test
    public void bookingDateUpdated_expectMondayFromThursday() {
        Transaction transaction = new Transaction();
        Assertions.assertNull(transaction.getValutaDate().getValue());

        transaction.getBookingDate().setValue(LocalDate.of(2022, 10, 27)); // Thursday
        Assertions.assertEquals(LocalDate.of(2022, 10, 31), transaction.getValutaDate().getValue()); // Monday
    }

    @Test
    public void bookingDateUpdated_expectMondayFromFriday() {
        Transaction transaction = new Transaction();
        Assertions.assertNull(transaction.getValutaDate().getValue());

        transaction.getBookingDate().setValue(LocalDate.of(2022, 10, 28)); // Friday
        Assertions.assertEquals(LocalDate.of(2022, 10, 31), transaction.getValutaDate().getValue()); // Monday
    }

    @Test
    public void bookingDateUpdated_expectNoOverwriteOfExistingValue() {
        Transaction transaction = new Transaction();
        transaction.getValutaDate().setValue(LocalDate.of(2022, 1, 1));

        transaction.getBookingDate().setValue(LocalDate.of(2022, 10, 28));
        Assertions.assertEquals(LocalDate.of(2022, 1, 1), transaction.getValutaDate().getValue());
    }

    @Test
    public void stockPricePerUnitUpdated_stockCountExisting() {
        Transaction transaction = new Transaction();
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));
        Assertions.assertNull(transaction.getStockValue().getValue());

        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(1.3d));
        Assertions.assertEquals(BigDecimal.valueOf(2.6d).setScale(5), transaction.getStockValue().getValue());
    }

    @Test
    public void stockPricePerUnitNulled_stockValueExisting() {
        Transaction transaction = new Transaction();
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(1.3d));
        Assertions.assertNotNull(transaction.getStockValue().getValue());

        transaction.getStockPricePerUnit().setValue(null);
        Assertions.assertNull(transaction.getStockValue().getValue());
    }

    @Test
    public void stockCountUpdated_PricePerUnitExisting() {
        Transaction transaction = new Transaction();
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(1.3d));
        Assertions.assertNull(transaction.getStockValue().getValue());

        transaction.getStockCount().setValue(BigDecimal.valueOf(2));
        Assertions.assertEquals(BigDecimal.valueOf(2.6d).setScale(5), transaction.getStockValue().getValue());
    }

    @Test
    public void stockCountNulled_stockValueExisting() {
        Transaction transaction = new Transaction();
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(1.3d));
        Assertions.assertNotNull(transaction.getStockValue().getValue());

        transaction.getStockCount().setValue(null);
        Assertions.assertNull(transaction.getStockValue().getValue());
    }

    @Test
    public void transactionTypeToBUY_expectPayoutValueCleared() {
        Transaction transaction = new Transaction();
        transaction.getPayoutValue().setValue(BigDecimal.valueOf(42));
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(3));
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));

        transaction.getType().setValue(TransactionType.BUY);
        Assertions.assertNull(transaction.getPayoutValue().getValue());
        Assertions.assertEquals(BigDecimal.valueOf(3), transaction.getStockPricePerUnit().getValue());
        Assertions.assertEquals(BigDecimal.valueOf(2), transaction.getStockCount().getValue());
    }

    @Test
    public void transactionTypeToSELL_expectPayoutValueCleared() {
        Transaction transaction = new Transaction();
        transaction.getPayoutValue().setValue(BigDecimal.valueOf(42));
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(3));
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));

        transaction.getType().setValue(TransactionType.SELL);
        Assertions.assertNull(transaction.getPayoutValue().getValue());
        Assertions.assertEquals(BigDecimal.valueOf(3), transaction.getStockPricePerUnit().getValue());
        Assertions.assertEquals(BigDecimal.valueOf(2), transaction.getStockCount().getValue());
    }

    @Test
    public void transactionTypeToPAYOUT_expectPayoutValueCleared() {
        Transaction transaction = new Transaction();
        transaction.getPayoutValue().setValue(BigDecimal.valueOf(42));
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(3));
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));

        transaction.getType().setValue(TransactionType.PAYOUT);
        Assertions.assertEquals(BigDecimal.valueOf(42), transaction.getPayoutValue().getValue());
        Assertions.assertNull(transaction.getStockPricePerUnit().getValue());
        Assertions.assertNull(transaction.getStockCount().getValue());
    }

    @Test
    public void bookingValueComputedFromBUY_sameCurrency() {
        Transaction transaction = new Transaction();
        transaction.getType().setValue(TransactionType.BUY);
        transaction.getStockCurrency().setValue("EUR");
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(3));
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));

        Assertions.assertEquals(BigDecimal.valueOf(6).setScale(5), transaction.getBookingValue().getValue());
    }

    @Test
    public void bookingValueComputedFromBUY_multipleCurrencies() {
        Transaction transaction = new Transaction();
        transaction.getType().setValue(TransactionType.BUY);
        transaction.getBookingCurrency().setValue("EUR");
        transaction.getBookingConversionRate().setValue(BigDecimal.valueOf(1.10d));
        transaction.getStockCurrency().setValue("USD");
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(3));
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));

        Assertions.assertEquals(BigDecimal.valueOf(6).setScale(5), transaction.getStockValue().getValue());
        Assertions.assertEquals(BigDecimal.valueOf(5.45455).setScale(5), transaction.getBookingValue().getValue());
    }

    @Test
    public void bookingValueComputedFromBUY_multipleCurrencies_noConversionRate() {
        Transaction transaction = new Transaction();
        transaction.getType().setValue(TransactionType.BUY);
        transaction.getBookingCurrency().setValue("EUR");
        transaction.getBookingConversionRate().setValue(null);
        transaction.getStockCurrency().setValue("USD");
        transaction.getStockPricePerUnit().setValue(BigDecimal.valueOf(3));
        transaction.getStockCount().setValue(BigDecimal.valueOf(2));

        Assertions.assertEquals(BigDecimal.valueOf(6).setScale(5), transaction.getStockValue().getValue());
        Assertions.assertNull(transaction.getBookingValue().getValue());
    }

}
