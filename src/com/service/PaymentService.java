package com.service;

import com.client.Menu;
import com.exception.*;
import com.management.PaymentManagement;
import com.management.PaymentManagement;
import com.model.*;
import com.model.Payment;
import com.util.ApplicationUtil;

import java.util.*;

public class PaymentService {
  Outpatient outpatient;
  Inpatient inpatient;
  double totalBill = 0.0;
  ApplicationUtil util = new ApplicationUtil();
  PaymentManagement paymentManagement = new PaymentManagement();
  AllocationService allocationService = new AllocationService();
  Menu menu = new Menu();
  String prefix;

  public int add(String paymentDetail) {
    // List<String> strList = util.splitRecord(paymentArray);
    Payment paymentList = build(Arrays.asList(paymentDetail));
    return addPaymentToDB(paymentList);
  }

  public Payment build(List<String> details) {
    OutpatientService outpatientService = new OutpatientService();
    InpatientService inpatientService = new InpatientService();
    int lastId = util.generatePaymentId();
    String name = null, patientType = null;

    // APL/OUP/101:credit
    for (int i = 0; i < details.size(); i++) {
      try {
        String[] detail = details.get(i).split(":");
        if (detail.length == 2) {
          String patientId = detail[0].toUpperCase();

          if (patientId.startsWith("APL/OUP/")) {
            outpatient = outpatientService.retrieveOutpatientDetailsById(patientId);
            if (outpatient != null) {
              name = outpatient.getPatientName();
              patientType = outpatient.getPatientType();
              totalBill = calculateBillForOutpatient();
            } else {
              System.out.println("Error: No Patient Found");
              return null;
            }
          } else if (patientId.startsWith("APL/INP/")) {
            inpatient = inpatientService.retrieveInpatientDetailByPatientId(patientId);
            if (inpatient != null) {
              name = inpatient.getPatientName();
              patientType = inpatient.getPatientType();
              totalBill = calculateBillForInpatient();
            } else {
              System.out.println("Error: No Patient Found");
              return null;
            }
          } else {
            System.out.println("Error: Invalid Patient ID");
            return null;
          }

          Date date = new Date();

          String paymentMode = detail[1].trim();
          String allocationId = "APL/PAY/" + ++lastId;

          return new Payment(allocationId, patientId, name, patientType, date, paymentMode, totalBill);
        } else throw new InvalidUserInputLength("Record " + (i + 1) + ": Please provide all the required data.");
      } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
        menu.missingDataId(i);
      } catch (InvalidUserInputLength | DBConnectionFailedException e) {
        System.out.println(e.getMessage());
      }
    }

    return null;
  }

  public double calculateBillForInpatient() throws DBConnectionFailedException {
    double treatmentCost = 0.0;
    double foodCost = 100;
    String roomType = inpatient.getRoomType();
    String treatment = inpatient.getTreatment();
    String foodPreference = inpatient.getWantFood();
    Allocation allocation = allocationService.retrieveAllocationDetailsByPatientId(inpatient.getPatientId());

    if (allocation != null) {
      Map<String, Integer> roomPrices = util.roomPrice();
      int noOfDays = allocation.getNoOfDaysAdmitted();

      TreatmentService treatmentService = new TreatmentService();
      List<Treatment> treatmentList = treatmentService.getTreatmentList();

      for (Treatment curTreatment : treatmentList) {
        if (curTreatment.getName().equalsIgnoreCase(treatment)) {
          if (foodPreference.equalsIgnoreCase("yes")) {
            treatmentCost += foodCost * noOfDays;
          }
        }
      }

      for (Map.Entry<String, Integer> temp : roomPrices.entrySet()) {
        if (temp.getKey().equalsIgnoreCase(roomType)) {
          treatmentCost += temp.getValue();
        }
      }

      return treatmentCost + inpatient.getAdmissionFees() + inpatient.getMedicineFee();
    }

    return 0;
  }

  public double calculateBillForOutpatient() throws DBConnectionFailedException {
    return outpatient.getRegistration_fees() + outpatient.getMedicineFee();
  }

  public int addPaymentToDB(Payment payment) {
    return paymentManagement.addPaymentToDB(payment);
  }

  public List<String> retrievePaymentDetailsByNameAndPhone(String name, String phone, String patientType) {
    return paymentManagement.retrievePaymentDetailsByNameAndPhone(name, phone, patientType);
  }

  public List<Payment> retrieveAllPaymentDetails() {
    return paymentManagement.retrieveAllPaymentDetails();
  }

  public List<String> retrievePaymentDetails(String patientId) throws DBConnectionFailedException {
    return paymentManagement.retrievePaymentDetails(patientId);
  }
}
