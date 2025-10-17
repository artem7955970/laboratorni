public class DecisionTableTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("АВТОМАТИЗОВАНІ ТЕСТИ (таблиця рішень)");

        testNoServiceSelected();
        testNoStreet();
        testNoHouseNumber();
        testInvalidName();
        testNotConfirmed();
        testSuccess();

        System.out.println("\n=== ПІДСУМОК ===");
        System.out.println("Пройдено: " + passed);
        System.out.println("Провалено: " + failed);
        System.out.println("Всього тестів: " + (passed + failed));
    }

    static void testNoServiceSelected() {
        String result = Main.createOrder(false, false, false, false, false);
        assertResult("Помилка: не обрано послугу", result, "testNoServiceSelected");
    }

    static void testNoStreet() {
        String result = Main.createOrder(true, false, true, true, true);
        assertResult("Помилка: не вказано вулицю", result, "testNoStreet");
    }

    static void testNoHouseNumber() {
        String result = Main.createOrder(true, true, false, true, true);
        assertResult("Помилка: не вказано номер будинку", result, "testNoHouseNumber");
    }

    static void testInvalidName() {
        String result = Main.createOrder(true, true, true, false, true);
        assertResult("Помилка: некоректне ПІБ", result, "testInvalidName");
    }

    static void testNotConfirmed() {
        String result = Main.createOrder(true, true, true, true, false);
        assertResult("Повідомлення: замовлення не підтверджено", result, "testNotConfirmed");
    }

    static void testSuccess() {
        String result = Main.createOrder(true, true, true, true, true);
        assertResult("Замовлення створено успішно", result, "testSuccess");
    }

    static void assertResult(String expected, String actual, String testName) {
        if (expected.equals(actual)) {
            System.out.println( " + testName + " — пройдено (" + actual + ")");
            passed++;
        } else {
            System.out.println("testName + " — провалено");
            System.out.println("   Очікувано: " + expected);
            System.out.println("   Отримано:  " + actual);
            failed++;
        }
    }
}
