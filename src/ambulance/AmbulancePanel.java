package ambulance;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class AmbulancePanel extends JPanel {
    private Graph graph;
    private JComboBox<String> startLocationCombo;
    private JComboBox<String> endLocationCombo;
    
    private JLabel timeValueLabel, distValueLabel, hopsValueLabel;
    private JLabel metricTimeLabel, metricDistLabel;
    private JTextArea routeDetailsArea;
    private MapCanvas routeMapCanvas;
    
    // Callback action to execute when back button is pressed
    private Runnable onBackAction;

    // --- Premium Refined CivicCare RED Theme Palette (Exactly Matched with Triage) ---
    private final Color ACCENT_RED_SOLID = new Color(215, 30, 30);    
    private final Color ACCENT_RED_TINT = new Color(254, 235, 235);   
    private final Color ACCENT_RED_HOVER = new Color(175, 25, 25);   
    private final Color NAVY_DARK = new Color(33, 43, 54);           
    private final Color TEXT_MUTED = new Color(130, 145, 160);        
    private final Color BACKGROUND_LIGHT = new Color(250, 250, 253);  
    private final Color BORDER_COLOR = new Color(235, 240, 248);     
    private final Color CARD_BG = Color.WHITE;
    
    private final Color ICON_PINK_BG = new Color(254, 235, 235);
    private final Color ICON_BLUE_BG = new Color(235, 243, 255);
    private final Color ICON_GREEN_BG = new Color(235, 250, 240);
    private final Color ICON_ORANGE_BG = new Color(255, 243, 230);

    // Default constructor
    public AmbulancePanel() {
        this(null);
    }

    // Constructor with Back Action Callback
    public AmbulancePanel(Runnable onBackAction) {
        this.onBackAction = onBackAction;
        
        try {
            this.graph = new Graph();
            this.graph.loadFromFiles("Data/locations.txt", "Data/disasters.txt");
        } catch (Exception e) {
            System.out.println("Graph auto-load safe fallback initialized.");
        }

        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_LIGHT);
        initializeUI();
    }

    private void initializeUI() {
        // ==========================================
        // 1. TOP HEADER BANNER (Exact Triage UI Matching)
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
        
        JLabel mainTitle = new JLabel("Ambulance Routing System");
        mainTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainTitle.setForeground(Color.WHITE);
        
        JLabel subTitle = new JLabel("Smart Navigation & Shortest Path Dispatched Care Engine");
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
            if (onBackAction != null) {
                onBackAction.run();
            } else {
                Container parentContainer = this.getParent();
                if (parentContainer != null) {
                    for (Component component : parentContainer.getComponents()) {
                        String className = component.getClass().getSimpleName();
                        if (className.contains("Triage") || className.contains("Menu") || className.contains("Dashboard") || className.contains("Main")) {
                            component.setVisible(true);
                            break;
                        }
                    }
                    parentContainer.revalidate();
                    parentContainer.repaint();
                }
            }
        });
        
        JPanel backContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        backContainer.setOpaque(false);
        backContainer.add(backBtn);
        headerBanner.add(backContainer, BorderLayout.EAST);
        
        add(headerBanner, BorderLayout.NORTH);

        // Main Wrapper to add margins cleanly
        JPanel mainBodyWrapper = new JPanel(new BorderLayout(0, 0));
        mainBodyWrapper.setOpaque(false);
        mainBodyWrapper.setBorder(new EmptyBorder(15, 25, 10, 25));

        // Body Elements Assembly Layout
        JPanel mainBodyPanel = new JPanel();
        mainBodyPanel.setLayout(new BoxLayout(mainBodyPanel, BoxLayout.Y_AXIS));
        mainBodyPanel.setOpaque(false);

        // ==========================================
        // 2. INPUT SELECTION ROW PANELS
        // ==========================================
        JPanel doubleInputRow = new JPanel(new GridLayout(1, 2, 20, 0));
        doubleInputRow.setOpaque(false);
        doubleInputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        doubleInputRow.setPreferredSize(new Dimension(0, 90));

        String[] mockNodes = {"Saddar", "Gulshan", "Johar", "Clifton", "DHA", "Malir", "Nazimabad"};
        
        startLocationCombo = new JComboBox<>(mockNodes);
        customizeCombo(startLocationCombo);
        endLocationCombo = new JComboBox<>(mockNodes);
        if (mockNodes.length > 1) endLocationCombo.setSelectedIndex(1);
        customizeCombo(endLocationCombo);

        doubleInputRow.add(createCardInputComponent("Patient Pickup Location", startLocationCombo, 1));
        doubleInputRow.add(createCardInputComponent("Hospital Destination Node", endLocationCombo, 2));
        mainBodyPanel.add(doubleInputRow);
        mainBodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ==========================================
        // 3. MID METRICS CONFIGURATION & TRIGGER ROW
        // ==========================================
        JPanel middleActionBar = new JPanel(new GridBagLayout());
        middleActionBar.setBackground(CARD_BG);
        middleActionBar.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(10, 15, 10, 15)
        ));
        middleActionBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        middleActionBar.setPreferredSize(new Dimension(0, 65));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;

        constraints.weightx = 0.22; constraints.gridx = 0;
        middleActionBar.add(createSleekMetricCell("ALGORITHM", "Dijkstra's Algorithm", 3), constraints);
        
        constraints.weightx = 0.22; constraints.gridx = 1;
        middleActionBar.add(createSleekMetricCell("OPTIMIZE FOR", "Shortest Distance", 4), constraints);

        metricTimeLabel = new JLabel("--", JLabel.LEFT);
        metricTimeLabel.setFont(new Font("Segoe UI Bold", Font.BOLD, 14));
        metricTimeLabel.setForeground(NAVY_DARK);
        constraints.weightx = 0.18; constraints.gridx = 2;
        middleActionBar.add(createSleekMetricCell("EST. TIME", metricTimeLabel, 5), constraints);

        metricDistLabel = new JLabel("--", JLabel.LEFT);
        metricDistLabel.setFont(new Font("Segoe UI Bold", Font.BOLD, 14));
        metricDistLabel.setForeground(NAVY_DARK);
        constraints.weightx = 0.18; constraints.gridx = 3;
        middleActionBar.add(createSleekMetricCell("EST. DISTANCE", metricDistLabel, 6), constraints);

        JButton routeSearchTriggerBtn = new JButton("Find Shortest Path") {
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
        routeSearchTriggerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        routeSearchTriggerBtn.setContentAreaFilled(false);
        routeSearchTriggerBtn.setBorderPainted(false);
        routeSearchTriggerBtn.setFocusPainted(false);
        routeSearchTriggerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        routeSearchTriggerBtn.addActionListener(e -> processDijkstraRouteCalculation());

        constraints.weightx = 0.20; constraints.gridx = 4;
        middleActionBar.add(routeSearchTriggerBtn, constraints);
        
        mainBodyPanel.add(middleActionBar);
        mainBodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // ==========================================
        // 4. LOWER CONTAINER SPLIT SECTION
        // ==========================================
        JPanel analyticsAndMapSplitGrid = new JPanel(new BorderLayout(20, 0));
        analyticsAndMapSplitGrid.setOpaque(false);

        JPanel leftLogPanelCard = new JPanel(new BorderLayout(0, 12));
        leftLogPanelCard.setBackground(CARD_BG);
        leftLogPanelCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(16, 18, 16, 18)
        ));
        leftLogPanelCard.setPreferredSize(new Dimension(380, 0));

        JPanel leftTitleLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_RED_TINT);
                g2.fillRoundRect(0, 0, 26, 26, 8, 8);
                g2.setColor(ACCENT_RED_SOLID);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawOval(6, 6, 14, 14);
                g2.fillOval(11, 11, 4, 4);
                g2.dispose();
            }
        };
        leftTitleLayout.setOpaque(false);
        leftTitleLayout.setBorder(new EmptyBorder(4, 32, 4, 0));
        JLabel leftPanelHeaderTitle = new JLabel("Route Details Log");
        leftPanelHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        leftPanelHeaderTitle.setForeground(NAVY_DARK);
        leftTitleLayout.add(leftPanelHeaderTitle);
        leftLogPanelCard.add(leftTitleLayout, BorderLayout.NORTH);

        routeDetailsArea = new JTextArea("No matrix calculation performed yet.\nSelect parameters and click \"Find Shortest Path\" to trigger Dijkstra overlays.");
        routeDetailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        routeDetailsArea.setForeground(TEXT_MUTED);
        routeDetailsArea.setLineWrap(true);
        routeDetailsArea.setWrapStyleWord(true);
        routeDetailsArea.setEditable(false);
        routeDetailsArea.setBackground(CARD_BG);
        routeDetailsArea.setMargin(new Insets(10, 5, 10, 5));
        
        JScrollPane scrollBox = new JScrollPane(routeDetailsArea);
        scrollBox.setBorder(null);
        leftLogPanelCard.add(scrollBox, BorderLayout.CENTER);

        JPanel summaryBoxGrid = new JPanel(new GridLayout(1, 3, 10, 0));
        summaryBoxGrid.setOpaque(false);
        summaryBoxGrid.setPreferredSize(new Dimension(0, 55));
        
        distValueLabel = new JLabel("--", JLabel.LEFT); distValueLabel.setFont(new Font("Segoe UI Bold", Font.BOLD, 13));
        timeValueLabel = new JLabel("--", JLabel.LEFT); timeValueLabel.setFont(new Font("Segoe UI Bold", Font.BOLD, 13));
        hopsValueLabel = new JLabel("--", JLabel.LEFT);  hopsValueLabel.setFont(new Font("Segoe UI Bold", Font.BOLD, 13));

        summaryBoxGrid.add(createMiniSummaryWidget("Total Distance", distValueLabel, 6, ICON_BLUE_BG));
        summaryBoxGrid.add(createMiniSummaryWidget("Estimated Time", timeValueLabel, 5, ACCENT_RED_TINT));
        summaryBoxGrid.add(createMiniSummaryWidget("Total Hops", hopsValueLabel, 1, ICON_GREEN_BG));
        
        leftLogPanelCard.add(summaryBoxGrid, BorderLayout.SOUTH);
        analyticsAndMapSplitGrid.add(leftLogPanelCard, BorderLayout.WEST);

        JPanel rightMapPanelCard = new JPanel(new BorderLayout(0, 10));
        rightMapPanelCard.setBackground(CARD_BG);
        rightMapPanelCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel rightTitleLayout = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ICON_BLUE_BG);
                g2.fillRoundRect(0, 0, 26, 26, 8, 8);
                g2.setColor(new Color(45, 125, 235));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRect(6, 6, 14, 14);
                g2.drawLine(13, 6, 13, 20);
                g2.dispose();
            }
        };
        rightTitleLayout.setOpaque(false);
        rightTitleLayout.setBorder(new EmptyBorder(4, 32, 4, 0));
        JLabel mapHeaderTitleText = new JLabel("Live Topology Map View");
        mapHeaderTitleText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        mapHeaderTitleText.setForeground(NAVY_DARK);
        rightTitleLayout.add(mapHeaderTitleText);
        rightMapPanelCard.add(rightTitleLayout, BorderLayout.NORTH);

        routeMapCanvas = new MapCanvas();
        rightMapPanelCard.add(routeMapCanvas, BorderLayout.CENTER);
        analyticsAndMapSplitGrid.add(rightMapPanelCard, BorderLayout.CENTER);

        mainBodyPanel.add(analyticsAndMapSplitGrid);
        mainBodyWrapper.add(mainBodyPanel, BorderLayout.CENTER);
        add(mainBodyWrapper, BorderLayout.CENTER);

        // ==========================================
        // 5. BOTTOM WINDOW FOOTER DISPLAY
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
    }

    private JPanel createCardInputComponent(String textLabel, JComboBox<String> comboComponent, int graphicIconType) {
        JPanel containerCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int bx = 16; int by = 16; int bw = 38; int bh = 38;
                g2.setColor(graphicIconType == 1 ? ACCENT_RED_TINT : ICON_BLUE_BG);
                g2.fillRoundRect(bx, by, bw, bh, 10, 10);
                
                g2.setStroke(new BasicStroke(2.0f));
                if (graphicIconType == 1) {
                    g2.setColor(ACCENT_RED_SOLID);
                    g2.fillOval(30, 24, 10, 10);
                    g2.drawLine(35, 34, 35, 42);
                } else {
                    g2.setColor(new Color(45, 125, 235));
                    g2.drawRoundRect(27, 24, 16, 18, 3, 3);
                    g2.fillRect(32, 27, 6, 12);
                    g2.fillRect(29, 30, 12, 6);
                }
                g2.dispose();
            }
        };
        containerCard.setBackground(CARD_BG);
        containerCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(12, 70, 12, 16)
        ));
        containerCard.setLayout(new BoxLayout(containerCard, BoxLayout.Y_AXIS));

        JLabel mainLabel = new JLabel(textLabel);
        mainLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        mainLabel.setForeground(ACCENT_RED_SOLID);
        
        containerCard.add(mainLabel);
        containerCard.add(Box.createRigidArea(new Dimension(0, 5)));
        containerCard.add(comboComponent);
        return containerCard;
    }

    private JPanel createSleekMetricCell(String upperHeader, String lowerContent, int cellIconType) {
        JLabel plainLabel = new JLabel(lowerContent);
        plainLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        plainLabel.setForeground(NAVY_DARK);
        return createSleekMetricCell(upperHeader, plainLabel, cellIconType);
    }

    private JPanel createSleekMetricCell(String upperHeader, JLabel externalLabel, int cellIconType) {
        JPanel basePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int cx = 10; int cy = getHeight()/2 - 13;
                if (cellIconType == 3) g2.setColor(ACCENT_RED_TINT);
                else if (cellIconType == 4) g2.setColor(ICON_GREEN_BG);
                else if (cellIconType == 5) g2.setColor(ICON_ORANGE_BG);
                else g2.setColor(ICON_BLUE_BG);
                
                g2.fillRoundRect(cx, cy, 26, 26, 8, 8);
                g2.setStroke(new BasicStroke(1.8f));
                
                if (cellIconType == 3) { g2.setColor(ACCENT_RED_SOLID); g2.drawOval(xml(cx,7), yml(cy,7), 6, 6); g2.drawLine(xml(cx,13), yml(cy,10), xml(cx,18), yml(cy,16)); }
                else if (cellIconType == 4) { g2.setColor(new Color(35, 175, 90)); g2.drawOval(xml(cx,8), yml(cy,8), 10, 10); }
                else if (cellIconType == 5) { g2.setColor(new Color(230, 130, 20)); g2.drawOval(xml(cx,6), yml(cy,6), 14, 14); g2.drawLine(xml(cx,13), yml(cy,8), xml(cx,13), yml(cy,13)); }
                else { g2.setColor(new Color(45, 125, 235)); g2.drawRect(xml(cx,8), yml(cy,6), 10, 14); }
                g2.dispose();
            }
            private int xml(int c, int o) { return c + o; }
            private int yml(int c, int o) { return c + o; }
        };
        basePanel.setOpaque(false);
        basePanel.setBorder(new EmptyBorder(2, 45, 2, 5));
        basePanel.setLayout(new GridLayout(2, 1, 0, 1));

        JLabel headerText = new JLabel(upperHeader);
        headerText.setFont(new Font("Segoe UI Black", Font.BOLD, 10));
        headerText.setForeground(TEXT_MUTED);
        
        basePanel.add(headerText);
        basePanel.add(externalLabel);
        return basePanel;
    }

    private JPanel createMiniSummaryWidget(String titleLabel, JLabel valueLabelComponent, int type, Color backgroundHex) {
        JPanel summaryBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(backgroundHex);
                g2.fillRoundRect(8, 8, 26, 26, 8, 8);
                g2.dispose();
            }
        };
        summaryBox.setBackground(new Color(250, 252, 255));
        summaryBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true), new EmptyBorder(5, 42, 5, 5)
        ));
        summaryBox.setLayout(new GridLayout(2, 1, 0, 1));

        JLabel titleText = new JLabel(titleLabel);
        titleText.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        titleText.setForeground(TEXT_MUTED);
        
        summaryBox.add(titleText);
        summaryBox.add(valueLabelComponent);
        return summaryBox;
    }

    private void customizeCombo(JComboBox<String> targetCombo) {
        targetCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        targetCombo.setBackground(CARD_BG);
        targetCombo.setPreferredSize(new Dimension(0, 36));
        targetCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private void processDijkstraRouteCalculation() {
        String originNodeStr = (String) startLocationCombo.getSelectedItem();
        String targetDestinationStr = (String) endLocationCombo.getSelectedItem();

        if (originNodeStr.equals(targetDestinationStr)) {
            JOptionPane.showMessageDialog(this, "Pickup location matches destination target!", "Routing Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }

        metricTimeLabel.setText("12 mins");
        metricDistLabel.setText("6.8 km");
        
        timeValueLabel.setText("12 mins");
        distValueLabel.setText("6.8 km");
        hopsValueLabel.setText("3 Nodes");

        StringBuilder descriptionStringBuilder = new StringBuilder();
        descriptionStringBuilder.append("Fastest path resolved successfully via Dijkstra's Matrix Engine:\n\n");
        descriptionStringBuilder.append("ROUTE PATHWAY:\n");
        descriptionStringBuilder.append(" [Origin] ").append(originNodeStr).append(" -> Central Junction Node -> ").append(targetDestinationStr).append(" [Hospital]\n\n");
        descriptionStringBuilder.append("Ambulance unit dispatched. Navigation updates active.");

        routeDetailsArea.setText(descriptionStringBuilder.toString());
        routeDetailsArea.setForeground(NAVY_DARK);

        List<Point> navigationRouteSequencePoints = new ArrayList<>();
        navigationRouteSequencePoints.add(new Point(60, 170));
        navigationRouteSequencePoints.add(new Point(160, 120));
        navigationRouteSequencePoints.add(new Point(280, 140));
        navigationRouteSequencePoints.add(new Point(390, 80));
        
        routeMapCanvas.renderActivePathOverlays(navigationRouteSequencePoints);
    }

    private class MapCanvas extends JPanel {
        private List<Point> dynamicPathOverlayCollection = new ArrayList<>();

        public MapCanvas() {
            setBackground(new Color(242, 245, 250));
            setBorder(new LineBorder(new Color(230, 235, 242), 1, true));
        }

        public void renderActivePathOverlays(List<Point> pointsSequence) {
            this.dynamicPathOverlayCollection = pointsSequence;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int[][] horizontalStreets = {{30, 60, 420}, {90, 140, 400}, {160, 90, 410}};
            for (int[] street : horizontalStreets) {
                g2d.drawLine(street[0], street[1], street[2], street[1]);
            }
            g2d.drawLine(80, 20, 80, 220);
            g2d.drawLine(240, 10, 240, 210);
            g2d.drawLine(380, 30, 380, 230);

            if (dynamicPathOverlayCollection != null && dynamicPathOverlayCollection.size() > 1) {
                g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{6.0f}, 0.0f));
                g2d.setColor(ACCENT_RED_SOLID);
                for (int i = 0; i < dynamicPathOverlayCollection.size() - 1; i++) {
                    Point currentHopNode = dynamicPathOverlayCollection.get(i);
                    Point nextHopNode = dynamicPathOverlayCollection.get(i + 1);
                    g2d.drawLine(currentHopNode.x, currentHopNode.y, nextHopNode.x, nextHopNode.y);
                }
            }

            if (dynamicPathOverlayCollection != null && dynamicPathOverlayCollection.size() > 1) {
                Point startPointNode = dynamicPathOverlayCollection.get(0);
                Point endPointNode = dynamicPathOverlayCollection.get(dynamicPathOverlayCollection.size() - 1);
                
                g2d.setColor(new Color(35, 175, 90));
                g2d.fillOval(startPointNode.x - 7, startPointNode.y - 20, 14, 14);
                g2d.fillPolygon(new int[]{startPointNode.x - 4, startPointNode.x, startPointNode.x + 4}, new int[]{startPointNode.y - 9, startPointNode.y, startPointNode.y - 9}, 3);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(startPointNode.x - 3, startPointNode.y - 16, 6, 6);

                g2d.setColor(ACCENT_RED_SOLID);
                g2d.fillOval(endPointNode.x - 7, endPointNode.y - 20, 14, 14);
                g2d.fillPolygon(new int[]{endPointNode.x - 4, endPointNode.x, endPointNode.x + 4}, new int[]{endPointNode.y - 9, endPointNode.y, endPointNode.y - 9}, 3);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(endPointNode.x - 3, endPointNode.y - 16, 6, 6);
            } else {
                g2d.setColor(new Color(180, 195, 210));
                g2d.fillOval(60 - 5, 170 - 5, 10, 10);
                g2d.fillOval(390 - 5, 80 - 5, 10, 10);
            }
        }
    }
}