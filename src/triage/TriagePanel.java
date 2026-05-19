package triage;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import ambulance.Graph;

public class TriagePanel extends JPanel {
    private HospitalRegistry registry;
    private TriageQueue queue;
    private Graph graph;
    
    private JTextField nameField, locField;
    private JComboBox<String> severityCombo;
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JPanel dashboardPanel;
    
    private Runnable onBackAction;

    // --- Premium Refined CivicCare RED Theme Palette ---
    private final Color ACCENT_RED_SOLID = new Color(215, 30, 30);    
    private final Color ACCENT_RED_TINT = new Color(254, 235, 235);   
    private final Color ACCENT_RED_HOVER = new Color(175, 25, 25);   
    private final Color NAVY_DARK = new Color(33, 43, 54);           
    private final Color TEXT_MUTED = new Color(130, 145, 160);        
    private final Color BACKGROUND_LIGHT = new Color(250, 250, 253);  
    private final Color BORDER_COLOR = new Color(235, 240, 248);     
    
    private final Color STATUS_RED = new Color(215, 40, 40);       
    private final Color STATUS_ORANGE = new Color(245, 140, 10);    
    private final Color STATUS_GREEN = new Color(35, 175, 90);      

    public TriagePanel(Runnable onBackAction) {
        this.onBackAction = onBackAction;

        try {
            this.graph = new Graph();
            this.graph.loadFromFiles("Data/locations.txt", "Data/disasters.txt"); 
        } catch (Exception e) {
            System.out.println("Graph dynamic initialization failed. Using fallback system.");
            e.printStackTrace();
        }
        
        this.registry = new HospitalRegistry(graph);
        this.queue = new TriageQueue();
        
        setLayout(new BorderLayout(25, 0));
        setBackground(BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(0, 0, 0, 0)); 
        initializeUI();
    }

    private void initializeUI() {
        Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
        Font labelFont = new Font("Segoe UI Semibold", Font.PLAIN, 13);

        // ==========================================
        // 1. TOP HEADER BANNER
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
        JLabel mainTitle = new JLabel("Emergency Triage Dashboard");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(Color.WHITE);
        JLabel subTitle = new JLabel("Smart Logic, Real-Time Prioritization Care Engine");
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
        backBtn.setPreferredSize(new Dimension(85, 34));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        backBtn.addActionListener(e -> {
            this.setVisible(false);
            if (onBackAction != null) {
                onBackAction.run(); 
            } else {
                Window win = SwingUtilities.getWindowAncestor(this);
                if (win != null) win.dispose();
            }
        });
        
        JPanel backContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        backContainer.setOpaque(false);
        backContainer.add(backBtn);
        headerBanner.add(backContainer, BorderLayout.EAST);
        
        add(headerBanner, BorderLayout.NORTH);

        JPanel mainBodyWrapper = new JPanel(new BorderLayout(25, 0));
        mainBodyWrapper.setOpaque(false);
        mainBodyWrapper.setBorder(new EmptyBorder(15, 25, 10, 25));

        // ==========================================
        // 2. LEFT CONTAINER (Form & Guidelines)
        // ==========================================
        JPanel leftContainer = new JPanel();
        leftContainer.setLayout(new BoxLayout(leftContainer, BoxLayout.Y_AXIS));
        leftContainer.setOpaque(false);
        leftContainer.setPreferredSize(new Dimension(310, 0));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(22, 20, 22, 20)
        ));
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.setMaximumSize(new Dimension(310, 340));

        JLabel formTitle = new JLabel("Patient Admission Form");
        formTitle.setFont(titleFont);
        formTitle.setForeground(NAVY_DARK);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(formTitle);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        nameField = createStyledTextField();
        locField = createStyledTextField();
        
        severityCombo = new JComboBox<>(new String[]{"1 - Critical Severity", "2 - Moderate Urgency", "3 - Stable Condition"});
        severityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        severityCombo.setMaximumSize(new Dimension(270, 38));
        severityCombo.setPreferredSize(new Dimension(270, 38));
        severityCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        severityCombo.setBackground(Color.WHITE);

        inputPanel.add(createLeftLabel("Patient Name", labelFont, ACCENT_RED_SOLID)); 
        inputPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        inputPanel.add(nameField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        inputPanel.add(createLeftLabel("Location (Node ID)", labelFont, ACCENT_RED_SOLID)); 
        inputPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        inputPanel.add(locField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        inputPanel.add(createLeftLabel("Severity Level", labelFont, ACCENT_RED_SOLID)); 
        inputPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        inputPanel.add(severityCombo);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 22)));
        
        JButton admitBtn = new JButton("+ Find Hospital & Admit") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_RED_HOVER);
                } else if (getModel().isRollover()) {
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
        admitBtn.setMaximumSize(new Dimension(270, 44));
        admitBtn.setPreferredSize(new Dimension(270, 44));
        admitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        admitBtn.setContentAreaFilled(false);
        admitBtn.setBorderPainted(false);
        admitBtn.setFocusPainted(false);
        admitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        admitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        admitBtn.addActionListener(e -> processTriage());
        
        inputPanel.add(admitBtn);
        leftContainer.add(inputPanel);
        leftContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel guidePanel = new JPanel();
        guidePanel.setLayout(new BoxLayout(guidePanel, BoxLayout.Y_AXIS));
        guidePanel.setBackground(Color.WHITE);
        guidePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(18, 20, 18, 20)
        ));
        guidePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        guidePanel.setMaximumSize(new Dimension(310, 185));

        JLabel guideTitle = new JLabel("Information Guide");
        guideTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        guideTitle.setForeground(NAVY_DARK);
        guidePanel.add(guideTitle);
        guidePanel.add(Box.createRigidArea(new Dimension(0, 12)));

        guidePanel.add(createGuideRow("1 - Critical", "Immediate emergency action.", STATUS_RED));
        guidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        guidePanel.add(createGuideRow("2 - Moderate", "Urgent clinical attention.", STATUS_ORANGE));
        guidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        guidePanel.add(createGuideRow("3 - Stable", "Standard clinical recovery.", STATUS_GREEN));

        leftContainer.add(guidePanel);
        mainBodyWrapper.add(leftContainer, BorderLayout.WEST);

        // ==========================================
        // 3. CENTER CONTAINER (Active Admissions Log Only)
        // ==========================================
        JPanel centerContainer = new JPanel(new BorderLayout(0, 15));
        centerContainer.setOpaque(false);

        String[] cols = {"Patient Name", "Urgency Tier", "Assigned Facility"};
        tableModel = new DefaultTableModel(cols, 0);
        
        patientTable = new JTable(tableModel) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2 - 45;
                    
                    g2d.setColor(ACCENT_RED_TINT);
                    g2d.fillRoundRect(cx - 25, cy - 25, 50, 50, 14, 14);
                    g2d.setColor(TEXT_MUTED);
                    g2d.setStroke(new BasicStroke(2.5f));
                    g2d.drawRoundRect(cx - 12, cy - 12, 24, 24, 6, 6);
                    g2d.drawLine(cx - 6, cy, cx - 2, cy + 4);
                    g2d.drawLine(cx - 2, cy + 4, cx + 6, cy - 4);

                    g2d.setColor(NAVY_DARK);
                    g2d.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
                    String text = "No active admissions";
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, cy + 50);

                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2d.setColor(TEXT_MUTED);
                    String subText = "Processed logs will automatically display here.";
                    FontMetrics fmSub = g2d.getFontMetrics();
                    g2d.drawString(subText, (getWidth() - fmSub.stringWidth(subText)) / 2, cy + 70);
                }
            }
        };
        
        styleTable(patientTable);
        JScrollPane tableScroll = new JScrollPane(patientTable);
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        // Table ab center container ki poori space le legi bina bottom boxes ke
        centerContainer.add(tableScroll, BorderLayout.CENTER);
        mainBodyWrapper.add(centerContainer, BorderLayout.CENTER);

        // ==========================================
        // 4. RIGHT CONTAINER (Real-Time Bed Availability)
        // ==========================================
        JPanel rightContainer = new JPanel(new BorderLayout(0, 15));
        rightContainer.setOpaque(false);
        rightContainer.setPreferredSize(new Dimension(320, 0));

        JLabel rightTitle = new JLabel("Real-Time Bed Availability"); 
        rightTitle.setFont(titleFont);
        rightTitle.setForeground(NAVY_DARK);
        rightContainer.add(rightTitle, BorderLayout.NORTH);

        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
        dashboardPanel.setOpaque(false);
        
        JScrollPane dashScroll = new JScrollPane(dashboardPanel);
        dashScroll.setOpaque(false); dashScroll.getViewport().setOpaque(false);
        dashScroll.setBorder(null);
        dashScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        rightContainer.add(dashScroll, BorderLayout.CENTER);
        mainBodyWrapper.add(rightContainer, BorderLayout.EAST);

        add(mainBodyWrapper, BorderLayout.CENTER);

        // ==========================================
        // 5. BOTTOM APPLICATION FOOTER
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

        refreshDashboard();
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(270, 38));
        tf.setPreferredSize(new Dimension(270, 38));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(215, 220, 230), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return tf;
    }

    private JLabel createLeftLabel(String text, Font font, Color redColor) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(redColor); 
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createGuideRow(String title, String desc, Color color) {
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        r.setOpaque(false);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bulletDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 4, 8, 8); 
                g2.dispose();
            }
        };
        bulletDot.setPreferredSize(new Dimension(12, 14));
        bulletDot.setOpaque(false);

        JLabel txt = new JLabel("<html><b>" + title + "</b><br><font color='#73808E' size='3'>" + desc + "</font></html>");
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txt.setForeground(NAVY_DARK);

        r.add(bulletDot);
        r.add(txt);
        return r;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(NAVY_DARK);
        
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(210, 220, 235));
        table.setIntercellSpacing(new Dimension(12, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        header.setBackground(Color.WHITE);
        header.setForeground(NAVY_DARK);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);   
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);   
    }

    public void refreshDashboard() {
        if (dashboardPanel == null || registry == null || registry.getAllHospitals() == null) return;
        
        dashboardPanel.removeAll();
        for (Hospital h : registry.getAllHospitals()) {
            if (h != null) {
                dashboardPanel.add(createHospitalCard(h));
                dashboardPanel.add(Box.createRigidArea(new Dimension(0, 15))); 
            }
        }
        dashboardPanel.revalidate();
        dashboardPanel.repaint();
    }

    private JPanel createHospitalCard(Hospital h) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(16, 18, 16, 18)
        ));
        card.setMaximumSize(new Dimension(320, 165));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel("Hospital: " + h.getName()); 
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(NAVY_DARK);
        card.add(nameLabel, BorderLayout.NORTH);

        JPanel barsPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        barsPanel.setOpaque(false);
        
        barsPanel.add(createStatBar("Critical Beds", h.getAvailableBeds(1), STATUS_RED));
        barsPanel.add(createStatBar("Moderate Beds", h.getAvailableBeds(2), STATUS_ORANGE));
        barsPanel.add(createStatBar("Stable Beds", h.getAvailableBeds(3), STATUS_GREEN));
        
        card.add(barsPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatBar(String label, int available, Color color) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        lbl.setForeground(new Color(90, 105, 125));
        lbl.setPreferredSize(new Dimension(85, 18));
        
        JProgressBar bar = new JProgressBar(0, 50) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(240, 243, 246));
                g2d.fillRoundRect(0, 4, getWidth(), 10, 8, 8);
                
                double percent = (double) getValue() / getMaximum();
                int fillWidth = (int) (getWidth() * percent);
                if (fillWidth > 0) {
                    g2d.setColor(color); 
                    g2d.fillRoundRect(0, 4, fillWidth, 10, 8, 8);
                }
                g2d.dispose();
            }
        };
        bar.setValue(available);
        bar.setOpaque(false);
        bar.setBorder(null);

        JLabel numLbl = new JLabel(available + " / 50", JLabel.RIGHT);
        numLbl.setFont(new Font("Segoe UI Bold", Font.BOLD, 12));
        numLbl.setForeground(NAVY_DARK);
        numLbl.setPreferredSize(new Dimension(45, 18));

        p.add(lbl, BorderLayout.WEST);
        p.add(bar, BorderLayout.CENTER);
        p.add(numLbl, BorderLayout.EAST);
        return p;
    }

    private void processTriage() {
        String name = nameField.getText().trim();
        String loc = locField.getText().trim();
        int sev = severityCombo.getSelectedIndex() + 1;

        if(name.isEmpty() || loc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields first!", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (registry == null) {
            System.out.println("ERROR: HospitalRegistry Object is null!");
            performDirectSafeAdmission(name, loc, sev);
            return;
        }

        try {
            RoutingResult result = registry.findBestHospital(loc, sev);
            
            if (result != null && result.hospital != null) {
                Patient p = new Patient(name, sev, loc);
                registry.admitPatient(result.hospital, p);
                p.setAssignedHospital(result.hospital.getName()); 
                if (queue != null) queue.addPatient(p);
                
                updateTable();
                refreshDashboard(); 
                clearFields();
            } else {
                performDirectSafeAdmission(name, loc, sev);
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in Dijkstra routing. Fallback triggered.");
            ex.printStackTrace(); 
            performDirectSafeAdmission(name, loc, sev);
        }
    }

    private void performDirectSafeAdmission(String name, String loc, int sev) {
        if (registry == null || registry.getAllHospitals() == null) {
            JOptionPane.showMessageDialog(this, "Critical Error: Core Registry Data Structure is unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Hospital fallbackHospital = null;
        for (Hospital h : registry.getAllHospitals()) {
            if (h != null && h.canAdmit(sev)) {
                fallbackHospital = h;
                break;
            }
        }

        if (fallbackHospital != null) {
            Patient p = new Patient(name, sev, loc);
            fallbackHospital.admitPatient(sev);
            p.setAssignedHospital(fallbackHospital.getName());
            if (queue != null) queue.addPatient(p);
            
            updateTable();
            refreshDashboard();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "No hospital beds available for this configuration matrix!", "Refused Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        if (tableModel == null || queue == null) return;
        tableModel.setRowCount(0);
        
        Patient[] activePatients = queue.getAll(); 
        if (activePatients == null) return;
        
        for (int i = 0; i < activePatients.length; i++) {
            Patient p = activePatients[i];
            if (p == null) continue;

            String statusWord = switch (p.getSeverityLevel()) {
                case 1 -> "1-Emergency Critical";
                case 2 -> "2-Moderate Urgency";
                case 3 -> "3-Stable Recovery";
                default -> "Active Triage Case";
            };

            tableModel.addRow(new Object[]{
                p.getName(), 
                statusWord, 
                p.getAssignedHospital()
            });
        }
    }

    private void clearFields() {
        nameField.setText("");
        locField.setText("");
        severityCombo.setSelectedIndex(0);
    }
}