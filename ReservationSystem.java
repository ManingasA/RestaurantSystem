/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package restaurantsystem;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.toedter.calendar.*;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.zinternaltools.TimeChangeEvent;
import java.time.LocalTime;

/**
 *
 * @author Maningas_Arnel
 */
public class ReservationSystem implements ActionListener{
    
    final private JFrame reservationFrame = new JFrame();
    final private JTextField name, pax;
    final private JButton btn;
    
    final private String sql = "INSERT INTO restodatabase.reservation(`Name`, PAX, Date, Time) VALUES (?, ?, ?, ?)";
    final private String url = "jdbc:mysql://127.0.0.1:3306/restodatabase";
    final private String user = "root";
    final private String password = "PassWord";
    
    final private JTable table;
    final private DefaultTableModel dtm;
    final private JScrollPane scrollBar;
    
    private JDateChooser calendar = new JDateChooser();
    
    private TimePicker time = new TimePicker();
    
    public ReservationSystem() {
        
        name = new JTextField();
        name.setBounds(50, 0,100, 50);
        pax = new JTextField();
        pax.setBounds(50, 100,100, 50);
        btn = new JButton("okay");
        btn.setBounds(50, 200,100, 50);
        btn.addActionListener(e -> {
            try (Connection con = DriverManager.getConnection(url, user, password);
                PreparedStatement pst = con.prepareStatement(sql)) {
                
                System.out.println("Connection Success");
                
                java.sql.Date d = new java.sql.Date(calendar.getDate().getTime());
                LocalTime st = time.getTime();
                java.sql.Time t = java.sql.Time.valueOf(st);
                
                int paxNumber = Integer.parseInt(pax.getText());
                pst.setString(1, name.getText());
                pst.setInt(2, paxNumber);
                pst.setDate(3, d);
                pst.setTime(4, t);
                
                int rowsAffected = pst.executeUpdate();
                
                System.out.println(rowsAffected);
            }
            catch(SQLException sqle) {
                System.out.println("Connection failed");
                System.out.println("Error: " + sqle.getMessage());
            }
        
        });
        
        String[] columnNames = {"Name", "ReservationNumber", "PAX", "Date", "Time"};

        dtm = new DefaultTableModel(columnNames, 0);
        
        table = new JTable(dtm);
        timer();
        
        scrollBar = new JScrollPane(table);
        scrollBar.setBounds(200, 200, 600, 100);
        
        calendar.setBounds(200, 300, 100, 20);
        
        time.setBounds(500, 300, 100, 30);
        
        reservationFrame.add(time);
        reservationFrame.add(calendar);
        reservationFrame.add(scrollBar);
        reservationFrame.add(name);
        reservationFrame.add(pax);
        reservationFrame.add(btn);
        
        reservationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        reservationFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        reservationFrame.setLayout(null);
        reservationFrame.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    public void retrieveData() {
        try(Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT Name, ReservationNumber, PAX, Date, Time FROM restodatabase.reservation");
                
                while (rs.next()) {
                    String n = rs.getString("Name");
                    int rn = rs.getInt("ReservationNumber");
                    int px = rs.getInt("PAX");
                    Date dt = rs.getDate("Date");
                    Object tm = rs.getTime("Time");
                    
                    dtm.addRow(new Object[]{n, rn, px, dt, tm});
                }
        }
        catch(SQLException sql) {
            System.out.println("Error: " + sql.getMessage());
        }
    }
    
    public void timer() {
        int delay = 500;
        ActionListener update = e -> {
            dtm.setRowCount(0);
            retrieveData();
        };
        new Timer(delay, update).start();
    }
}
