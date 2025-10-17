public class CalculationTests {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("ТЕСТИ РОЗРАХУНКІВ (таблиця рішень)");

        testAddition();
        testSubtraction();
        testDiscountCalculation();
        testAverageValue();

        System.out.println("\n=== ПІДСУМОК ===");
        System.out.println("Пройдено: " + passed);
        System.out.println("Провалено: " + failed);
        System.out.println("Всього тестів: " + (passed + failed));
    }

    static void testAddition() {
        double result = Main.add(5, 7);  // Виклик твого методу
        assertEqual(12.0, result, 0.0001, "testAddition");
    }

    static void testSubtraction() {
        double result = Main.subtract(10, 4);
        assertEqual(6.0, result, 0.0001, "testSubtraction");
    }

    static void testDiscountCalculation() {
        double result = Main.calculateDiscount(1000, 10);
        assertEqual(900.0, result, 0.0001, "testDiscountCalculation");
    }

    static void testAverageValue() {
        double[] data = {2, 4, 6, 8, 10};
        double result = Main.average(data);
        assertEqual(6.0, result, 0.0001, "testAverageValue");
    }
    static void assertEqual(double expected, double actual, double tolerance, String testName) {
        if (Math.abs(expected - actual) <= tolerance) {
            System.out.println("testName + " — пройдено (" + actual + ")");
            passed++;
        } else {
            System.out.println("testName + " — провалено");
            System.out.println("   Очікувано: " + expected);
            System.out.println("   Отримано:  " + actual);
            failed++;
        }
    }
}
