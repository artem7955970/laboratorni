import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.Date;

public class Validation {

    public boolean isValidClientName(String clientName) {
        if (clientName == null || clientName.trim().isEmpty()) {
            return false;
        }
        String[] parts = clientName.trim().split("\\s+");
        if (parts.length != 3) {
            return false;
        }
        for (String part : parts) {
            if (part.isEmpty() || !Character.isUpperCase(part.charAt(0))) {
                return false;
            }
            for (int i = 1; i < part.length(); i++) {
                if (!Character.isLowerCase(part.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showErrorMessage(Component parentComponent, String message, String title) {
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showInformationMessage(Component parentComponent, String message, String title) {
        JOptionPane.showMessageDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public int showConfirmationMessage(Component parentComponent, Object message, String title) {
        return JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public boolean areServicesSelected(List<String> selectedServices) {
        return selectedServices != null && !selectedServices.isEmpty();
    }

    public boolean isStreetSelected(String street) {
        return street != null && !street.trim().isEmpty();
    }

    public boolean isHouseNumberValid(String houseNumber) {
        return houseNumber != null && !houseNumber.trim().isEmpty();
    }

    public float parseAndValidateArea(String areaText) {
        if (areaText == null || areaText.trim().isEmpty()) {
            throw new IllegalArgumentException("Площа не може бути порожньою.");
        }
        try {
            float area = Float.parseFloat(areaText.replace(",", "."));
            if (area <= 10) {
                throw new IllegalArgumentException("Площа повинна бути більшою за 10 квадратних метрів.");
            }
            if (area >= 10000) {
                throw new IllegalArgumentException("Площа повинна бути меншою за 10000 квадратних метрів.");
            }
            return area;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Невірний формат площі. Використовуйте числа (наприклад, 100.5).");
        }
    }

    public float parseAndValidatePrice(String priceText) {
        if (priceText == null || priceText.trim().isEmpty()) {
            throw new IllegalArgumentException("Ціна не може бути порожньою.");
        }
        try {
            float price = Float.parseFloat(priceText.replace(",", "."));
            if (price < 0) {
                throw new IllegalArgumentException("Ціна не може бути від'ємною.");
            }
            return price;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Невірний формат ціни. Використовуйте числа (наприклад, 1500.75).");
        }
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\d{9}");
    }

    public Date parseAndValidateDate(String dateText) {
        if (dateText == null || dateText.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
            return Date.valueOf(localDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Невірний формат дати. Використовуйте формат РРРР-ММ-ДД.");
        }
    }
}

