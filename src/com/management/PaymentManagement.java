package com.management;

import com.exception.DBConnectionFailedException;
import com.model.Payment;
import com.util.ApplicationUtil;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class PaymentManagement {
  ApplicationUtil util = new ApplicationUtil();

  public int addPaymentToDB(Payment payment) {
    String query = "INSERT into payment values (?,?,?,?,?,?,?,?)";

    try (Connection con = DBConnectionManager.establishConnection();
         PreparedStatement ps = con.prepareStatement(query)) {
    	
    	ps.setString(1, payment.getPaymentId());
    	
    	String PatientType=payment.getPatientType();
    	String InPatientId=null;
    	String OutPatientId=null;
    	
      if(PatientType.toUpperCase().contains("INP")) {
    	  InPatientId= payment.getPaymentId();
    	  ps.setString(2, InPatientId);
      }else {
    	  ps.setString(2,InPatientId);
      }
      if(PatientType.toUpperCase().contains("OUT")) {
    	  OutPatientId=payment.getPaymentId();
    	  ps.setString(3, OutPatientId);
      }else {
    	  ps.setString(3,OutPatientId);
      }
      
      ps.setString(4, payment.getPatientName());
      ps.setString(5, PatientType);
      ps.setDate(6, Date.valueOf(util.dateToStringConversion(payment.getPaymentDate())));
      ps.setString(7, payment.getPaymentMode());
      ps.setDouble(8, payment.getTotalBill());
      return ps.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return -1;
    } catch (NullPointerException e) {
      System.out.println(e.getMessage());
    }

    return 0;
  }


  public List<String> retrievePaymentDetailsByNameAndPhone(String name, String phone, String patientType) {
    String query;
    List<String> list = new ArrayList<>();

    if (patientType.equalsIgnoreCase("oup"))
      query = """
              select * from payment p
              inner join outpatient o
              on p.patient_id = o.patient_id
              where lower(o.patient_name) = lower('?') and phone_number = ?;
              """;
    else
      query = """
              select * from payment p
              inner join inpatient o
              on p.patient_id = o.patient_id
              where lower(o.patient_name) = lower('?') and phone_number = ?;
              """;

    try (Connection connection = DBConnectionManager.establishConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, name);
      ps.setString(2, phone);
      ResultSet rs = ps.executeQuery(query);

      if (rs.next()) {
        list.add(rs.getString("payment_id"));
        list.add(rs.getString("patient_name"));
        list.add(rs.getString("phone_number"));
        list.add(util.dateToStringConversion(rs.getDate("payment_date")));
        list.add(rs.getString("mode_of_payment"));
        list.add(rs.getString("bill_amount"));
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
    return list;
  }

//  public boolean isUserExists(String paymentId) {
//
//    String query = "SELECT patient_id from payment WHERE payment_id=?";
//    try (Connection con = DBConnectionManager.establishConnection();
//         PreparedStatement ps = con.prepareStatement(query)) {
//
//      ps.setString(1, paymentId);
//
//      try (ResultSet rs = ps.executeQuery()) {
//        return rs.next();
//      }
//
//    } catch (SQLException e) {
//      e.getStackTrace();
//    }
//    return false;
//  }

  public List<Payment> retrieveAllPaymentDetails() {
    String query = "SELECT * from payment order by payment_Id";

    try (Connection con = DBConnectionManager.establishConnection();
         PreparedStatement ps = con.prepareStatement(query)) {
      List<Payment> paymentDetails = new ArrayList<>();

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          String paymentId = rs.getString("payment_id");
          String patientId = rs.getString("patient_id");
          String patientName = rs.getString("patient_name");
          String patientType = rs.getString("patient_type");
          java.util.Date paymentDate = rs.getDate("payment_date");
          String paymentMode = rs.getString("mode_of_payment");
          double bill = rs.getDouble("bill_amount");

          paymentDetails.add(new Payment(paymentId, patientId, patientName, patientType, paymentDate, paymentMode, bill));
        }

        return paymentDetails;
      }

    } catch (SQLException e) {
      return null;
    }
  }

  public List<String> retrievePaymentDetails(String patientId) throws DBConnectionFailedException {
    String query;
    List<String> list = new ArrayList<>();

    String patientType = patientId.substring(4, 7);

    if (patientType.equalsIgnoreCase("oup"))
      query = """
              select * from payment p
              inner join outpatient o
              on p.patient_id = o.patient_id
              where p.patient_id = '?';
              """;
    else
      query = """
              select * from payment p
              inner join inpatient o
              on p.patient_id = o.patient_id
              where p.patient_id = '?';
              """;

    try (Connection connection = DBConnectionManager.establishConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, patientId);
//      ps.setString(2, phone);
      ResultSet rs = ps.executeQuery(query);

      if (rs.next()) {
        list.add(rs.getString("payment_id"));
        list.add(rs.getString("patient_name"));
        list.add(rs.getString("phone_number"));
        list.add(util.dateToStringConversion(rs.getDate("payment_date")));
        list.add(rs.getString("mode_of_payment"));
        list.add(rs.getString("bill_amount"));
      }
      return list;
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  public String getLastId() {
    String query = "select * from payment order by payment_id desc limit 1";
    try (Connection con = DBConnectionManager.establishConnection();
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(query)) {

      if (rs.next()) {
        return rs.getString("payment_id");
      }

      return null;
    } catch (SQLException e) {
      e.getStackTrace();
    }

    return null;
  }
}
