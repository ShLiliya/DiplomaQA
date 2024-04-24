package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {
    private SelenideElement cardNumberField = $$("[class='input__inner']").find(exactText("Номер карты"));
    private SelenideElement monthField = $$("[class='input-group__input-case']").find(exactText("Месяц"));
    private SelenideElement yearField = $$("[class='input-group__input-case']").find(exactText("Год"));
    private SelenideElement nameOwnerField = $$("[class='input-group__input-case']").find(exactText("Владелец"));
    private SelenideElement cvcField = $$("[class='input-group__input-case']").find(exactText("CVC/CVV"));
    private SelenideElement button = $$("button").find(exactText("Продолжить"));
    private SelenideElement errorFormatNotification = $$("[class=input__sub]").find(exactText("Неверный формат"));
    private SelenideElement errorDeadlineNotification = $$("[class=input__sub]").find(exactText("Неверно указан срок " +
            "действия карты"));
    private SelenideElement expiredDeadlineNotification = $$("[class=input__sub]").find(exactText("Истёк срок " +
            "действия карты"));
    private SelenideElement obligatoryFieldNotification = $$("[class=input__sub]").find(exactText("Поле обязательно " +
            "для заполнения"));
    private final SelenideElement successfulNotification = $(".notification_status_ok");
    private final SelenideElement errorNotification = $(".notification_status_error");


    public void fillForm(DataHelper.CheckObjects info) {
        setCardNumberField(info.getNumber());
        setMonthField(info.getMonth());
        setYearField(info.getYear());
        setNameOwnerField(info.getOwner());
        setCvcField(info.getCvc());
        buttonClick();
    }

    public void setCardNumberField(String number) {
        cardNumberField.$("input").setValue(number);
    }

    public void setMonthField(String month) {
        monthField.$("input").setValue(month);
    }

    public void setYearField(String year) {
        yearField.$("input").setValue(year);
    }

    public void setNameOwnerField(String nameOwner) {
        nameOwnerField.$("input").setValue(nameOwner);
    }

    public void setCvcField(String cvc) {
        cvcField.$("input").setValue(cvc);
    }

    public void buttonClick() {
        button.click();
    }

    public void seeSuccessfulNotification(String expectedText) {
        successfulNotification.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void notSeeSuccessfulNotification(String expectedText) {
        successfulNotification.shouldNotHave(text(expectedText));
    }

    public void seeErrorNotification(String expectedText) {
        errorNotification.shouldHave(text(expectedText), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void notSeeErrorNotification(String expectedText) {
        errorNotification.shouldNotHave(text(expectedText));
    }

    public void seeErrorFormatNotification(String expectedText) {
        errorFormatNotification.shouldHave(text(expectedText)).shouldBe(visible);
    }

    public void notSeeErrorFormatNotification(String expectedText) {
        errorFormatNotification.shouldNotHave(text(expectedText));
    }

    public void seeErrorDeadlineNotification(String expectedText) {
        errorDeadlineNotification.shouldHave(text(expectedText)).shouldBe(visible);
    }

    public void seeExpiredDeadlineNotification(String expectedText) {
        expiredDeadlineNotification.shouldHave(text(expectedText)).shouldBe(visible);
    }

    public void seeObligatoryFieldNotification(String expectedText) {
        obligatoryFieldNotification.shouldHave(text(expectedText)).shouldBe(visible);
    }

    public void notSeeObligatoryFieldNotification(String expectedText) {
        obligatoryFieldNotification.shouldNotHave(text(expectedText));
    }

    public void getCardNumberField(String cardNumber) {
        cardNumberField.$("input").shouldHave(value(cardNumber));
    }

    public void getMonthField(String month) {
        monthField.$("input").shouldHave(value(month));
    }

    public void getYearField(String year) {
        yearField.$("input").shouldHave(value(year));
    }

    public void getNameOwnerField(String nameOwner) {
        nameOwnerField.$("input").shouldHave(value(nameOwner));
    }

    public void getCvcField(String cvc) {
        cvcField.$("input").shouldHave(value(cvc));
    }
}