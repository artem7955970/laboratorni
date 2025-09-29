import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.Map;

public class Main {

    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/kursova_schema";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "795597Artem";

    public static final float MATERIAL_QUANTITY_BASE_SQM = 100.0f;

    private static void showInstructions(String title, String instructionsText) {
        JFrame instructionFrame = new JFrame(title);
        instructionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        instructionFrame.setSize(600, 400);
        instructionFrame.setLocationRelativeTo(null);
        instructionFrame.setLayout(new BorderLayout(10, 10));
        instructionFrame.getContentPane().setBackground(new Color(229, 241, 224));

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText(instructionsText);
        textPane.setEditable(false);
        textPane.setFocusable(false);
        textPane.setBackground(new Color(229, 241, 224));
        textPane.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        instructionFrame.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Закрити");
        closeButton.setFont(new Font("Arial", Font.BOLD, 18));
        closeButton.setBackground(new Color(52, 152, 219));
        closeButton.setForeground(Color.BLACK);
        closeButton.addActionListener(e -> instructionFrame.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(229, 241, 224));
        buttonPanel.add(closeButton);
        instructionFrame.add(buttonPanel, BorderLayout.SOUTH);

        instructionFrame.setVisible(true);
    }

    private static void openEmployeeDashboard(String employeePib, String activityType, Connection connection, int employeeId) {
        if ("Адміністратор-Замовлень".equals(activityType)) {
            openNewWindow(employeePib, connection);
        } else if ("Координатор-Завдань".equals(activityType)) {
            openCoordinatorWindow(employeePib, connection, employeeId);
        } else if ("Бухгалтер".equals(activityType)) {
            openAccountantWindow(employeePib, connection);
        } else if ("Садівник".equals(activityType)) {
            openGardenerDashboard(employeePib, connection, employeeId);
        } else {
            new Validation().showErrorMessage(null, "Невідомий вид діяльності: " + activityType, "Помилка авторизації");
        }
    }

    private static void openNewWindow(String employeePib, Connection connection) {
        JFrame newFrame = new JFrame("Вікно адміністратора замовлень");
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setSize(1600, 1000);
        newFrame.setLocationRelativeTo(null);
        newFrame.setLayout(null);
        newFrame.getContentPane().setBackground(new Color(204, 229, 204));

        JLabel employeeLabel = new JLabel("Ви увішли як: " + employeePib);
        employeeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        employeeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        employeeLabel.setBounds(0, 30, newFrame.getWidth(), 40);
        newFrame.add(employeeLabel);

        JButton instructionsButton = new JButton("I");
        instructionsButton.setFont(new Font("Arial", Font.BOLD, 24));
        instructionsButton.setBounds(newFrame.getWidth() - 90, 20, 60, 60);
        instructionsButton.setBackground(new Color(231, 76, 60));
        instructionsButton.setForeground(Color.BLACK);
        instructionsButton.setFocusPainted(false);
        instructionsButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        newFrame.add(instructionsButton);

        instructionsButton.addActionListener(e -> {
            String instructions = "<html><body style='font-family: Arial; font-size: 16px;'>" +
                    "<h2>Вікно Адміністратора-Замовлень:</h2>" +
                    "<ul>" +
                    "<li><b>\"Створити замовлення\"</b>: Відкриває форму для створення нового замовлення.</li>" +
                    "<li><b>\"Редагувати Замовлення\"</b>: Дозволяє вибрати і змінити деталі існуючого замовлення.</li>" +
                    "<li><b>\"Видалити Замовлення\"</b>: Дозволяє вибрати і видалити існуюче замовлення.</li>" +
                    "<li><b><b>\"Завершити Замовлення\"</b>: Дозволяє вибрати замовлення та позначити його як завершене, додавши запис про оплату.</li>" +
                    "<li><b>\"Вийти\"</b>: Закриває програму.</li>" +
                    "</ul>" +
                    "</body></html>";
            showInstructions("Інструкції для Адміністратора-Замовлень", instructions);
        });

        int buttonY = 200;
        int buttonWidth = 450;
        int buttonHeight = 100;
        int verticalPadding = 20;
        int buttonX = (newFrame.getWidth() - buttonWidth) / 2;

        JButton createOrderButton = new JButton("Створити замовлення");
        createOrderButton.setFont(new Font("Arial", Font.BOLD, 30));
        createOrderButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        createOrderButton.setBackground(new Color(52, 152, 219));
        createOrderButton.setForeground(Color.BLACK);
        newFrame.add(createOrderButton);
        buttonY += buttonHeight + verticalPadding;

        createOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreateOrderWindow(connection);
            }
        });

        JButton editOrderButton = new JButton("Редагувати Замовлення");
        editOrderButton.setFont(new Font("Arial", Font.BOLD, 30));
        editOrderButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        editOrderButton.setBackground(new Color(241, 196, 15));
        editOrderButton.setForeground(Color.BLACK);
        newFrame.add(editOrderButton);
        buttonY += buttonHeight + verticalPadding;

        editOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEditOrderWindow(connection);
            }
        });

        JButton deleteOrderButton = new JButton("Видалити Замовлення");
        deleteOrderButton.setFont(new Font("Arial", Font.BOLD, 30));
        deleteOrderButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        deleteOrderButton.setBackground(new Color(192, 57, 43));
        deleteOrderButton.setForeground(Color.BLACK);
        newFrame.add(deleteOrderButton);
        buttonY += buttonHeight + verticalPadding;

        deleteOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOrder(connection);
            }
        });

        JButton completeOrderButton = new JButton("Завершити Замовлення");
        completeOrderButton.setFont(new Font("Arial", Font.BOLD, 30));
        completeOrderButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        completeOrderButton.setBackground(new Color(46, 204, 113));
        completeOrderButton.setForeground(Color.BLACK);
        newFrame.add(completeOrderButton);
        buttonY += buttonHeight + verticalPadding;

        completeOrderButton.addActionListener(e -> {
            List<Order.OrderDisplayItem> orders = getAllOrders(connection);
            List<Order.OrderDisplayItem> completableOrders = new ArrayList<>();
            StringBuilder infoMessage = new StringBuilder();

            for (Order.OrderDisplayItem order : orders) {
                if (!"Завершено".equalsIgnoreCase(order.getStatus())) {
                    completableOrders.add(order);
                }
            }

            if (completableOrders.isEmpty()) {
                infoMessage.append("Немає замовлень для завершення.");
                infoMessage.append("\nМожливо, у замовленнях ще не визначено робітників, недостатньо матеріалів, або не встановлено дату виконання.");
                new Validation().showInformationMessage(newFrame, infoMessage.toString(), "Інформація");
                return;
            }

            JComboBox<Order.OrderDisplayItem> orderComboBox = new JComboBox<>(completableOrders.toArray(new Order.OrderDisplayItem[0]));
            orderComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBackground(new Color(229, 241, 224));
            JLabel selectOrderLabel = new JLabel("Виберіть замовлення для завершення:");
            selectOrderLabel.setFont(new Font("Arial", Font.BOLD, 18));
            selectOrderLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(selectOrderLabel, BorderLayout.NORTH);
            panel.add(orderComboBox, BorderLayout.CENTER);

            int result = new Validation().showConfirmationMessage(newFrame, panel, "Завершити Замовлення");

            if (result == JOptionPane.OK_OPTION) {
                Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
                if (selectedOrder != null) {
                    int orderIdToComplete = selectedOrder.getId();
                    String currentStatus = selectedOrder.getStatus();

                    if ("Завершено".equalsIgnoreCase(currentStatus)) {
                        new Validation().showInformationMessage(newFrame, "Це замовлення вже завершено.", "Інформація");
                        return;
                    }

                    Order orderLogic = new Order();
                    try {

                        float procurementCost = orderLogic.calculateMaterialProcurementCost(connection, orderIdToComplete, selectedOrder.getArea());
                        if (procurementCost > 0.0f) {
                            new Validation().showErrorMessage(newFrame, String.format("Не всі матеріали для цього замовлення в наявності. Вартість закупівлі: %.2f грн. Будь ласка, дочекайтеся закупівлі матеріалів.", procurementCost), "Помилка");
                            return;
                        }

                        if (!orderLogic.areEnoughWorkersAssigned(connection, orderIdToComplete)) {
                            new Validation().showErrorMessage(newFrame, "Для завершення замовлення ID: " + orderIdToComplete + " необхідно призначити достатню кількість садівників.", "Помилка");
                            return;
                        }

                        Order.FullOrderDetails fullDetails = Order.getFullOrderDetails(connection, orderIdToComplete);
                        if (fullDetails == null || fullDetails.getCompletionDate() == null) {
                            new Validation().showErrorMessage(newFrame, "Дата виконання для замовлення ID: " + orderIdToComplete + " не встановлена. Замовлення не може бути завершено.", "Помилка: Невизначена дата виконання");
                            return;
                        }

                    } catch (SQLException ex) {
                        new Validation().showErrorMessage(newFrame, "Помилка при перевірці умов завершення замовлення: " + ex.getMessage(), "Помилка БД");
                        return;
                    }

                    int confirmFinal = new Validation().showConfirmationMessage(newFrame,
                            "Ви впевнені, що хочете завершити замовлення ID: " + orderIdToComplete + "?",
                            "Підтвердження завершення");

                    if (confirmFinal == JOptionPane.YES_OPTION) {
                        try {
                            orderLogic.completeOrder(connection, orderIdToComplete);
                            new Validation().showInformationMessage(newFrame, "Замовлення ID: " + orderIdToComplete + " успішно завершено!", "Успіх");
                        } catch (SQLException ex) {
                            new Validation().showErrorMessage(newFrame, "Помилка при завершенні замовлення: " + ex.getMessage(), "Помилка БД");
                        }
                    }
                }
            }
        });

        JButton exitAppButton = new JButton("Вийти");
        exitAppButton.setFont(new Font("Arial", Font.BOLD, 30));
        exitAppButton.setBounds(630, buttonY, 350, buttonHeight);
        exitAppButton.setBackground(new Color(231, 76, 60));
        exitAppButton.setForeground(Color.BLACK);
        newFrame.add(exitAppButton);

        exitAppButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmResult = new Validation().showConfirmationMessage(
                        newFrame,
                        "Ви впевнені, що хочете вийти з програми?",
                        "Підтвердження виходу"
                );

                if (confirmResult == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        newFrame.setVisible(true);
    }

    private static void openCoordinatorWindow(final String employeePib, final Connection connection, final int coordinatorId) {
        JFrame coordinatorFrame = new JFrame("Вікно Координатора Завдань");
        coordinatorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        coordinatorFrame.setSize(1600, 1000);
        coordinatorFrame.setLocationRelativeTo(null);
        coordinatorFrame.setLayout(null);
        coordinatorFrame.getContentPane().setBackground(new Color(204, 229, 204));

        JLabel greetingLabel = new JLabel("Привіт, " + employeePib + "! Ви увійшли як Координатор-Завдань.");
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 36));
        greetingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        greetingLabel.setBounds(0, 30, coordinatorFrame.getWidth(), 40);
        coordinatorFrame.add(greetingLabel);

        JButton instructionsButton = new JButton("I");
        instructionsButton.setFont(new Font("Arial", Font.BOLD, 24));
        instructionsButton.setBounds(coordinatorFrame.getWidth() - 90, 20, 60, 60);
        instructionsButton.setBackground(new Color(231, 76, 60));
        instructionsButton.setForeground(Color.BLACK);
        instructionsButton.setFocusPainted(false);
        instructionsButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        coordinatorFrame.add(instructionsButton);

        instructionsButton.addActionListener(e -> {
            String instructions = "<html><body style='font-family: Arial; font-size: 16px;'>" +
                    "<h2>Вікно Координатора-Завдань:</h2>" +
                    "<ul>" +
                    "<li><b>\"Подивитися робітників\"</b>: Відкриває список доступних садівників та їхні деталі.</li>" +
                    "<li><b>\"Призначити Садівників\"</b>: Дозволяє вибрати замовлення та призначити йому доступних садівників.</li>" +
                    "<li><b>\"Відкріпити Садівників\"</b>: Дозволяє відкріпити садівників від замовлення.</li>" +
                    "<li><b>\"Вийти\"</b>: Закриває програму.</li>" +
                    "</ul>" +
                    "</body></html>";
            showInstructions("Інструкції для Координатора-Завдань", instructions);
        });

        int buttonWidth = 400;
        int buttonHeight = 100;
        int padding = 20;
        int startX = (coordinatorFrame.getWidth() - buttonWidth) / 2;
        int startY = 250;

        JButton viewWorkersButton = new JButton("Подивитися робітників");
        viewWorkersButton.setFont(new Font("Arial", Font.BOLD, 30));
        viewWorkersButton.setBounds(startX, startY, buttonWidth, buttonHeight);
        viewWorkersButton.setBackground(new Color(52, 152, 219));
        viewWorkersButton.setForeground(Color.BLACK);
        coordinatorFrame.add(viewWorkersButton);

        viewWorkersButton.addActionListener(e -> openViewWorkersWindow(connection));

        JButton assignGardenersButton = new JButton("Призначити Садівників");
        assignGardenersButton.setFont(new Font("Arial", Font.BOLD, 30));
        assignGardenersButton.setBounds(startX, startY + buttonHeight + padding, buttonWidth, buttonHeight);
        assignGardenersButton.setBackground(new Color(241, 196, 15));
        assignGardenersButton.setForeground(Color.BLACK);
        coordinatorFrame.add(assignGardenersButton);

        assignGardenersButton.addActionListener(e -> openAssignGardenersWindow(connection, employeePib, coordinatorId));

        JButton unassignGardenersButton = new JButton("Відкріпити Садівників");
        unassignGardenersButton.setFont(new Font("Arial", Font.BOLD, 30));
        unassignGardenersButton.setBounds(startX, startY + (buttonHeight + padding) * 2, buttonWidth, buttonHeight);
        unassignGardenersButton.setBackground(new Color(192, 57, 43));
        unassignGardenersButton.setForeground(Color.BLACK);
        coordinatorFrame.add(unassignGardenersButton);

        unassignGardenersButton.addActionListener(e -> openUnassignGardenersWindow(connection));

        JButton exitButton = new JButton("Вийти");
        exitButton.setFont(new Font("Arial", Font.BOLD, 30));
        exitButton.setBounds(startX, startY + (buttonHeight + padding) * 3, buttonWidth, buttonHeight);
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.BLACK);
        coordinatorFrame.add(exitButton);

        exitButton.addActionListener(e -> {
            int confirmResult = new Validation().showConfirmationMessage(
                    coordinatorFrame,
                    "Ви впевнені, що хочете вийти з програми?",
                    "Підтвердження виходу"
            );

            if (confirmResult == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        coordinatorFrame.setVisible(true);
    }

    private static void openAccountantWindow(String employeePib, Connection connection) {
        JFrame accountantFrame = new JFrame("Вікно Бухгалтера");
        accountantFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        accountantFrame.setSize(1600, 1000);
        accountantFrame.setLocationRelativeTo(null);
        accountantFrame.setLayout(null);
        accountantFrame.getContentPane().setBackground(new Color(204, 229, 204));

        JLabel greetingLabel = new JLabel("Привіт, " + employeePib + "! Ви увійшли як Бухгалтер.");
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 30));
        greetingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        greetingLabel.setBounds(0, 20, accountantFrame.getWidth(), 40);
        accountantFrame.add(greetingLabel);

        JButton instructionsButton = new JButton("I");
        instructionsButton.setFont(new Font("Arial", Font.BOLD, 24));
        instructionsButton.setBounds(accountantFrame.getWidth() - 90, 20, 60, 60);
        instructionsButton.setBackground(new Color(231, 76, 60));
        instructionsButton.setForeground(Color.BLACK);
        instructionsButton.setFocusPainted(false);
        instructionsButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        accountantFrame.add(instructionsButton);

        instructionsButton.addActionListener(e -> {
            String instructions = "<html><body style='font-family: Arial; font-size: 16px;'>" +
                    "<h2>Вікно Бухгалтера:</h2>" +
                    "<ul>" +
                    "<li><b>\"Виберіть Замовлення\"</b>: Випадаючий список для вибору замовлення, для якого потрібно розрахувати матеріали.</li>" +
                    "<li><b>\"Розрахувати матеріали\"</b>: Обчислює необхідні матеріали для обраного замовлення та відображає їх вартість.</li>" +
                    "<li><b>\"Закупівля Матеріалів\"</b>: Відкриває вікно для підтвердження закупівлі відсутніх матеріалів та оновлення їх наявності на складі.</li>" +
                    "<li><b>\"Вийти\"</b>: Закриває програму.</li>" +
                    "</ul>" +
                    "</body></html>";
            showInstructions("Інструкції для Бухгалтера", instructions);
        });

        JPanel orderSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        orderSelectionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Виберіть Замовлення", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 20), Color.BLACK));
        orderSelectionPanel.setBackground(new Color(229, 241, 224));
        orderSelectionPanel.setBounds((accountantFrame.getWidth() - 880) / 2, 100, 880, 150);
        accountantFrame.add(orderSelectionPanel);

        JLabel orderLabel = new JLabel("Замовлення:");
        orderLabel.setFont(new Font("Arial", Font.BOLD, 20));
        orderSelectionPanel.add(orderLabel);

        List<Order.OrderDisplayItem> allOrders = getAllOrders(connection);
        List<Order.OrderDisplayItem> purchasableOrders = new ArrayList<>();
        for (Order.OrderDisplayItem order : allOrders) {
            // Фільтруємо замовлення, які не завершені
            if (!"Завершено".equalsIgnoreCase(order.getStatus())) {
                purchasableOrders.add(order);
            }
        }

        JComboBox<Order.OrderDisplayItem> orderComboBox = new JComboBox<>(purchasableOrders.toArray(new Order.OrderDisplayItem[0]));
        orderComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        orderComboBox.setPreferredSize(new Dimension(800, 40));
        orderSelectionPanel.add(orderComboBox);

        orderComboBox.setSelectedIndex(-1);

        JLabel orderCostLabel = new JLabel("Попередня вартість замовлення: 0.00 грн");
        orderCostLabel.setFont(new Font("Arial", Font.BOLD, 18));
        orderSelectionPanel.add(orderCostLabel);

        orderComboBox.addActionListener(e -> {
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            if (selectedOrder != null) {
                orderCostLabel.setText(String.format("Попередня вартість замовлення: %.2f грн", selectedOrder.getTotalCost()));
            } else {
                orderCostLabel.setText("Попередня вартість замовлення: 0.00 грн");
            }
        });

        JButton calculateMaterialsButton = new JButton("Розрахувати матеріали");
        calculateMaterialsButton.setFont(new Font("Arial", Font.BOLD, 24));
        calculateMaterialsButton.setBackground(new Color(52, 152, 219));
        calculateMaterialsButton.setForeground(Color.BLACK);
        calculateMaterialsButton.setBounds((accountantFrame.getWidth() - 400) / 2, 300, 400, 80);
        accountantFrame.add(calculateMaterialsButton);

        calculateMaterialsButton.addActionListener(e -> {
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            if (selectedOrder == null) {
                new Validation().showErrorMessage(accountantFrame, "Будь ласка, оберіть замовлення для розрахунку матеріалів.", "Помилка");
                return;
            }

            Order.FullOrderDetails fullDetails = Order.getFullOrderDetails(connection, selectedOrder.getId());
            if (fullDetails != null) {
                Order orderInstance = new Order();
                orderInstance.displayMaterialCalculation(accountantFrame, connection, fullDetails.getOrderId(), fullDetails.getArea(), fullDetails.getSelectedServiceNames());
            } else {
                new Validation().showErrorMessage(accountantFrame, "Не вдалося завантажити деталі замовлення для розрахунку матеріалів.", "Помилка");
            }
        });

        JButton exitButton = new JButton("Вийти");
        exitButton.setFont(new Font("Arial", Font.BOLD, 24));
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.BLACK);
        exitButton.setBounds((accountantFrame.getWidth() - 200) / 2, 450, 200, 70);
        accountantFrame.add(exitButton);

        exitButton.addActionListener(e -> {
            int confirmResult = new Validation().showConfirmationMessage(
                    accountantFrame,
                    "Ви впевнені, що хочете вийти з програми?",
                    "Підтвердження виходу"
            );

            if (confirmResult == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        accountantFrame.setVisible(true);
    }

    private static void openCreateOrderWindow(Connection connection) {
        JFrame orderFrame = new JFrame("Створити замовлення");
        orderFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        orderFrame.setSize(1000, 800);
        orderFrame.setLocationRelativeTo(null);
        orderFrame.setLayout(null);
        orderFrame.getContentPane().setBackground(new Color(229, 241, 224));

        orderFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int confirmResult = new Validation().showConfirmationMessage(
                        orderFrame,
                        "Чи дійсно ви хочете вийти? Всі введені дані не будуть збережені.",
                        "Підтвердження виходу"
                );

                if (confirmResult == JOptionPane.YES_OPTION) {
                    orderFrame.dispose();
                }
            }
        });

        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(null);
        orderPanel.setBounds(0, 0, orderFrame.getWidth(), orderFrame.getHeight());
        orderPanel.setBackground(new Color(229, 241, 224));
        orderFrame.add(orderPanel);

        Address addressData = new Address();
        List<String> streets = addressData.getStreetsList();

        int currentY = 50;
        int labelWidth = 250;
        int fieldWidth = 350;
        int height = 40;
        int xLabel = 50;
        int xField = xLabel + labelWidth + 20;

        JLabel serviceTypeLabel = new JLabel("Тип послуг:");
        serviceTypeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        serviceTypeLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(serviceTypeLabel);

        List<JCheckBox> serviceCheckBoxes = new ArrayList<>();
        String[] serviceNames = {"Озеленення території", "Підрізання дерев"};

        int checkBoxX = xField;
        for (String serviceName : serviceNames) {
            JCheckBox checkBox = new JCheckBox(serviceName);
            checkBox.setFont(new Font("Arial", Font.PLAIN, 20));
            checkBox.setBounds(checkBoxX, currentY, 250, height);
            checkBox.setBackground(new Color(229, 241, 224));
            orderPanel.add(checkBox);
            serviceCheckBoxes.add(checkBox);
            checkBoxX += 260;
            if (checkBoxX > xField + fieldWidth) {
                checkBoxX = xField;
                currentY += height + 10;
            }
        }
        currentY += height + 30;

        JLabel streetLabel = new JLabel("Вулиця:");
        streetLabel.setFont(new Font("Arial", Font.BOLD, 20));
        streetLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(streetLabel);

        List<String> mutableStreets = new java.util.ArrayList<>(streets);
        mutableStreets.add(0, "");
        JComboBox<String> streetComboBox = new JComboBox<>(mutableStreets.toArray(new String[0]));
        streetComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
        streetComboBox.setBounds(xField, currentY, fieldWidth, height);
        orderPanel.add(streetComboBox);
        currentY += height + 30;

        JLabel houseNumberLabel = new JLabel("Номер дому/ділянки:");
        houseNumberLabel.setFont(new Font("Arial", Font.BOLD, 20));
        houseNumberLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(houseNumberLabel);

        JTextField houseNumberField = new JTextField();
        houseNumberField.setFont(new Font("Arial", Font.PLAIN, 20));
        houseNumberField.setBounds(xField, currentY, 150, height);
        orderPanel.add(houseNumberField);
        currentY += height + 30;

        JLabel areaLabel = new JLabel("Площа (кв.м.):");
        areaLabel.setFont(new Font("Arial", Font.BOLD, 20));
        areaLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(areaLabel);

        JTextField areaField = new JTextField();
        areaField.setFont(new Font("Arial", Font.PLAIN, 20));
        areaField.setBounds(xField, currentY, 150, height);
        orderPanel.add(areaField);

        ((AbstractDocument) areaField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newText.matches("\\d*\\.?\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
                if (newText.matches("\\d*\\.?\\d*")) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        currentY += height + 30;

        JLabel clientNameLabel = new JLabel("ПІБ Клієнта:");
        clientNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        clientNameLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(clientNameLabel);

        JTextField clientNameField = new JTextField();
        clientNameField.setFont(new Font("Arial", Font.PLAIN, 20));
        clientNameField.setBounds(xField, currentY, fieldWidth, height);
        orderPanel.add(clientNameField);
        currentY += height + 30;

        JLabel contactDataLabel = new JLabel("Контактні дані:");
        contactDataLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contactDataLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(contactDataLabel);

        JLabel phonePrefixLabel = new JLabel("+380");
        phonePrefixLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        phonePrefixLabel.setBounds(xField, currentY, 60, height);
        orderPanel.add(phonePrefixLabel);

        JTextField phoneNumberField = new JTextField();
        phoneNumberField.setFont(new Font("Arial", Font.PLAIN, 20));
        phoneNumberField.setBounds(xField + 65, currentY, 150, height);
        orderPanel.add(phoneNumberField);

        ((AbstractDocument) phoneNumberField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                int totalLength = currentText.length() - length + text.length();
                if (totalLength <= 9 && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                int totalLength = currentText.length() + string.length();
                if (totalLength <= 9 && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        currentY += height + 50;

        Border defaultTextFieldBorder = clientNameField.getBorder();
        Border defaultComboBoxBorder = streetComboBox.getBorder();

        JButton submitOrderButton = new JButton("Створити");
        submitOrderButton.setFont(new Font("Arial", Font.BOLD, 24));
        submitOrderButton.setBounds(orderFrame.getWidth() / 2 - 100, currentY, 200, 60);
        submitOrderButton.setBackground(new Color(46, 204, 113));
        submitOrderButton.setForeground(Color.BLACK);
        orderPanel.add(submitOrderButton);

        final Validation validator = new Validation();

        submitOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBorders(clientNameField, phoneNumberField, areaField, houseNumberField,
                        serviceCheckBoxes, streetComboBox,
                        defaultTextFieldBorder, defaultComboBoxBorder);

                String street = (String) streetComboBox.getSelectedItem();
                String houseNumber = houseNumberField.getText().trim();
                String clientName = clientNameField.getText().trim();
                String phoneNumber = phoneNumberField.getText().trim();
                String areaText = areaField.getText().trim();

                List<String> selectedServices = new ArrayList<>();
                for (JCheckBox checkBox : serviceCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedServices.add(checkBox.getText());
                    }
                }

                boolean isValid = true;
                StringBuilder errorMessage = new StringBuilder("Будь ласка, заповніть усі поля коректно:\n");

                if (!validator.areServicesSelected(selectedServices)) {
                    errorMessage.append("- Оберіть хоча б одну послугу\n");
                    isValid = false;
                }

                if (!validator.isStreetSelected(street)) {
                    highlightEmptyField(streetComboBox);
                    errorMessage.append("- Вулиця не обрана\n");
                    isValid = false;
                }

                if (!validator.isHouseNumberValid(houseNumber)) {
                    highlightEmptyField(houseNumberField);
                    errorMessage.append("- Номер дому/ділянки не заповнено\n");
                    isValid = false;
                }

                float area = 0.0f;
                try {
                    area = validator.parseAndValidateArea(areaText);
                } catch (IllegalArgumentException ex) {
                    highlightEmptyField(areaField);
                    errorMessage.append("- Площа: ").append(ex.getMessage()).append("\n");
                    isValid = false;
                }

                if (!validator.isValidClientName(clientName)) {
                    highlightEmptyField(clientNameField);
                    errorMessage.append("- ПІБ Клієнта (має складатися з 3 слів, кожне з великої літери та бути коректним)\n");
                    isValid = false;
                }

                if (!validator.isValidPhoneNumber(phoneNumber)) {
                    highlightEmptyField(phoneNumberField);
                    errorMessage.append("- Номер телефону (має бути рівно 9 цифр)\n");
                    isValid = false;
                }

                if (!isValid) {
                    new Validation().showErrorMessage(orderFrame, errorMessage.toString(), "Помилка вводу");
                    return;
                }

                Map<Integer, Material> neededMaterials = null;
                boolean materialsSufficient = true;
                StringBuilder missingMaterialsMessage = new StringBuilder("Не вистачає наступних матеріалів для створення замовлення:\n");
                try {
                    neededMaterials = new Calc().calculateNeededMaterials(connection, selectedServices, area);
                    for (Material material : neededMaterials.values()) {
                        if (material.getNeededQuantity() > material.getAvailableQuantity()) {
                            materialsSufficient = false;
                            missingMaterialsMessage.append(String.format("- %s: потрібно %.2f %s, в наявності %.2f %s\n",
                                    material.getName(), material.getNeededQuantity(), material.getUnitOfMeasure(),
                                    material.getAvailableQuantity(), material.getUnitOfMeasure()));
                        }
                    }
                } catch (SQLException ex) {
                    new Validation().showErrorMessage(orderFrame, "Помилка при розрахунку необхідних матеріалів: " + ex.getMessage(), "Помилка БД");
                    return;
                }


                Calc calculator = new Calc();
                calculator.setSize(area);
                float serviceCost = 0.0f;
                if (selectedServices != null && !selectedServices.isEmpty()) {
                    serviceCost = calculator.calculateTotalCost(selectedServices);
                }

                float materialsCost = 0.0f;
                try {
                    materialsCost = new Calc().calculateTotalMaterialsCostForOrder(connection, selectedServices, area);
                } catch (SQLException ex) {
                    new Validation().showErrorMessage(orderFrame, "Помилка при розрахунку вартості матеріалів: " + ex.getMessage(), "Помилка БД");
                    return;
                }
                final float overallTotalCost = serviceCost + materialsCost; // Використовуємо 'final' для доступу в анонімному класі, якщо це потрібно для інших місць


                int confirmResult = new Validation().showConfirmationMessage(orderFrame,
                        String.format("Чи дійсно ви хочете створити замовлення?\nПопередня ціна: %.2f грн", overallTotalCost),
                        "Підтвердження замовлення");

                if (confirmResult == JOptionPane.YES_OPTION) {
                    String fullPhoneNumber = "+380" + phoneNumber;
                    String fullAddress = street + ", " + houseNumber;

                    Calc calculator1 = new Calc();
                    calculator.setSize(area);
                    float serviceCost1 = 0.0f;
                    if (selectedServices != null && !selectedServices.isEmpty()) {
                        serviceCost = calculator.calculateTotalCost(selectedServices);
                    }

                    float materialsCost1 = 0.0f;
                    try {
                        materialsCost1 = new Calc().calculateTotalMaterialsCostForOrder(connection, selectedServices, area);
                    } catch (SQLException ex) {
                        new Validation().showErrorMessage(orderFrame, "Помилка при розрахунку вартості матеріалів: " + ex.getMessage(), "Помилка БД");
                        return;
                    }
                    float overallTotalCost1 = serviceCost1 + materialsCost1;

                    Order newOrder = new Order();
                    try {
                        newOrder.createOrder(connection, selectedServices, fullAddress, clientName, fullPhoneNumber, area, overallTotalCost1, materialsSufficient);
                        orderFrame.dispose();
                    } catch (SQLException ex) {
                        new Validation().showErrorMessage(orderFrame,
                                "Помилка при збереженні замовлення в базу даних: " + ex.getMessage(),
                                "Помилка бази даних");
                    }
                }
            }
        });

        orderFrame.setVisible(true);
    }

    private static void openOrderEditForm(Connection connection, Order.FullOrderDetails orderDetails) {
        JFrame editOrderFrame = new JFrame("Редагування Замовлення ID: " + orderDetails.getOrderId());
        editOrderFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        editOrderFrame.setSize(1000, 800);
        editOrderFrame.setLocationRelativeTo(null);
        editOrderFrame.setLayout(null);
        editOrderFrame.getContentPane().setBackground(new Color(229, 241, 224));

        editOrderFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                int confirmResult = new Validation().showConfirmationMessage(
                        editOrderFrame,
                        "Чи дійсно ви хочете вийти? Всі внесені зміни не будуть збережені.",
                        "Підтвердження виходу"
                );

                if (confirmResult == JOptionPane.YES_OPTION) {
                    editOrderFrame.dispose();
                }
            }
        });

        JPanel orderPanel = new JPanel();
        orderPanel.setLayout(null);
        orderPanel.setBounds(0, 0, editOrderFrame.getWidth(), editOrderFrame.getHeight());
        orderPanel.setBackground(new Color(229, 241, 224));
        editOrderFrame.add(orderPanel);

        Address addressData = new Address();
        List<String> streets = addressData.getStreetsList();

        int currentY = 50;
        int labelWidth = 250;
        int fieldWidth = 350;
        int height = 40;
        int xLabel = 50;
        int xField = xLabel + labelWidth + 20;

        JLabel orderIdLabel = new JLabel("Замовлення ID: " + orderDetails.getOrderId());
        orderIdLabel.setFont(new Font("Arial", Font.BOLD, 24));
        orderIdLabel.setBounds(xLabel, 10, 500, 30);
        orderPanel.add(orderIdLabel);
        currentY = 50;

        JLabel serviceTypeLabel = new JLabel("Тип послуг:");
        serviceTypeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        serviceTypeLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(serviceTypeLabel);

        List<JCheckBox> serviceCheckBoxes = new ArrayList<>();
        String[] serviceNames = {"Озеленення території", "Підрізання дерев"};

        int checkBoxX = xField;
        for (String serviceName : serviceNames) {
            JCheckBox checkBox = new JCheckBox(serviceName);
            checkBox.setFont(new Font("Arial", Font.PLAIN, 20));
            checkBox.setBounds(checkBoxX, currentY, 250, height);
            checkBox.setBackground(new Color(229, 241, 224));
            if (orderDetails.getSelectedServiceNames().contains(serviceName)) {
                checkBox.setSelected(true);
            }
            orderPanel.add(checkBox);
            serviceCheckBoxes.add(checkBox);
            checkBoxX += 260;
            if (checkBoxX > xField + fieldWidth) {
                checkBoxX = xField;
                currentY += height + 10;
            }
        }
        currentY += height + 30;

        JLabel streetLabel = new JLabel("Вулиця:");
        streetLabel.setFont(new Font("Arial", Font.BOLD, 20));
        streetLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(streetLabel);

        List<String> mutableStreets = new java.util.ArrayList<>(streets);
        mutableStreets.add(0, "");
        JComboBox<String> streetComboBox = new JComboBox<>(mutableStreets.toArray(new String[0]));
        streetComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
        streetComboBox.setBounds(xField, currentY, fieldWidth, height);
        String[] addressParts = orderDetails.getAddress().split(", ");
        if (addressParts.length > 0) {
            streetComboBox.setSelectedItem(addressParts[0]);
        }
        orderPanel.add(streetComboBox);
        currentY += height + 30;

        JLabel houseNumberLabel = new JLabel("Номер дому/ділянки:");
        houseNumberLabel.setFont(new Font("Arial", Font.BOLD, 20));
        houseNumberLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(houseNumberLabel);

        JTextField houseNumberField = new JTextField();
        houseNumberField.setFont(new Font("Arial", Font.PLAIN, 20));
        houseNumberField.setBounds(xField, currentY, 150, height);
        if (addressParts.length > 1) {
            houseNumberField.setText(addressParts[1].trim());
        }
        orderPanel.add(houseNumberField);
        currentY += height + 30;

        JLabel areaLabel = new JLabel("Площа (кв.м.):");
        areaLabel.setFont(new Font("Arial", Font.BOLD, 20));
        areaLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(areaLabel);

        JTextField areaField = new JTextField();
        areaField.setFont(new Font("Arial", Font.PLAIN, 20));
        areaField.setBounds(xField, currentY, 150, height);
        areaField.setText(String.valueOf(orderDetails.getArea()));
        orderPanel.add(areaField);

        ((AbstractDocument) areaField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newText.matches("\\d*\\.?\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
                if (newText.matches("\\d*\\.?\\d*")) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        currentY += height + 30;

        JLabel priceLabel = new JLabel("Ціна (грн.):");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        priceLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(priceLabel);

        JTextField priceField = new JTextField();
        priceField.setFont(new Font("Arial", Font.PLAIN, 20));
        priceField.setBounds(xField, currentY, fieldWidth, height);
        priceField.setText(String.format("%.2f", orderDetails.getTotalCost()));
        orderPanel.add(priceField);

        final boolean[] priceFieldManuallyEdited = {false};

        ((AbstractDocument) priceField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                String validatedText = newText.replace(",", ".");
                if (validatedText.matches("\\d*\\.?\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                    priceFieldManuallyEdited[0] = true;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
                String validatedText = newText.replace(",", ".");
                if (validatedText.matches("\\d*\\.?\\d*")) {
                    super.insertString(fb, offset, string, attr);
                    priceFieldManuallyEdited[0] = true;
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        currentY += height + 30;

        // Додаємо поле для дати виконання
        JLabel completionDateLabel = new JLabel("Дата виконання (РРРР-ММ-ДД):");
        completionDateLabel.setFont(new Font("Arial", Font.BOLD, 20));
        completionDateLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(completionDateLabel);

        JTextField completionDateField = new JTextField();
        completionDateField.setFont(new Font("Arial", Font.PLAIN, 20));
        completionDateField.setBounds(xField, currentY, 200, height);
        if (orderDetails.getCompletionDate() != null) {
            completionDateField.setText(orderDetails.getCompletionDate().toString());
        }
        orderPanel.add(completionDateField);
        currentY += height + 30;


        JLabel clientNameLabel = new JLabel("ПІБ Клієнта:");
        clientNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        clientNameLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(clientNameLabel);

        JTextField clientNameField = new JTextField();
        clientNameField.setFont(new Font("Arial", Font.PLAIN, 20));
        clientNameField.setBounds(xField, currentY, fieldWidth, height);
        clientNameField.setText(orderDetails.getClientName());
        orderPanel.add(clientNameField);
        currentY += height + 30;

        JLabel contactDataLabel = new JLabel("Контактні дані:");
        contactDataLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contactDataLabel.setBounds(xLabel, currentY, labelWidth, height);
        orderPanel.add(contactDataLabel);

        JLabel phonePrefixLabel = new JLabel("+380");
        phonePrefixLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        phonePrefixLabel.setBounds(xField, currentY, 60, height);
        orderPanel.add(phonePrefixLabel);

        JTextField phoneNumberField = new JTextField();
        phoneNumberField.setFont(new Font("Arial", Font.PLAIN, 20));
        phoneNumberField.setBounds(xField + 65, currentY, 150, height);
        String rawPhoneNumber = orderDetails.getPhoneNumber().replace("+380", "");
        phoneNumberField.setText(rawPhoneNumber);
        orderPanel.add(phoneNumberField);

        ((AbstractDocument) phoneNumberField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                int totalLength = currentText.length() - length + text.length();
                if (totalLength <= 9 && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                int totalLength = currentText.length() + string.length();
                if (totalLength <= 9 && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        currentY += height + 50;

        Border defaultTextFieldBorder = clientNameField.getBorder();
        Border defaultComboBoxBorder = streetComboBox.getBorder();

        JButton updateOrderButton = new JButton("Оновити Замовлення");
        updateOrderButton.setFont(new Font("Arial", Font.BOLD, 24));
        updateOrderButton.setBounds(editOrderFrame.getWidth() / 2 - 150, currentY, 300, 60);
        updateOrderButton.setBackground(new Color(46, 204, 113));
        updateOrderButton.setForeground(Color.BLACK);
        orderPanel.add(updateOrderButton);

        final Validation validator = new Validation();
        final ContactData clientData = new ContactData();

        updateOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBorders(clientNameField, phoneNumberField, areaField, houseNumberField, priceField, completionDateField,
                        serviceCheckBoxes, streetComboBox,
                        defaultTextFieldBorder, defaultComboBoxBorder);

                String street = (String) streetComboBox.getSelectedItem();
                String houseNumber = houseNumberField.getText().trim();
                String clientName = clientNameField.getText().trim();
                String phoneNumber = phoneNumberField.getText().trim();
                String areaText = areaField.getText().trim();
                String priceText = priceField.getText().trim();
                String completionDateText = completionDateField.getText().trim();


                List<String> selectedServices = new ArrayList<>();
                for (JCheckBox checkBox : serviceCheckBoxes) {
                    if (checkBox.isSelected()) {
                        selectedServices.add(checkBox.getText());
                    }
                }

                boolean isValid = true;
                StringBuilder errorMessage = new StringBuilder("Будь ласка, заповніть усі поля коректно:\n");

                if (!validator.areServicesSelected(selectedServices)) {
                    errorMessage.append("- Оберіть хоча б одну послугу\n");
                    isValid = false;
                }

                if (!validator.isStreetSelected(street)) {
                    highlightEmptyField(streetComboBox);
                    errorMessage.append("- Вулиця не обрана\n");
                    isValid = false;
                }

                if (!validator.isHouseNumberValid(houseNumber)) {
                    highlightEmptyField(houseNumberField);
                    errorMessage.append("- Номер дому/ділянки не заповнено\n");
                    isValid = false;
                }

                float area = 0.0f;
                try {
                    area = validator.parseAndValidateArea(areaText);
                } catch (IllegalArgumentException ex) {
                    highlightEmptyField(areaField);
                    errorMessage.append("- Площа: ").append(ex.getMessage()).append("\n");
                    isValid = false;
                }

                float newPrice = 0.0f;
                try {
                    newPrice = validator.parseAndValidatePrice(priceText);
                } catch (IllegalArgumentException ex) {
                    highlightEmptyField(priceField);
                    errorMessage.append("- Ціна: ").append(ex.getMessage()).append("\n");
                    isValid = false;
                }

                Date newCompletionDate = null;
                if (!completionDateText.isEmpty()) {
                    try {
                        newCompletionDate = validator.parseAndValidateDate(completionDateText);
                    } catch (IllegalArgumentException ex) {
                        highlightEmptyField(completionDateField);
                        errorMessage.append("- Дата виконання: ").append(ex.getMessage()).append("\n");
                        isValid = false;
                    }
                }

                if (!validator.isValidClientName(clientName)) {
                    highlightEmptyField(clientNameField);
                    errorMessage.append("- ПІБ Клієнта (має складатися з 3 слів, кожне з великої літери та бути коректним)\n");
                    isValid = false;
                }

                if (!validator.isValidPhoneNumber(phoneNumber)) {
                    highlightEmptyField(phoneNumberField);
                    errorMessage.append("- Номер телефону (має бути рівно 9 цифр)\n");
                    isValid = false;
                }

                if (!isValid) {
                    new Validation().showErrorMessage(editOrderFrame, errorMessage.toString(), "Помилка вводу");
                    return;
                }

                int confirmResult = new Validation().showConfirmationMessage(editOrderFrame,
                        "Чи дійсно ви хочете оновити замовлення?",
                        "Підтвердження оновлення");

                if (confirmResult == JOptionPane.YES_OPTION) {
                    try {
                        String fullPhoneNumber = "+380" + phoneNumber;
                        String fullAddress = street + ", " + houseNumber;

                        float finalPriceToSave;
                        if (priceFieldManuallyEdited[0]) {
                            finalPriceToSave = newPrice;
                        } else {
                            Calc calculator = new Calc();
                            calculator.setSize(area);
                            float serviceCost = 0.0f;
                            if (selectedServices != null && !selectedServices.isEmpty()) {
                                serviceCost = calculator.calculateTotalCost(selectedServices);
                            }
                            float materialsCost = new Calc().calculateTotalMaterialsCostForOrder(connection, selectedServices, area);
                            finalPriceToSave = serviceCost + materialsCost;
                        }

                        int finalClientId = orderDetails.getClientId();
                        if (!clientName.equals(orderDetails.getClientName()) || !fullPhoneNumber.equals(orderDetails.getPhoneNumber())) {
                            String newClientIdString = clientData.saveClientToDatabase(connection, clientName, fullPhoneNumber);
                            finalClientId = Integer.parseInt(newClientIdString);
                        }

                        Calc calculator = new Calc();
                        calculator.setSize(area);
                        int updatedGardenersNeeded = calculator.calculateWorkersNeeded(area);

                        Order orderToUpdate = new Order();
                        orderToUpdate.updateOrder(connection, orderDetails.getOrderId(), fullAddress, finalClientId, finalPriceToSave, updatedGardenersNeeded, selectedServices, area, newCompletionDate);

                        editOrderFrame.dispose();

                    } catch (SQLException ex) {
                        new Validation().showErrorMessage(editOrderFrame,
                                "Помилка при оновленні замовлення в базі даних: " + ex.getMessage(),
                                "Помилка бази даних");
                    }
                }
            }
        });

        editOrderFrame.setVisible(true);
    }

    private static void openEditOrderWindow(Connection connection) {
        List<Order.OrderDisplayItem> orders = getAllOrders(connection);
        List<Order.OrderDisplayItem> editableOrders = new ArrayList<>();
        for (Order.OrderDisplayItem order : orders) {
            if (!"Завершено".equalsIgnoreCase(order.getStatus())) {
                editableOrders.add(order);
            }
        }

        if (editableOrders.isEmpty()) {
            new Validation().showInformationMessage(null, "Немає замовлень для редагування.", "Інформація");
            return;
        }

        JComboBox<Order.OrderDisplayItem> orderComboBox = new JComboBox<>(editableOrders.toArray(new Order.OrderDisplayItem[0]));
        orderComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(229, 241, 224));
        JLabel selectOrderLabel = new JLabel("Виберіть замовлення для редагування:");
        selectOrderLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectOrderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(selectOrderLabel, BorderLayout.NORTH);
        panel.add(orderComboBox, BorderLayout.CENTER);

        int result = new Validation().showConfirmationMessage(null, panel, "Редагувати Замовлення");

        if (result == JOptionPane.OK_OPTION) {
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            if (selectedOrder != null) {
                Order.FullOrderDetails fullDetails = Order.getFullOrderDetails(connection, selectedOrder.getId());
                if (fullDetails != null) {
                    openOrderEditForm(connection, fullDetails);
                } else {
                    new Validation().showErrorMessage(null, "Не вдалося завантажити деталі замовлення.", "Помилка");
                }
            }
        }
    }

    private static void deleteOrder(Connection connection) {
        List<Order.OrderDisplayItem> orders = getAllOrders(connection);
        if (orders.isEmpty()) {
            new Validation().showInformationMessage(null, "Немає замовлень для видалення.", "Інформація");
            return;
        }

        JComboBox<Order.OrderDisplayItem> orderComboBox = new JComboBox<>(orders.toArray(new Order.OrderDisplayItem[0]));
        orderComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(229, 241, 224));
        JLabel selectOrderLabel = new JLabel("Виберіть замовлення для видалення:");
        selectOrderLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectOrderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(selectOrderLabel, BorderLayout.NORTH);
        panel.add(orderComboBox, BorderLayout.CENTER);

        int result = new Validation().showConfirmationMessage(null, panel, "Видалити Замовлення");

        if (result == JOptionPane.OK_OPTION) {
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            if (selectedOrder != null) {
                int orderId = selectedOrder.getId();
                int confirm = new Validation().showConfirmationMessage(null,
                        "Ви впевнені, що хочете видалити замовлення з ID: " + orderId + "?\n" + selectedOrder.toString(),
                        "Підтвердження видалення");

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Order orderToDelete = new Order();
                        orderToDelete.deleteOrder(connection, orderId);
                    } catch (SQLException e) {
                        new Validation().showErrorMessage(null, "Помилка бази даних при видаленні замовлення: " + e.getMessage(), "Помилка");
                    }
                }
            }
        }
    }

    private static List<Order.OrderDisplayItem> getAllOrders(Connection connection) {
        List<Order.OrderDisplayItem> orders = new ArrayList<>();
        String sql = "SELECT o.ID, o.Адреса, o.Стан, o.ДатаУкладання, o.IDКлієнта, k.ПІБ, o.Ціна, o.КількістьСадівників, MAX(ops.Обсяг) AS Обсяг " +
                "FROM Замовлення o JOIN Клієнт k ON o.IDКлієнта = k.ID " +
                "LEFT JOIN ЗамовленняПослуга ops ON o.ID = ops.IDЗамовлення " +
                "GROUP BY o.ID, o.Адреса, o.Стан, o.ДатаУкладання, o.IDКлієнта, k.ПІБ, o.Ціна, o.КількістьСадівників";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ID");
                String address = rs.getString("Адреса");
                String status = rs.getString("Стан");
                String date = rs.getDate("ДатаУкладання").toString();
                int clientId = rs.getInt("IDКлієнта");
                String clientPib = rs.getString("ПІБ");
                float totalCost = rs.getFloat("Ціна");
                int gardenersNeeded = rs.getInt("КількістьСадівників");
                float area = rs.getFloat("Обсяг");

                Order tempOrder = new Order();
                float procurementCost = tempOrder.calculateMaterialProcurementCost(connection, id, area);

                orders.add(new Order.OrderDisplayItem(id, address, status, date, clientId, clientPib, totalCost, gardenersNeeded, area, procurementCost));
            }
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Помилка при отриманні списку замовлень: " + e.getMessage(), "Помилка БД");
        }
        return orders;
    }

    private static void openViewWorkersWindow(Connection connection) {
        ContactData contactData = new ContactData();
        List<Order.GardenerDisplayItem> availableGardeners = null;
        try {
            availableGardeners = contactData.getAvailableGardeners(connection);
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Помилка при отриманні доступних садівників: " + e.getMessage(), "Помилка БД");
            return;
        }

        if (availableGardeners.isEmpty()) {
            new Validation().showInformationMessage(null, "Наразі немає доступних садівників.", "Інформація");
            return;
        }

        JComboBox<Order.GardenerDisplayItem> gardenerComboBox = new JComboBox<>(availableGardeners.toArray(new Order.GardenerDisplayItem[0]));
        gardenerComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(229, 241, 224));
        JLabel selectGardenerLabel = new JLabel("Виберіть садівника для перегляду деталей:");
        selectGardenerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        selectGardenerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(selectGardenerLabel, BorderLayout.NORTH);
        panel.add(gardenerComboBox, BorderLayout.CENTER);

        int result = new Validation().showConfirmationMessage(null, panel, "Переглянути Робітників");

        if (result == JOptionPane.OK_OPTION) {
            Order.GardenerDisplayItem selectedGardener = (Order.GardenerDisplayItem) gardenerComboBox.getSelectedItem();
            if (selectedGardener != null) {
                openGardenerDetailsTable(selectedGardener);
            }
        }
    }

    private static void openGardenerDetailsTable(Order.GardenerDisplayItem gardener) {
        JFrame detailsFrame = new JFrame("Деталі Садівника: " + gardener.getPib());
        detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailsFrame.setSize(800, 500);
        detailsFrame.setLocationRelativeTo(null);
        detailsFrame.setLayout(new BorderLayout());
        detailsFrame.getContentPane().setBackground(new Color(229, 241, 224));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(new Color(229, 241, 224));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        Font detailsFont = new Font("Verdana", Font.BOLD, 24);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(detailsFont);
        detailsPanel.add(idLabel, gbc);

        gbc.gridx = 1;
        JLabel idValueLabel = new JLabel(String.valueOf(gardener.getId()));
        idValueLabel.setFont(detailsFont);
        detailsPanel.add(idValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel pibLabel = new JLabel("ПІБ:");
        pibLabel.setFont(detailsFont);
        detailsPanel.add(pibLabel, gbc);

        gbc.gridx = 1;
        JLabel pibValueLabel = new JLabel(gardener.getPib());
        pibValueLabel.setFont(detailsFont);
        detailsPanel.add(pibValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel phoneLabel = new JLabel("Номер телефону:");
        phoneLabel.setFont(detailsFont);
        detailsPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        JLabel phoneValueLabel = new JLabel(gardener.getPhoneNumber());
        phoneValueLabel.setFont(detailsFont);
        detailsPanel.add(phoneValueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel genderLabel = new JLabel("Стать:");
        genderLabel.setFont(detailsFont);
        detailsPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        String displayedGender = gardener.getGender();
        if ("Ч".equals(displayedGender)) {
            displayedGender = "Чоловіча";
        } else if ("Ж".equals(displayedGender)) {
            displayedGender = "Жіноча";
        }
        JLabel genderValueLabel = new JLabel(displayedGender);
        genderValueLabel.setFont(detailsFont);
        detailsPanel.add(genderValueLabel, gbc);

        detailsFrame.add(detailsPanel, BorderLayout.CENTER);

        detailsFrame.setVisible(true);
    }

    private static void openAssignGardenersWindow(Connection connection, String coordinatorPib, int coordinatorId) {
        JFrame assignFrame = new JFrame("Призначити Садівників до Замовлення");
        assignFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        assignFrame.setSize(800, 600);
        assignFrame.setLocationRelativeTo(null);
        assignFrame.setLayout(new BorderLayout(10, 10));
        assignFrame.getContentPane().setBackground(new Color(229, 241, 224));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(229, 241, 224));

        JPanel orderSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        orderSelectionPanel.setBorder(BorderFactory.createTitledBorder("Виберіть Замовлення"));
        orderSelectionPanel.setBackground(new Color(229, 241, 224));

        JLabel orderLabel = new JLabel("Замовлення:");
        orderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        orderSelectionPanel.add(orderLabel);

        List<Order.OrderDisplayItem> ordersNeedingGardeners = getOrdersNeedingGardeners(connection);
        JComboBox<Order.OrderDisplayItem> orderComboBox = new JComboBox<>(ordersNeedingGardeners.toArray(new Order.OrderDisplayItem[0]));
        orderComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        orderComboBox.setPreferredSize(new Dimension(400, 30));
        orderSelectionPanel.add(orderComboBox);

        orderComboBox.setSelectedIndex(-1);

        JLabel neededGardenersLabel = new JLabel("Потрібно садівників: 0");
        neededGardenersLabel.setFont(new Font("Arial", Font.BOLD, 16));
        orderSelectionPanel.add(neededGardenersLabel);

        orderComboBox.addActionListener(e -> {
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            if (selectedOrder != null) {
                neededGardenersLabel.setText("Потрібно садівників: " + selectedOrder.getGardenersNeeded());
            } else {
                neededGardenersLabel.setText("Потрібно садівників: 0");
            }
        });

        mainPanel.add(orderSelectionPanel);

        JPanel gardenerSelectionPanel = new JPanel(new BorderLayout(10, 5));
        gardenerSelectionPanel.setBorder(BorderFactory.createTitledBorder("Виберіть Садівників"));
        gardenerSelectionPanel.setBackground(new Color(229, 241, 224));

        ContactData contactData = new ContactData();
        List<Order.GardenerDisplayItem> availableGardeners = null;
        try {
            availableGardeners = contactData.getAvailableGardeners(connection);
        } catch (SQLException e) {
            new Validation().showErrorMessage(assignFrame, "Помилка при отриманні доступних садівників: " + e.getMessage(), "Помилка БД");
            return;
        }

        DefaultListModel<Order.GardenerDisplayItem> gardenerListModel = new DefaultListModel<>();
        for (Order.GardenerDisplayItem gardener : availableGardeners) {
            gardenerListModel.addElement(gardener);
        }
        JList<Order.GardenerDisplayItem> gardenerList = new JList<>(gardenerListModel);
        gardenerList.setFont(new Font("Arial", Font.PLAIN, 16));
        gardenerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane gardenerScrollPane = new JScrollPane(gardenerList);
        gardenerScrollPane.setPreferredSize(new Dimension(700, 200));
        gardenerSelectionPanel.add(gardenerScrollPane, BorderLayout.CENTER);

        mainPanel.add(gardenerSelectionPanel);

        JButton assignButton = new JButton("Призначити");
        assignButton.setFont(new Font("Arial", Font.BOLD, 20));
        assignButton.setBackground(new Color(46, 204, 113));
        assignButton.setForeground(Color.BLACK);
        assignButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        assignButton.addActionListener(e -> {
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            List<Order.GardenerDisplayItem> selectedGardeners = gardenerList.getSelectedValuesList();

            if (selectedOrder == null) {
                new Validation().showErrorMessage(assignFrame, "Будь ласка, виберіть замовлення.", "Помилка");
                return;
            }

            if (selectedGardeners.isEmpty()) {
                new Validation().showErrorMessage(assignFrame, "Будь ласка, виберіть хоча б одного садівника.", "Помилка");
                return;
            }

            if (selectedGardeners.size() > selectedOrder.getGardenersNeeded()) {
                new Validation().showErrorMessage(assignFrame,
                        String.format("Ви вибрали %d садівників, але для цього замовлення потрібно лише %d.",
                                selectedGardeners.size(), selectedOrder.getGardenersNeeded()),
                        "Помилка");
                return;
            }

            int confirm = new Validation().showConfirmationMessage(assignFrame,
                    String.format("Призначити %d садівників до замовлення ID: %d?", selectedGardeners.size(), selectedOrder.getId()),
                    "Підтвердження призначення");

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Order orderInstance = new Order();
                    orderInstance.attachWorkers(connection, selectedOrder, selectedGardeners, coordinatorId);
                    new Validation().showInformationMessage(assignFrame, "Садівників успішно призначено!", "Успіх");
                    refreshAssignGardenersWindow(connection, orderComboBox, gardenerListModel, neededGardenersLabel);
                } catch (SQLException ex) {
                    new Validation().showErrorMessage(assignFrame, "Помилка при призначенні садівників: " + ex.getMessage(), "Помилка БД");
                }
            }
        });

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(assignButton);
        mainPanel.add(Box.createVerticalStrut(20));

        assignFrame.add(mainPanel, BorderLayout.CENTER);
        assignFrame.setVisible(true);
    }

    private static void refreshAssignGardenersWindow(Connection connection, JComboBox<Order.OrderDisplayItem> orderComboBox, DefaultListModel<Order.GardenerDisplayItem> gardenerListModel, JLabel neededGardenersLabel) {
        List<Order.OrderDisplayItem> updatedOrders = getOrdersNeedingGardeners(connection);
        orderComboBox.setModel(new DefaultComboBoxModel<>(updatedOrders.toArray(new Order.OrderDisplayItem[0])));
        orderComboBox.setSelectedIndex(-1);
        if (orderComboBox.getSelectedItem() != null) {
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            neededGardenersLabel.setText("Потрібно садівників: " + selectedOrder.getGardenersNeeded());
        } else {
            neededGardenersLabel.setText("Потрібно садівників: 0");
        }

        ContactData contactData = new ContactData();
        List<Order.GardenerDisplayItem> updatedGardeners = null;
        try {
            updatedGardeners = contactData.getAvailableGardeners(connection);
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Помилка при оновленні списку доступних садівників: " + e.getMessage(), "Помилка БД");
            return;
        }

        gardenerListModel.clear();
        for (Order.GardenerDisplayItem gardener : updatedGardeners) {
            gardenerListModel.addElement(gardener);
        }
    }

    private static List<Order.OrderDisplayItem> getOrdersNeedingGardeners(Connection connection) {
        List<Order.OrderDisplayItem> orders = new ArrayList<>();
        String sql = "SELECT o.ID, o.Адреса, o.Стан, o.ДатаУкладання, o.IDКлієнта, k.ПІБ, o.Ціна, o.КількістьСадівників, " +
                "       COUNT(DISTINCT sz.IDСпівробітник) AS ПризначеноСадівників, MAX(ops.Обсяг) AS Обсяг " +
                "FROM Замовлення o " +
                "JOIN Клієнт k ON o.IDКлієнта = k.ID " +
                "LEFT JOIN СпівробітникЗамовлення sz ON o.ID = sz.IDЗамовлення AND sz.IDВидДіяльності IN (1, 2) " +
                "LEFT JOIN ЗамовленняПослуга ops ON o.ID = ops.IDЗамовлення " +
                "GROUP BY o.ID, o.Адреса, o.Стан, o.ДатаУкладання, o.IDКлієнта, k.ПІБ, o.Ціна, o.КількістьСадівників " +
                "HAVING o.КількістьСадівників > COUNT(DISTINCT sz.IDСпівробітник) AND o.Стан = 'Виконується'";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ID");
                String address = rs.getString("Адреса");
                String status = rs.getString("Стан");
                String date = rs.getDate("ДатаУкладання").toString();
                int clientId = rs.getInt("IDКлієнта");
                String clientPib = rs.getString("ПІБ");
                float totalCost = rs.getFloat("Ціна");
                float area = rs.getFloat("Обсяг");
                int gardenersNeeded = rs.getInt("КількістьСадівників") - rs.getInt("ПризначеноСадівників");
                if (gardenersNeeded > 0) {
                    Order tempOrder = new Order();
                    float procurementCost = 0.0f;
                    try {
                        procurementCost = tempOrder.calculateMaterialProcurementCost(connection, id, area);
                    } catch (SQLException e) {
                        new Validation().showErrorMessage(null, "Помилка при розрахунку вартості закупівлі матеріалів: " + e.getMessage(), "Помилка БД");
                    }

                    orders.add(new Order.OrderDisplayItem(id, address, status, date, clientId, clientPib, totalCost, gardenersNeeded, area, procurementCost));
                }
            }
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Помилка при отриманні замовлень, що потребують садівників: " + e.getMessage(), "Помилка БД");
        }
        return orders;
    }

    private static void openUnassignGardenersWindow(Connection connection) {
        JFrame unassignFrame = new JFrame("Відкріпити Садівників від Замовлення");
        unassignFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        unassignFrame.setSize(800, 600);
        unassignFrame.setLocationRelativeTo(null);
        unassignFrame.setLayout(new BorderLayout(10, 10));
        unassignFrame.getContentPane().setBackground(new Color(229, 241, 224));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(229, 241, 224));

        JPanel orderSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        orderSelectionPanel.setBorder(BorderFactory.createTitledBorder("Виберіть Замовлення"));
        orderSelectionPanel.setBackground(new Color(229, 241, 224));

        JLabel orderLabel = new JLabel("Замовлення:");
        orderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        orderSelectionPanel.add(orderLabel);

        List<Order.OrderDisplayItem> ordersWithAssignedGardeners = getOrdersWithAssignedGardeners(connection);
        JComboBox<Order.OrderDisplayItem> orderComboBox = new JComboBox<>(ordersWithAssignedGardeners.toArray(new Order.OrderDisplayItem[0]));
        orderComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        orderComboBox.setPreferredSize(new Dimension(400, 30));
        orderSelectionPanel.add(orderComboBox);

        orderComboBox.setSelectedIndex(-1);

        mainPanel.add(orderSelectionPanel);

        JPanel gardenerSelectionPanel = new JPanel(new BorderLayout(10, 5));
        gardenerSelectionPanel.setBorder(BorderFactory.createTitledBorder("Виберіть Садівників для відкріплення"));
        gardenerSelectionPanel.setBackground(new Color(229, 241, 224));

        DefaultListModel<Order.GardenerDisplayItem> assignedGardenerListModel = new DefaultListModel<>();
        JList<Order.GardenerDisplayItem> assignedGardenerList = new JList<>(assignedGardenerListModel);
        assignedGardenerList.setFont(new Font("Arial", Font.PLAIN, 16));
        assignedGardenerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane gardenerScrollPane = new JScrollPane(assignedGardenerList);
        gardenerScrollPane.setPreferredSize(new Dimension(700, 200));
        gardenerSelectionPanel.add(gardenerScrollPane, BorderLayout.CENTER);

        mainPanel.add(gardenerSelectionPanel);

        orderComboBox.addActionListener(e -> {
            assignedGardenerListModel.clear();
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            if (selectedOrder != null) {
                ContactData contactData = new ContactData();
                try {
                    List<Order.GardenerDisplayItem> gardenersAssignedToOrder = contactData.getGardenersAssignedToOrder(connection, selectedOrder.getId());
                    for (Order.GardenerDisplayItem gardener : gardenersAssignedToOrder) {
                        assignedGardenerListModel.addElement(gardener);
                    }
                } catch (SQLException ex) {
                    new Validation().showErrorMessage(unassignFrame, "Помилка при завантаженні призначених садівників: " + ex.getMessage(), "Помилка БД");
                }
            }
        });

        JButton unassignButton = new JButton("Відкріпити");
        unassignButton.setFont(new Font("Arial", Font.BOLD, 20));
        unassignButton.setBackground(new Color(231, 76, 60));
        unassignButton.setForeground(Color.BLACK);
        unassignButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        unassignButton.addActionListener(e -> {
            Order.OrderDisplayItem selectedOrder = (Order.OrderDisplayItem) orderComboBox.getSelectedItem();
            List<Order.GardenerDisplayItem> selectedGardeners = assignedGardenerList.getSelectedValuesList();

            if (selectedOrder == null) {
                new Validation().showErrorMessage(unassignFrame, "Будь ласка, виберіть замовлення.", "Помилка");
                return;
            }

            if (selectedGardeners.isEmpty()) {
                new Validation().showErrorMessage(unassignFrame, "Будь ласка, виберіть хоча б одного садівника для відкріплення.", "Помилка");
                return;
            }

            int confirm = new Validation().showConfirmationMessage(unassignFrame,
                    String.format("Відкріпити %d садівників від замовлення ID: %d?", selectedGardeners.size(), selectedOrder.getId()),
                    "Підтвердження відкріплення");

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Order orderInstance = new Order();
                    orderInstance.unassignGardenersFromOrder(connection, selectedOrder.getId(), selectedGardeners);
                    new Validation().showInformationMessage(unassignFrame, "Садівників успішно відкріплено!", "Успіх");
                    refreshUnassignGardenersWindow(connection, orderComboBox, assignedGardenerListModel);
                } catch (SQLException ex) {
                    new Validation().showErrorMessage(unassignFrame, "Помилка при відкріпленні садівників: " + ex.getMessage(), "Помилка БД");
                }
            }
        });

        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(unassignButton);
        mainPanel.add(Box.createVerticalStrut(20));

        unassignFrame.add(mainPanel, BorderLayout.CENTER);
        unassignFrame.setVisible(true);
    }

    private static void refreshUnassignGardenersWindow(Connection connection, JComboBox<Order.OrderDisplayItem> orderComboBox, DefaultListModel<Order.GardenerDisplayItem> assignedGardenerListModel) {
        List<Order.OrderDisplayItem> updatedOrders = getOrdersWithAssignedGardeners(connection);
        orderComboBox.setModel(new DefaultComboBoxModel<>(updatedOrders.toArray(new Order.OrderDisplayItem[0])));
        orderComboBox.setSelectedIndex(-1);

        assignedGardenerListModel.clear();
    }

    private static List<Order.OrderDisplayItem> getOrdersWithAssignedGardeners(Connection connection) {
        List<Order.OrderDisplayItem> orders = new ArrayList<>();
        String sql = "SELECT o.ID, o.Адреса, o.Стан, o.ДатаУкладання, o.IDКлієнта, k.ПІБ, o.Ціна, o.КількістьСадівників, MAX(ops.Обсяг) AS Обсяг " +
                "FROM Замовлення o " +
                "JOIN Клієнт k ON o.IDКлієнта = k.ID " +
                "JOIN СпівробітникЗамовлення sz ON o.ID = sz.IDЗамовлення AND sz.IDВидДіяльності IN (1, 2) " +
                "LEFT JOIN ЗамовленняПослуга ops ON o.ID = ops.IDЗамовлення " +
                "WHERE o.Стан = 'Виконується' " +
                "GROUP BY o.ID, o.Адреса, o.Стан, o.ДатаУкладання, o.IDКлієнта, k.ПІБ, o.Ціна, o.КількістьСадівників " +
                "HAVING COUNT(DISTINCT sz.IDСпівробітник) > 0";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ID");
                String address = rs.getString("Адреса");
                String status = rs.getString("Стан");
                String date = rs.getDate("ДатаУкладання").toString();
                int clientId = rs.getInt("IDКлієнта");
                String clientPib = rs.getString("ПІБ");
                float totalCost = rs.getFloat("Ціна");
                int gardenersNeeded = rs.getInt("КількістьСадівників");
                float area = rs.getFloat("Обсяг");

                Order tempOrder = new Order();
                float procurementCost = 0.0f;
                try {
                    procurementCost = tempOrder.calculateMaterialProcurementCost(connection, id, area);
                } catch (SQLException e) {
                    new Validation().showErrorMessage(null, "Помилка при розрахунку вартості закупівлі матеріалів: " + e.getMessage(), "Помилка БД");
                }
                orders.add(new Order.OrderDisplayItem(id, address, status, date, clientId, clientPib, totalCost, gardenersNeeded, area, procurementCost));
            }
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Помилка при отриманні замовлень з призначеними садівниками: " + e.getMessage(), "Помилка БД");
        }
        return orders;
    }

    private static void openGardenerDashboard(String employeePib, Connection connection, int gardenerId) {
        JFrame gardenerFrame = new JFrame("Вікно Садівника: " + employeePib);
        gardenerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gardenerFrame.setSize(1200, 800);
        gardenerFrame.setLocationRelativeTo(null);
        gardenerFrame.setLayout(null);
        gardenerFrame.getContentPane().setBackground(new Color(204, 229, 204));

        JLabel greetingLabel = new JLabel("Привіт, " + employeePib + "! Ви увійшли як Садівник.");
        greetingLabel.setFont(new Font("Arial", Font.BOLD, 30));
        greetingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        greetingLabel.setBounds(0, 20, gardenerFrame.getWidth(), 40);
        gardenerFrame.add(greetingLabel);

        JButton instructionsButton = new JButton("I");
        instructionsButton.setFont(new Font("Arial", Font.BOLD, 24));
        instructionsButton.setBounds(gardenerFrame.getWidth() - 90, 20, 60, 60);
        instructionsButton.setBackground(new Color(231, 76, 60));
        instructionsButton.setForeground(Color.BLACK);
        instructionsButton.setFocusPainted(false);
        instructionsButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        gardenerFrame.add(instructionsButton);

        instructionsButton.addActionListener(e -> {
            String instructions = "<html><body style='font-family: Arial; font-size: 16px;'>" +
                    "<h2>Вікно Садівника:</h2>" +
                    "<ul>" +
                    "<li>Це вікно відображає ваше поточне активне замовлення.</li>" +
                    "<li><b>\"Оновити Замовлення\"</b>: Оновлює інформацію про поточне замовлення, перевіряючи наявність матеріалів та очікувану дату виконання.</li>" +
                    "<li><b>\"Вийти\"</b>: Закриває програму.</li>" +
                    "</ul>" +
                    "</body></html>";
            showInstructions("Інструкції для Садівника", instructions);
        });

        JPanel orderDisplayPanel = new JPanel();
        orderDisplayPanel.setLayout(new GridBagLayout());
        orderDisplayPanel.setBounds(50, 100, gardenerFrame.getWidth() - 100, 400);
        orderDisplayPanel.setBackground(new Color(229, 241, 224));
        gardenerFrame.add(orderDisplayPanel);

        JButton refreshButton = new JButton("Оновити Замовлення");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 20));
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setBounds((gardenerFrame.getWidth() - 300) / 2, 520, 300, 70);
        gardenerFrame.add(refreshButton);

        JButton exitButton = new JButton("Вийти");
        exitButton.setFont(new Font("Arial", Font.BOLD, 20));
        exitButton.setBackground(new Color(231, 76, 60));
        exitButton.setForeground(Color.BLACK);
        exitButton.setBounds((gardenerFrame.getWidth() - 200) / 2, 600, 200, 70);
        gardenerFrame.add(exitButton);

        exitButton.addActionListener(e -> {
            int confirmResult = new Validation().showConfirmationMessage(
                    gardenerFrame,
                    "Ви впевнені, що хочете вийти з програми?",
                    "Підтвердження виходу"
            );

            if (confirmResult == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        Runnable loadOrders = () -> {
            orderDisplayPanel.removeAll();

            try {
                Order.GardenerOrderDisplayItem assignedOrder = Order.getGardenerAssignedOrder(connection, gardenerId);
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
                    Order orderInstance = new Order();
                    orderInstance.displayGardenerOrderInfo(connection, gardenerId, orderDisplayPanel);
                }
            } catch (SQLException e) {
                new Validation().showErrorMessage(gardenerFrame, "Помилка при завантаженні замовлень: " + e.getMessage(), "Помилка БД");
            } finally {
                orderDisplayPanel.revalidate();
                orderDisplayPanel.repaint();
            }
        };

        refreshButton.addActionListener(e -> loadOrders.run());

        loadOrders.run();

        gardenerFrame.setVisible(true);
    }

    private static void resetBorders(JTextField clientNameField, JTextField phoneNumberField, JTextField areaField,
                                     JTextField houseNumberField,
                                     List<JCheckBox> serviceCheckBoxes,
                                     JComboBox<String> streetComboBox,
                                     Border defaultTextFieldBorder, Border defaultComboBoxBorder) {
        clientNameField.setBorder(defaultTextFieldBorder);
        phoneNumberField.setBorder(defaultTextFieldBorder);
        areaField.setBorder(defaultTextFieldBorder);
        houseNumberField.setBorder(defaultTextFieldBorder);
        streetComboBox.setBorder(defaultComboBoxBorder);
    }

    private static void resetBorders(JTextField clientNameField, JTextField phoneNumberField, JTextField areaField,
                                     JTextField houseNumberField, JTextField priceField, JTextField completionDateField,
                                     List<JCheckBox> serviceCheckBoxes,
                                     JComboBox<String> streetComboBox,
                                     Border defaultTextFieldBorder, Border defaultComboBoxBorder) {
        clientNameField.setBorder(defaultTextFieldBorder);
        phoneNumberField.setBorder(defaultTextFieldBorder);
        areaField.setBorder(defaultTextFieldBorder);
        houseNumberField.setBorder(defaultTextFieldBorder);
        priceField.setBorder(defaultTextFieldBorder);
        completionDateField.setBorder(defaultTextFieldBorder);
        streetComboBox.setBorder(defaultComboBoxBorder);
    }

    private static void highlightEmptyField(JComponent component) {
        component.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    }

    public static void main(String[] args) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS Співробітник (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "ПІБ CHAR(40) NOT NULL, " +
                        "НазваПосади VARCHAR(100) NOT NULL, " +
                        "ДатаНародження DATE NOT NULL, " +
                        "Стать CHAR NOT NULL, " +
                        "КонтактніДані CHAR(40) NOT NULL, " +
                        "Пароль CHAR(255) NOT NULL" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS ВидДіяльності (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Назва CHAR(20) NOT NULL" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS Клієнт (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "ПІБ CHAR(40) NOT NULL, " +
                        "КонтактніДані CHAR(40) NOT NULL" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS Оплата (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Ціна FLOAT NOT NULL, " +
                        "ДатаОплати DATE NOT NULL, " +
                        "IDКлієнта INT NOT NULL, " +
                        "FOREIGN KEY (IDКлієнта) REFERENCES Клієнт(id)" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS Замовлення (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "ДатаУкладання DATE NOT NULL, " +
                        "Стан CHAR(20) NOT NULL, " +
                        "IDКлієнта INT NOT NULL, " +
                        "Адреса VARCHAR(255) NOT NULL, " +
                        "Ціна FLOAT NOT NULL, " +
                        "КількістьСадівників INT, " +
                        "ДатаВиконання DATE, " +
                        "FOREIGN KEY (IDКлієнта) REFERENCES Клієнт(id)" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS СпівробітникЗамовлення (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "IDСпівробітник INT NOT NULL, " +
                        "IDЗамовлення INT NOT NULL, " +
                        "IDВидДіяльності INT NOT NULL, " +
                        "FOREIGN KEY (IDСпівробітник) REFERENCES Співробітник(id), " +
                        "FOREIGN KEY (IDЗамовлення) REFERENCES Замовлення(id), " +
                        "FOREIGN KEY (IDВидДіяльності) REFERENCES ВидДіяльності(id)" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS Послуга (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Назва CHAR(30) NOT NULL" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS ЗамовленняПослуга (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "IDЗамовлення INT NOT NULL, " +
                        "IDПослуга INT NOT NULL, " +
                        "Обсяг FLOAT NOT NULL, " +
                        "FOREIGN KEY (IDЗамовлення) REFERENCES Замовлення(id), " +
                        "FOREIGN KEY (IDПослуга) REFERENCES Послуга(id)" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS Устаткування (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Назва CHAR(30) NOT NULL" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS УстаткуванняПослуга (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "IDУстаткування INT NOT NULL, " +
                        "IDПослуга INT NOT NULL, " +
                        "FOREIGN KEY (IDУстаткування) REFERENCES Устаткування(id), " +
                        "FOREIGN KEY (IDПослуга) REFERENCES Послуга(id)" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS Матеріал (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Назва CHAR(30) NOT NULL, " +
                        "Ціна FLOAT NOT NULL, " +
                        "ОдиницяВиміру CHAR(20) NOT NULL DEFAULT 'шт.', " +
                        "КількістьВНаявності FLOAT NOT NULL DEFAULT 0.0" +
                        ")");
                try (ResultSet rs = connection.getMetaData().getColumns(null, null, "Матеріал", "ОдиницяВиміру")) {
                    if (!rs.next()) {
                        stmt.execute("ALTER TABLE Матеріал ADD COLUMN ОдиницяВиміру CHAR(20) NOT NULL DEFAULT 'шт.'");
                    }
                }

                stmt.execute("CREATE TABLE IF NOT EXISTS ПослугаМатеріал (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "IDПослуга INT NOT NULL, " +
                        "IDМатеріал INT NOT NULL, " +
                        "Кількість FLOAT NOT NULL, " +
                        "FOREIGN KEY (IDПослуга) REFERENCES Послуга(id), " +
                        "FOREIGN KEY (IDМатеріал) REFERENCES Матеріал(id)" +
                        ")");

                try (ResultSet rs = connection.getMetaData().getColumns(null, null, "ПослугаМатеріал", "Кількість")) {
                    if (rs.next()) {
                        String columnType = rs.getString("TYPE_NAME");
                        if (!columnType.toLowerCase().contains("float") && !columnType.toLowerCase().contains("double")) {
                            stmt.execute("ALTER TABLE ПослугаМатеріал MODIFY COLUMN Кількість FLOAT NOT NULL");
                        }
                    }
                }
                try (ResultSet rs = connection.getMetaData().getIndexInfo(null, null, "ПослугаМатеріал", false, false)) {
                    boolean uniqueIndexExists = false;
                    while (rs.next()) {
                        if (rs.getString("INDEX_NAME") != null && rs.getString("INDEX_NAME").equals("IDПослуга_IDМатеріал_Unique")) {
                            uniqueIndexExists = true;
                            break;
                        }
                    }
                    if (!uniqueIndexExists) {
                        stmt.execute("ALTER TABLE ПослугаМатеріал ADD CONSTRAINT IDПослуга_IDМатеріал_Unique UNIQUE (IDПослуга, IDМатеріал)");
                    }
                }

                stmt.execute("CREATE TABLE IF NOT EXISTS Закупівля (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Дата DATE NOT NULL" +
                        ")");

                stmt.execute("CREATE TABLE IF NOT EXISTS МатеріалЗакупівля (" +
                        "id INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "IDМатеріал INT NOT NULL, " +
                        "IDЗакупівля INT NOT NULL, " +
                        "Кількість FLOAT NOT NULL, " +
                        "FOREIGN KEY (IDМатеріал) REFERENCES Матеріал(id), " +
                        "FOREIGN KEY (IDЗакупівля) REFERENCES Закупівля(id)" +
                        ")");



                String insertMaterialSql = "INSERT IGNORE INTO Матеріал (id, Назва, Ціна, КількістьВНаявності, ОдиницяВиміру) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertMaterialSql)) {
                    pstmt.setInt(1, 1); pstmt.setString(2, "Ґрунт родючий"); pstmt.setFloat(3, 85.50f); pstmt.setFloat(4, 1000.0f); pstmt.setString(5, "м³"); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setString(2, "Торф"); pstmt.setFloat(3, 65.00f); pstmt.setFloat(4, 800.0f); pstmt.setString(5, "м³"); pstmt.addBatch();
                    pstmt.setInt(1, 3); pstmt.setString(2, "Дренажний щебінь"); pstmt.setFloat(3, 120.00f); pstmt.setFloat(4, 500.0f); pstmt.setString(5, "м³"); pstmt.addBatch();
                    pstmt.setInt(1, 4); pstmt.setString(2, "Мульча"); pstmt.setFloat(3, 55.75f); pstmt.setFloat(4, 400.0f); pstmt.setString(5, "м³"); pstmt.addBatch();
                    pstmt.setInt(1, 5); pstmt.setString(2, "Деревна кора"); pstmt.setFloat(3, 48.00f); pstmt.setFloat(4, 600.0f); pstmt.setString(5, "м³"); pstmt.addBatch();
                    pstmt.setInt(1, 6); pstmt.setString(2, "Компост"); pstmt.setFloat(3, 35.00f); pstmt.setFloat(4, 700.0f); pstmt.setString(5, "м³"); pstmt.addBatch();
                    pstmt.setInt(1, 7); pstmt.setString(2, "Газонна трава"); pstmt.setFloat(3, 230.00f); pstmt.setFloat(4, 500.0f); pstmt.setString(5, "кг"); pstmt.addBatch();
                    pstmt.setInt(1, 8); pstmt.setString(2, "Саджанці дерев"); pstmt.setFloat(3, 180.00f); pstmt.setFloat(4, 300.0f); pstmt.setString(5, "шт."); pstmt.addBatch();
                    pstmt.setInt(1, 9); pstmt.setString(2, "Саджанці кущів"); pstmt.setFloat(3, 95.00f); pstmt.setFloat(4, 800.0f); pstmt.setString(5, "шт."); pstmt.addBatch();
                    pstmt.setInt(1, 10); pstmt.setString(2, "Саджанці квітів"); pstmt.setFloat(3, 25.00f); pstmt.setFloat(4, 2000.0f); pstmt.setString(5, "шт."); pstmt.addBatch();
                    pstmt.setInt(1, 11); pstmt.setString(2, "Пісок річковий"); pstmt.setFloat(3, 40.00f); pstmt.setFloat(4, 400.0f); pstmt.setString(5, "м³"); pstmt.addBatch();
                    pstmt.setInt(1, 12); pstmt.setString(2, "Підв’язки для дерев"); pstmt.setFloat(3, 10.00f); pstmt.setFloat(4, 300.0f); pstmt.setString(5, "шт."); pstmt.addBatch();
                    pstmt.setInt(1, 13); pstmt.setString(2, "Металеві кілки"); pstmt.setFloat(3, 55.00f); pstmt.setFloat(4, 200.0f); pstmt.setString(5, "шт."); pstmt.addBatch();
                    pstmt.setInt(1, 14); pstmt.setString(2, "Секатори"); pstmt.setFloat(3, 340.00f); pstmt.setFloat(4, 100.0f); pstmt.setString(5, "шт."); pstmt.addBatch();
                    pstmt.setInt(1, 15); pstmt.setString(2, "Бензопила"); pstmt.setFloat(3, 4300.00f); pstmt.setFloat(4, 0.0f); pstmt.setString(5, "шт."); pstmt.addBatch();
                    pstmt.setInt(1, 16); pstmt.setString(2, "Добрива мінеральні"); pstmt.setFloat(3, 130.00f); pstmt.setFloat(4, 600.0f); pstmt.setString(5, "кг"); pstmt.addBatch();
                    pstmt.setInt(1, 17); pstmt.setString(2, "Добрива органічні"); pstmt.setFloat(3, 95.00f); pstmt.setFloat(4, 500.0f); pstmt.setString(5, "кг"); pstmt.addBatch();
                    pstmt.setInt(1, 18); pstmt.setString(2, "Антисептик для зрізів"); pstmt.setFloat(3, 65.00f); pstmt.setFloat(4, 0.0f); pstmt.setString(5, "л"); pstmt.addBatch();
                    pstmt.setInt(1, 19); pstmt.setString(2, "Засіб від шкідників"); pstmt.setFloat(3, 75.00f); pstmt.setFloat(4, 200.0f); pstmt.setString(5, "л"); pstmt.addBatch();
                    pstmt.setInt(1, 20); pstmt.setString(2, "Сітка від кротів"); pstmt.setFloat(3, 150.00f); pstmt.setFloat(4, 100.0f); pstmt.setString(5, "м²"); pstmt.addBatch();
                    pstmt.executeBatch();
                } catch (SQLException e) {
                    new Validation().showErrorMessage(null, "Помилка при вставці початкових матеріалів: " + e.getMessage(), "Помилка БД");
                }

                String insertServiceSql = "INSERT IGNORE INTO Послуга (id, Назва) VALUES (?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertServiceSql)) {
                    pstmt.setInt(1, 1); pstmt.setString(2, "Підрізання дерев"); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setString(2, "Озеленення території"); pstmt.addBatch();
                    pstmt.executeBatch();
                } catch (SQLException e) {
                    new Validation().showErrorMessage(null, "Помилка при вставці початкових послуг: " + e.getMessage(), "Помилка БД");
                }


                String insertPmsSql = "INSERT IGNORE INTO ПослугаМатеріал (IDПослуга, IDМатеріал, Кількість) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertPmsSql)) {
                    pstmt.setInt(1, 1); pstmt.setInt(2, 1); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 2); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 3); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 4); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 5); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 6); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 7); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 8); pstmt.setFloat(3, 0.05f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 9); pstmt.setFloat(3, 0.03f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 10); pstmt.setFloat(3, 0.1f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 11); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 12); pstmt.setFloat(3, 0.1f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 13); pstmt.setFloat(3, 0.05f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 14); pstmt.setFloat(3, 0.01f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 15); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 16); pstmt.setFloat(3, 0.02f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 17); pstmt.setFloat(3, 0.01f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 18); pstmt.setFloat(3, 0.01f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 19); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 1); pstmt.setInt(2, 20); pstmt.setFloat(3, 0.0f); pstmt.addBatch();

                    pstmt.setInt(1, 2); pstmt.setInt(2, 1); pstmt.setFloat(3, 1.0f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 2); pstmt.setFloat(3, 0.8f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 3); pstmt.setFloat(3, 0.5f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 4); pstmt.setFloat(3, 0.4f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 5); pstmt.setFloat(3, 0.6f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 6); pstmt.setFloat(3, 0.7f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 7); pstmt.setFloat(3, 0.5f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 8); pstmt.setFloat(3, 0.05f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 9); pstmt.setFloat(3, 0.1f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 10); pstmt.setFloat(3, 0.5f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 11); pstmt.setFloat(3, 0.4f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 12); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 13); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 14); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 15); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 16); pstmt.setFloat(3, 0.6f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 17); pstmt.setFloat(3, 0.5f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 18); pstmt.setFloat(3, 0.0f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 19); pstmt.setFloat(3, 0.2f); pstmt.addBatch();
                    pstmt.setInt(1, 2); pstmt.setInt(2, 20); pstmt.setFloat(3, 0.1f); pstmt.addBatch();

                    pstmt.executeBatch();
                } catch (SQLException e) {
                    new Validation().showErrorMessage(null, "Помилка при вставці початкових даних ПослугаМатеріал: " + e.getMessage(), "Помилка БД");
                }

            } catch (SQLException e) {
                new Validation().showErrorMessage(null, "Помилка при перевірці/оновленні схеми бази даних: " + e.getMessage() + "\nБудь ласка, перевірте схему таблиць.", "Помилка схеми БД");
                return;
            }

            ToolTipManager.sharedInstance().setInitialDelay(0);
            JFrame frame = new JFrame("Вхід");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1600, 1000);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(null);
            rightPanel.setPreferredSize(new Dimension(1600, 1000));
            rightPanel.setBackground(new Color(204, 229, 204));
            frame.add(rightPanel, BorderLayout.CENTER);

            JLabel projectLabel = new JLabel("Проєкт: Зелентрест");
            projectLabel.setFont(new Font("Arial", Font.BOLD, 50));
            projectLabel.setBounds(400, 50, 800, 60);
            projectLabel.setHorizontalAlignment(SwingConstants.CENTER);
            rightPanel.add(projectLabel);

            JLabel authorLabel = new JLabel("Зробив: АС-232, Лациновський Артем");
            authorLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            authorLabel.setBounds(1600 - 500, 20, 400, 20);
            authorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            rightPanel.add(authorLabel);

            JLabel[] labels = {
                    new JLabel("Номер телефону:"),
                    new JLabel("Пароль:")
            };
            JPasswordField passwordField = new JPasswordField();

            JTextField[] textFields = {
                    new JTextField(),
                    passwordField
            };
            JLabel phonePrefixLabel = new JLabel("+380");
            phonePrefixLabel.setFont(new Font("Arial", Font.BOLD, 32));
            int yPosition = 300;

            int centerX = frame.getWidth() / 2;
            int labelX = centerX - 250;
            int fieldX = centerX + 50;

            for (int i = 0; i < labels.length; i++) {
                labels[i].setBounds(labelX, yPosition, 300, 50);
                labels[i].setFont(new Font("Arial", Font.BOLD, 32));
                rightPanel.add(labels[i]);
                if (i == 0) {
                    phonePrefixLabel.setBounds(fieldX, yPosition, 100, 50);
                    rightPanel.add(phonePrefixLabel);
                    textFields[i].setBounds(fieldX + 100, yPosition, 150, 50);
                    ((AbstractDocument) textFields[i].getDocument()).setDocumentFilter(new DocumentFilter() {
                        @Override
                        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                            int totalLength = currentText.length() - length + text.length();
                            if (totalLength <= 9 && text.matches("\\d*")) {
                                super.replace(fb, offset, length, text, attrs);
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }

                        @Override
                        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                            int totalLength = currentText.length() + string.length();
                            if (totalLength <= 9 && string.matches("\\d*")) {
                                super.insertString(fb, offset, string, attr);
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }
                    });

                } else {
                    textFields[i].setBounds(fieldX + 50, yPosition, 200, 50);
                    JCheckBox showPasswordCheckBox = new JCheckBox("Показувати символи паролю");
                    showPasswordCheckBox.setBounds(fieldX + 50, yPosition + 60, 400, 30);
                    showPasswordCheckBox.setFont(new Font("Arial", Font.PLAIN, 20));
                    showPasswordCheckBox.setBackground(new Color(204, 229, 204));
                    rightPanel.add(showPasswordCheckBox);

                    showPasswordCheckBox.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (showPasswordCheckBox.isSelected()) {
                                passwordField.setEchoChar((char) 0);
                            } else {
                                passwordField.setEchoChar('*');
                            }
                        }
                    });
                }
                textFields[i].setFont(new Font("Arial", Font.PLAIN, 28));
                rightPanel.add(textFields[i]);

                yPosition += 100;
            }

            int buttonWidth = 300;
            int buttonHeight = 100;
            int buttonX = centerX - (buttonWidth / 2) + 50;

            JButton loginButton = new JButton("Увійти");
            loginButton.setBounds(buttonX, yPosition + 40, buttonWidth, buttonHeight);
            loginButton.setFont(new Font("Arial", Font.BOLD, 30));
            loginButton.setBackground(new Color(46, 204, 113));
            loginButton.setForeground(Color.BLACK);
            rightPanel.add(loginButton);

            JButton exitButton = new JButton("Вихід");
            exitButton.setBounds(buttonX, yPosition + 160, buttonWidth, buttonHeight);
            exitButton.setFont(new Font("Arial", Font.BOLD, 30));
            exitButton.setBackground(new Color(231, 76, 60));
            exitButton.setForeground(Color.BLACK);
            rightPanel.add(exitButton);

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int confirmResult = new Validation().showConfirmationMessage(
                            frame,
                            "Ви впевнені, що хочете вийти зі всіх вікон та повернутись до головного меню?",
                            "Підтвердження дії"
                    );

                    if (confirmResult == JOptionPane.YES_OPTION) {
                        for (Window window : Window.getWindows()) {
                            if (window instanceof JFrame && window != frame) {
                                window.dispose();
                            }
                        }
                        if (frame != null) {
                            frame.setVisible(true);
                            frame.toFront();
                            frame.requestFocus();
                        } else {
                            new Validation().showErrorMessage(null, "Не вдалося повернутися до головного вікна, оскільки воно не ініціалізоване.", "Помилка");
                        }
                    }
                }
            });

            final Connection finalConnection = connection;
            final Validation validator = new Validation();

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String phoneNumberPart = textFields[0].getText();
                    String password = new String(passwordField.getPassword());
                    String fullPhoneNumber = "+380" + phoneNumberPart;

                    String[] authResult = authenticateEmployee(finalConnection, fullPhoneNumber, password);

                    if (authResult != null) {
                        String employeePib = authResult[0];
                        String activityType = authResult[1];
                        int employeeId = Integer.parseInt(authResult[2]);

                        frame.dispose();

                        openEmployeeDashboard(employeePib, activityType, finalConnection, employeeId);

                    } else {
                        new Validation().showErrorMessage(frame, "Введено неправильний номер телефону або пароль.", "Помилка входу");
                    }
                }
            });
            frame.setVisible(true);
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Не вдалося підключитися до бази даних. Перевірте конфігурацію.", "Помилка підключення");
        } finally {
            final Connection connToClose = connection;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (connToClose != null) {
                    try {
                        connToClose.close();
                    } catch (SQLException e) {
                        new Validation().showErrorMessage(null, "Помилка при закритті з'єднання з базою даних: " + e.getMessage(), "Помилка БД");
                    }
                }
            }));
        }
    }

    private static String[] authenticateEmployee(Connection connection, String phoneNumber, String password) {
        String pib = null;
        String activityType = null;
        int employeeId = -1;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT s.ID, s.ПІБ, s.НазваПосади " +
                "FROM Співробітник s " +
                "WHERE REPLACE(TRIM(s.КонтактніДані), ' ', '') = ? AND TRIM(s.Пароль) = ?";
        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, phoneNumber);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                employeeId = rs.getInt("ID");
                pib = rs.getString("ПІБ");
                activityType = rs.getString("НазваПосади");
            }
        } catch (SQLException e) {
            new Validation().showErrorMessage(null, "Помилка при автентифікації співробітника: " + e.getMessage(), "Помилка БД");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    new Validation().showErrorMessage(null, "Помилка при закритті ResultSet: " + e.getMessage(), "Помилка БД");
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    new Validation().showErrorMessage(null, "Помилка при закритті PreparedStatement: " + e.getMessage(), "Помилка БД");
                }
            }
        }
        if (pib != null && activityType != null && employeeId != -1) {
            return new String[]{pib, activityType, String.valueOf(employeeId)};
        }
        return null;
    }
}
