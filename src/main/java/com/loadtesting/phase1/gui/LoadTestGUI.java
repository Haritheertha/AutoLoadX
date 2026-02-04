package com.loadtesting.phase1.gui;

import com.loadtesting.phase1.model.PerformanceMetrics;
import com.loadtesting.phase1.model.TestConfiguration;
import com.loadtesting.phase1.service.LoadTestExecutor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class LoadTestGUI extends JFrame {
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;
    
    // Components
    private JTextField endpointField;
    private JComboBox<String> methodCombo;
    private JTextArea bodyArea;
    private JTextField usersField;
    private JTextField durationField;
    private JButton manualButton;
    private JButton autoButton;
    private JButton clearButton;
    private JTextArea resultsArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JTabbedPane tabbedPane;
    private JPanel metricsPanel;
    private JLabel totalRequestsLabel;
    private JLabel successRateLabel;
    private JLabel avgResponseLabel;
    private JLabel throughputLabel;
    private DecimalFormat df = new DecimalFormat("#.##");
    
    public LoadTestGUI() {
        setTitle("AutoLoadX - Professional Load Testing Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setIconImage(createAppIcon());
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default look and feel
        }
        
        initComponents();
        layoutComponents();
        applyModernStyling();
    }
    
    private void initComponents() {
        // Input fields
        endpointField = new JTextField("http://testphp.vulnweb.com/", 30);
        methodCombo = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE"});
        bodyArea = new JTextArea(4, 30);
        usersField = new JTextField("10", 10);
        durationField = new JTextField("60", 10);
        
        // Buttons
        manualButton = new JButton("🚀 Start Manual Test");
        autoButton = new JButton("⚡ Run Automated Test");
        clearButton = new JButton("🗑️ Clear Results");
        
        // Results and status
        resultsArea = new JTextArea(20, 60);
        progressBar = new JProgressBar();
        statusLabel = new JLabel("Ready to start testing");
        
        // Metrics labels
        totalRequestsLabel = new JLabel("0");
        successRateLabel = new JLabel("0%");
        avgResponseLabel = new JLabel("0 ms");
        throughputLabel = new JLabel("0 req/s");
        
        // Configure components
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        bodyArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        
        // Add action listeners
        manualButton.addActionListener(this::runManualTest);
        autoButton.addActionListener(this::runAutomatedTest);
        clearButton.addActionListener(e -> clearResults());
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Main container with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Configuration tab
        JPanel configTab = createConfigurationPanel();
        tabbedPane.addTab("⚙️ Configuration", configTab);
        
        // Results tab
        JPanel resultsTab = createResultsPanel();
        tabbedPane.addTab("📊 Results", resultsTab);
        
        // Metrics tab
        JPanel metricsTab = createMetricsPanel();
        tabbedPane.addTab("📈 Live Metrics", metricsTab);
        
        // Status panel at bottom
        JPanel statusPanel = createStatusPanel();
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createConfigurationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        
        // API Configuration
        JPanel apiPanel = createCardPanel("🌐 API Configuration");
        apiPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        apiPanel.add(createStyledLabel("Endpoint URL:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        apiPanel.add(endpointField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        apiPanel.add(createStyledLabel("HTTP Method:"), gbc);
        gbc.gridx = 1;
        apiPanel.add(methodCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        apiPanel.add(createStyledLabel("Request Body:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyScroll.setBorder(BorderFactory.createLoweredBevelBorder());
        apiPanel.add(bodyScroll, gbc);
        
        // Load Configuration
        JPanel loadPanel = createCardPanel("⚡ Load Configuration");
        loadPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        loadPanel.add(createStyledLabel("Concurrent Users:"), gbc);
        gbc.gridx = 1;
        loadPanel.add(usersField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        loadPanel.add(createStyledLabel("Duration (seconds):"), gbc);
        gbc.gridx = 1;
        loadPanel.add(durationField, gbc);
        
        // Control buttons
        JPanel buttonPanel = createCardPanel("🎮 Test Controls");
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(manualButton);
        buttonPanel.add(autoButton);
        buttonPanel.add(clearButton);
        
        panel.add(apiPanel, BorderLayout.NORTH);
        panel.add(loadPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        
        JPanel resultsCard = createCardPanel("📋 Test Results");
        resultsCard.setLayout(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        resultsCard.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(resultsCard, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create metric cards
        panel.add(createMetricCard("📊 Total Requests", totalRequestsLabel, PRIMARY_COLOR));
        panel.add(createMetricCard("✅ Success Rate", successRateLabel, SUCCESS_COLOR));
        panel.add(createMetricCard("⏱️ Avg Response Time", avgResponseLabel, WARNING_COLOR));
        panel.add(createMetricCard("🚀 Throughput", throughputLabel, SECONDARY_COLOR));
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JPanel progressPanel = new JPanel(new BorderLayout(5, 0));
        progressPanel.setBackground(BACKGROUND_COLOR);
        progressPanel.add(new JLabel("Status:"), BorderLayout.WEST);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        panel.add(progressPanel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 12),
                PRIMARY_COLOR
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }
    
    private JPanel createMetricCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        titleLabel.setForeground(color);
        
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        valueLabel.setForeground(SECONDARY_COLOR);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        label.setForeground(SECONDARY_COLOR);
        return label;
    }
    
    private void applyModernStyling() {
        // Style buttons
        styleButton(manualButton, PRIMARY_COLOR);
        styleButton(autoButton, SUCCESS_COLOR);
        styleButton(clearButton, WARNING_COLOR);
        
        // Style input fields
        styleTextField(endpointField);
        styleTextField(usersField);
        styleTextField(durationField);
        
        // Style combo box
        methodCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        
        // Style text areas
        bodyArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        resultsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Style progress bar
        progressBar.setForeground(PRIMARY_COLOR);
        
        // Style status label
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        statusLabel.setForeground(SECONDARY_COLOR);
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
    private Image createAppIcon() {
        // Create a simple icon
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(PRIMARY_COLOR);
        g2d.fillRoundRect(2, 2, 28, 28, 8, 8);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        g2d.drawString("AX", 8, 20);
        g2d.dispose();
        return icon;
    }
    
    private void clearResults() {
        resultsArea.setText("");
        updateMetrics(null);
        statusLabel.setText("Results cleared");
        progressBar.setValue(0);
        progressBar.setString("Ready");
    }
    
    private void updateMetrics(PerformanceMetrics metrics) {
        if (metrics == null) {
            totalRequestsLabel.setText("0");
            successRateLabel.setText("0%");
            avgResponseLabel.setText("0 ms");
            throughputLabel.setText("0 req/s");
        } else {
            totalRequestsLabel.setText(String.valueOf(metrics.getTotalRequests()));
            double successRate = 100.0 - metrics.getErrorRate();
            successRateLabel.setText(df.format(successRate) + "%");
            avgResponseLabel.setText(df.format(metrics.getAverageResponseTime()) + " ms");
            throughputLabel.setText(df.format(metrics.getThroughputPerSecond()) + " req/s");
        }
    }
    
    private void runManualTest(ActionEvent e) {
        TestConfiguration config = createConfig();
        if (config == null) return;
        
        tabbedPane.setSelectedIndex(1); // Switch to results tab
        statusLabel.setText("Starting manual test...");
        progressBar.setIndeterminate(true);
        progressBar.setString("Running...");
        
        runTest(config);
    }
    
    private void runAutomatedTest(ActionEvent e) {
        String endpoint = endpointField.getText().trim();
        if (endpoint.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an endpoint", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tabbedPane.setSelectedIndex(1); // Switch to results tab
        statusLabel.setText("Starting automated test sequence...");
        progressBar.setIndeterminate(true);
        progressBar.setString("Automated Testing...");
        
        SwingWorker<Void, Object[]> worker = new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() throws Exception {
                int[] userCounts = {10, 100, 1000, 5000, 10000, 20000, 50000, 100000};
                
                for (int i = 0; i < userCounts.length; i++) {
                    int users = userCounts[i];
                    publish(new Object[]{"text", "\n🚀 === AUTOMATED TEST: " + users + " Users ===\n"});
                    publish(new Object[]{"progress", (i * 100) / userCounts.length});
                    
                    TestConfiguration config = new TestConfiguration();
                    config.setApiEndpoint(endpoint);
                    config.setHttpMethod((String) methodCombo.getSelectedItem());
                    config.setRequestBody(bodyArea.getText().trim());
                    config.setConcurrentUsers(users);
                    config.setTestDurationSeconds(60);
                    config.setRampUpSeconds(Math.min(users / 10, 300));
                    
                    LoadTestExecutor executor = new LoadTestExecutor();
                    PerformanceMetrics results = executor.executeLoadTest(config);
                    
                    publish(new Object[]{"text", formatResults(results)});
                    publish(new Object[]{"metrics", results});
                    
                    // Stop conditions with better messaging
                    if (results.getErrorRate() > 10) {
                        publish(new Object[]{"text", "⚠️ Stopping: Error rate exceeded 10% (" + df.format(results.getErrorRate()) + "%)\n"});
                        break;
                    }
                    
                    if (results.getAverageResponseTime() > 5000) {
                        publish(new Object[]{"text", "⚠️ Stopping: Response time exceeded 5 seconds (" + df.format(results.getAverageResponseTime()) + " ms)\n"});
                        break;
                    }
                    
                    if (results.getThroughputPerSecond() < 1) {
                        publish(new Object[]{"text", "⚠️ Stopping: Throughput too low (" + df.format(results.getThroughputPerSecond()) + " req/s)\n"});
                        break;
                    }
                    
                    Thread.sleep(5000); // Wait between tests
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<Object[]> chunks) {
                for (Object[] chunk : chunks) {
                    String type = (String) chunk[0];
                    if ("text".equals(type)) {
                        String text = (String) chunk[1];
                        resultsArea.append(text);
                        resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
                    } else if ("metrics".equals(type)) {
                        PerformanceMetrics metrics = (PerformanceMetrics) chunk[1];
                        updateMetrics(metrics);
                        tabbedPane.setSelectedIndex(2); // Show metrics
                    } else if ("progress".equals(type)) {
                        int progress = (Integer) chunk[1];
                        progressBar.setValue(progress);
                    }
                }
            }
            
            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
                progressBar.setString("Completed");
                statusLabel.setText("Automated test sequence completed");
            }
        };
        
        worker.execute();
    }
    
    private TestConfiguration createConfig() {
        try {
            TestConfiguration config = new TestConfiguration();
            config.setApiEndpoint(endpointField.getText().trim());
            config.setHttpMethod((String) methodCombo.getSelectedItem());
            config.setRequestBody(bodyArea.getText().trim());
            config.setConcurrentUsers(Integer.parseInt(usersField.getText()));
            config.setTestDurationSeconds(Integer.parseInt(durationField.getText()));
            config.setRampUpSeconds(Integer.parseInt(usersField.getText()) / 10);
            
            if (!config.isValid()) {
                JOptionPane.showMessageDialog(this, "Invalid configuration");
                return null;
            }
            
            return config;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number format");
            return null;
        }
    }
    
    private void runTest(TestConfiguration config) {
        SwingWorker<PerformanceMetrics, Void> worker = new SwingWorker<PerformanceMetrics, Void>() {
            @Override
            protected PerformanceMetrics doInBackground() throws Exception {
                LoadTestExecutor executor = new LoadTestExecutor();
                return executor.executeLoadTest(config);
            }
            
            @Override
            protected void done() {
                try {
                    PerformanceMetrics results = get();
                    resultsArea.append(formatResults(results));
                    updateMetrics(results);
                    tabbedPane.setSelectedIndex(2); // Show metrics
                    statusLabel.setText("Manual test completed successfully");
                } catch (Exception e) {
                    resultsArea.append("❌ Error: " + e.getMessage() + "\n");
                    statusLabel.setText("Test failed: " + e.getMessage());
                } finally {
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    progressBar.setString("Completed");
                }
            }
        };
        
        worker.execute();
    }
    
    private String formatResults(PerformanceMetrics metrics) {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 Test Results:\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append(String.format("📈 Total Requests:     %,d\n", metrics.getTotalRequests()));
        sb.append(String.format("✅ Successful:         %,d\n", metrics.getSuccessfulRequests()));
        sb.append(String.format("❌ Failed:             %,d\n", metrics.getFailedRequests()));
        sb.append(String.format("📉 Error Rate:         %s%%\n", df.format(metrics.getErrorRate())));
        sb.append(String.format("⚡ Min Response:       %,d ms\n", metrics.getMinResponseTime()));
        sb.append(String.format("🔥 Max Response:       %,d ms\n", metrics.getMaxResponseTime()));
        sb.append(String.format("⏱️ Avg Response:       %s ms\n", df.format(metrics.getAverageResponseTime())));
        sb.append(String.format("🚀 Throughput:         %s req/s\n", df.format(metrics.getThroughputPerSecond())));
        sb.append("═══════════════════════════════════════\n\n");
        return sb.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoadTestGUI().setVisible(true);
        });
    }
}