package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;


public class DataHelper {

    private DataHelper() {
    }

    private static final Faker FAKER = new Faker(new Locale("en"));

    @Value
    public static class CheckObjects {
        String number;
        String month;
        String year;
        String owner;
        String cvc;
    }

    public static String getGenerateMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM", new Locale("ru")));
    }

    public static String getMonthField() {
        return LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM", new Locale("ru")));
    }

    public static String getGenerateYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yy", new Locale("ru")));
    }

    public static String getYearPlus6Years() {
        return LocalDate.now().plusYears(6).format(DateTimeFormatter.ofPattern("yy", new Locale("ru")));
    }

    public static String getYearField() {
        return LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yy", new Locale("ru")));
    }

    public static String getGenerateName() {
        String name = FAKER.name().firstName() + " " + FAKER.name().lastName();
        return name;
    }

    public static String getGenerateCvc() {
        String cvc = FAKER.regexify("[0-9]{3}");
        return cvc;
    }

    public static CheckObjects getGenerateCheckObjectsApproved() {
        String number = getGenerateCardApproved().getNumber();
        String month = getGenerateMonth();
        String year = getGenerateYear();
        String user = getGenerateName();
        String cvc = getGenerateCvc();
        return new CheckObjects(number, month, year, user, cvc);
    }

    public static CheckObjects getGenerateCheckObjectsDeclined() {
        String number = getGenerateCardDeclined().getNumber();
        String month = getGenerateMonth();
        String year = getGenerateYear();
        String user = getGenerateName();
        String cvc = getGenerateCvc();
        return new CheckObjects(number, month, year, user, cvc);
    }

    public static CheckObjects getGenerateCheckObjectsNew() {
        String number = getGenerateCardNew().getNumber();
        String month = getGenerateMonth();
        String year = getGenerateYear();
        String user = getGenerateName();
        String cvc = getGenerateCvc();
        return new CheckObjects(number, month, year, user, cvc);
    }

    @Value
    public static class CardStatus {
        String number;
        String status;
    }

    public static CardStatus getGenerateCardApproved() {
        return new CardStatus("4444 4444 4444 4441", "APPROVED");
    }

    public static CardStatus getGenerateCardDeclined() {
        return new CardStatus("4444 4444 4444 4442", "DECLINED");
    }

    public static CardStatus getGenerateCardNew() {
        return new CardStatus("4444 4444 4444 4443","NEW");
    }

    public static String getVerifyStatus(String number) {
        if (number == getGenerateCardApproved().getNumber()) {
            return getGenerateCardApproved().getStatus();
        }
        if (number == getGenerateCardDeclined().getNumber()) {
            return getGenerateCardDeclined().getStatus();
        } else {
            return null;
        }
    }

    public static String getLetters() {
        String nameLet = FAKER.regexify("[A-Z]{20}");
        return nameLet;
    }

    public static String getCyrillicLetters() {
        String nameLet = FAKER.regexify("[А-Я]{20}");
        return nameLet;
    }

    public static String get51Letters() {
        String nameLet = FAKER.regexify("[A-Z]{51}");
        return nameLet;
    }

    public static String get1Letters() {
        String nameLet = FAKER.regexify("[A-Z]{1}");
        return nameLet;
    }

    public static String getSymbols() {
        String[] specialSymbols = {"!", "@", "#", "$", "%", "^", "&", "(", ")", "'", "_", "+", "?", "<"};
        int numberSpecialSymbols = 14;
        int randomIndexSpecialSymbols = (int) (Math.random() * numberSpecialSymbols);
        int i = 0;
        String result = "";
        while (i < 10) {
            result = result + specialSymbols[randomIndexSpecialSymbols];
            randomIndexSpecialSymbols = (int) (Math.random() * numberSpecialSymbols);
            i++;
        }
        return result;
    }

    public static String get17Numbers() {
        return FAKER.regexify("[0-9]{17}");
    }

    public static String get15Numbers() {
        return FAKER.regexify("[0-9]{15}");
    }

    public static String getNumber() {
        return FAKER.regexify("[0-9]{1}");
    }

    public static String get3Numbers() {
        return FAKER.regexify("[0-9]{3}");
    }

    public static String getMoreThen12() {
        int randomNum = new Random().nextInt(99 - 12 + 1) + 12;
        return Integer.toString(randomNum);
    }
}