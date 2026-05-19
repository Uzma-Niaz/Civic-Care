package blood;

import utils.FileUtil;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BloodService extends JPanel {
    private HashMap<String, List<Donor>> donorMap;
    private JTable donorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    // Labels for dynamic metrics updating
    private JLabel totalDonorsLabel;
    private JLabel groupsCountLabel;
    private JLabel locationsCountLabel;

    // --- Premium Refined CivicCare RED Theme Palette (Strict Identity Sync) ---
    private final Color ACCENT_RED_SOLID = new Color(215, 30, 30);    
    private final Color ACCENT_RED_TINT = new Color(254, 235, 235);   
    private final Color ACCENT_RED_HOVER = new Color(175, 25, 25);   
    private final Color NAVY_DARK = new Color(33, 43, 54);           
    private final Color TEXT_MUTED = new Color(130, 145, 160);        
    private final Color BACKGROUND_LIGHT = new Color(250, 250, 253);  
    private final Color BORDER_COLOR = new Color(235, 240, 248);     
    private final Color CARD_BG = Color.WHITE;
    
    private final Color STATUS_GREEN_BG = new Color(230, 248, 235);
    private final Color STATUS_GREEN_TXT = new Color(35, 160, 80);

    public BloodService() {
        donorMap = new HashMap<>();
        
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_LIGHT);
        initializeUI();
        
        // Load data first to establish sizes
        loadDonors();
        
        // Initial draw of all loaded active donors
        refreshTableWithMatches("");
    }

    private void initializeUI() {
        // ==========================================
        // 1. TOP HEADER BANNER (Exact UI Synchronized)
        // ==========================================
        JPanel headerBanner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_RED_SOLID, getWidth(), 0, new Color(165, 25, 25));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.setStroke(new BasicStroke(1.5f));
                for (int i = 0; i < getWidth(); i += 40) {
                    g2d.drawLine(i, 0, i + 20, getHeight());
                }
                g2d.dispose();
            }
        };
        headerBanner.setPreferredSize(new Dimension(0, 85));
        headerBanner.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 2));
        titlePanel.setOpaque(false);
        
        JLabel mainTitle = new JLabel("Blood Donor Matching");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(Color.WHITE);
        
        JLabel subTitle = new JLabel("Real-Time Compatibility, Life Saving Operations Unit");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTitle.setForeground(new Color(255, 220, 220));
        
        titlePanel.add(mainTitle);
        titlePanel.add(subTitle);
        headerBanner.add(titlePanel, BorderLayout.WEST);

        JButton backBtn = new JButton("<  Back") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(ACCENT_RED_SOLID);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        backBtn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        backBtn.setContentAreaFilled(false);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(85, 34));
        
        backBtn.addActionListener(e -> {
            this.setVisible(false);
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win != null) {
                win.dispose();
            }
            new triage.ModuleMenu().setVisible(true);
        });
        
        JPanel backContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        backContainer.setOpaque(false);
        backContainer.add(backBtn);
        headerBanner.add(backContainer, BorderLayout.EAST);
        
        add(headerBanner, BorderLayout.NORTH);

        // Core Layout Wrapper Container
        JPanel mainBodyPanel = new JPanel();
        mainBodyPanel.setLayout(new BoxLayout(mainBodyPanel, BoxLayout.Y_AXIS));
        mainBodyPanel.setOpaque(false);
        mainBodyPanel.setBorder(new EmptyBorder(18, 25, 15, 25));

        // ==========================================
        // 2. TOP METRICS ROW PANELS
        // ==========================================
        JPanel metricsGridRow = new JPanel(new GridLayout(1, 4, 15, 0));
        metricsGridRow.setOpaque(false);
        metricsGridRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        metricsGridRow.setPreferredSize(new Dimension(0, 80));

        totalDonorsLabel = new JLabel("0", JLabel.LEFT);
        groupsCountLabel = new JLabel("0", JLabel.LEFT);
        locationsCountLabel = new JLabel("0", JLabel.LEFT);

        metricsGridRow.add(createFancyMetricCard("Total Donors", totalDonorsLabel, "Registered Volunteers", new Color(45, 125, 235), new Color(235, 243, 255)));
        metricsGridRow.add(createFancyMetricCard("Groups Available", groupsCountLabel, "Distinct Compatibility Tiers", ACCENT_RED_SOLID, ACCENT_RED_TINT));
        metricsGridRow.add(createFancyMetricCard("Lives Impacted", new JLabel("320+"), "Successful Matched Requests", new Color(35, 175, 90), new Color(232, 250, 238)));
        metricsGridRow.add(createFancyMetricCard("Active Locations", locationsCountLabel, "Operational Civic Nodes", new Color(245, 135, 15), new Color(255, 244, 230)));
        
        mainBodyPanel.add(metricsGridRow);
        mainBodyPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        // ==========================================
        // 3. ADVANCED SELECTION FILTER & ACTION BAR
        // ==========================================
        JPanel searchFilterBarCard = new JPanel(new GridBagLayout());
        searchFilterBarCard.setBackground(CARD_BG);
        searchFilterBarCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(12, 18, 12, 18)
        ));
        searchFilterBarCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        searchFilterBarCard.setPreferredSize(new Dimension(0, 68));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        JLabel inputPromptLbl = new JLabel("Enter Blood Group Cluster:");
        inputPromptLbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        inputPromptLbl.setForeground(NAVY_DARK);
        
        gbc.weightx = 0.20; gbc.gridx = 0;
        searchFilterBarCard.add(inputPromptLbl, gbc);

        searchField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString("e.g. A+, B-, O+ ...", 12, (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2);
                    g2.dispose();
                }
            }
        };
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(210, 220, 235), 1, true),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        
        gbc.weightx = 0.45; gbc.gridx = 1;
        searchFilterBarCard.add(searchField, gbc);
        
        // Spacer block
        gbc.weightx = 0.05; gbc.gridx = 2;
        searchFilterBarCard.add(Box.createHorizontalStrut(1), gbc);

        JButton searchBtn = new JButton("Search Group") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed() || getModel().isRollover()) {
                    g2.setColor(ACCENT_RED_HOVER);
                } else {
                    g2.setColor(ACCENT_RED_SOLID);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchBtn.setContentAreaFilled(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.weightx = 0.15; gbc.gridx = 3;
        searchFilterBarCard.add(searchBtn, gbc);
        
        gbc.weightx = 0.02; gbc.gridx = 4;
        searchFilterBarCard.add(Box.createHorizontalStrut(1), gbc);

        JButton resetBtn = new JButton("Clear / View All") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 243, 248));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(NAVY_DARK);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        resetBtn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        resetBtn.setContentAreaFilled(false);
        resetBtn.setBorderPainted(false);
        resetBtn.setFocusPainted(false);
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.weightx = 0.13; gbc.gridx = 5;
        searchFilterBarCard.add(resetBtn, gbc);

        mainBodyPanel.add(searchFilterBarCard);
        mainBodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ==========================================
        // 4. CENTRAL REFINED DONORS GRID LIST LOG 
        // ==========================================
        String[] columns = {"Volunteer Name", "Blood Cluster", "Contact Info/Registry ID", "Geographic Node Location", "Availability Badge"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        donorTable = new JTable(tableModel);
        styleTableArchitecture(donorTable);
        
        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        
        mainBodyPanel.add(scrollPane);
        add(mainBodyPanel, BorderLayout.CENTER);

        // ==========================================
        // 5. APPLICATION COMPLIANCE FOOTER
        // ==========================================
        JPanel appFooterLine = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(230, 235, 242));
                g.fillRect(0, 0, getWidth(), 1); 
            }
        };
        appFooterLine.setOpaque(false);
        appFooterLine.setPreferredSize(new Dimension(0, 35));
        appFooterLine.setBorder(new EmptyBorder(10, 25, 5, 25));
        
        JLabel footerLeftBranding = new JLabel("CivicCare - Serving Humanity, Saving Lives");
        footerLeftBranding.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLeftBranding.setForeground(TEXT_MUTED);
        
        JLabel footerRightClockStr = new JLabel("Live Systems Operational Status Connected");
        footerRightClockStr.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerRightClockStr.setForeground(TEXT_MUTED);
        
        appFooterLine.add(footerLeftBranding, BorderLayout.WEST);
        appFooterLine.add(footerRightClockStr, BorderLayout.EAST);
        add(appFooterLine, BorderLayout.SOUTH);

        // --- Active Routing Listeners ---
        searchBtn.addActionListener(e -> performFilteredSearch());
        searchField.addActionListener(e -> performFilteredSearch());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            refreshTableWithMatches("");
        });
    }

    private JPanel createFancyMetricCard(String heading, JLabel countComponent, String subDesc, Color highlightColor, Color tintBg) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(highlightColor);
                g2.fillRect(0, 0, 5, getHeight()); // Premium left marker strip
                g2.dispose();
            }
        };
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(10, 16, 10, 14)
        ));
        card.setLayout(new BorderLayout(0, 0));

        JLabel titleLbl = new JLabel(heading.toUpperCase());
        titleLbl.setFont(new Font("Segoe UI Black", Font.BOLD, 10));
        titleLbl.setForeground(TEXT_MUTED);

        countComponent.setFont(new Font("Segoe UI", Font.BOLD, 22));
        countComponent.setForeground(NAVY_DARK);

        JLabel descLbl = new JLabel(subDesc);
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_MUTED);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(countComponent, BorderLayout.CENTER);
        card.add(descLbl, BorderLayout.SOUTH);
        return card;
    }

    private void styleTableArchitecture(JTable table) {
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(NAVY_DARK);
        table.setSelectionBackground(ACCENT_RED_TINT);
        table.setSelectionForeground(NAVY_DARK);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(NAVY_DARK);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        // Column Alignment Renderers
        DefaultTableCellRenderer standardLeftRenderer = new DefaultTableCellRenderer();
        standardLeftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer centerGroupRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isSel, hasF, r, c);
                l.setFont(new Font("Segoe UI Bold", Font.BOLD, 13));
                l.setForeground(ACCENT_RED_SOLID);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        };

        // Custom Beautiful Pill Badge Layout Renderer for Status Column
        DefaultTableCellRenderer statusBadgePillRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasF, int r, int c) {
                JPanel pillWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 7)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(STATUS_GREEN_BG);
                        g2.fillRoundRect(getWidth()/2 - 45, 4, 90, 24, 12, 12);
                        g2.dispose();
                    }
                };
                pillWrapper.setOpaque(true);
                pillWrapper.setBackground(isSel ? t.getSelectionBackground() : Color.WHITE);
                
                JLabel textLabel = new JLabel("Available");
                textLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
                textLabel.setForeground(STATUS_GREEN_TXT);
                pillWrapper.add(textLabel);
                return pillWrapper;
            }
        };

        table.add(Box.createRigidArea(new Dimension(0, 0))); // Structural Anchor
        table.getColumnModel().getColumn(0).setCellRenderer(standardLeftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerGroupRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(standardLeftRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(standardLeftRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(statusBadgePillRenderer);
        
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
    }

    private void performFilteredSearch() {
        String inputGroup = searchField.getText().trim().toUpperCase();

        // Exception & Custom Validation Handling
        if (inputGroup.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Search field cannot be empty! Please type a blood group cluster.", 
                "Notice", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Strict Blood Group Cluster Checking
        String[] validClusters = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        boolean isMatchFound = false;
        for (String validG : validClusters) {
            if (validG.equals(inputGroup)) {
                isMatchFound = true;
                break;
            }
        }

        if (!isMatchFound) {
            // Throw custom validation UI interceptor
            JOptionPane.showMessageDialog(this, 
                "INVALID CLUSTER DETECTED: '" + inputGroup + "' is not a recognized blood group!\n\n" +
                "Please enter a medically valid format:\n" +
                "👉 A+, A-, B+, B-, AB+, AB-, O+, O-", 
                "Medical Database Error", 
                JOptionPane.ERROR_MESSAGE);
            return; // Pure protection bypass: execution stops here cleanly.
        }

        refreshTableWithMatches(inputGroup);
    }

    private void refreshTableWithMatches(String criteria) {
        if (tableModel == null) return;
        tableModel.setRowCount(0);

        if (criteria.isEmpty()) {
            // View All configuration option loops through entire dataset
            for (String clusterKey : donorMap.keySet()) {
                List<Donor> clusterList = donorMap.get(clusterKey);
                if (clusterList != null) {
                    for (Donor d : clusterList) {
                        tableModel.addRow(new Object[]{d.name, d.bloodGroup, d.contact, d.city, "Available"});
                    }
                }
            }
        } else {
            // Focused map look up optimization loop
            String sanitizedKey = criteria.toUpperCase();
            List<Donor> targetedMatches = donorMap.getOrDefault(sanitizedKey, new ArrayList<>());
            for (Donor d : targetedMatches) {
                tableModel.addRow(new Object[]{d.name, d.bloodGroup, d.contact, d.city, "Available"});
            }
        }
    }

    private void loadDonors() {
        String[] lines = FileUtil.readLines("Data/donors.txt");
        int countLoaded = 0;
        HashSet<String> distinctCities = new HashSet<>();

        if (lines != null) {
            for (String line : lines) {
                String[] p = line.split(",");
                if (p.length == 4) {
                    Donor d = new Donor(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim());
                    
                    donorMap.putIfAbsent(d.bloodGroup.toUpperCase(), new ArrayList<>());
                    donorMap.get(d.bloodGroup.toUpperCase()).add(d);
                    
                    distinctCities.add(d.city.toLowerCase());
                    countLoaded++;
                }
            }
        }
        
        // Populate analytical summaries onto UI labels dynamically
        if (totalDonorsLabel != null) totalDonorsLabel.setText(String.valueOf(countLoaded));
        if (groupsCountLabel != null) groupsCountLabel.setText(String.valueOf(donorMap.size()));
        if (locationsCountLabel != null) locationsCountLabel.setText(String.valueOf(distinctCities.size()));
    }
}