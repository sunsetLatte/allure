package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;



public class DeliveryTest {
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
        open("http://localhost:9999");
    }


    @Test                                        // Позитивный тест. Регстрация проходит, далее – перепланировка на другую дату
    void shouldRegisteredAndReplanMeetingDay() {
//        Configuration.holdBrowserOpen = true;
        DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 4;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        int daysToAddForSecondMeeting = 7;
        String secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(withText("Успешно")).shouldBe(visible, Duration.ofSeconds(50));
        $("[class='notification__content']")
                .shouldBe(visible)
                .shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);
        $$("button").find(exactText("Запланировать")).click();
        $(withText("У вас уже запланирована встреча на другую дату. Перепланировать?")).shouldBe(visible);
        $("[data-test-id='replan-notification'] button.button").click();
        $(withText("Успешно")).shouldBe(visible, Duration.ofSeconds(50));
        $(".notification__content").shouldBe(visible)
                .shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate));
    }

    @Test                // Негативный тест. Регистрация не проходит с невалидным городом
    void shouldNotRegisteredWithInvalidCity() {
//        Configuration.holdBrowserOpen = true;
        DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 5;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id='city'] input").setValue("Moscow");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(byText("Доставка в выбранный город недоступна"))
                .shouldBe(visible)
                .shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test                // Негативный тест. Регистрация не проходит с незаполенным полем города
    void shouldNotRegisteredWithEmptyCity() {
//        Configuration.holdBrowserOpen = true;
        DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 6;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id='city'] input").setValue("");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(byText("Поле обязательно для заполнения"))
                .shouldBe(visible)
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test                // Негативный тест. Регистрация не проходит с невалидным именем
    void shouldNotRegisteredWithInvalidName() {
//        Configuration.holdBrowserOpen = true;
        DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 7;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue("Vasiliy Pupkin");
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(withText("Имя и Фамилия указаные неверно"))
                .shouldBe(visible)
                .shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test                // Негативный тест. Регистрация не проходит с незаполенным полем имени
    void shouldNotRegisteredWithEmptyName() {
//        Configuration.holdBrowserOpen = true;
        DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 8;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue("");
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(withText("Поле обязательно для заполнения"))
                .shouldBe(visible)
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test                // Позитивный тест. Решистация порходит, если имя содержит букву "ё"
    void shouldRegisteredAndReplanMeetingDayWihLetterYoInName() {
//        Configuration.holdBrowserOpen = true;
        DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 4;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        int daysToAddForSecondMeeting = 7;
        String secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue("Фёдор Смелов");
        $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(withText("Успешно")).shouldBe(visible, Duration.ofSeconds(50));
        $("[class='notification__content']")
                .shouldBe(visible)
                .shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);
        $$("button").find(exactText("Запланировать")).click();
        $(withText("У вас уже запланирована встреча на другую дату. Перепланировать?")).shouldBe(visible);
        $("[data-test-id='replan-notification'] button.button").click();
        $(withText("Успешно")).shouldBe(visible, Duration.ofSeconds(50));
        $(".notification__content").shouldBe(visible)
                .shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate));
    }

    @Test                 // Негативный тест. Регистрация не проходит с невалидным телефоном
    void   shouldNotRegisteredWithInvalidPhone() {
//        Configuration.holdBrowserOpen = true;
        DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 10;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue("911");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(withText("Телефон указан неверно"))
                .shouldBe(visible)
                .shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test                 // Негативный тест. Регистрация не проходит с пустым полем телефона
    void   shouldNotRegisteredWithEmptyPhone() {
//        Configuration.holdBrowserOpen = true;
        DataGenerator.UserInfo validUser = DataGenerator.Registration.generateUser("ru");
        int daysToAddForFirstMeeting = 11;
        String firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        $("[data-test-id='city'] input").setValue(validUser.getCity());
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser.getName());
        $("[data-test-id='phone'] input").setValue("");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Запланировать")).click();
        $(withText("Поле обязательно для заполнения"))
                .shouldBe(visible)
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }
}