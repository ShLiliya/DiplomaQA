package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.CardType;
import ru.netology.page.ShopPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class DebitCardTest {
    CardType debitType;
    private String successfulNotification = "Операция одобрена Банком.";
    private String errorNotification = "Ошибка! Банк отказал в проведении операции.";
    private String errorFormat = "Неверный формат";
    private String errorPeriod = "Неверно указан срок действия карты";
    private String expiredCard = "Истёк срок действия карты";
    private String obligatoryField = "Поле обязательно для заполнения";

    @BeforeEach
    void setup() {
        debitType = open("http://localhost:8080", ShopPage.class).paymentGate();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("'Операция одобрена Банком' при приобретении путевки одобренной банком дебетовой картой" +
            "с номером 4444 4444 4444 4441")
    void shouldSuccessfulWhenInputApprovedDebitCard() {
        var cardInfo = DataHelper.getGenerateCheckObjectsApproved();
        debitType.fillForm(cardInfo);
        debitType.seeSuccessfulNotification(successfulNotification);
        debitType.notSeeErrorNotification(errorNotification);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getStatusDebitCard());
    }

    @Test  //Баг - покупка тура состоялась, но должна быть отклонена
    @DisplayName("'Ошибка! Банк отказал в проведении операции' при приобретении путевки отклоненной банком" +
            "крединой картой с номером 4444 4444 4444 4442")
    void shouldErrorWhenInputDeclancedDebitCard() {
        var cardInfo = DataHelper.getGenerateCheckObjectsDeclined();
        debitType.fillForm(cardInfo);
        debitType.seeErrorNotification(errorNotification);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getStatusDebitCard());
    }

    @Test // баг - после закрытия уведомления об отказе банка появляется уведомление об одобрении кредита банком
    @DisplayName("'Успешно. Операция одобрена банком' при отправке заявки в банк с помощью новой карты")
    void shouldSuccessfulWhenInputNewDebitCard() {
        var cardInfo = DataHelper.getGenerateCheckObjectsNew();
        debitType.fillForm(cardInfo);
        debitType.seeSuccessfulNotification(successfulNotification);
        debitType.notSeeErrorNotification(errorNotification);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getStatusDebitCard());
    }

    //Некорректный ввод номера дебетовой банковской карты
    @Test
    @DisplayName("Нет доступа к вводу букв в поле 'Номер карты'")
    void shouldErrorWhenInputLettersInsteadOfNumberDebitCard() {
        debitType.setCardNumberField(getLetters());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу спецсимволов в поле 'Номер карты'")
    void shouldErrorWhenInputSymbolsInsteadOfNumberDebitCard() {
        debitType.setCardNumberField(getSymbols());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу 17 цифр в поле 'Номер карты'")
    void shouldErrorWhenInput17FiguresInNumberDebitCard() {
        var cardNumber = get17Numbers();
        debitType.setCardNumberField(cardNumber);
        debitType.getCardNumberField(cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 8) + " " + cardNumber.substring(8, 12) + " " + cardNumber.substring(12, 16));
    }

    @Test
    @DisplayName("'Неверный формат' при вводе 15 цифр в поле 'Номер карты'")
    void shouldErrorWhenInput15FiguresInNumberDebitCard() {
        debitType.setCardNumberField(get15Numbers());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("'Неверный формат' при вводе 1 цифры в поле 'Номер карты'")
    void shouldErrorWhenNotInput1FigureInNumberDebitCard() {
        debitType.setCardNumberField(getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test  //баг - "Неверный формат" вместо "Поле обязательно для заполнения"
    @DisplayName("'Поле обязательно для заполнения' при отсутствии цифр в поле 'Номер карты'")
    void shouldErrorWhenNotInputFiguresInNumberDebitCard() {
        debitType.setCardNumberField("");
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeObligatoryFieldNotification(obligatoryField);
    }

    //Некорректный ввод месяца дебетовой банковской карты
    @Test
    @DisplayName("Нет доступа к вводу букв в поле 'Месяц'")
    void shouldErrorWhenInputLettersInsteadOfMonthDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getLetters());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу спецсимволов в поле 'Месяц'")
    void shouldErrorWhenInputSymbolsInsteadOfMonthDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getSymbols());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("'Неверный формат' при вводе одной цифры в поле 'Месяц'")
    void shouldErrorWhenNotInput1FigureInMonthDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getNumber());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Неверный срок действия карты при вводе числа больше 12 в поле 'Месяц'")
    void shouldErrorWhenNotInputMore12FigureInMonthDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getMoreThen12());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorDeadlineNotification(errorPeriod);
    }

    @Test
    @DisplayName("Нет доступа к вводу более 2 цифр в поле 'Месяц'")
    void shouldErrorWhenInputMore2FiguresInMonthDebitCard() {
        var month = get3Numbers();
        debitType.setMonthField(month);
        debitType.getMonthField(month.substring(0, 2));
    }

    @Test  ///баг - "Неверный формат" вместо "Поле обязательно для заполнения"
    @DisplayName("'Поле обязательно для заполнения' при отсутствии номера месяца в поле 'Месяц'")
    void shouldErrorWhenNotInputFiguresInMonthDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField("");
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeObligatoryFieldNotification(obligatoryField);
    }

    //Некорректный ввод года окончания действия дебетовой карты

    @Test
    @DisplayName("Нет доступа к вводу букв в поле 'Год'")
    void shouldErrorWhenInputLettersInsteadOfYearDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getLetters());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу спецсимволов в поле 'Год'")
    void shouldErrorWhenInputSymbolsInsteadOfYearDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getSymbols());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("'Неверно указан срок действия карты' при вводе в поле 'Год' года на 5 лет позже текущего")
    void shouldErrorWhenInput6YearLaterOfYearDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getYearPlus6Years());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorDeadlineNotification(errorPeriod);
    }

    @Test  //баг - "Неверно указан срок действия карты" вместо "Истёк срок действия карты"
    @DisplayName("'Истёк срок действия карты' при вводе в поле 'Год' текущий год, в поле 'Месяц' на 1 месяц раньше текущего")
    void shouldErrorWhenInputExpiredMonthInMonthFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getMonthField());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeExpiredDeadlineNotification(expiredCard);
    }

    @Test
    @DisplayName("'Истёк срок действия карты' при вводе в поле 'Год' года на 1 год раньше текущего")
    void shouldErrorWhenInputExpired1YearInYearFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getYearField());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeExpiredDeadlineNotification(expiredCard);
    }

    @Test
    @DisplayName("'Неверный формат' при вводе в поле 'Год' 1 цифры")
    void shouldErrorWhenInput1FigureInYearFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getNumber());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу более 2 цифр в поле 'Год'")
    void shouldErrorWhenInputMore2FigureInYearFieldDebitCard() {
        var year = get3Numbers();
        debitType.setYearField(year);
        debitType.getYearField(year.substring(0, 2));
    }

    @Test //баг - "Неверный формат" вместо "Поле обязательно для заполнения"
    @DisplayName("'Поле обязательно для заполнения' при отсутствии цифр в поле 'Год'")
    void shouldErrorWhenInput0FigureInYearFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField("");
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeObligatoryFieldNotification(obligatoryField);
    }

    //Некорректный ввод имени владельца
    @Test  // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("'Неверный формат' при вводе спецсимволов в поле 'Владелец'")
    void shouldErrorWhenInputSymbolsInOwnerFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getSymbols());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test  // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("'Неверный формат' при вводе цифр в поле 'Владелец'")
    void shouldErrorWhenInputFiguresInOwnerFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(get17Numbers());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("'Поле обязательно для заполнения' при пустом поле 'Владелец'")
    void shouldErrorWhenInput0FiguresInOwnerFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField("");
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeObligatoryFieldNotification(obligatoryField);
    }

    @Test  // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("'Неверный формат' при вводе букв кириллицей в поле 'Владелец'")
    void shouldErrorWhenInputCyrillicLettersInOwnerFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getCyrillicLetters());
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("Неверный формат' при вводе более 50 букв без пробела в поле 'Владелец'")
    void shouldErrorWhenInput51LettersInOwnerFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        var userName = get51Letters();
        debitType.setNameOwnerField(userName);
        debitType.getNameOwnerField(userName.substring(0, 51));
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("Неверный формат' при вводе 1 буквы без пробела в поле 'Владелец'")
    void shouldErrorWhenInput1LetterInOwnerFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        var userName = get1Letters();
        debitType.setNameOwnerField(userName);
        debitType.getNameOwnerField(userName.substring(0, 1));
        debitType.setCvcField(getGenerateCvc());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    //Некорректный ввод CVC/CVV
    @Test  // Баг - "Поле обязательно для заполнения" в поле 'Владелец' при "Неверный формат" в поле 'CVC/CVV'
    @DisplayName("Отсутвие сообщения об ошибке в поле 'Владелец' при возникновении ошибки в поле 'CVC/CVV'")
    void shouldErrorInOwnerFieldWhenErrorVisibleInCVCFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getSymbols());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
        debitType.notSeeObligatoryFieldNotification(obligatoryField);
    }

    @Test
    @DisplayName("Нет доступа к вводу букв в поле 'CVC/CVV'")
    void shouldErrorWhenInputLettersInCVCFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getLetters());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу спецсимволов в поле CVC/CVV")
    void shouldErrorWhenInputSymbolsInCVCFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getSymbols());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }

    @Test // Баг - "Неверный формат" вместо "Поле обязательно для заполнения" в поле 'CVC/CVV'
    @DisplayName("'Поле обязательно для заполнения' при пустом поле 'CVC/CVV'")
    void shouldErrorWhenInput0FiguresInCVCFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField("");
        debitType.buttonClick();
        debitType.seeObligatoryFieldNotification(obligatoryField);
        debitType.notSeeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу более 3 цифр в поле CVC/CVV")
    void shouldErrorWhenInputMore3FiguresInCVCFieldDebitCard() {
        var cvc = get15Numbers();
        debitType.setCvcField(cvc);
        debitType.getCvcField(cvc.substring(0, 3));
    }

    @Test
    @DisplayName("'Неверный формат' при вводе 1 цифры в поле 'CVC/CVV'")
    void shouldErrorWhenInput1FigureInCVCFieldDebitCard() {
        debitType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        debitType.setMonthField(getGenerateMonth());
        debitType.setYearField(getGenerateYear());
        debitType.setNameOwnerField(getGenerateName());
        debitType.setCvcField(getNumber());
        debitType.buttonClick();
        debitType.seeErrorFormatNotification(errorFormat);
    }
}