package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.netology.data.DBHelper;
import ru.netology.data.DataHelper;
import ru.netology.page.CreditCardPage;
import ru.netology.page.MainPage;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;


public class CreditCardTest {
    CreditCardPage creditCardPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        DBHelper.clearDB();
        open("http://localhost:8080");
        MainPage mainPage = new MainPage();
        creditCardPage = mainPage.buyWithCredit();
        creditCardPage.checkVisibleHeadingCreditCard();
    }

    //Позитивные
    @Test
    void shouldCheckCreditBuyWithApprovedCard() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorNotificationForm();
        assertTrue(DBHelper.tableCreditRequestHasRows());
        assertEquals("APPROVED", DBHelper.getTransactionCreditRequestStatus());
    }

    @Test
    void shouldCheckCreditBuyWithDeclinedCard() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.successNotificationForm();
        assertTrue(DBHelper.tableCreditRequestHasRows());
        assertEquals("DECLINED", DBHelper.getTransactionCreditRequestStatus());
    }

    @Test
    void shouldCheckCreditBuyWithRandomCard() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getRandomCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorNotificationForm();
        creditCardPage.closeErrorNotification();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableCreditRequestHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    //Негативные

    @Test
    void shouldCheckCreditBuyWithCardExpiredMonth() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(-1);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageExpiredOrNonexistentMonth();
        assertFalse(DBHelper.tableCreditRequestHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCardNonexistentMonth() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageExpiredOrNonexistentMonth();
        assertFalse(DBHelper.tableCreditRequestHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCardEmptyMonth() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageMonthFieldEmpty();
        assertFalse(DBHelper.tableCreditRequestHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCardMonthZeroZero() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,2,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCardMonthSуmbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,2,0,0,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tableCreditRequestHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCardMonthLetters() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,0,2);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tableCreditRequestHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCardInvalidMonthLongerLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(3,0,0,0,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
//        creditCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tableCreditRequestHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCardInvalidMonthLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(1,0,0,0,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

//    Поле год
    @Test
    void shouldCheckCreditBuyWithEmptyYearField() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageYearFieldEmpty();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCardExpiredYear() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(-2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageExpiredYear();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithYearFieldZeroZero() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(0,0,0,2,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageExpiredYear();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithYearInvalidLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(1,0,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidYearField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithYearFieldSymbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(0,2,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidYearField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithYearFieldLetters() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(0,0,1,0,1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidYearField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

//    Поле владелец
    @Test
    void shouldCheckCreditBuyWithOwnerFieldEmpty() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageOwnerFieldEmpty();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithOwnerFieldCyrillicLetters() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidOwnerField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithOwnerFieldOneLetter() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,1);
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidOwnerField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithOwnerFieldTooLong() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,50);
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidOwnerField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithOwnerFieldDigitsAndSymbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getInvalidFieldFormat(5,5,0,0,0);
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidOwnerField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }
// Поле код
    @Test
    void shouldCheckCreditBuyWithCodeFieldEmpty() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageCodeFieldEmpty();
        creditCardPage.errorMessageOwnerFieldEmptyWhenCVCTest();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCodeInvalidLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(2,0,0,0,0);
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidCodeField();
        creditCardPage.errorMessageOwnerFieldEmptyWhenCVCTest();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCodeLetters() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(1);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,0,1,0,2);
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidCodeField();
        creditCardPage.errorMessageOwnerFieldEmptyWhenCVCTest();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithCodeSymbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,3,0,0,0);
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidCodeField();
        creditCardPage.errorMessageOwnerFieldEmptyWhenCVCTest();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    //Поле номер карты
    @Test
    void shouldCheckCreditBuyWithInvalidCardNumberLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getInvalidFieldFormat(12,0,0,0,0);
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidCardNumberField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithInvalidCardNumberDigitsZero() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getInvalidFieldFormat(0,0,0,16,0);
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidCardNumberField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithInvalidCardNumberLettersAndSymbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getInvalidFieldFormat(0,6,5,0,5);
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageInvalidCardNumberField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckCreditBuyWithEmptyCardNumberField() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var month = DataHelper.getMonth(1);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditCardPage.fillOutFields(cardNumber, month, year, owner, code);
        creditCardPage.errorMessageCardNumberFieldEmpty();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

}
