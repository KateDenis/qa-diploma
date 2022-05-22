package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.data.DBHelper;
import ru.netology.data.DataHelper;
import ru.netology.page.DebitCardPage;
import ru.netology.page.MainPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;


public class DebitCardTest {
    DebitCardPage debitCardPage;

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
        debitCardPage = mainPage.buyWithCard();
        debitCardPage.checkVisibleHeadingDebitCard();
    }

    //Позитивные
    @Test
    void shouldCheckDebitBuyWithApprovedCard() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.successNotificationForm();
        assertTrue(DBHelper.tablePaymentHasRows());
        assertEquals("APPROVED", DBHelper.getTransactionPaymentStatus());
    }

    @Test
    void shouldCheckDebitBuyWithDeclinedCard() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(1);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorNotificationForm();
        assertTrue(DBHelper.tablePaymentHasRows());
        assertEquals("DECLINED", DBHelper.getTransactionPaymentStatus());
    }

    @Test
    void shouldCheckDebitBuyWithRandomCard() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getRandomCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorNotificationForm();
        debitCardPage.closeErrorNotification();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableCreditRequestHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    //Негативные
//    Поле месяц
    @Test
    void shouldCheckDebitBuyWithCardExpiredMonth() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(-1);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageExpiredOrNonexistentMonth();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCardNonexistentMonth() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageExpiredOrNonexistentMonth();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCardEmptyMonth() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageMonthFieldEmpty();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCardMonthZeroZero() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,2,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCardMonthSуmbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,2,0,0,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCardMonthLetters() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,0,2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCardInvalidMonthLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(1,0,0,0,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCardInvalidMonthLongerLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(3,0,0,0,0);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
//        debitCardPage.errorMessageInvalidMonthField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    //    Поле год
    @Test
    void shouldCheckDebitBuyWithEmptyYearField() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageYearFieldEmpty();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCardExpiredYear() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(-2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageExpiredYear();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithYearFieldZeroZero() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(0,0,0,2,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageExpiredYear();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithYearInvalidLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(1,0,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidYearField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithYearFieldSymbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(0,2,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidYearField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithYearFieldLetters() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getInvalidFieldFormat(0,0,1,0,1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidYearField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    //    Поле владелец
    @Test
    void shouldCheckDebitBuyWithOwnerFieldEmpty() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageOwnerFieldEmpty();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithOwnerFieldCyrillicLetters() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidOwnerField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithOwnerFieldOneLetter() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,1);
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidOwnerField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithOwnerFieldTooLong() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,50);
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidOwnerField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithOwnerFieldDigitsAndSymbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(5,5,0,0,0);
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidOwnerField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

//    Поле код
    @Test
    void shouldCheckDebitBuyWithCodeFieldEmpty() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(1);
        var year = DataHelper.getYear(4);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageCodeFieldEmpty();
        debitCardPage.errorMessageOwnerFieldEmptyWhenCVCТest();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCodeInvalidLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(2,0,0,0,0);
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidCodeField();
        debitCardPage.errorMessageOwnerFieldEmptyWhenCVCТest();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    //не дает вводить, поле пустое и из-за этого ошибка, как при пустом поле
    @Test
    void shouldCheckDebitBuyWithCodeLetters() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,0,1,0,2);
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidCodeField();
        debitCardPage.errorMessageOwnerFieldEmptyWhenCVCТest();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithCodeSymbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,3,0,0,0);
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidCodeField();
        debitCardPage.errorMessageOwnerFieldEmptyWhenCVCТest();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    //Поле номер карты
    @Test
    void shouldCheckDebitBuyWithInvalidCardNumberLength() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getInvalidFieldFormat(12,0,0,0,0);
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidCardNumberField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithInvalidCardNumberDigitsZero() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getInvalidFieldFormat(0,0,0,16,0);
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidCardNumberField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithInvalidCardNumberLettersAndSymbols() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getInvalidFieldFormat(0,6,5,0,5);
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageInvalidCardNumberField();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

    @Test
    void shouldCheckDebitBuyWithEmptyCardNumberField() {
//        Configuration.holdBrowserOpen = true;
        var cardNumber = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var month = DataHelper.getMonth(1);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitCardPage.fillOutFields(cardNumber, month, year, owner, code);
        debitCardPage.errorMessageCardNumberFieldEmpty();
        assertFalse(DBHelper.tablePaymentHasRows());
        assertFalse(DBHelper.tableOrderHasRows());
    }

}
