package com.ipid.demo.db;

import static com.ipid.demo.constants.Constants.CUSTOMER_ID_PREFIX;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ipid.demo.db.dao.BankAccountDao;
import com.ipid.demo.db.dao.CurrencyConversionDao;
import com.ipid.demo.db.dao.LookupBankDao;
import com.ipid.demo.db.dao.LookupCountryDao;
import com.ipid.demo.db.dao.LookupCurrencyDao;
import com.ipid.demo.db.dao.CustomerDao;
import com.ipid.demo.db.dao.NotificationDao;
import com.ipid.demo.db.dao.PaymentDao;
import com.ipid.demo.db.dao.PaymentDetailsDao;
import com.ipid.demo.db.entity.BankAccount;
import com.ipid.demo.db.entity.CurrencyConversion;
import com.ipid.demo.db.entity.Customer;
import com.ipid.demo.db.entity.LookupBank;
import com.ipid.demo.db.entity.LookupCountry;
import com.ipid.demo.db.entity.LookupCurrency;
import com.ipid.demo.db.entity.Notification;
import com.ipid.demo.db.entity.Payment;
import com.ipid.demo.db.entity.PaymentDetails;
import com.ipid.demo.utils.DateUtils;
import com.ipid.demo.utils.RandomUtils;

import java.util.List;

@Database(entities = {BankAccount.class, CurrencyConversion.class, Customer.class, LookupBank.class, LookupCountry.class, LookupCurrency.class, Notification.class, Payment.class, PaymentDetails.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BankAccountDao bankAccountDao();

    public abstract CurrencyConversionDao currencyConversionDao();

    public abstract CustomerDao customerDao();

    public abstract LookupBankDao lookupBankDao();

    public abstract LookupCountryDao lookupCountryDao();

    public abstract LookupCurrencyDao lookupCurrencyDao();

    public abstract NotificationDao notificationDao();

    public abstract PaymentDao paymentDao();

    public abstract PaymentDetailsDao paymentDetailsDao();

    private static AppDatabase INSTANCE;

    public synchronized static AppDatabase getDbInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "IPID_DEMO")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
            initializeData();
        }

        return INSTANCE;
    }

    private static void initializeData() {
        initializeCustomer();
        initializeBank();
        initializeBankAccount();
        initializeCountry();
        initializeCurrency();
        initializeCurrencyConversion();
    }

    private static void initializeCustomer() {
        Customer customer = INSTANCE.customerDao().findByFirstLastName("System", "System");

        if (customer == null) {
            // User for testing, this is to indicate that DB initialization has been completed
            customer = new Customer();
            customer.firstName = "System";
            customer.lastName = "System";
            customer.phoneNumber = "+65 1111 1111";
            customer.emailAddress = "system@gmail.com";
            customer.status = true; // Testing
            customer.password = "ipiddemo0701";
            customer.customerId = CUSTOMER_ID_PREFIX + RandomUtils.getRandomNumberString();
            INSTANCE.customerDao().insertCustomer(customer);

            customer = new Customer();
            customer.firstName = "Adrien";
            customer.lastName = "Baery";
            customer.phoneNumber = "+65 8432 4458";
            customer.emailAddress = "adrien.baery@gmail.com";
            customer.status = true;
            customer.password = "ipiddemo0701";
            customer.customerId = CUSTOMER_ID_PREFIX + RandomUtils.getRandomNumberString();
            INSTANCE.customerDao().insertCustomer(customer);

            customer = new Customer();
            customer.firstName = "Antoine";
            customer.lastName = "Wong";
            customer.phoneNumber = "+65 8220 1108";
            customer.emailAddress = "antoine.wong@gmail.com";
            customer.status = true;
            customer.password = "ipiddemo0701";
            customer.customerId = CUSTOMER_ID_PREFIX + RandomUtils.getRandomNumberString();
            INSTANCE.customerDao().insertCustomer(customer);

            customer = new Customer();
            customer.firstName = "Amie";
            customer.lastName = "Lee";
            customer.phoneNumber = "+65 9118 3646";
            customer.emailAddress = "amie.lee@gmail.com";
            customer.status = true;
            customer.password = "ipiddemo0701";
            customer.customerId = CUSTOMER_ID_PREFIX + RandomUtils.getRandomNumberString();
            INSTANCE.customerDao().insertCustomer(customer);

            customer = new Customer();
            customer.firstName = "David";
            customer.lastName = "Tan";
            customer.phoneNumber = "+65 8733 2155";
            customer.emailAddress = "david.tan@gmail.com";
            customer.status = true;
            customer.password = "ipiddemo0701";
            customer.customerId = CUSTOMER_ID_PREFIX + RandomUtils.getRandomNumberString();
            INSTANCE.customerDao().insertCustomer(customer);

            customer = new Customer();
            customer.firstName = "John";
            customer.lastName = "Sy";
            customer.phoneNumber = "+65 9135 7655";
            customer.emailAddress = "john.sy@gmail.com";
            customer.status = true;
            customer.password = "ipiddemo0701";
            customer.customerId = CUSTOMER_ID_PREFIX + RandomUtils.getRandomNumberString();
            INSTANCE.customerDao().insertCustomer(customer);
        }
    }

    private static void initializeBank() {
        List<LookupBank> lookupBanks = INSTANCE.lookupBankDao().getAllBanks();

        if (lookupBanks.isEmpty()) {
            LookupBank lookupBank = new LookupBank();
            lookupBank.code = "CTBAAU2S";
            lookupBank.name = "Commonwealth Bank";
            lookupBank.country = "Australia";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "NATAAU33";
            lookupBank.name = "National Australian Bank";
            lookupBank.country = "Australia";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "QBANAU4B";
            lookupBank.name = "Bank of Queensland";
            lookupBank.country = "Australia";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "BKCHCNBJ";
            lookupBank.name = "Bank of China";
            lookupBank.country = "China";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "ABOCCNBJ";
            lookupBank.name = "Agricultural Bank of China";
            lookupBank.country = "China";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "CMBCCNBS";
            lookupBank.name = "China Merchants Bank";
            lookupBank.country = "China";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "BKIDINBBTRY";
            lookupBank.name = "Bank of India";
            lookupBank.country = "India";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "SBININBBFXD";
            lookupBank.name = "State Bank of India";
            lookupBank.country = "India";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "CBININBB";
            lookupBank.name = "Central Bank of India";
            lookupBank.country = "India";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "DBSSSGSG";
            lookupBank.name = "DBS";
            lookupBank.country = "Singapore";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "UOVBSGSG";
            lookupBank.name = "OCBC";
            lookupBank.country = "Singapore";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "OCBCSGSG";
            lookupBank.name = "UOB";
            lookupBank.country = "Singapore";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "BARCGB22";
            lookupBank.name = "Barclays";
            lookupBank.country = "United Kingdom";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "MONZGB2L";
            lookupBank.name = "Monzo";
            lookupBank.country = "United Kingdom";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);

            lookupBank = new LookupBank();
            lookupBank.code = "LOYDGB2L";
            lookupBank.name = "Lloyds Bank";
            lookupBank.country = "United Kingdom";
            lookupBank.status = true;
            INSTANCE.lookupBankDao().insertBank(lookupBank);
        }
    }

    private static void initializeBankAccount() {
        List<BankAccount> bankAccounts = INSTANCE.bankAccountDao().getAllBankAccounts();

        if (bankAccounts.isEmpty()) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.customerId = 1;
            bankAccount.bankId = 1;
            bankAccount.accountNumber = "56778899";
            bankAccount.country = "Australia";
            bankAccount.status = true;
            bankAccount.preferred = true;
            bankAccount.createdDate = DateUtils.getDateTime();
            INSTANCE.bankAccountDao().insertBankAccount(bankAccount);
        }
    }


    private static void initializeCountry() {
        List<LookupCountry> lookupCountries = INSTANCE.lookupCountryDao().getAllCountries();

        if (lookupCountries.isEmpty()) {
            LookupCountry lookupCountry = new LookupCountry();
            lookupCountry.description = "Australia";
            lookupCountry.status = true;
            INSTANCE.lookupCountryDao().insertCountry(lookupCountry);

            lookupCountry = new LookupCountry();
            lookupCountry.description = "China";
            lookupCountry.status = true;
            INSTANCE.lookupCountryDao().insertCountry(lookupCountry);

            lookupCountry = new LookupCountry();
            lookupCountry.description = "India";
            lookupCountry.status = true;
            INSTANCE.lookupCountryDao().insertCountry(lookupCountry);

            lookupCountry = new LookupCountry();
            lookupCountry.description = "Singapore";
            lookupCountry.status = true;
            INSTANCE.lookupCountryDao().insertCountry(lookupCountry);

            lookupCountry = new LookupCountry();
            lookupCountry.description = "United Kingdom";
            lookupCountry.status = true;
            INSTANCE.lookupCountryDao().insertCountry(lookupCountry);
        }
    }

    // 15 currencies
    private static void initializeCurrency() {
        List<LookupCurrency> lookupCurrencies = INSTANCE.lookupCurrencyDao().getAllCurrencies();

        if (lookupCurrencies.isEmpty()) {
            LookupCurrency lookupCurrency = new LookupCurrency();
            lookupCurrency.description = "AUD";
            lookupCurrency.status = true;
            lookupCurrency.country = "Australia";
            INSTANCE.lookupCurrencyDao().insertCurrency(lookupCurrency);

            lookupCurrency = new LookupCurrency();
            lookupCurrency.description = "CNY";
            lookupCurrency.status = true;
            lookupCurrency.country = "China";
            INSTANCE.lookupCurrencyDao().insertCurrency(lookupCurrency);

            lookupCurrency = new LookupCurrency();
            lookupCurrency.description = "INR";
            lookupCurrency.status = true;
            lookupCurrency.country = "India";
            INSTANCE.lookupCurrencyDao().insertCurrency(lookupCurrency);

            lookupCurrency = new LookupCurrency();
            lookupCurrency.description = "SGD";
            lookupCurrency.status = true;
            lookupCurrency.country = "Singapore";
            INSTANCE.lookupCurrencyDao().insertCurrency(lookupCurrency);

            lookupCurrency = new LookupCurrency();
            lookupCurrency.description = "GBP";
            lookupCurrency.status = true;
            lookupCurrency.country = "United Kingdom";
            INSTANCE.lookupCurrencyDao().insertCurrency(lookupCurrency);
        }
    }

    private static void initializeCurrencyConversion() {
        List<CurrencyConversion> currencyConversions = INSTANCE.currencyConversionDao().getAllCurrencyConversions();

        if (currencyConversions.isEmpty()) {
            CurrencyConversion currencyConversion = new CurrencyConversion();
            currencyConversion.currencyFrom = 1; // AUD
            currencyConversion.currencyTo = 2; // CNY
            currencyConversion.status = true;
            currencyConversion.value = 4.86;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion = new CurrencyConversion();
            currencyConversion.currencyFrom = 1; // AUD
            currencyConversion.currencyTo = 3; // INR
            currencyConversion.status = true;
            currencyConversion.value = 56.01;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion = new CurrencyConversion();
            currencyConversion.currencyFrom = 1; // AUD
            currencyConversion.currencyTo = 4; // SGD
            currencyConversion.status = true;
            currencyConversion.value = 1.01;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion = new CurrencyConversion();
            currencyConversion.currencyFrom = 1; // AUD
            currencyConversion.currencyTo = 5; // GBP
            currencyConversion.status = true;
            currencyConversion.value = 0.53;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 2; // CNY
            currencyConversion.currencyTo = 1; // AUD
            currencyConversion.status = true;
            currencyConversion.value = 0.21;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 2; // CNY
            currencyConversion.currencyTo = 3; // INR
            currencyConversion.status = true;
            currencyConversion.value = 11.53;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 2; // CNY
            currencyConversion.currencyTo = 4; // SGD
            currencyConversion.status = true;
            currencyConversion.value = 0.21;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 2; // CNY
            currencyConversion.currencyTo = 5; // GBP
            currencyConversion.status = true;
            currencyConversion.value = 0.11;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 3; // INR
            currencyConversion.currencyTo = 1; // AUD
            currencyConversion.status = true;
            currencyConversion.value = 0.018;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 3; // INR
            currencyConversion.currencyTo = 2; // CNY
            currencyConversion.status = true;
            currencyConversion.value = 0.087;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 3; // INR
            currencyConversion.currencyTo = 4; // SGD
            currencyConversion.status = true;
            currencyConversion.value = 0.018;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 3; // INR
            currencyConversion.currencyTo = 5; // GBP
            currencyConversion.status = true;
            currencyConversion.value = 0.0098;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 4; // SGD
            currencyConversion.currencyTo = 1; // AUD
            currencyConversion.status = true;
            currencyConversion.value = 0.99;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 4; // SGD
            currencyConversion.currencyTo = 2; // CNY
            currencyConversion.status = true;
            currencyConversion.value = 4.80;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 4; // SGD
            currencyConversion.currencyTo = 3; // INR
            currencyConversion.status = true;
            currencyConversion.value = 55.35;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 4; // SGD
            currencyConversion.currencyTo = 5; // GBP
            currencyConversion.status = true;
            currencyConversion.value = 0.54;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 5; // GBP
            currencyConversion.currencyTo = 1; // AUD
            currencyConversion.status = true;
            currencyConversion.value = 1.90;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 5; // GBP
            currencyConversion.currencyTo = 2; // CNY
            currencyConversion.status = true;
            currencyConversion.value = 8.90;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 5; // GBP
            currencyConversion.currencyTo = 3; // INR
            currencyConversion.status = true;
            currencyConversion.value = 101.90;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);

            currencyConversion.currencyFrom = 5; // GBP
            currencyConversion.currencyTo = 4; // SGD
            currencyConversion.status = true;
            currencyConversion.value = 1.85;
            INSTANCE.currencyConversionDao().insertCurrencyConversion(currencyConversion);
        }
    }
}
