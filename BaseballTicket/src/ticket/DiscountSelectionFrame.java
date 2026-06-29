package ticket;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DiscountSelectionFrame extends JFrame {
    private JComboBox<Integer> generalBox;
    private JComboBox<Integer> childBox;
    private JComboBox<Integer> seniorBox;
    private JComboBox<Integer> disabledBox;

    public DiscountSelectionFrame(GameInfo gameInfo, List<Integer> selectedSeats) {
        setTitle("할인 선택");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        int totalSeats = selectedSeats.size();
        Integer[] seatOptions = new Integer[totalSeats + 1];
        for (int i = 0; i <= totalSeats; i++) seatOptions[i] = i;

        JLabel titleLabel = new JLabel("할인 선택");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(50, 30, 200, 40);
        add(titleLabel);

        JPanel discountPanel = new JPanel();
        discountPanel.setLayout(new GridLayout(4, 2, 20, 20));
        discountPanel.setBounds(50, 100, 400, 250);

        JLabel generalLabel = new JLabel("일반");
        generalBox = new JComboBox<>(seatOptions);

        JLabel childLabel = new JLabel("어린이");
        childBox = new JComboBox<>(seatOptions);

        JLabel seniorLabel = new JLabel("노인");
        seniorBox = new JComboBox<>(seatOptions);

        JLabel disabledLabel = new JLabel("장애인");
        disabledBox = new JComboBox<>(seatOptions);

        discountPanel.add(generalLabel); discountPanel.add(generalBox);
        discountPanel.add(childLabel); discountPanel.add(childBox);
        discountPanel.add(seniorLabel); discountPanel.add(seniorBox);
        discountPanel.add(disabledLabel); discountPanel.add(disabledBox);

        add(discountPanel);

        // 우측 경기 정보 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBounds(600, 50, 550, 300);
        infoPanel.setBorder(BorderFactory.createTitledBorder("경기 정보"));

        infoPanel.add(new JLabel("경기 일시: " + gameInfo.getDate() + " " + gameInfo.getTime()));
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(new JLabel("경기 팀: " + gameInfo.getTeam() + " vs " + gameInfo.getOpponentTeam()));
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(new JLabel("경기장: " + gameInfo.getStadium()));
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(new JLabel("선택한 좌석 번호: " + selectedSeats.toString()));

        for (Component comp : infoPanel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setFont(new Font("SansSerif", Font.PLAIN, 18));
            }
        }

        add(infoPanel);

        // 완료 버튼
        JButton completeBtn = new JButton("다음 단계");
        completeBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        completeBtn.setBounds(450, 400, 200, 50);
        add(completeBtn);

        completeBtn.addActionListener(e -> {
            int totalSelected = (int) generalBox.getSelectedItem()
                              + (int) childBox.getSelectedItem()
                              + (int) seniorBox.getSelectedItem()
                              + (int) disabledBox.getSelectedItem();

            if (totalSelected != totalSeats) {
                JOptionPane.showMessageDialog(this,
                    "선택한 좌석 수(" + totalSeats + ")와 할인 인원 수(" + totalSelected + ")가 일치하지 않습니다.",
                    "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 여기서 할인 정보 처리 가능
            JOptionPane.showMessageDialog(this, "다음 단계로 진행합니다!");
            // 예: new PaymentFrame(gameInfo, selectedSeats, 할인정보 Map 등)
            this.dispose();
        });

        setVisible(true);
    }
}
