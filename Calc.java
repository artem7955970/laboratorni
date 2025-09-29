import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calc {
    private float size;

    public Calc() {
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float calculateTotalCost(List<String> selectedServices) {
        float totalCost = 0;
        for (String service : selectedServices) {
            switch (service) {
                case "Озеленення території":
                    totalCost += this.size * 150;
                    break;
                case "Підрізання дерев":
                    totalCost += this.size * 50;
                    break;
            }
        }
        return totalCost;
    }

    public int calculateWorkersNeeded(float area) {
        if (area <= 50) {
            return 1;
        } else if (area <= 200) {
            return 2;
        } else if (area <= 500) {
            return 3;
        } else {
            return 4;
        }
    }

    public Map<Integer, Material> calculateNeededMaterials(Connection conn, List<String> selectedServiceNames, float orderArea) throws SQLException {
        Map<Integer, Material> aggregatedMaterials = new HashMap<>();
        if (orderArea <= 0 || selectedServiceNames == null || selectedServiceNames.isEmpty()) {
            return aggregatedMaterials;
        }

        List<Integer> serviceIds = new java.util.ArrayList<>();
        for (String serviceName : selectedServiceNames) {
            int serviceId = getServiceIdByName(conn, serviceName);
            if (serviceId != -1) {
                serviceIds.add(serviceId);
            }
        }

        if (serviceIds.isEmpty()) {
            return aggregatedMaterials;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append("m.id AS MaterialID, ");
        sqlBuilder.append("m.Назва AS НазваМатеріалу, ");
        sqlBuilder.append("pm.Кількість AS КількістьНаБазу, ");
        sqlBuilder.append("m.Ціна AS ЦінаЗаОдиницю, ");
        sqlBuilder.append("m.ОдиницяВиміру AS ОдиницяВиміру, ");
        sqlBuilder.append("m.КількістьВНаявності AS КількістьВНаявності ");
        sqlBuilder.append("FROM ПослугаМатеріал pm ");
        sqlBuilder.append("JOIN Матеріал m ON pm.IDМатеріал = m.id ");
        sqlBuilder.append("WHERE pm.IDПослуга IN (");
        for (int i = 0; i < serviceIds.size(); i++) {
            sqlBuilder.append("?");
            if (i < serviceIds.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < serviceIds.size(); i++) {
                pstmt.setInt(i + 1, serviceIds.get(i));
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int materialId = rs.getInt("MaterialID");
                String materialName = rs.getString("НазваМатеріалу");
                float quantityBase = rs.getFloat("КількістьНаБазу");
                float pricePerUnit = rs.getFloat("ЦінаЗаОдиницю");
                String unitOfMeasure = rs.getString("ОдиницяВиміру");
                float availableQuantity = rs.getFloat("КількістьВНаявності");

                float calculatedNeededQuantity = (quantityBase / Main.MATERIAL_QUANTITY_BASE_SQM) * orderArea;

                if (aggregatedMaterials.containsKey(materialId)) {
                    Material existingMaterial = aggregatedMaterials.get(materialId);
                    existingMaterial.setNeededQuantity(existingMaterial.getNeededQuantity() + calculatedNeededQuantity);
                } else {
                    Material newMaterial = new Material(materialId, materialName, pricePerUnit, availableQuantity, unitOfMeasure);
                    newMaterial.setNeededQuantity(calculatedNeededQuantity);
                    aggregatedMaterials.put(materialId, newMaterial);
                }
            }
        }
        return aggregatedMaterials;
    }


    public float calculateTotalMaterialsCostForOrder(Connection conn, List<String> selectedServices, float orderArea) throws SQLException {
        float totalMaterialsCost = 0.0f;
        if (orderArea <= 0 || selectedServices == null || selectedServices.isEmpty()) {
            return 0.0f;
        }

        List<Integer> serviceIds = new java.util.ArrayList<>();
        for (String serviceName : selectedServices) {
            int serviceId = getServiceIdByName(conn, serviceName);
            if (serviceId != -1) {
                serviceIds.add(serviceId);
            }
        }

        if (serviceIds.isEmpty()) {
            return 0.0f;
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append("pm.Кількість AS КількістьНаБазу, ");
        sqlBuilder.append("m.Ціна AS ЦінаЗаОдиницю ");
        sqlBuilder.append("FROM ПослугаМатеріал pm ");
        sqlBuilder.append("JOIN Матеріал m ON pm.IDМатеріал = m.id ");
        sqlBuilder.append("WHERE pm.IDПослуга IN (");
        for (int i = 0; i < serviceIds.size(); i++) {
            sqlBuilder.append("?");
            if (i < serviceIds.size() - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(")");

        try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < serviceIds.size(); i++) {
                pstmt.setInt(i + 1, serviceIds.get(i));
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                float quantityBase = rs.getFloat("КількістьНаБазу");
                float pricePerUnit = rs.getFloat("ЦінаЗаОдиницю");
                float neededQuantity = (quantityBase / Main.MATERIAL_QUANTITY_BASE_SQM) * orderArea;
                totalMaterialsCost += neededQuantity * pricePerUnit;
            }
        }
        return totalMaterialsCost;
    }

    private int getServiceIdByName(Connection connection, String serviceName) throws SQLException {
        String sql = "SELECT id FROM Послуга WHERE Назва = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serviceName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    return -1;
                }
            }
        }
    }
}
