package com.loadtesting.phase1.gui;

import com.loadtesting.phase1.model.PerformanceMetrics;
import com.loadtesting.phase1.model.TestConfiguration;
import com.loadtesting.phase1.service.LoadTestExecutor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoadTestGUI extends JFrame {
    private JTextField endpointField;
    private JComboBox<String> methodCombo;
    private JTextArea bodyArea;
    private JTextField usersField;
    private JTextField durationField;
    private JButton manualButton;
    private JButton autoButton;
    private JTextArea resultsArea;
    private JProgressBar progressBar;
    
    public LoadTestGUI() {
        setTitle("AutoLoadX - Load Testing Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        initComponents();
        layoutComponents();
    }
    
    private void initComponents() {
        endpointField = new JTextField("http://testphp.vulnweb.com/");
        methodCombo = new JComboBox<>(new String[]{"GET", "POST", "PUT", "DELETE"});
        bodyArea = new JTextArea(3, 30);
        usersField = new JTextField("10");
        durationField = new JTextField("60");
        manualButton = new JButton("Manual Test");
        autoButton = new JButton("Automated Test");
        resultsArea = new JTextArea(15, 50);
        progressBar = new JProgressBar();
        
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        manualButton.addActionListener(this::runManualTest);
        autoButton.addActionListener(this::runAutomatedTest);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Endpoint:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(endpointField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Method:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(methodCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Body:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(new JScrollPane(bodyArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(new JLabel("Users:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(usersField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Duration:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(durationField, gbc);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(manualButton);
        buttonPanel.add(autoButton);
        
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(new JScrollPane(resultsArea), BorderLayout.SOUTH);
    }
    
    private void runManualTest(ActionEvent e) {
        TestConfiguration config = createConfig();
        if (config == null) return;
        
        runTest(config);
    }
    
    private void runAutomatedTest(ActionEvent e) {
        String endpoint = endpointField.getText().trim();
        if (endpoint.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an endpoint");
            return;
        }
        
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                int[] userCounts = {10, 100, 1000, 5000, 10000, 20000, 50000, 100000};
                
                for (int users : userCounts) {
                    publish("\\n=== AUTOMATED TEST: " + users + " Users ===\\n");
                    
                    TestConfiguration config = new TestConfiguration();
                    config.setApiEndpoint(endpoint);
                    config.setHttpMethod((String) methodCombo.getSelectedItem());
                    config.setRequestBody(bodyArea.getText().trim());
                    config.setConcurrentUsers(users);
                    config.setTestDurationSeconds(60);
                    config.setRampUpSeconds(Math.min(users / 10, 300));
                    
                    LoadTestExecutor executor = new LoadTestExecutor();
                    PerformanceMetrics results = executor.executeLoadTest(config);
                    
                    publish(formatResults(results));
                    
                    if (results.getErrorRate() > 10) {
                        publish("\\nStopping automated test due to high error rate\\n");
                        break;
                    }
                    
                    Thread.sleep(5000); // Wait between tests
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String chunk : chunks) {
                    resultsArea.append(chunk);
                    resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
                }
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
                } catch (Exception e) {
                    resultsArea.append("Error: " + e.getMessage() + "\\n");
                }
            }
        };
        
        worker.execute();
    }
    
    private String formatResults(PerformanceMetrics metrics) {
        return String.format(
            "Total Requests: %d\\n" +
            "Successful: %d\\n" +
            "Failed: %d\\n" +
            "Error Rate: %.4f%%\\n" +
            "Min Response: %d ms\\n" +
            "Max Response: %d ms\\n" +
            "Avg Response: %.2f ms\\n" +
            "Throughput: %.2f req/s\\n\\n",
            metrics.getTotalRequests(),
            metrics.getSuccessfulRequests(),
            metrics.getFailedRequests(),
            metrics.getErrorRate(),
            metrics.getMinResponseTime(),
            metrics.getMaxResponseTime(),
            metrics.getAverageResponseTime(),
            metrics.getThroughputPerSecond()
        );
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoadTestGUI().setVisible(true);
        });
    }
}