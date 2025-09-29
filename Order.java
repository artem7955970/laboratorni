import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Order {
    private Date completionDate;

    public Order() {

    }


    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public static class OrderDisplayItem {
        private int id;
        private String address;
        private String status;
        private String date;
        private int clientId;
        private String clientPib;
        private float totalCost;
        private int gardenersNeeded;
        private float area;
        private float procurementCost;

        public OrderDisplayItem(int id, String address, String status, String date, int clientId, String clientPib, float totalCost, int gardenersNeeded, float area, float procurementCost) {
            this.id = id;
            this.address = address;
            this.status = status;
            this.date = date;
            this.clientId = clientId;
            this.clientPib = clientPib;
            this.totalCost = totalCost;
            this.gardenersNeeded = gardenersNeeded;
            this.area = area;
            this.procurementCost = procurementCost;
        }

        public int getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }

        public float getTotalCost() {
            return totalCost;
        }

        public int getGardenersNeeded() {
            return gardenersNeeded;
        }

        public float getArea() {
            return area;
        }

        @Override
        public String toString() {
            return String.format("ID: %d, Адреса: %s, ПІБ Клієнта: %s",
                    id, address, clientPib);
        }
    }


    public static class FullOrderDetails {
        private int orderId;
        private String address;
        private String clientName;
        private String phoneNumber;
        private float area;
        private List<String> selectedServiceNames;
        private int clientId;
        private float totalCost;
        private int gardenersNeeded;
        private String orderDate;
        private Date completionDate;
        private String status;

        public FullOrderDetails(int orderId, String address, String clientName, String phoneNumber, float area, List<String> selectedServiceNames, int clientId, float totalCost, int gardenersNeeded, String orderDate, Date completionDate, String status) {
            this.orderId = orderId;
            this.address = address;
            this.clientName = clientName;
            this.phoneNumber = phoneNumber;
            this.area = area;
            this.selectedServiceNames = selectedServiceNames;
            this.clientId = clientId; // Initialize clientId
            this.totalCost = totalCost;
            this.gardenersNeeded = gardenersNeeded;
            this.orderDate = orderDate;
            this.completionDate = completionDate;
            this.status = status;
        }

        public int getOrderId() {
            return orderId;
        }

        public String getAddress() {
            return address;
        }

        public String getClientName() {
            return clientName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public float getArea() {
            return area;
        }

        public List<String> getSelectedServiceNames() {
            return selectedServiceNames;
        }

        public int getClientId() {
            return clientId;
        }

        public float getTotalCost() {
            return totalCost;
        }

        public Date getCompletionDate() {
            return completionDate;
        }
    }

    public static class GardenerOrderDisplayItem {
        private int orderId;
        private String address;
        private String clientPib;
        private float totalCost;
        private Date completionDate;
        private List<String> selectedServiceNames;
        private float area;
        private String status;

        public GardenerOrderDisplayItem(int orderId, String address, String clientPib, float totalCost, Date completionDate, List<String> selectedServiceNames, float area, String status) {
            this.orderId = orderId;
            this.address = address;
            this.clientPib = clientPib;
            this.totalCost = totalCost;
            this.completionDate = completionDate;
            this.selectedServiceNames = selectedServiceNames;
            this.area = area;
            this.status = status;
        }

        public String getAddress() {
            return address;
        }

        public String getClientPib() {
            return clientPib;
        }

        public float getTotalCost() {
            return totalCost;
        }

        public Date getCompletionDate() {
            return completionDate;
        }

        public List<String> getSelectedServiceNames() {
            return selectedServiceNames;
        }

        public float getArea() {
            return area;
        }
    }

    public static class GardenerDisplayItem {
        private int id;
        private String pib;
        private String phoneNumber;
        private String gender;

        public GardenerDisplayItem(int id, String pib, String phoneNumber, String gender) {
            this.id = id;
            this.pib = pib;
            this.phoneNumber = phoneNumber;
            this.gender = gender;
        }

        public int getId() {
            return id;
        }

        public String getPib() {
            return pib;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getGender() {
            return gender;
        }

        @Override
        public String toString() {
            return String.format("ID: %d, ПІБ: %s", id, pib);
        }
    }


    public void createOrder(Connection connection, List<String> selectedServices, String fullAddress, String clientName, String fullPhoneNumber, float area, float overallTotalCost, boolean materialsSufficient) throws SQLException {
        connection.setAutoCommit(false);

        try {
            ContactData clientData = new ContactData();
            String clientIdStr = clientData.saveClientToDatabase(connection, clientName, fullPhoneNumber);
            int clientId = Integer.parseInt(clientIdStr);

            Calc calculator = new Calc();
            int gardenersNeeded = calculator.calculateWorkersNeeded(area);

            String insertOrderSql = "INSERT INTO Замовлення (ДатаУкладання, Стан, IDКлієнта, Адреса, Ціна, КількістьСадівників, ДатаВиконання) VALUES (?, ?, ?, ?, ?, ?, ?)";
            int orderId = -1;
            Date defaultCompletionDate = Date.valueOf(LocalDate.now().plusDays(7));
            try (PreparedStatement psOrder = connection.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setDate(1, Date.valueOf(LocalDate.now()));
                psOrder.setString(2, "Виконується");
                psOrder.setInt(3, clientId);
                psOrder.setString(4, fullAddress);
                psOrder.setFloat(5, overallTotalCost);
                psOrder.setInt(6, gardenersNeeded);

                psOrder.setDate(7, defaultCompletionDate);

                psOrder.executeUpdate();

                ResultSet rs = psOrder.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Не вдалося отримати ID нового замовлення.");
                }
            }

            String insertOrderServiceSql = "INSERT INTO ЗамовленняПослуга (IDЗамовлення, IDПослуга, Обсяг) VALUES (?, ?, ?)";
            try (PreparedStatement psInsertServices = connection.prepareStatement(insertOrderServiceSql)) {
                for (String serviceName : selectedServices) {
                    int serviceId = getServiceIdByName(connection, serviceName);
                    if (serviceId != -1) {
                        psInsertServices.setInt(1, orderId);
                        psInsertServices.setInt(2, serviceId);
                        psInsertServices.setFloat(3, area);
                        psInsertServices.addBatch();
                    }
                }
                psInsertServices.executeBatch();
            }

            if (materialsSufficient) {
                Map<Integer, Material> neededMaterials = new Calc().calculateNeededMaterials(connection, selectedServices, area);
                String updateMaterialQuantitySql = "UPDATE Матеріал SET КількістьВНаявності = КількістьВНаявності - ? WHERE id = ?";
                try (PreparedStatement updatePst = connection.prepareStatement(updateMaterialQuantitySql)) {
                    for (Map.Entry<Integer, Material> entry : neededMaterials.entrySet()) {
                        Material material = entry.getValue();
                        updatePst.setFloat(1, material.getNeededQuantity());
                        updatePst.setInt(2, material.getId());
                        updatePst.addBatch();
                    }
                    updatePst.executeBatch();
                }
            }

            connection.commit();

            StringBuilder successMessage = new StringBuilder("Замовлення створено та збережено:\n");
            successMessage.append("Обрані послуги: ").append(selectedServices).append("\n");
            successMessage.append("Адреса: ").append(fullAddress).append("\n");
            successMessage.append("Площа: ").append(area).append(" кв.м.\n");
            successMessage.append("Розрахована вартість: ").append(String.format("%.2f", overallTotalCost)).append(" грн.\n");
            successMessage.append("ПІБ Клієнта: ").append(clientName).append("\n");
            successMessage.append("Контактні дані: ").append(fullPhoneNumber).append("\n");
            successMessage.append("ID Клієнта: ").append(clientId).append("\n");
            successMessage.append("Статус замовлення: Виконується\n");
            successMessage.append("Дата виконання: ").append(defaultCompletionDate).append("\n");

            if (!materialsSufficient) {
                successMessage.append("\nУвага: Матеріалів недостатньо. Хоча дата виконання встановлена, можливо, знадобиться закупівля.\n");
                Map<Integer, Material> missingMaterials = new Calc().calculateNeededMaterials(connection, selectedServices, area);
                StringBuilder missingMaterialsMessage = new StringBuilder();
                for (Material material : missingMaterials.values()) {
                    if (material.getNeededQuantity() > material.getAvailableQuantity()) {
                        missingMaterialsMessage.append(String.format("- %s: потрібно %.2f %s, в наявності %.2f %s\n",
                                material.getName(), material.getNeededQuantity(), material.getUnitOfMeasure(),
                                material.getAvailableQuantity(), material.getUnitOfMeasure()));
                    }
                }
                successMessage.append(missingMaterialsMessage.toString());
            }

            new Validation().showInformationMessage(null, successMessage.toString(), "Підтвердження замовлення");

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void updateOrder(Connection connection, int orderId, String fullAddress, int clientId, float finalPriceToSave, int updatedGardenersNeeded, List<String> selectedServices, float area, Date newCompletionDate) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String updateOrderSql = "UPDATE Замовлення SET Адреса = ?, IDКлієнта = ?, Ціна = ?, КількістьСадівників = ?, ДатаВиконання = ? WHERE ID = ?";
            try (PreparedStatement psOrder = connection.prepareStatement(updateOrderSql)) {
                psOrder.setString(1, fullAddress);
                psOrder.setInt(2, clientId);
                psOrder.setFloat(3, finalPriceToSave);
                psOrder.setInt(4, updatedGardenersNeeded);
                if (newCompletionDate != null) {
                    psOrder.setDate(5, newCompletionDate);
                } else {
                    psOrder.setNull(5, Types.DATE);
                }
                psOrder.setInt(6, orderId);
                psOrder.executeUpdate();
            }

            String deleteOrderServiceSql = "DELETE FROM ЗамовленняПослуга WHERE IDЗамовлення = ?";
            try (PreparedStatement psDeleteServices = connection.prepareStatement(deleteOrderServiceSql)) {
                psDeleteServices.setInt(1, orderId);
                psDeleteServices.executeUpdate();
            }

            String insertOrderServiceSql = "INSERT INTO ЗамовленняПослуга (IDЗамовлення, IDПослуга, Обсяг) VALUES (?, ?, ?)";
            try (PreparedStatement psInsertServices = connection.prepareStatement(insertOrderServiceSql)) {
                for (String serviceName : selectedServices) {
                    int serviceId = getServiceIdByName(connection, serviceName);
                    if (serviceId != -1) {
                        psInsertServices.setInt(1, orderId);
                        psInsertServices.setInt(2, serviceId);
                        psInsertServices.setFloat(3, area);
                        psInsertServices.addBatch();
                    }
                }
                psInsertServices.executeBatch();
            }
            checkMaterialAndWorkerAvailabilityAndSetCompletionDate(connection, orderId);
            connection.commit();
            new Validation().showInformationMessage(null,
                    "Замовлення ID: " + orderId + " успішно оновлено в базі даних." +
                            "\nНова розрахована вартість: " + String.format("%.2f", finalPriceToSave) + " грн." +
                            "\nОновлена кількість садівників, що потрібна: " + updatedGardenersNeeded +
                            "\nДата виконання: " + (newCompletionDate != null ? newCompletionDate.toString() : "Не встановлена"),
                    "Оновлення успішне");
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void deleteOrder(Connection connection, int orderId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String deleteEmployeeOrderSql = "DELETE FROM СпівробітникЗамовлення WHERE IDЗамовлення = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteEmployeeOrderSql)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }

            String deleteOrderServiceSql = "DELETE FROM ЗамовленняПослуга WHERE IDЗамовлення = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteOrderServiceSql)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }

            String deleteOrderSql = "DELETE FROM Замовлення WHERE ID = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteOrderSql)) {
                ps.setInt(1, orderId);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    new Validation().showInformationMessage(null, "Замовлення з ID: " + orderId + " успішно видалено.", "Успіх");
                } else {
                    new Validation().showErrorMessage(null, "Замовлення з ID: " + orderId + " не знайдено.", "Помилка");
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void completeOrder(Connection connection, int orderId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            FullOrderDetails orderDetails = getFullOrderDetails(connection, orderId);
            if (orderDetails == null) {
                throw new SQLException("Не вдалося знайти деталі замовлення ID: " + orderId);
            }
            Map<Integer, Material> neededMaterials = new Calc().calculateNeededMaterials(connection, orderDetails.getSelectedServiceNames(), orderDetails.getArea());
            String updateMaterialQuantitySql = "UPDATE Матеріал SET КількістьВНаявності = КількістьВНаявності - ? WHERE id = ?";
            try (PreparedStatement updatePst = connection.prepareStatement(updateMaterialQuantitySql)) {
                for (Map.Entry<Integer, Material> entry : neededMaterials.entrySet()) {
                    Material material = entry.getValue();
                    updatePst.setFloat(1, material.getNeededQuantity());
                    updatePst.setInt(2, material.getId());
                    updatePst.addBatch();
                }
                updatePst.executeBatch();
            }
            String insertPaymentSql = "INSERT INTO Оплата (Ціна, ДатаОплати, IDКлієнта) VALUES (?, ?, ?)";
            try (PreparedStatement psPayment = connection.prepareStatement(insertPaymentSql)) {
                psPayment.setFloat(1, orderDetails.getTotalCost());
                psPayment.setDate(2, Date.valueOf(LocalDate.now()));
                psPayment.setInt(3, orderDetails.getClientId());
                psPayment.executeUpdate();
            }
            String updateOrderStatusSql = "UPDATE Замовлення SET Стан = ?, ДатаВиконання = ? WHERE ID = ?";
            try (PreparedStatement psUpdateStatus = connection.prepareStatement(updateOrderStatusSql)) {
                psUpdateStatus.setString(1, "Завершено");
                psUpdateStatus.setDate(2, Date.valueOf(LocalDate.now()));
                psUpdateStatus.setInt(3, orderId);
                psUpdateStatus.executeUpdate();
            }

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public float calculateMaterialProcurementCost(Connection conn, int orderId, float orderArea) throws SQLException {
        float procurementCost = 0.0f;
        FullOrderDetails orderDetails = getFullOrderDetails(conn, orderId);
        if (orderDetails == null) {
            return 0.0f;
        }
        Map<Integer, Material> neededMaterials = new Calc().calculateNeededMaterials(conn, orderDetails.getSelectedServiceNames(), orderArea);
        List<Material> materialsList = new ArrayList<>(neededMaterials.values());
        for (int i = 0; i < materialsList.size(); i++) {
            Material material = materialsList.get(i);
            float quantityToPurchase = Math.max(0, material.getNeededQuantity() - material.getAvailableQuantity());
            procurementCost += quantityToPurchase * material.getCost();
        }
        return procurementCost;
    }

    public void checkMaterialAndWorkerAvailabilityAndSetCompletionDate(Connection connection, int orderId) throws SQLException {
        FullOrderDetails orderDetails = getFullOrderDetails(connection, orderId);
        if (orderDetails == null) {
            return;
        }

        boolean allMaterialsAvailable = (calculateMaterialProcurementCost(connection, orderId, orderDetails.getArea()) == 0.0f);
        boolean enoughWorkersAssigned = areEnoughWorkersAssigned(connection, orderId);
        Date currentDbCompletionDate = orderDetails.getCompletionDate();
        if (allMaterialsAvailable && enoughWorkersAssigned && currentDbCompletionDate == null) {
            String updateCompletionDateSql = "UPDATE Замовлення SET ДатаВиконання = ? WHERE ID = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateCompletionDateSql)) {
                Date newCompletionDate = Date.valueOf(LocalDate.now().plusDays(7));
                ps.setDate(1, newCompletionDate);
                ps.setInt(2, orderId);
                ps.executeUpdate();
                this.setCompletionDate(newCompletionDate);
            }
        }
    }
    public boolean areEnoughWorkersAssigned(Connection connection, int orderId) throws SQLException {
        String sql = "SELECT o.КількістьСадівників, COUNT(sz.IDСпівробітник) AS AssignedWorkers " +
                "FROM Замовлення o " +
                "LEFT JOIN СпівробітникЗамовлення sz ON o.ID = sz.IDЗамовлення AND sz.IDВидДіяльності IN (1, 2) " +
                "WHERE o.ID = ? " +
                "GROUP BY o.ID, o.КількістьСадівників";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int requiredGardeners = rs.getInt("КількістьСадівників");
                    int assignedWorkers = rs.getInt("AssignedWorkers");
                    return assignedWorkers >= requiredGardeners;
                }
            }
        }
        return false;
    }


    public static FullOrderDetails getFullOrderDetails(Connection connection, int orderId) {
        String orderSql = "SELECT o.ID, o.Адреса, k.ПІБ, k.КонтактніДані, o.IDКлієнта, o.Ціна, o.КількістьСадівників, o.ДатаУкладання, o.ДатаВиконання, o.Стан, MAX(ops.Обсяг) AS Обсяг " +
                "FROM Замовлення o JOIN Клієнт k ON o.IDКлієнта = k.ID " +
                "LEFT JOIN ЗамовленняПослуга ops ON o.ID = ops.IDЗамовлення " +
                "WHERE o.ID = ? " +
                "GROUP BY o.ID, o.Адреса, k.ПІБ, k.КонтактніДані, o.IDКлієнта, o.Ціна, o.КількістьСадівників, o.ДатаУкладання, o.ДатаВиконання, o.Стан";

        String servicesSql = "SELECT p.Назва FROM ЗамовленняПослуга ops " +
                "JOIN Послуга p ON ops.IDПослуга = p.ID WHERE ops.IDЗамовлення = ?";

        String address = null;
        String clientName = null;
        String phoneNumber = null;
        float area = 0.0f;
        List<String> selectedServiceNames = new ArrayList<>();
        int clientId = -1;
        float totalCost = 0.0f;
        int gardenersNeeded = 0;
        String orderDate = null;
        Date completionDate = null;
        String status = null;

        try (PreparedStatement psOrder = connection.prepareStatement(orderSql)) {
            psOrder.setInt(1, orderId);
            try (ResultSet rsOrder = psOrder.executeQuery()) {
                if (rsOrder.next()) {
                    address = rsOrder.getString("Адреса");
                    clientName = rsOrder.getString("ПІБ");
                    phoneNumber = rsOrder.getString("КонтактніДані");
                    clientId = rsOrder.getInt("IDКлієнта");
                    totalCost = rsOrder.getFloat("Ціна");
                    gardenersNeeded = rsOrder.getInt("КількістьСадівників");
                    orderDate = rsOrder.getDate("ДатаУкладання").toString();
                    completionDate = rsOrder.getDate("ДатаВиконання");
                    status = rsOrder.getString("Стан");
                    area = rsOrder.getFloat("Обсяг");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Помилка при отриманні деталей замовлення: " + e.getMessage(), "Помилка БД");
            return null;
        }

        try (PreparedStatement psServices = connection.prepareStatement(servicesSql)) {
            psServices.setInt(1, orderId);
            try (ResultSet rsServices = psServices.executeQuery()) {
                while (rsServices.next()) {
                    selectedServiceNames.add(rsServices.getString("Назва"));
                }
            }
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Помилка при отриманні послуг замовлення: " + e.getMessage(), "Помилка БД");
        }

        return new FullOrderDetails(orderId, address, clientName, phoneNumber, area, selectedServiceNames, clientId, totalCost, gardenersNeeded, orderDate, completionDate, status);
    }


    public void attachWorkers(Connection connection, OrderDisplayItem order, List<GardenerDisplayItem> gardeners, int coordinatorId) throws SQLException {
        connection.setAutoCommit(false);

        try {
            FullOrderDetails orderDetails = getFullOrderDetails(connection, order.getId());
            if (orderDetails == null) {
                throw new SQLException("Не вдалося отримати деталі замовлення для призначення.");
            }
            List<String> orderServices = orderDetails.getSelectedServiceNames();

            int representativeGardenerActivityId = -1;
            if (orderServices.contains("Озеленення території")) {
                representativeGardenerActivityId = getServiceActivityId("Озеленення території");
            } else if (orderServices.contains("Підрізання дерев")) {
                representativeGardenerActivityId = getServiceActivityId("Підрізання дерев");
            }

            if (representativeGardenerActivityId == -1 && !orderServices.isEmpty()) {
                representativeGardenerActivityId = 1;
            }

            List<int[]> assignmentsToMake = new ArrayList<>();

            String checkExistingGardenerAssignmentSql = "SELECT COUNT(*) FROM СпівробітникЗамовлення WHERE IDСпівробітник = ? AND IDЗамовлення = ? AND IDВидДіяльності IN (1, 2)";

            for (GardenerDisplayItem gardener : gardeners) {
                boolean alreadyAssigned = false;
                try (PreparedStatement checkPs = connection.prepareStatement(checkExistingGardenerAssignmentSql)) {
                    checkPs.setInt(1, gardener.getId());
                    checkPs.setInt(2, order.getId());
                    ResultSet checkRs = checkPs.executeQuery();
                    if (checkRs.next() && checkRs.getInt(1) > 0) {
                        alreadyAssigned = true;
                    }
                }

                if (!alreadyAssigned && representativeGardenerActivityId != -1) {
                    assignmentsToMake.add(new int[]{gardener.getId(), order.getId(), representativeGardenerActivityId});
                }
            }

            if (!assignmentsToMake.isEmpty()) {
                String insertAssignmentSql = "INSERT INTO СпівробітникЗамовлення (IDСпівробітник, IDЗамовлення, IDВидДіяльності) VALUES (?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertAssignmentSql)) {
                    for (int[] assignment : assignmentsToMake) {
                        ps.setInt(1, assignment[0]);
                        ps.setInt(2, assignment[1]);
                        ps.setInt(3, assignment[2]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            String checkCoordinatorAssignmentSql = "SELECT COUNT(*) FROM СпівробітникЗамовлення WHERE IDСпівробітник = ? AND IDЗамовлення = ? AND IDВидДіяльності = ?";
            boolean coordinatorAlreadyAssigned = false;
            try (PreparedStatement checkCoordPs = connection.prepareStatement(checkCoordinatorAssignmentSql)) {
                checkCoordPs.setInt(1, coordinatorId);
                checkCoordPs.setInt(2, order.getId());
                checkCoordPs.setInt(3, 3);
                ResultSet checkCoordRs = checkCoordPs.executeQuery();
                if (checkCoordRs.next() && checkCoordRs.getInt(1) > 0) {
                    coordinatorAlreadyAssigned = true;
                }
            }

            if (!coordinatorAlreadyAssigned) {
                String insertCoordinatorAssignmentSql = "INSERT INTO СпівробітникЗамовлення (IDСпівробітник, IDЗамовлення, IDВидДіяльності) VALUES (?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertCoordinatorAssignmentSql)) {
                    ps.setInt(1, coordinatorId);
                    ps.setInt(2, order.getId());
                    ps.setInt(3, 3);
                    ps.executeUpdate();
                }
            }
            checkMaterialAndWorkerAvailabilityAndSetCompletionDate(connection, order.getId());

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void unassignGardenersFromOrder(Connection connection, int orderId, List<GardenerDisplayItem> gardenersToUnassign) throws SQLException {
        connection.setAutoCommit(false);
        try {
            String deleteAssignmentSql = "DELETE FROM СпівробітникЗамовлення WHERE IDСпівробітник = ? AND IDЗамовлення = ? AND IDВидДіяльності IN (1, 2)";
            try (PreparedStatement ps = connection.prepareStatement(deleteAssignmentSql)) {
                for (GardenerDisplayItem gardener : gardenersToUnassign) {
                    ps.setInt(1, gardener.getId());
                    ps.setInt(2, orderId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            checkMaterialAndWorkerAvailabilityAndSetCompletionDate(connection, orderId);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
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

    private int getServiceActivityId(String serviceName) {
        switch (serviceName) {
            case "Підрізання дерев":
                return 1;
            case "Озеленення території":
                return 2;
            case "Призначення":
                return 3;
            default:
                return -1;
        }
    }

    public void openPurchaseDialog(final JFrame materialCalculationFrame, final Connection conn, final Map<Integer, Material> neededMaterials, final int orderId) {
        final JFrame purchaseFrame = new JFrame("Закупівля Матеріалів");
        purchaseFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        purchaseFrame.setSize(800, 500);
        purchaseFrame.setLocationRelativeTo(null);
        purchaseFrame.setLayout(new BorderLayout(10, 10));
        purchaseFrame.getContentPane().setBackground(new Color(229, 241, 224));

        final DefaultTableModel purchaseModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int intColumn) {
                return intColumn == 1 || intColumn == 2;
            }
        };
        purchaseModel.addColumn("Матеріал");
        purchaseModel.addColumn("Кількість для закупівлі");
        purchaseModel.addColumn("Ціна за одиницю");
        purchaseModel.addColumn("Одиниця виміру");
        purchaseModel.addColumn("Загальна вартість закупівлі");

        for (Material material : neededMaterials.values()) {
            float quantityToPurchase = Math.max(0, material.getNeededQuantity() - material.getAvailableQuantity());
            purchaseModel.addRow(new Object[]{
                    material.getName(),
                    String.format("%.2f", quantityToPurchase),
                    String.format("%.2f", material.getCost()),
                    material.getUnitOfMeasure(),
                    String.format("%.2f", quantityToPurchase * material.getCost())
            });
        }

        JTable purchaseTable = new JTable(purchaseModel);
        purchaseTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(purchaseTable);

        final JLabel totalPurchaseCostLabel = new JLabel("Загальна вартість закупівлі: 0.00 грн");
        totalPurchaseCostLabel.setFont(new Font("Serif", Font.BOLD, 16));
        totalPurchaseCostLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final Runnable recalculateTotalPurchaseCost = () -> {
            float currentTotalPurchaseCost = 0.0f;
            for (int i = 0; i < purchaseModel.getRowCount(); i++) {
                try {
                    String quantityString = purchaseModel.getValueAt(i, 1).toString().replace(",", ".");
                    float quantity = Float.parseFloat(quantityString);

                    String unitPriceString = purchaseModel.getValueAt(i, 2).toString().replace(",", ".");
                    float unitPrice = Float.parseFloat(unitPriceString);

                    currentTotalPurchaseCost += quantity * unitPrice;
                } catch (NumberFormatException ex) {
                }
            }
            totalPurchaseCostLabel.setText(String.format("Загальна вартість закупівлі: %.2f грн", currentTotalPurchaseCost));
        };

        purchaseModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 1 || column == 2) {
                    try {
                        String quantityString = purchaseModel.getValueAt(row, 1).toString().replace(",", ".");
                        float purchaseAmount = Float.parseFloat(quantityString);

                        String pricePerUnitString = purchaseModel.getValueAt(row, 2).toString().replace(",", ".");
                        float pricePerUnit = Float.parseFloat(pricePerUnitString);

                        float totalRowCost = purchaseAmount * pricePerUnit;

                        purchaseModel.setValueAt(String.format("%.2f", totalRowCost), row, 4);
                        recalculateTotalPurchaseCost.run();
                    } catch (NumberFormatException ex) {
                        new Validation().showErrorMessage(purchaseFrame, "Будь ласка, введіть дійсне число.", "Помилка введення");
                        if (column == 1) purchaseModel.setValueAt(String.format("%.2f", 0.0f), row, 1);
                        if (column == 2) purchaseModel.setValueAt(String.format("%.2f", 0.0f), row, 2);
                        purchaseModel.setValueAt(String.format("%.2f", 0.0f), row, 4);
                        recalculateTotalPurchaseCost.run();
                    }
                }
            }
        });

        recalculateTotalPurchaseCost.run();

        JButton confirmPurchaseButton = new JButton("Підтвердити Закупівлю");
        confirmPurchaseButton.setForeground(Color.BLACK);
        confirmPurchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float finalPurchaseCost = 0.0f;
                try {
                    finalPurchaseCost = Float.parseFloat(totalPurchaseCostLabel.getText().replace("Загальна вартість закупівлі: ", "").replace(" грн", "").replace(",", "."));
                } catch (NumberFormatException ex) {
                    new Validation().showErrorMessage(purchaseFrame, "Помилка розрахунку загальної вартості. Спробуйте ще раз.", "Помилка");
                    return;
                }

                if (finalPurchaseCost <= 0.0f) {
                    new Validation().showInformationMessage(purchaseFrame, "Закупівля не потрібна, оскільки загальна вартість дорівнює 0.", "Інформація");
                    return;
                }

                try {
                    conn.setAutoCommit(false);

                    String insertPurchaseSql = "INSERT INTO Закупівля (Дата) VALUES (?)";
                    int purchaseId = -1;
                    try (PreparedStatement pstmt = conn.prepareStatement(insertPurchaseSql, Statement.RETURN_GENERATED_KEYS)) {
                        pstmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                        pstmt.executeUpdate();
                        ResultSet rs = pstmt.getGeneratedKeys();
                        if (rs.next()) {
                            purchaseId = rs.getInt(1);
                        } else {
                            throw new SQLException("Не вдалося отримати ID закупівлі.");
                        }
                    }

                    String insertMaterialPurchaseSql = "INSERT INTO МатеріалЗакупівля (IDМатеріал, IDЗакупівля, Кількість) VALUES (?, ?, ?)";
                    String updateMaterialQuantitySql = "UPDATE Матеріал SET КількістьВНаявності = КількістьВНаявності + ? WHERE id = ?";

                    try (PreparedStatement insertPst = conn.prepareStatement(insertMaterialPurchaseSql);
                         PreparedStatement updatePst = conn.prepareStatement(updateMaterialQuantitySql)) {

                        for (int i = 0; i < purchaseModel.getRowCount(); i++) {
                            String materialName = (String) purchaseModel.getValueAt(i, 0);
                            String quantityString = purchaseModel.getValueAt(i, 1).toString().replace(",", ".");
                            float quantityToBuyFloat = Float.parseFloat(quantityString);

                            if (quantityToBuyFloat > 0) {
                                int materialId = getMaterialIdByName(conn, materialName);

                                if (materialId != -1) {
                                    insertPst.setInt(1, materialId);
                                    insertPst.setInt(2, purchaseId);
                                    insertPst.setFloat(3, quantityToBuyFloat);
                                    insertPst.addBatch();

                                    updatePst.setFloat(1, quantityToBuyFloat);
                                    updatePst.setInt(2, materialId);
                                    updatePst.addBatch();
                                } else {
                                    new Validation().showErrorMessage(purchaseFrame, "Матеріал '" + materialName + "' не знайдено в базі даних.", "Помилка");
                                }
                            }
                        }
                        insertPst.executeBatch();
                        updatePst.executeBatch();
                    }

                    conn.commit();
                    new Validation().showInformationMessage(purchaseFrame, "Закупівлю успішно виконано!", "Успіх");
                    purchaseFrame.dispose();
                    if (materialCalculationFrame != null) {
                        materialCalculationFrame.dispose();
                    }
                    checkMaterialAndWorkerAvailabilityAndSetCompletionDate(conn, orderId);
                    FullOrderDetails currentOrderDetails = getFullOrderDetails(conn, orderId);
                    if (currentOrderDetails != null) {
                        Component actualParent = (materialCalculationFrame instanceof JFrame) ? materialCalculationFrame.getParent() : materialCalculationFrame;
                        displayMaterialCalculation(actualParent, conn, currentOrderDetails.getOrderId(), currentOrderDetails.getArea(), currentOrderDetails.getSelectedServiceNames());
                    }



                } catch (SQLException | NumberFormatException ex) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        new Validation().showErrorMessage(purchaseFrame, "Помилка відкату транзакції: " + rollbackEx.getMessage(), "Помилка БД");
                    }
                    new Validation().showErrorMessage(purchaseFrame, "Помилка при здійсненні закупівлі: " + ex.getMessage(), "Помилка");
                } finally {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException finalEx) {
                        new Validation().showErrorMessage(purchaseFrame, "Помилка при поверненні автокоміту: " + finalEx.getMessage(), "Помилка БД");
                    }
                }
            }
        });

        JPanel purchaseBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        purchaseBottomPanel.setBackground(new Color(229, 241, 224));
        purchaseBottomPanel.add(totalPurchaseCostLabel);
        purchaseBottomPanel.add(confirmPurchaseButton);

        purchaseFrame.add(scrollPane, BorderLayout.CENTER);
        purchaseFrame.add(purchaseBottomPanel, BorderLayout.SOUTH);
        purchaseFrame.setVisible(true);
    }

    public void displayMaterialCalculation(Component parentComponent, final Connection conn, final int orderId, final float area, final List<String> selectedServiceNames) {
        final float orderArea = area;

        if (orderArea <= 0) {
            new Validation().showInformationMessage(parentComponent, "Площа замовлення не визначена або дорівнює 0. Розрахунок матеріалів неможливий.", "Інформація");
            return;
        }

        Map<Integer, Material> aggregatedMaterials = null;
        try {
            aggregatedMaterials = new Calc().calculateNeededMaterials(conn, selectedServiceNames, orderArea);
        } catch (SQLException e) {
            new Validation().showErrorMessage(parentComponent, "Помилка при розрахунку необхідних матеріалів: " + e.getMessage(), "Помилка БД");
            return;
        }

        final Map<Integer, Material> finalAggregatedMaterials = aggregatedMaterials;

        final DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 3;
            }
        };
        model.addColumn("Назва Матеріалу");
        model.addColumn("Необхідна Кількість (для замовлення)");
        model.addColumn("В наявності");
        model.addColumn("Ціна за одиницю");
        model.addColumn("Одиниця виміру");
        model.addColumn("Вартість закупівлі (для цієї позиції)");

        float totalProcurementCost = 0.0f;

        for (Material material : finalAggregatedMaterials.values()) {
            float quantityToPurchase = Math.max(0, material.getNeededQuantity() - material.getAvailableQuantity());
            float totalMaterialProcurementCost = quantityToPurchase * material.getCost();
            totalProcurementCost += totalMaterialProcurementCost;

            model.addRow(new Object[]{
                    material.getName(),
                    String.format("%.2f", material.getNeededQuantity()),
                    String.format("%.2f", material.getAvailableQuantity()),
                    String.format("%.2f", material.getCost()),
                    material.getUnitOfMeasure(),
                    String.format("%.2f", totalMaterialProcurementCost)
            });
        }

        final JFrame frame = new JFrame("Розрахунок Матеріалів для Замовлення ID: " + orderId);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(229, 241, 224));

        JTable materialTable = new JTable(model);
        materialTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(materialTable);

        final JLabel totalPurchaseCostLabelInDisplay = new JLabel(String.format("Загальна вартість закупівлі: %.2f грн", totalProcurementCost));
        totalPurchaseCostLabelInDisplay.setFont(new Font("Serif", Font.BOLD, 16));
        totalPurchaseCostLabelInDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final Runnable recalculateTotalPurchaseCostInDisplay = () -> {
            float currentTotalPurchaseCost = 0.0f;
            for (int i = 0; i < model.getRowCount(); i++) {
                try {
                    String neededQuantityStr = model.getValueAt(i, 1).toString().replace(",", ".");
                    float neededQuantity = Float.parseFloat(neededQuantityStr);

                    String availableQuantityStr = model.getValueAt(i, 2).toString().replace(",", ".");
                    float availableQuantity = Float.parseFloat(availableQuantityStr);

                    String unitPriceString = model.getValueAt(i, 3).toString().replace(",", ".");
                    float unitPrice = Float.parseFloat(unitPriceString);

                    float quantityToPurchase = Math.max(0, neededQuantity - availableQuantity);
                    currentTotalPurchaseCost += quantityToPurchase * unitPrice;

                    model.setValueAt(String.format("%.2f", quantityToPurchase * unitPrice), i, 5);

                } catch (NumberFormatException ex) {
                    new Validation().showErrorMessage(frame, "Будь ласка, введіть дійсне число.", "Помилка введення");
                    model.setValueAt(String.format("%.2f", 0.0f), i, 5);
                }
            }
            totalPurchaseCostLabelInDisplay.setText(String.format("Загальна вартість закупівлі: %.2f грн", currentTotalPurchaseCost));
        };

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 1 || column == 3) {
                    recalculateTotalPurchaseCostInDisplay.run();
                }
            }
        });

        recalculateTotalPurchaseCostInDisplay.run();

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(229, 241, 224));
        bottomPanel.add(totalPurchaseCostLabelInDisplay, BorderLayout.WEST);

        JButton purchaseButton = new JButton("Закупівля Матеріалів");
        purchaseButton.setForeground(Color.BLACK);
        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPurchaseDialog(frame, conn, finalAggregatedMaterials, orderId);
            }
        });

        bottomPanel.add(purchaseButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private int getMaterialIdByName(Connection conn, String materialName) throws SQLException {
        String sql = "SELECT id FROM Матеріал WHERE Назва = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, materialName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }
    public void displayGardenerOrderInfo(Connection connection, int gardenerId, JPanel orderDisplayPanel) throws SQLException {
        orderDisplayPanel.removeAll();

        GardenerOrderDisplayItem assignedOrder = getGardenerAssignedOrder(connection, gardenerId);
        if (assignedOrder == null) {
            JLabel noOrderLabel = new JLabel("Садівник не має призначених замовлень.");
            noOrderLabel.setFont(new Font("Arial", Font.BOLD, 36));
            noOrderLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noOrderLabel.setForeground(Color.GRAY);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            orderDisplayPanel.add(noOrderLabel, gbc);
        } else {
            Font detailFont = new Font("Arial", Font.PLAIN, 20);
            Font boldDetailFont = new Font("Arial", Font.BOLD, 20);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 10, 10, 10);

            int y = 0;
            gbc.gridx = 0;
            gbc.gridy = y++;
            JLabel addressLabel = new JLabel("Адреса: ");
            addressLabel.setFont(boldDetailFont);
            orderDisplayPanel.add(addressLabel, gbc);
            gbc.gridx = 1;
            JLabel addressValue = new JLabel(assignedOrder.getAddress());
            addressValue.setFont(detailFont);
            orderDisplayPanel.add(addressValue, gbc);

            gbc.gridx = 0;
            gbc.gridy = y++;
            JLabel clientLabel = new JLabel("Клієнт: ");
            clientLabel.setFont(boldDetailFont);
            orderDisplayPanel.add(clientLabel, gbc);
            gbc.gridx = 1;
            JLabel clientValue = new JLabel(assignedOrder.getClientPib());
            clientValue.setFont(detailFont);
            orderDisplayPanel.add(clientValue, gbc);

            gbc.gridx = 0;
            gbc.gridy = y++;
            JLabel servicesLabel = new JLabel("Послуги: ");
            servicesLabel.setFont(boldDetailFont);
            orderDisplayPanel.add(servicesLabel, gbc);
            gbc.gridx = 1;
            JLabel servicesValue = new JLabel(String.join(", ", assignedOrder.getSelectedServiceNames()));
            servicesValue.setFont(detailFont);
            orderDisplayPanel.add(servicesValue, gbc);

            gbc.gridx = 0;
            gbc.gridy = y++;
            JLabel areaLabel = new JLabel("Площа: ");
            areaLabel.setFont(boldDetailFont);
            orderDisplayPanel.add(areaLabel, gbc);
            gbc.gridx = 1;
            JLabel areaValue = new JLabel(String.format("%.2f кв.м", assignedOrder.getArea()));
            areaValue.setFont(detailFont);
            orderDisplayPanel.add(areaValue, gbc);

            gbc.gridx = 0;
            gbc.gridy = y++;
            JLabel totalCostLabel = new JLabel("Загальна вартість: ");
            totalCostLabel.setFont(boldDetailFont);
            orderDisplayPanel.add(totalCostLabel, gbc);
            gbc.gridx = 1;
            JLabel totalCostValue = new JLabel(String.format("%.2f грн", assignedOrder.getTotalCost()));
            totalCostValue.setFont(detailFont);
            orderDisplayPanel.add(totalCostValue, gbc);

            gbc.gridx = 0;
            gbc.gridy = y++;
            JLabel completionDateLabel = new JLabel("Очікувана дата виконання: ");
            completionDateLabel.setFont(boldDetailFont);
            orderDisplayPanel.add(completionDateLabel, gbc);
            gbc.gridx = 1;
            JLabel completionDateValue = new JLabel(assignedOrder.getCompletionDate() != null ? assignedOrder.getCompletionDate().toString() : "Не встановлена (недостатньо матеріалів або робітників)");
            completionDateValue.setFont(detailFont);
            orderDisplayPanel.add(completionDateValue, gbc);
        }
        orderDisplayPanel.revalidate();
        orderDisplayPanel.repaint();
    }

    public static GardenerOrderDisplayItem getGardenerAssignedOrder(Connection connection, int gardenerId) throws SQLException {
        String sql = "SELECT o.ID, o.Адреса, o.Ціна, o.ДатаВиконання, k.ПІБ, MAX(zp.Обсяг) AS Обсяг, o.Стан " +
                "FROM Замовлення o " +
                "JOIN СпівробітникЗамовлення сз ON o.ID = сз.IDЗамовлення " +
                "JOIN Клієнт k ON o.IDКлієнта = k.ID " +
                "LEFT JOIN ЗамовленняПослуга zp ON o.ID = zp.IDЗамовлення " +
                "WHERE сз.IDСпівробітник = ? AND o.Стан = 'Виконується' " +
                "GROUP BY o.ID, o.Адреса, o.Ціна, o.ДатаВиконання, k.ПІБ, o.Стан " +
                "LIMIT 1";

        int orderId = -1;
        String address = null;
        String clientPib = null;
        float totalCost = 0.0f;
        Date completionDate = null;
        float orderArea = 0.0f;
        String status = null;
        List<String> selectedServiceNames = new ArrayList<>();

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, gardenerId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    orderId = rs.getInt("ID");
                    address = rs.getString("Адреса");
                    totalCost = rs.getFloat("Ціна");
                    completionDate = rs.getDate("ДатаВиконання");
                    clientPib = rs.getString("ПІБ");
                    orderArea = rs.getFloat("Обсяг");
                    status = rs.getString("Стан");

                    String servicesSql = "SELECT p.Назва FROM ЗамовленняПослуга ops " +
                            "JOIN Послуга p ON ops.IDПослуга = p.ID WHERE ops.IDЗамовлення = ?";
                    try (PreparedStatement psServices = connection.prepareStatement(servicesSql)) {
                        psServices.setInt(1, orderId);
                        try (ResultSet rsServices = psServices.executeQuery()) {
                            while (rsServices.next()) {
                                selectedServiceNames.add(rsServices.getString("Назва"));
                            }
                        }
                    }
                    return new GardenerOrderDisplayItem(orderId, address, clientPib, totalCost, completionDate, selectedServiceNames, orderArea, status);
                }
            }
        }
        return null;
    }

}
