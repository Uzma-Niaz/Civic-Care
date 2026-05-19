package triage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.*;
import ambulance.AmbulancePanel;
import blood.BloodService;

public class ModuleMenu extends JFrame {

    private final Color HEADER_GRADIENT_START = new Color(13, 63, 168);
    private final Color HEADER_GRADIENT_END = new Color(24, 118, 220);
    private final Color BACKGROUND_COLOR = new Color(248, 250, 252);

    public ModuleMenu() {

        setTitle("CivicCare Management System");
        // Laptop screens ke liye optimized height taake taskbar ke peeche na chupe
        setSize(1280, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // ================= HEADER =================

        JPanel header = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, HEADER_GRADIENT_START,
                        getWidth(), getHeight(), HEADER_GRADIENT_END);

                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Left medical cross design
                g2d.setColor(new Color(255, 255, 255, 18));
                g2d.fillRoundRect(65, 45, 30, 110, 14, 14);
                g2d.fillRoundRect(25, 85, 110, 30, 14, 14);
                g2d.fillRoundRect(175, 115, 18, 60, 10, 10);
                g2d.fillRoundRect(155, 135, 60, 18, 10, 10);

                // Right circle decorations
                g2d.setStroke(new BasicStroke(6f));
                g2d.drawOval(getWidth() - 170, 35, 95, 95);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawOval(getWidth() - 245, 65, 40, 40);

                // dotted pattern
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {
                        g2d.fillOval(getWidth() - 95 + (j * 10), 130 + (i * 10), 4, 4);
                    }
                }
            }
        };

        // Header ki height thodi kam ki taake niche cards ko achi space mile
        header.setPreferredSize(new Dimension(1280, 200));

        GridBagConstraints hGbc = new GridBagConstraints();
        hGbc.gridx = 0;
        hGbc.gridy = 0;

        JLabel title = new JLabel("Civic-Care");
        title.setFont(new Font("Segoe UI", Font.BOLD, 52));
        title.setForeground(Color.WHITE);
        header.add(title, hGbc);

        // Decorative line
        JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        linePanel.setOpaque(false);

        JSeparator left = new JSeparator();
        left.setPreferredSize(new Dimension(140, 2));
        left.setForeground(new Color(255, 255, 255, 130));

        JLabel heart = new JLabel("♡");
        heart.setForeground(Color.WHITE);
        heart.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 26));

        JSeparator right = new JSeparator();
        right.setPreferredSize(new Dimension(140, 2));
        right.setForeground(new Color(255, 255, 255, 130));

        linePanel.add(left);
        linePanel.add(heart);
        linePanel.add(right);

        hGbc.gridy = 1;
        hGbc.insets = new Insets(6, 0, 6, 0);
        header.add(linePanel, hGbc);

        JLabel tagline = new JLabel("Smart Logic, Real-Time Care");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        tagline.setForeground(new Color(225, 240, 255));

        hGbc.gridy = 2;
        header.add(tagline, hGbc);

        // ================= CARDS CONTAINER =================

        JPanel cardsContainer = new JPanel(new GridLayout(1, 3, 35, 0));
        cardsContainer.setOpaque(false);
        // Vertical gap aur padding set ki taake layout tightly fit ho jaye
        cardsContainer.setBorder(new EmptyBorder(25, 50, 25, 50));

        cardsContainer.add(createModuleCard(
                "Emergency Triage", 1,
                "Register and prioritize emergency cases and patients.",
                new Color(239, 68, 68), new Color(254, 242, 242)));

        cardsContainer.add(createModuleCard(
                "Ambulance Routing", 2,
                "Find the nearest ambulance and optimize routes.",
                new Color(37, 99, 235), new Color(239, 246, 255)));

        cardsContainer.add(createModuleCard(
                "Blood Donation", 3,
                "Manage donors, blood groups and donation requests.",
                new Color(34, 197, 94), new Color(240, 253, 244)));

        // ================= FOOTER =================

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setPreferredSize(new Dimension(1280, 35));

        JLabel footerLabel = new JLabel("🛡 CivicCare – Serving Humanity, Saving Lives");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        footerLabel.setForeground(new Color(100, 116, 139));
        footerPanel.add(footerLabel);

        // ================= MAIN =================

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(cardsContainer, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createModuleCard(String title, int id, String description, Color themeColor, Color tintBg) {

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Safe proportional heights drawing bounds
                int cardH = getHeight() - 15;

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 14));
                g2d.fillRoundRect(8, 10, getWidth() - 16, cardH - 6, 28, 28);

                // Card body
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 8, cardH, 28, 28);

                int cx = getWidth() / 2;

                // Icons background circle - shifted up to y=85
                g2d.setColor(tintBg);
                g2d.fillOval(cx - 50, 85, 100, 100);

                // ================= ICONS GRAPHICS =================
                g2d.setColor(themeColor);

                if (id == 1) {
                    g2d.setStroke(new BasicStroke(3f));
                    g2d.drawRoundRect(cx - 30, 124, 52, 30, 8, 8);
                    g2d.fillRect(cx + 22, 131, 12, 23);
                    g2d.fillOval(cx - 16, 154, 12, 12);
                    g2d.fillOval(cx + 12, 154, 12, 12);
                    g2d.fillRect(cx - 8, 135, 16, 5);
                    g2d.fillRect(cx - 3, 130, 5, 15);
                } else if (id == 2) {
                    g2d.setStroke(new BasicStroke(3.5f));
                    g2d.drawOval(cx - 15, 114, 30, 30);
                    g2d.fillOval(cx - 5, 124, 10, 10);
                    g2d.drawLine(cx, 144, cx - 10, 158);
                    g2d.drawLine(cx, 144, cx + 10, 158);
                    g2d.fillRect(cx + 24, 141, 20, 10);
                } else {
                    int[] xP = {cx - 18, cx, cx + 18};
                    int[] yP = {145, 110, 145};
                    g2d.fillPolygon(xP, yP, 3);
                    g2d.fillOval(cx - 18, 128, 36, 26);
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(1.8f));
                    g2d.drawLine(cx - 10, 139, cx - 3, 139);
                    g2d.drawLine(cx - 3, 139, cx, 130);
                    g2d.drawLine(cx, 130, cx + 3, 144);
                    g2d.drawLine(cx + 3, 144, cx + 10, 139);
                }

                // Underline accent line
                g2d.setColor(themeColor);
                g2d.fillRoundRect(cx - 18, 205, 36, 4, 10, 10);
            }
        };

        card.setOpaque(false);

        // ================= 1. BLACK HEADING =================
        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(30, 41, 59));
        lblTitle.setBounds(10, 25, 340, 35);
        card.add(lblTitle);

        // ================= 2. DESCRIPTION =================
        JTextArea txtDesc = new JTextArea(description);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setForeground(new Color(100, 116, 139));
        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        txtDesc.setOpaque(false);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setLineWrap(true);
        // Position set perfectly between circle line and button
        txtDesc.setBounds(45, 225, 270, 50);
        card.add(txtDesc);

        // ================= 3. ORIGINAL BUTTON =================
        String btnText = id == 1 ? "Open Triage  ›" : id == 2 ? "Open Routing  ›" : "Open Donations  ›";

        JButton actBtn = new JButton(btnText);
        // Position shifted safe up (y=300) so it clears the taskbar gracefully
        actBtn.setBounds(80, 300, 200, 42);
        actBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        actBtn.setBackground(themeColor);
        actBtn.setForeground(Color.WHITE);
        actBtn.setFocusPainted(false);
        actBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Listeners logic code mapping
        actBtn.addActionListener(e -> {
            JFrame frame = new JFrame(title);
            frame.setSize(1200, 750);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            if (id == 1) {
                TriagePanel triage = new TriagePanel(() -> {
                    frame.dispose();
                    this.setVisible(true);
                });
                frame.add(triage);
            } else if (id == 2) {
                AmbulancePanel ambulance = new AmbulancePanel(() -> {
                    frame.dispose();
                    this.setVisible(true);
                });
                frame.add(ambulance);
            } else if (id == 3) {
                frame.add(new BloodService());
            }

            this.setVisible(false);
            frame.setVisible(true);
        });

        // Bringing layout rendering components on top index layer
        card.add(actBtn, 0);

        // Matrix resize calculation anchors
        card.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = card.getWidth();
                lblTitle.setBounds((w - 340) / 2, 25, 340, 35);
                txtDesc.setBounds((w - 270) / 2, 225, 270, 50);
                actBtn.setBounds((w - 200) / 2, 300, 200, 42);
            }
        });

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModuleMenu().setVisible(true));
    }
}