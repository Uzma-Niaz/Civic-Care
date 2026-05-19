package ui;
import ambulance.AmbulancePanel;
import triage.TriagePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import blood.*;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private BloodService bloodService = new BloodService();

    private static final Color BG_COLOR       = new Color(245, 247, 250);
    private static final Color HEADER_COLOR   = new Color(30,  60,  114);
    private static final Color BTN_AMBULANCE  = new Color(220, 53,  69);
    private static final Color BTN_DISASTER   = new Color(255, 140, 0);
    private static final Color BTN_BLOOD      = new Color(40,  167, 69);
    private static final Color BTN_TEXT       = Color.WHITE;

    public MainWindow() {
        setTitle("CivicCare — Emergency & Resource Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

     // --- Header Section ---
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setPreferredSize(new Dimension(700, 120)); // Height 120 rakhi hai taake tagline ki jagah banay

        GridBagConstraints hGbc = new GridBagConstraints();
        hGbc.gridx = 0; // Column 0

        // 1. Title (CivicCare)
        JLabel titleLabel = new JLabel("CivicCare");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        hGbc.gridy = 0; // Row 0 (Upar)
        headerPanel.add(titleLabel, hGbc);

        // 2. Tagline (Smart Logic, Real-time Care.)
        JLabel taglineLabel = new JLabel("Smart Logic, Real-time Care.");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        taglineLabel.setForeground(new Color(200, 220, 255)); // Light Blue color
        hGbc.gridy = 1; // Row 1 (Niche)
        hGbc.insets = new Insets(5, 0, 0, 0); // Thora sa gap title aur tagline ke beech
        headerPanel.add(taglineLabel, hGbc);

        add(headerPanel, BorderLayout.NORTH);

        // Center Panel for Buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JButton ambulanceBtn = createModuleButton("Ambulance Routing", "Shortest path to hospital", BTN_AMBULANCE);
        JButton disasterBtn = createModuleButton("Disaster Management", "Priority allocation", BTN_DISASTER);
        JButton bloodBtn = createModuleButton("Blood Donor Matching", "Find compatible donors", BTN_BLOOD);

        centerPanel.add(ambulanceBtn, gbc);
        gbc.gridy = 1;
        centerPanel.add(disasterBtn, gbc);
        gbc.gridy = 2;
        centerPanel.add(bloodBtn, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // --- Action Listeners ---
     // --- Action Listeners ---
        ambulanceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAmbulanceModule();
            }
        });

        disasterBtn.addActionListener(e -> {
            javax.swing.JFrame triageFrame = new javax.swing.JFrame("Emergency Triage & Bed Management");
            triageFrame.setSize(980, 680);
            triageFrame.setLocationRelativeTo(null);
            triageFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
            triageFrame.add(new triage.TriagePanel()); 
            triageFrame.setVisible(true);
        });

        bloodBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String group = JOptionPane.showInputDialog(MainWindow.this, "Enter Blood Group (e.g., A+, O-):");
                if (group != null && !group.isEmpty()) {
                    java.util.List<Donor> matches = bloodService.findMatch(group.trim().toUpperCase());
                    if (matches.isEmpty()) {
                        JOptionPane.showMessageDialog(MainWindow.this, "No donors found for " + group);
                    } else {
                        StringBuilder sb = new StringBuilder("Compatible Donors Found:\n\n");
                        for (Donor d : matches) {
                            sb.append(d.toString()).append("\n");
                        }
                        JOptionPane.showMessageDialog(MainWindow.this, sb.toString());
                    }
                }
            }
        });

        setVisible(true);
    }

    private JButton createModuleButton(String title, String subtitle, Color color) {
        JButton btn = new JButton("<html><center><b>" + title + "</b><br>" + subtitle + "</center></html>");
        btn.setBackground(color);
        btn.setForeground(BTN_TEXT);
        btn.setPreferredSize(new Dimension(400, 60));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showComingSoon(String moduleName) {
        JOptionPane.showMessageDialog(this, moduleName + " module will be implemented soon.");
    }
 // Ye function ambulance ki nayi window kholega
    private void openAmbulanceModule() {
        javax.swing.JFrame ambulanceFrame = new javax.swing.JFrame("Ambulance Routing");
        ambulanceFrame.setSize(680, 640);
        ambulanceFrame.setLocationRelativeTo(this);
        ambulanceFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        
        // ambulance package se panel add ho raha hai
     // Line 124 ko aise likhein:
        ambulanceFrame.add(new AmbulancePanel());
        
        ambulanceFrame.setVisible(true);
    }
}