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


public class CreditCartTest {
    CardType creditType;
    private String successfulNotification = "Операция одобрена Банком.";
    private String errorNotification = "Ошибка! Банк отказал в проведении операции.";
    private String errorFormat = "Неверный формат";
    private String errorPeriod = "Неверно указан срок действия карты";
    private String expiredCard = "Истёк срок действия карты";
    private String obligatoryField = "Поле обязательно для заполнения";


    @BeforeEach
    void setup() {
        creditType = open("http://localhost:8080", ShopPage.class).creditGate();
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
    @DisplayName("'Операция одобрена Банком' при приобретении путевки одобренной банком кредитной картой" +
            " с номером 4444 4444 4444 4441")
    void shouldSuccessfulWhenInputApprovedCreditCard() {
        var cardInfo = DataHelper.getGenerateCheckObjectsApproved();
        creditType.fillForm(cardInfo);
        creditType.seeSuccessfulNotification(successfulNotification);
        creditType.notSeeErrorNotification(errorNotification);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getStatusCreditCard());
    }

    @Test  //Баг - покупка тура состоялась, но должна быть отклонена
    @DisplayName("'Ошибка! Банк отказал в проведении операции' при приобретении путевки отклоненной банком" +
            " крединой картой с номером 4444 4444 4444 4442")
    void shouldErrorWhenInputDeclancedCreditCard() {
        var cardInfo = DataHelper.getGenerateCheckObjectsDeclined();
        creditType.fillForm(cardInfo);
        creditType.seeErrorNotification(errorNotification);
        creditType.notSeeSuccessfulNotification(successfulNotification);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getStatusCreditCard());
    }

    @Test // баг - после закрытия уведомления об отказе банка появляется уведомление об одобрении кредита банком
    @DisplayName("'Успешно. Операция одобрена банком' при отправке заявки в банк с помощью новой карты")
    void shouldSuccessfulWhenInputNewCreditCard() {
        var cardInfo = DataHelper.getGenerateCheckObjectsNew();
        creditType.fillForm(cardInfo);
        creditType.seeSuccessfulNotification(successfulNotification);
        creditType.notSeeErrorNotification(errorNotification);
        var number = cardInfo.getNumber();
        assertEquals(DataHelper.getVerifyStatus(number), SQLHelper.getStatusCreditCard());
    }

    //Некорректный ввод номера кредитной банковской карты
    @Test
    @DisplayName("Нет доступа к вводу букв в поле 'Номер карты'")
    void shouldErrorWhenInputLettersInsteadOfNumberCreditCard() {
        creditType.setCardNumberField(getLetters());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу спецсимволов в поле 'Номер карты'")
    void shouldErrorWhenInputSymbolsInsteadOfNumberCreditCard() {
        creditType.setCardNumberField(getSymbols());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу 17 цифр в поле 'Номер карты'")
    void shouldErrorWhenInput17FiguresInNumberCreditCard() {
        var cardNumber = get17Numbers();
        creditType.setCardNumberField(cardNumber);
        creditType.getCardNumberField(cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 8) + " " + cardNumber.substring(8, 12) + " " + cardNumber.substring(12, 16));
    }

    @Test
    @DisplayName("'Неверный формат' при вводе 15 цифр в поле 'Номер карты'")
    void shouldErrorWhenInput15FiguresInNumberCreditCard() {
        creditType.setCardNumberField(get15Numbers());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("'Неверный формат' при вводе 1 цифры в поле 'Номер карты'")
    void shouldErrorWhenNotInput1FigureInNumberCreditCard() {
        creditType.setCardNumberField(getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test  //баг - "Неверный формат" вместо "Поле обязательно для заполнения"
    @DisplayName("'Поле обязательно для заполнения' при отсутствии цифр в поле 'Номер карты'")
    void shouldErrorWhenNotInputFiguresInNumberCreditCard() {
        creditType.setCardNumberField("");
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeObligatoryFieldNotification(obligatoryField);
    }

    //Некорректный ввод месяца кредитной банковской карты
    @Test
    @DisplayName("Нет доступа к вводу букв в поле 'Месяц'")
    void shouldErrorWhenInputLettersInsteadOfMonthCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getLetters());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу спецсимволов в поле 'Месяц'")
    void shouldErrorWhenInputSymbolsInsteadOfMonthCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getSymbols());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("'Неверный формат' при вводе одной цифры в поле 'Месяц'")
    void shouldErrorWhenNotInput1FigureInMonthCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getNumber());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Неверный срок действия карты при вводе числа больше 12 в поле 'Месяц'")
    void shouldErrorWhenNotInputMore12FigureInMonthCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getMoreThen12());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorDeadlineNotification(errorPeriod);
    }

    @Test
    @DisplayName("Нет доступа к вводу более 2 цифр в поле 'Месяц'")
    void shouldErrorWhenInputMore2FiguresInMonthCreditCard() {
        var month = get3Numbers();
        creditType.setMonthField(month);
        creditType.getMonthField(month.substring(0, 2));
    }

    @Test //баг - "Неверный формат" вместо "Поле обязательно для заполнения"
    @DisplayName("'Поле обязательно для заполнения' при отсутствии номера месяца в поле 'Месяц'")
    void shouldErrorWhenNotInputFiguresInMonthCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField("");
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeObligatoryFieldNotification(obligatoryField);
    }

    //Некорректный ввод года окончания действия кредитной карты
    @Test
    @DisplayName("Нет доступа к вводу букв в поле 'Год'")
    void shouldErrorWhenInputLettersInsteadOfYearCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getLetters());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу спецсимволов в поле 'Год'")
    void shouldErrorWhenInputSymbolsInsteadOfYearCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getSymbols());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("'Неверно указан срок действия карты' при вводе в поле 'Год' года на 5 лет позже текущего")
    void shouldErrorWhenInput6YearLaterOfYearCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getYearPlus6Years());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorDeadlineNotification(errorPeriod);
    }

    @Test //баг - "Неверно указан срок действия карты" вместо "Истёк срок действия карты"
    @DisplayName("'Истёк срок действия карты' при вводе в поле 'Год' текущий год, в поле 'Месяц' на 1 месяц раньше текущего")
    void shouldErrorWhenInputExpiredMonthInMonthFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getMonthField());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeExpiredDeadlineNotification(expiredCard);
    }

    @Test
    @DisplayName("'Истёк срок действия карты' при вводе в поле 'Год' года на 1 год раньше текущего")
    void shouldErrorWhenInputExpired1YearInYearFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getYearField());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeExpiredDeadlineNotification(expiredCard);
    }

    @Test
    @DisplayName("'Неверный формат' при вводе в поле 'Год' 1 цифры")
    void shouldErrorWhenInput1FigureInYearFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getNumber());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу более 2 цифр в поле 'Год'")
    void shouldErrorWhenInputMore2FiguresInYearFieldCreditCard() {
        var year = get3Numbers();
        creditType.setYearField(year);
        creditType.getYearField(year.substring(0, 2));
    }

    @Test //баг - "Неверный формат" вместо "Поле обязательно для заполнения"
    @DisplayName("'Поле обязательно для заполнения' при отсутствии цифр в поле 'Год'")
    void shouldErrorWhenInput0FiguresInYearFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField("");
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeObligatoryFieldNotification(obligatoryField);
    }

    //Некорректный ввод имени владельца
    @Test  // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("'Неверный формат' при вводе спецсимволов в поле 'Владелец'")
    void shouldErrorWhenInputSymbolsInOwnerFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getSymbols());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test  // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("'Неверный формат' при вводе цифр в поле 'Владелец'")
    void shouldErrorWhenInputFiguresInOwnerFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(get17Numbers());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("'Поле обязательно для заполнения' при пустом поле 'Владелец'")
    void shouldErrorWhenInput0FiguresInOwnerFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField("");
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeObligatoryFieldNotification(obligatoryField);
    }

    @Test  // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("'Неверный формат' при вводе букв кириллицей в поле 'Владелец'")
    void shouldErrorWhenInputCyrillicLettersInOwnerFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getCyrillicLetters());
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("Неверный формат' при вводе более 50 букв в поле 'Владелец'")
    void shouldErrorWhenInput51LettersInOwnerFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        var userName = get51Letters();
        creditType.setNameOwnerField(userName);
        creditType.getNameOwnerField(userName.substring(0, 51));
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test // Баг - 'Операция одобрена Банком.' вместо 'Неверный формат'
    @DisplayName("Неверный формат' при вводе 1 буквы без пробела в поле 'Владелец'")
    void shouldErrorWhenInput1LetterInOwnerFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        var userName = get1Letters();
        creditType.setNameOwnerField(userName);
        creditType.getNameOwnerField(userName.substring(0, 1));
        creditType.setCvcField(getGenerateCvc());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    //Некорректный ввод CVC/CVV
    @Test  // Баг - "Поле обязательно для заполнения" в поле 'Владелец' при отсутствии символов в поле 'CVC/CVV'
    @DisplayName("Отсутвие сообщения об ошибке в поле 'Владелец' при отсутствии символов в поле 'CVC/CVV'")
    void shouldErrorInOwnerFieldWhenErrorVisibleInCVCFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField("");
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
        creditType.notSeeObligatoryFieldNotification(obligatoryField);
    }

    @Test
    @DisplayName("Нет доступа к вводу букв в поле 'CVC/CVV'")
    void shouldErrorWhenInputLettersInCVCFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getLetters());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу спецсимволов в поле CVC/CVV")
    void shouldErrorWhenInputSymbolsInCVCFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getSymbols());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }


    @Test // Баг - "Неверный формат" вместо "Поле обязательно для заполнения" в поле 'CVC/CVV'
    @DisplayName("'Поле обязательно для заполнения' при пустом поле 'CVC/CVV'")
    void shouldErrorWhenInput0FiguresInCVCFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField("");
        creditType.buttonClick();
        creditType.seeObligatoryFieldNotification(obligatoryField);
        creditType.notSeeErrorFormatNotification(errorFormat);
    }

    @Test
    @DisplayName("Нет доступа к вводу более 3 цифр в поле CVC/CVV")
    void shouldErrorWhenInputMore3FiguresInCVCFieldCreditCard() {
        var cvc = get15Numbers();
        creditType.setCvcField(cvc);
        creditType.getCvcField(cvc.substring(0, 3));
    }

    @Test
    @DisplayName("'Неверный формат' при вводе менее чем 1 цифры в поле 'CVC/CVV'")
    void shouldErrorWhenInput1FigureInCVCFieldCreditCard() {
        creditType.setCardNumberField(DataHelper.getGenerateCheckObjectsApproved().getNumber());
        creditType.setMonthField(getGenerateMonth());
        creditType.setYearField(getGenerateYear());
        creditType.setNameOwnerField(getGenerateName());
        creditType.setCvcField(getNumber());
        creditType.buttonClick();
        creditType.seeErrorFormatNotification(errorFormat);
    }
}