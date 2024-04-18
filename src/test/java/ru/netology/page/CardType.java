package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class CardType extends DashboardPage {
    private SelenideElement buyCreditCard = $(byText("Кредит по данным карты"));
    private SelenideElement buyDebitCard = $(byText("Оплата по карте"));

    public DashboardPage dashboardPage() {
        buyCreditCard.should(visible);
        buyDebitCard.should(visible);
        return new DashboardPage();
    }
}