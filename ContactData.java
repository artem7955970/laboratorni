import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactData {

    public String saveClientToDatabase(Connection connection, String clientName, String phoneNumber) throws SQLException {
        String clientId = null;
        String checkClientSql = "SELECT ID FROM Клієнт WHERE ПІБ = ? AND КонтактніДані = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkClientSql)) {
            checkPs.setString(1, clientName);
            checkPs.setString(2, phoneNumber);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    clientId = String.valueOf(rs.getInt("ID"));
                }
            }
        }

        if (clientId == null) {
            String insertClientSql = "INSERT INTO Клієнт (ПІБ, КонтактніДані) VALUES (?, ?)";
            try (PreparedStatement insertPs = connection.prepareStatement(insertClientSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertPs.setString(1, clientName);
                insertPs.setString(2, phoneNumber);
                insertPs.executeUpdate();
                try (ResultSet rs = insertPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        clientId = String.valueOf(rs.getInt(1));
                    }
                }
            }
        }
        return clientId;
    }

    public List<Order.GardenerDisplayItem> getAvailableGardeners(Connection connection) throws SQLException {
        List<Order.GardenerDisplayItem> gardeners = new ArrayList<>();
        String sql = "SELECT s.ID, s.ПІБ, s.КонтактніДані, s.Стать " +
                "FROM Співробітник s " +
                "WHERE TRIM(s.НазваПосади) = 'Садівник' " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 " +
                "    FROM СпівробітникЗамовлення сз " +
                "    JOIN Замовлення z ON сз.IDЗамовлення = z.ID " +
                "    WHERE сз.IDСпівробітник = s.ID AND z.Стан != 'Завершено' " +
                ");";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ID");
                String pib = rs.getString("ПІБ");
                String phoneNumber = rs.getString("КонтактніДані");
                String gender = rs.getString("Стать");
                gardeners.add(new Order.GardenerDisplayItem(id, pib, phoneNumber, gender));
            }
        }
        return gardeners;
    }

    public List<Order.GardenerDisplayItem> getGardenersAssignedToOrder(Connection connection, int orderId) throws SQLException {
        List<Order.GardenerDisplayItem> assignedGardeners = new ArrayList<>();
        String sql = "SELECT s.ID, s.ПІБ, s.КонтактніДані, s.Стать " +
                "FROM Співробітник s " +
                "JOIN СпівробітникЗамовлення sz ON s.ID = sz.IDСпівробітник " +
                "WHERE sz.IDЗамовлення = ? AND s.НазваПосади = 'Садівник'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String pib = rs.getString("ПІБ");
                    String phoneNumber = rs.getString("КонтактніДані");
                    String gender = rs.getString("Стать");
                    assignedGardeners.add(new Order.GardenerDisplayItem(id, pib, phoneNumber, gender));
                }
            }
        }
        return assignedGardeners;
    }
}
