package by.obs.portal.testdata;

import by.obs.portal.persistence.model.contracts.Tariff;
import by.obs.portal.utils.TestMatchers;

import java.util.List;

public class TariffTestData {

    public static final Tariff TARIFF_1 = new Tariff(1, "НДС", 1, 0, 0,
            20.00, "Налон на добавленную стоимость");
    public static final Tariff TARIFF_2 = new Tariff(2, "ГО1", 2, 1, 9,
            85.00, "Тариф от 1 до 9 пользователей");
    private static final Tariff TARIFF_3 = new Tariff(3, "ГО2", 2, 10, 19,
            70.00, "Тариф от 10 до 19 пользователей");
    private static final Tariff TARIFF_4 = new Tariff(4, "ГО3", 2, 20, 49,
            60.00, "Тариф от 20 до 49 пользователей");
    private static final Tariff TARIFF_5 = new Tariff(5, "ГО4", 2, 50, 1000,
            45.00, "Тариф более 50 полезователей");

    public static final List<Tariff> TARIFFS = List.of(TARIFF_1, TARIFF_2, TARIFF_3, TARIFF_4, TARIFF_5);

    public static TestMatchers<Tariff> TARIFF_MATCHERS =
            TestMatchers.useFieldsComparator(Tariff.class, "minUsers", "maxUsers");
}