import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends JFrame implements ActionListener {
    JPanel mainPanel;
    JPanel currentPanel;
    JButton addBtn;
    JButton showCars;
    JLabel currentPanelLabel = new JLabel("", SwingConstants.CENTER);

    String url = "jdbc:mysql://localhost:3306/car";
    String username = "root";
    String password = "12345678";
    ResultSet result;

    Main() {
        setTitle("Cars");
        setVisible(true);
        setSize(new Dimension(700, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        currentPanel = new JPanel();
        currentPanel.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());

        JLabel title = new JLabel("Pasha Cars", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        header.add(title, BorderLayout.NORTH);

        JPanel btnContainer = new JPanel();

        addBtn = new JButton("Add Car");
        addBtn.addActionListener(this);
        showCars = new JButton("Show Cars");
        showCars.addActionListener(this);

        btnContainer.add(addBtn);
        btnContainer.add(showCars);

        header.add(btnContainer, BorderLayout.SOUTH);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(currentPanel, BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.NORTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            currentPanelLabel.setText("Add a new Car");
            currentPanelLabel.setFont(new Font("Arial", Font.ITALIC, 20));

            JPanel carForm = new JPanel();
            carForm.setLayout(new GridLayout(4, 2));

            JLabel name = new JLabel("Name");
            JTextField nameField = new JTextField();

            JLabel yearModel = new JLabel("Year Model");
            JTextField yearField = new JTextField();

            JButton addNewCar = new JButton("Submit");
            addNewCar.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (nameField.getText().isEmpty() || yearField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Empty Fields");
                    } else {
                        String query = "INSERT INTO cars (name, model) VALUES (?, ?)";
                        try (Connection conn = DriverManager.getConnection(url, username, password);
                             PreparedStatement statement = conn.prepareStatement(query)) {
                            statement.setString(1, nameField.getText());
                            statement.setString(2, yearField.getText());
                            statement.executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            carForm.add(name);
            carForm.add(nameField);
            carForm.add(yearModel);
            carForm.add(yearField);
            carForm.add(new JLabel());
            carForm.add(addNewCar);

            currentPanel.add(currentPanelLabel, BorderLayout.NORTH);
            currentPanel.add(carForm, BorderLayout.SOUTH);

            revalidate();
            repaint();
        } else if (e.getSource() == showCars) {
            currentPanelLabel.setText("List of Cars");
            currentPanelLabel.setFont(new Font("Arial", Font.ITALIC, 20));

            try (Connection conn = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = conn.prepareStatement("SELECT * FROM cars");
                 ResultSet resultSet = statement.executeQuery()) {

                StringBuilder carsList = new StringBuilder();
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String model = resultSet.getString("model");
                    carsList.append("Name: ").append(name).append(", Model: ").append(model).append("\n");
                }

                JTextArea carsTextArea = new JTextArea(carsList.toString());
                carsTextArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(carsTextArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

                currentPanel.removeAll();
                currentPanel.add(currentPanelLabel, BorderLayout.NORTH);
                currentPanel.add(scrollPane, BorderLayout.CENTER);
                currentPanel.revalidate();
                currentPanel.repaint();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        new Main();
    }
}