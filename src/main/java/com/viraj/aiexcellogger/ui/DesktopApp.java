package com.viraj.aiexcellogger.ui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viraj.aiexcellogger.AiexcelloggerApplication;
import com.viraj.aiexcellogger.model.WorkLog;
import com.viraj.aiexcellogger.service.ExcelService;
import com.viraj.aiexcellogger.service.OpenAIService;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.BorderFactory;
import java.awt.Cursor;
import java.awt.Desktop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;

public class DesktopApp {

    private static boolean darkMode = true;
    private static ArrayList<String> history = new ArrayList<>();

    // ✅ Spring beans fetched from context
    private static OpenAIService aiService;
    private static ExcelService excelService;

    public static void main(String[] args) {

        // ✅ Hide Spring Boot banner in console
        System.setProperty("spring.main.banner-mode", "off");
        System.setProperty("java.awt.headless", "false"); // ✅ Force Swing to work

        System.out.println("Starting AI Work Logger...");

        // ✅ Start Spring context
        ConfigurableApplicationContext context =
                AiexcelloggerApplication.start(args);

        // ✅ Get beans
        aiService = context.getBean(OpenAIService.class);
        excelService = context.getBean(ExcelService.class);

        // ✅ Launch Swing UI
        SwingUtilities.invokeLater(() -> {
            startReminder();
            createUI();
        });
    }

    private static void createUI() {

        JFrame frame = new JFrame("AI Work Logger");
        frame.setSize(1000, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // ── Colors ──────────────────────────────────────────
        Color BG_DARK     = new Color(26, 26, 46);
        Color BG_DARKER   = new Color(18, 18, 31);
        Color BG_CARD     = new Color(30, 30, 53);
        Color ACCENT      = new Color(108, 99, 255);
        Color ACCENT_SOFT = new Color(167, 139, 250);
        Color TEXT_MAIN   = new Color(224, 224, 224);
        Color TEXT_MUTED  = new Color(102, 102, 136);
        Color BORDER      = new Color(42, 42, 74);

        if (!darkMode) {
            // light overrides
        }

        // ── Root ────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DARK);
        frame.setContentPane(root);

        // ── Title Bar ───────────────────────────────────────
        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        titleBar.setBackground(BG_DARKER);
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel appIcon = new JLabel("  AI Work Logger");
        appIcon.setForeground(TEXT_MUTED);
        appIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleBar.add(appIcon);

        JButton themeBtn = new JButton("☀ Light");
        themeBtn.setForeground(TEXT_MUTED);
        themeBtn.setBackground(BG_CARD);
        themeBtn.setBorder(BorderFactory.createLineBorder(BORDER));
        themeBtn.setFocusPainted(false);
        themeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        themeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel titleRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        titleRight.setBackground(BG_DARKER);
        titleRight.add(themeBtn);

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(BG_DARKER);
        titleWrapper.add(titleBar, BorderLayout.WEST);
        titleWrapper.add(titleRight, BorderLayout.EAST);
        root.add(titleWrapper, BorderLayout.NORTH);

        // ── Sidebar ─────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_DARKER);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        sidebar.setPreferredSize(new Dimension(260, 0));

        // Logo header
        JLabel logoLabel = new JLabel("🧠  Work Logger");
        logoLabel.setForeground(new Color(200, 200, 255));
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Powered by Llama3");
        subLabel.setForeground(TEXT_MUTED);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Date badge
        JLabel dateLabel = new JLabel("  " + java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEE, MMM d yyyy")));
        dateLabel.setForeground(TEXT_MUTED);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setOpaque(true);
        dateLabel.setBackground(BG_CARD);
        dateLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Section label helper
        JLabel inputLabel = makeSectionLabel("What did you do today?", TEXT_MUTED);

        // Text input
        JTextArea inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBackground(BG_CARD);
        inputArea.setForeground(TEXT_MAIN);
        inputArea.setCaretColor(TEXT_MAIN);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        inputArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        inputScroll.setBackground(BG_CARD);
        inputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        inputScroll.setPreferredSize(new Dimension(228, 180));

        // Buttons
        JButton generateBtn = makeButton("⚡  Generate & Save", ACCENT, Color.WHITE);
        JButton excelBtn    = makeButton("📂  Open Excel", BG_CARD, TEXT_MUTED);
        JButton pdfBtn      = makeButton("📄  Open PDF", BG_CARD, TEXT_MUTED);

        // Status
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(new Color(68, 170, 119));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logoLabel);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(subLabel);
        sidebar.add(Box.createVerticalStrut(16));
        sidebar.add(dateLabel);
        sidebar.add(Box.createVerticalStrut(14));
        sidebar.add(inputLabel);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(inputScroll);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(generateBtn);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(excelBtn);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(pdfBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(statusLabel);

        // ── Content Area ────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_DARK);

        // Tab bar
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(BG_DARKER);
        tabBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel previewTab = makeTab("Preview", true, ACCENT, TEXT_MUTED, BG_DARKER);
        JLabel historyTab = makeTab("History", false, ACCENT, TEXT_MUTED, BG_DARKER);
        tabBar.add(previewTab);
        tabBar.add(historyTab);
        content.add(tabBar, BorderLayout.NORTH);

        // Two panels side by side
        JPanel panels = new JPanel(new GridLayout(1, 2, 12, 0));
        panels.setBackground(BG_DARK);
        panels.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // Preview panel
        JTextArea previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setBackground(BG_DARKER);
        previewArea.setForeground(TEXT_MAIN);
        previewArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel previewPanel = makePanel("Task Summary", new Color(108, 99, 255),
                new JScrollPane(previewArea), BG_DARKER, BORDER, TEXT_MUTED);

        // History panel
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setBackground(BG_DARKER);
        historyArea.setForeground(TEXT_MAIN);
        historyArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JPanel historyPanel = makePanel("Log History", ACCENT_SOFT,
                new JScrollPane(historyArea), BG_DARKER, BORDER, TEXT_MUTED);

        panels.add(previewPanel);
        panels.add(historyPanel);
        content.add(panels, BorderLayout.CENTER);

        // ── Layout ──────────────────────────────────────────
        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);

        ObjectMapper mapper = new ObjectMapper();

        // ── Generate Button ─────────────────────────────────
        generateBtn.addActionListener(e -> {
            String userInput = inputArea.getText().trim();
            if (userInput.isEmpty()) {
                statusLabel.setText("Please enter your work summary.");
                return;
            }

            statusLabel.setForeground(new Color(250, 200, 50));
            statusLabel.setText("Processing...");
            generateBtn.setEnabled(false);

            new Thread(() -> {
                try {
                    String raw = aiService.getStructuredJson(userInput);
                    JsonNode root2 = mapper.readTree(raw);
                    // ✅ NVIDIA NIM format:
                    String content2 = root2.get("choices")
                            .get(0)
                            .get("message")
                            .get("content")
                            .asText();

                    int start = content2.indexOf("{");
                    int end = content2.lastIndexOf("}");
                    if (start == -1 || end == -1)
                        throw new RuntimeException("Invalid AI response");

                    String json = content2.substring(start, end + 1);
                    WorkLog log = mapper.readValue(json, WorkLog.class);

                    log.setDate(java.time.LocalDate.now().toString());
                    if (log.getHours() == 0) log.setHours(8);
                    if (log.getProjectName() == null || log.getProjectName().isEmpty())
                        log.setProjectName("General Work");
                    if (log.getNextAction() == null || log.getNextAction().isEmpty())
                        log.setNextAction("Continue work");

                    excelService.writeToExcel(log);
                    excelService.exportToPDF(log);

                    String preview =
                            "TASK SUMMARY\n" +
                                    "─────────────────────────\n" +
                                    log.getTaskSummary() + "\n\n" +
                                    "DESCRIPTION\n" +
                                    "─────────────────────────\n" +
                                    log.getDescription() + "\n\n" +
                                    "NEXT ACTION\n" +
                                    "─────────────────────────\n" +
                                    log.getNextAction();

                    SwingUtilities.invokeLater(() -> {
                        previewArea.setText(preview);
                        history.add(0, log.getDate() + " — " + log.getProjectName()
                                + "\n" + log.getTaskSummary());
                        historyArea.setText(String.join("\n\n─────────────────\n\n", history));
                        statusLabel.setForeground(new Color(68, 170, 119));
                        statusLabel.setText("Saved to Excel + PDF");
                        generateBtn.setEnabled(true);
                    });

                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setForeground(new Color(255, 80, 80));
                        statusLabel.setText("Error: " + ex.getMessage());
                        generateBtn.setEnabled(true);
                    });
                }
            }).start();
        });

        excelBtn.addActionListener(e -> {
            try { Desktop.getDesktop().open(new File("worklog.xlsx")); }
            catch (Exception ex) { statusLabel.setText("Excel not found!"); }
        });

        pdfBtn.addActionListener(e -> {
            try { Desktop.getDesktop().open(new File("worklog.pdf")); }
            catch (Exception ex) { statusLabel.setText("PDF not found!"); }
        });

        themeBtn.addActionListener(e -> {
            darkMode = !darkMode;
            frame.dispose();
            createUI();
        });

        frame.setVisible(true);
    }

    // ── Helper methods ───────────────────────────────────
    private static JLabel makeSectionLabel(String text, Color color) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 42, 74)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return btn;
    }

    private static JLabel makeTab(String text, boolean active,
                                  Color accent, Color muted, Color bg) {
        JLabel tab = new JLabel(text);
        tab.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tab.setForeground(active ? accent : muted);
        tab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, active ? 2 : 0, 0, accent),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        tab.setOpaque(true);
        tab.setBackground(bg);
        return tab;
    }

    private static JPanel makePanel(String title, Color dotColor,
                                    JScrollPane scroll, Color bg, Color border, Color textMuted) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createLineBorder(border));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        header.setBackground(bg);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, border));

        JLabel dot = new JLabel("●");
        dot.setForeground(dotColor);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 8));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setForeground(textMuted);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        header.add(dot);
        header.add(titleLabel);

        scroll.setBorder(null);
        scroll.setBackground(bg);
        scroll.getViewport().setBackground(bg);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private static void startReminder() {
        Timer timer = new Timer(60000, e -> {
            LocalTime now = LocalTime.now();
            if (now.getHour() == 19 && now.getMinute() == 0) {
                JOptionPane.showMessageDialog(null,
                        "⏰ Time to log your work!",
                        "Reminder",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        timer.start();
    }
}