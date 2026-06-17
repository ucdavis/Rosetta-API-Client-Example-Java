package edu.ucdavis.rosetta;

import java.util.ArrayList;
import java.util.List;

public class RosettaPerson 
{
    public String IAM_ID = "";
    public String Login_ID = "";
    public String Mothra_ID = "";
    public String Employee_ID = "";
    public String Mail_ID_Campus = "";
    public String Mail_ID_Health = "";
    public String Email_Address_Campus = "";
    public String Email_Address_Health = "";
    public String Lived_First_Name = "";
    public String Lived_Last_Name = "";
    public String DisplayName = "";
    public String Provisioning_Status_Primary = "";
    public String Provisioning_Status_Employee = "";
    public String Provisioning_Status_Faculty = "";
    public String Provisioning_Status_Student = "";
    public boolean Affiliation_Employee = false;
    public boolean Affiliation_Faculty = false;
    public boolean Affiliation_Temporary_Affiliate = false;
    public boolean Affiliation_Student = false;
    public boolean Affiliation_Student_Applicant = false;
    public boolean Affiliation_Health_Affiliate = false;
    public boolean Employment_Is_Academic = false;
    public boolean Employment_Is_Academic_Senate = false;
    public boolean Employment_Is_Academic_Federation = false;
    public boolean Employment_Is_Faculty = false;
    public boolean Employment_Is_Teaching_Faculty = false;
    public boolean Employment_Is_Ladder_Rank = false;
    public boolean Employment_Is_Without_Salary = false;
    public boolean Employment_Is_MSP = false;
    public boolean Employment_Is_SSP = false;
    public boolean Employment_Is_Manager = false;
    public boolean Employment_Is_Campus_Employee = false;
    public boolean Employment_Is_Health_Employee = false;
    public List<RosettaEmployeeAssociation> lEmployeeAssociations = new ArrayList<>();
    public List<RosettaStudentAssociationShort> lStudentAssociations = new ArrayList<>();

    public RosettaPerson()
    {

    }

}
