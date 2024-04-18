package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ShopPage {
    private SelenideElement head = $("h2");

    public ShopPage() {
        head.shouldBe(visible);
    }

    public CardType creditGate() {
        $$("button").find(exactText("Купить в кредит")).click();
        return new CardType();
    }

    public CardType paymentGate() {
        $$("button").find(exactText("Купить")).click();
        return new CardType();
    }
}