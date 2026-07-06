package edu.ucdavis.rosetta;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class RosettaAPIWorker {
    
    public String Base_Url;
    public String Token_Url;
    private String _Client_ID;
    private String _Client_Secret;
    private String _OAuth_Token;
    private String _OAuth_Scopes;
    public String Test_ID;
    public String Export_Location;
    public LocalDateTime Expires_In;

    public RosettaAPIWorker()
    {
        //Load the .env File
        Dotenv dotenv = Dotenv.load();

        //Load API Information
        _Client_ID = dotenv.get("ROSETTA_CLIENT_ID");
        _Client_Secret = dotenv.get("ROSETTA_CLIENT_SECRET");
        _OAuth_Scopes = dotenv.get("ROSETTA_SCOPES");
        Base_Url = dotenv.get("ROSETTA_BASE_URL");
        Token_Url = dotenv.get("ROSETTA_OAUTH_URL");
        Test_ID = dotenv.get("ROSETTA_TEST_ID");
        Export_Location = dotenv.get("ROSETTA_EXPORT_LOCATION");

        //Configure Intitial Expires In Value
        Expires_In = LocalDateTime.now().minusHours(1);
        
    }

    public enum PeopleSearchBy
    {
        iamid,
        loginid,
        email,
        employeeid,
        studentid,
        mailid,
        department
    }

    public enum EmployeeSearchBy
    {
        iamid,
        departmentid,
        divisionid,
        subdivisionid,
        subdivisionl4id,
        organizationid
    }

    public boolean CheckOAuthToken()
    {
        //Var for Return Status
        boolean bTokenStatus = true;

        //Check Token Expiration
        if(LocalDateTime.now().plusMinutes(1).isAfter(Expires_In))
        {

            //HttpClient for API Call to Rosetta API
            try(HttpClient raHttpClient  = HttpClient.newHttpClient())
            {

                //Initiate Object Mapper to Parse Returned Json
                ObjectMapper joMapper = new ObjectMapper();

                //Build Request with Custom Header for OAuth Call
                HttpRequest raHttpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(Token_Url))
                        .header("client_id", _Client_ID)
                        .header("client_secret", _Client_Secret)
                        .header("grant_type","CLIENT_CREDENTIALS")
                        .header("scope",_OAuth_Scopes)
                        .POST(BodyPublishers.noBody())
                        .build();

                
                //Send Request via HTTP Client
                HttpResponse<String> raHttpResponse = raHttpClient.send(raHttpRequest, HttpResponse.BodyHandlers.ofString());

                //Check Return Status Code
                if(raHttpResponse.statusCode() == 200)
                {
                    //Create Json Object of Returned Json
                    JsonNode jnOAuthToken = joMapper.readTree(raHttpResponse.body());

                    //Check for Required Fields
                    if(jnOAuthToken.hasNonNull("access_token") && jnOAuthToken.hasNonNull("expires_in"))
                    {
                        //Load OAuth Access Token
                        _OAuth_Token = jnOAuthToken.get("access_token").asText();

                        //Update Expires In Value
                        Expires_In = LocalDateTime.now().plusSeconds(Long.parseLong(jnOAuthToken.get("expires_in").asText()));
                    }
                    else
                    {
                        bTokenStatus = false;
                    }
                    
                }
                else
                {
                    bTokenStatus = false;
                }

            }
            catch (Exception e) {
                bTokenStatus = false;
            }


        }//End of Expires In Check

        return bTokenStatus;
    }

    public RosettaDepartment ParseRosettaDepartmentJson(JsonNode jeDepartment)
    {
        //Initialize Department to Return
        RosettaDepartment rosettaDepartment = new RosettaDepartment();

        //Retrieve Department ID
        if(jeDepartment.hasNonNull("department_id"))
        {
            rosettaDepartment.Department_ID = jeDepartment.get("department_id").asText();
        }

        //Retrieve Department Title
        if(jeDepartment.hasNonNull("department_title"))
        {
            rosettaDepartment.Department_Title = jeDepartment.get("department_title").asText();
        }

        //Retrieve Department Short Tiele
        if(jeDepartment.hasNonNull("department_short_title"))
        {
            rosettaDepartment.Department_Short_Title = jeDepartment.get("department_short_title").asText();
        }

        //Retrieve Subdivision ID
        if(jeDepartment.hasNonNull("subdivision_id"))
        {
            rosettaDepartment.Subdivision_ID = jeDepartment.get("subdivision_id").asText();
        }

        //Retrieve Subdivision Title
        if(jeDepartment.hasNonNull("subdivision_title"))
        {
            rosettaDepartment.Subdivision_Title = jeDepartment.get("subdivision_title").asText();
        }

        //Retrieve Subdivision L4 ID
        if(jeDepartment.hasNonNull("subdivision_l4_id"))
        {
            rosettaDepartment.Subdivision_L4_ID = jeDepartment.get("subdivision_l4_id").asText();
        }

        //Retrieve Subdivision L4 Title
        if(jeDepartment.hasNonNull("subdivision_l4_title"))
        {
            rosettaDepartment.Subdivision_L4_Title = jeDepartment.get("subdivision_l4_title").asText();
        }

        //Retrieve Division ID
        if(jeDepartment.hasNonNull("division_id"))
        {
            rosettaDepartment.Division_ID = jeDepartment.get("division_id").asText();
        }

        //Retrieve Division Title
        if(jeDepartment.hasNonNull("division_title"))
        {
            rosettaDepartment.Division_Title = jeDepartment.get("division_title").asText();
        }

        //Retrieve Organization ID
        if(jeDepartment.hasNonNull("organization_id"))
        {
            rosettaDepartment.Organization_ID = jeDepartment.get("organization_id").asText();
        }

        //Retrieve Organization Title
        if(jeDepartment.hasNonNull("organization_title"))
        {
            rosettaDepartment.Organization_Title = jeDepartment.get("organization_title").asText();
        }

        return rosettaDepartment;
    }

    public RosettaStudentAssociationShort ParseRosettaStudentAssocShortJson(JsonNode jeStudentAssocShrt)
    {
        //Initialize Student Association to Return
        RosettaStudentAssociationShort rosettaStudentAssoc = new RosettaStudentAssociationShort();

        //Retrieve College Code
        if(jeStudentAssocShrt.hasNonNull("college_code"))
        {
            rosettaStudentAssoc.College_Code = jeStudentAssocShrt.get("college_code").asText();
        }

        //Retrieve College Title 
        if(jeStudentAssocShrt.hasNonNull("college_title"))
        {
            rosettaStudentAssoc.College_Title = jeStudentAssocShrt.get("college_title").asText();
        }

        //Retrieve Major Code
        if(jeStudentAssocShrt.hasNonNull("major_code"))
        {
            rosettaStudentAssoc.Major_Code = jeStudentAssocShrt.get("major_code").asText();
        }

        //Retrieve Major Title
        if(jeStudentAssocShrt.hasNonNull("major_title"))
        {
            rosettaStudentAssoc.Major_Title = jeStudentAssocShrt.get("major_title").asText();
        }

        //Retrieve Academic Level
        if(jeStudentAssocShrt.hasNonNull("academic_level"))
        {
            rosettaStudentAssoc.Academic_Level = jeStudentAssocShrt.get("academic_level").asText();
        }

        //Retrieve Class Level
        if(jeStudentAssocShrt.hasNonNull("class_level"))
        {
            rosettaStudentAssoc.Class_Level = jeStudentAssocShrt.get("class_level").asText();
        }

        return rosettaStudentAssoc;
    }

    public RosettaStudentAssociation ParseRosettaStudentAssocJson(JsonNode jeStudentAssoc)
    {
        //Initialize Student Association to Return
        RosettaStudentAssociation rosettaStudentAssoc = new RosettaStudentAssociation();

        //Retrieve IAM ID
        if(jeStudentAssoc.hasNonNull("iam_id"))
        {
            rosettaStudentAssoc.IAM_ID = jeStudentAssoc.get("iam_id").asText();
        }

        //Retrieve Student ID
        if(jeStudentAssoc.hasNonNull("student_id"))
        {
            rosettaStudentAssoc.Student_ID = jeStudentAssoc.get("student_id").asText();
        }

        //Retrieve PIDM
        if(jeStudentAssoc.hasNonNull("pidm"))
        {
            rosettaStudentAssoc.PIDM = jeStudentAssoc.get("pidm").asText();
        }

        //Retrieve College Code
        if(jeStudentAssoc.hasNonNull("college_code"))
        {
            rosettaStudentAssoc.College_Code = jeStudentAssoc.get("college_code").asText();
        }

        //Retrieve College Title 
        if(jeStudentAssoc.hasNonNull("college_title"))
        {
            rosettaStudentAssoc.College_Title = jeStudentAssoc.get("college_title").asText();
        }

        //Retrieve Major Code
        if(jeStudentAssoc.hasNonNull("major_code"))
        {
            rosettaStudentAssoc.Major_Code = jeStudentAssoc.get("major_code").asText();
        }

        //Retrieve Major Title
        if(jeStudentAssoc.hasNonNull("major_title"))
        {
            rosettaStudentAssoc.Major_Title = jeStudentAssoc.get("major_title").asText();
        }

        //Retrieve Level Affiliation Code
        if(jeStudentAssoc.hasNonNull("lvl_affiliation_code"))
        {
            rosettaStudentAssoc.Level_Affiliation_Code = jeStudentAssoc.get("lvl_affiliation_code").asText();
        }

        //Retrieve Class Affiliation Code
        if(jeStudentAssoc.hasNonNull("cls_affiliation_code"))
        {
            rosettaStudentAssoc.Class_Affiliation_Code = jeStudentAssoc.get("cls_affiliation_code").asText();
        }

        //Retrieve Rank
        if(jeStudentAssoc.hasNonNull("rank"))
        {
            rosettaStudentAssoc.IAM_ID = jeStudentAssoc.get("rank").asText();
        }

        return rosettaStudentAssoc;
    }

    public RosettaEmployeeAssociation ParseRosettaEmployeeAssocJson(JsonNode jeEmploymentAssoc)
    {
        //Initialize Employee Association to Return
        RosettaEmployeeAssociation rosettaEmplAssoc = new RosettaEmployeeAssociation();

        //Retrieve IAM ID
        if(jeEmploymentAssoc.hasNonNull("iam_id"))
        {
            rosettaEmplAssoc.IAM_ID = jeEmploymentAssoc.get("iam_id").asText();
        }

        //Retrieve Employee Record
        if(jeEmploymentAssoc.hasNonNull("employee_record"))
        {
            rosettaEmplAssoc.Employee_Record = jeEmploymentAssoc.get("employee_record").asText();
        }

        //Retrieve Employee ID
        if(jeEmploymentAssoc.hasNonNull("employee_id"))
        {
            rosettaEmplAssoc.Employee_ID = jeEmploymentAssoc.get("employee_id").asText();
        }

        //Retrieve Position Number
        if(jeEmploymentAssoc.hasNonNull("position_number"))
        {
            rosettaEmplAssoc.Position_Number = jeEmploymentAssoc.get("position_number").asText();
        }

        //Retrieve Position Title
        if(jeEmploymentAssoc.hasNonNull("position_title"))
        {
            rosettaEmplAssoc.Position_Title = jeEmploymentAssoc.get("position_title").asText();
        }

        //Retrieve Relationship to Organization
        if(jeEmploymentAssoc.hasNonNull("relationship_to_organization"))
        {
            rosettaEmplAssoc.Relationship_To_Organization = jeEmploymentAssoc.get("relationship_to_organization").asText();
        }

        //Retrieve Employee Classification
        if(jeEmploymentAssoc.hasNonNull("employee_classification"))
        {
            rosettaEmplAssoc.Employee_Classification = jeEmploymentAssoc.get("employee_classification").asText();
        }

        //Retrieve Employee Classification Description
        if(jeEmploymentAssoc.hasNonNull("employee_classification_description"))
        {
            rosettaEmplAssoc.Employee_Classification_Description = jeEmploymentAssoc.get("employee_classification_description").asText();
        }

        //Retrieve Status
        if(jeEmploymentAssoc.hasNonNull("status"))
        {
            rosettaEmplAssoc.Status = jeEmploymentAssoc.get("status").asText();
        }

        //Retrieve Hire Date
        if(jeEmploymentAssoc.hasNonNull("hire_date"))
        {
            rosettaEmplAssoc.Hire_Date = jeEmploymentAssoc.get("hire_date").asText();
        }

        //Retrieve Start Date
        if(jeEmploymentAssoc.hasNonNull("start_date"))
        {
            rosettaEmplAssoc.Start_Date = jeEmploymentAssoc.get("start_date").asText();
        }

        //Retrieve FTE Percentage
        if(jeEmploymentAssoc.hasNonNull("fte_percentage"))
        {
            rosettaEmplAssoc.FTE_Percentage = jeEmploymentAssoc.get("fte_percentage").asText();
        }

        //Retrieve Joy Type ID
        if(jeEmploymentAssoc.hasNonNull("job_type_id"))
        {
            rosettaEmplAssoc.Job_Type_ID = jeEmploymentAssoc.get("job_type_id").asText();
        }

        //Retrieve Job Type Description
        if(jeEmploymentAssoc.hasNonNull("job_type_description"))
        {
            rosettaEmplAssoc.Job_Type_Description = jeEmploymentAssoc.get("job_type_description").asText();
        }

        //Retrieve Organization ID
        if(jeEmploymentAssoc.hasNonNull("organization_id"))
        {
            rosettaEmplAssoc.Organization_ID = jeEmploymentAssoc.get("organization_id").asText();
        }

        //Retrieve Organization Title
        if(jeEmploymentAssoc.hasNonNull("organization_title"))
        {
            rosettaEmplAssoc.Organization_Title = jeEmploymentAssoc.get("organization_title").asText();
        }

        //Retrieve Division ID
        if(jeEmploymentAssoc.hasNonNull("division_id"))
        {
            rosettaEmplAssoc.Division_ID = jeEmploymentAssoc.get("division_id").asText();
        }

        //Retrieve Division Title
        if(jeEmploymentAssoc.hasNonNull("division_title"))
        {
            rosettaEmplAssoc.Division_Title = jeEmploymentAssoc.get("division_title").asText();
        }


        //Retrieve Subdivision ID
        if(jeEmploymentAssoc.hasNonNull("subdivision_id"))
        {
            rosettaEmplAssoc.Subdivision_ID = jeEmploymentAssoc.get("subdivision_id").asText();
        }

        //Retrieve Subdivision Title
        if(jeEmploymentAssoc.hasNonNull("subdivision_title"))
        {
            rosettaEmplAssoc.Subdivision_Title = jeEmploymentAssoc.get("subdivision_title").asText();
        }

        //Retrieve Subdivision L4 ID
        if(jeEmploymentAssoc.hasNonNull("subdivision_l4_id"))
        {
            rosettaEmplAssoc.Subdivision_L4_ID = jeEmploymentAssoc.get("subdivision_l4_id").asText();
        }

        //Retrieve Subdivision L4 Title
        if(jeEmploymentAssoc.hasNonNull("subdivision_l4_title"))
        {
            rosettaEmplAssoc.Subdivision_L4_Title = jeEmploymentAssoc.get("subdivision_l4_title").asText();
        }

        //Retrieve Business Unit ID
        if(jeEmploymentAssoc.hasNonNull("business_unit_id"))
        {
            rosettaEmplAssoc.Business_Unit_ID = jeEmploymentAssoc.get("business_unit_id").asText();
        }

        //Retrieve Business Unit Title
        if(jeEmploymentAssoc.hasNonNull("business_unit_title"))
        {
            rosettaEmplAssoc.Business_Unit_Title = jeEmploymentAssoc.get("business_unit_title").asText();
        }

        //Retrieve Department ID
        if(jeEmploymentAssoc.hasNonNull("department_id"))
        {
            rosettaEmplAssoc.Department_ID = jeEmploymentAssoc.get("department_id").asText();
        }

        //Retrieve Department Title
        if(jeEmploymentAssoc.hasNonNull("department_title"))
        {
            rosettaEmplAssoc.Department_Title = jeEmploymentAssoc.get("department_title").asText();
        }

        //Retrieve Department Short Title
        if(jeEmploymentAssoc.hasNonNull("department_short_title"))
        {
            rosettaEmplAssoc.Department_Short_Title = jeEmploymentAssoc.get("department_short_title").asText();
        }

        //Retrieve Reports to Position
        if(jeEmploymentAssoc.hasNonNull("reports_to_position"))
        {
            rosettaEmplAssoc.Reports_To_Position = jeEmploymentAssoc.get("reports_to_position").asText();
        }

        //Retrieve Reports To IAM ID
        if(jeEmploymentAssoc.hasNonNull("reports_to_iam_id"))
        {
            rosettaEmplAssoc.Reports_To_IAM_ID = jeEmploymentAssoc.get("reports_to_iam_id").asText();
        }

        //Retrieve Reports to Employee ID
        if(jeEmploymentAssoc.hasNonNull("reports_to_employee_id"))
        {
            rosettaEmplAssoc.Reports_To_Employee_ID = jeEmploymentAssoc.get("reports_to_employee_id").asText();
        }

        //Retrieve Is Health Position
        if(jeEmploymentAssoc.hasNonNull("is_health_position"))
        {
            rosettaEmplAssoc.Is_Health_Position = jeEmploymentAssoc.get("is_health_position").asText();
        }

        //Retrieve Is Campus Position
        if(jeEmploymentAssoc.hasNonNull("is_campus_position"))
        {
            rosettaEmplAssoc.Is_Campus_Position = jeEmploymentAssoc.get("is_campus_position").asText();
        }


        return rosettaEmplAssoc;
    }

    public RosettaPerson ParseRosettaPersonJson(JsonNode jePeople)
    {
        //Initialize Person to Return
        RosettaPerson rosettaPerson = new RosettaPerson();

        //Retrieve Display Name
        if(jePeople.hasNonNull("displayname"))
        {
            rosettaPerson.DisplayName = jePeople.get("displayname").asText();
        }

        //Retrieve IAM ID
        if(jePeople.hasNonNull("iam_id"))
        {
            rosettaPerson.IAM_ID = jePeople.get("iam_id").asText();
        }

        //Retrieve IDs
        if(jePeople.hasNonNull("id"))
        {

            //Pull ID Node
            JsonNode jeIDs = jePeople.get("id");

            //Retrieve IAM ID
            if(jeIDs.hasNonNull("iam_id"))
            {
                rosettaPerson.IAM_ID = jeIDs.get("iam_id").asText();
            }

            //Retrieve Login ID
            if(jeIDs.hasNonNull("login_id"))
            {
                rosettaPerson.Login_ID = jeIDs.get("login_id").asText();
            }

            //Retrieve Mothra ID
            if(jeIDs.hasNonNull("mothra_id"))
            {
                rosettaPerson.Mothra_ID = jeIDs.get("mothra_id").asText();
            }

            //Retrieve Employee ID
            if(jeIDs.hasNonNull("employee_id"))
            {
                rosettaPerson.Employee_ID = jeIDs.get("employee_id").asText();
            }

            //Retrieve Mail IDs
            if(jeIDs.hasNonNull("mail_id"))
            {
                //Pull Mail ID Node
                JsonNode jeIDsMail = jeIDs.get("mail_id");

                //Check for Campus Mail ID
                if(jeIDsMail.hasNonNull("campus"))
                {
                    rosettaPerson.Mail_ID_Campus = jeIDsMail.get("campus").asText();
                }

                //Check for Health Mail ID
                if(jeIDsMail.hasNonNull("health"))
                {
                    rosettaPerson.Mail_ID_Health = jeIDsMail.get("health").asText();
                }

            }

        }//End of IDs

        //Retrieve Names
        if(jePeople.hasNonNull("name"))
        {
            //Retrieve Name Node
            JsonNode jeNames = jePeople.get("name");

            //Check for Lived First Name
            if(jeNames.hasNonNull("lived_first_name"))
            {
                rosettaPerson.Lived_First_Name = jeNames.get("lived_first_name").asText();
            }

            //Check for Lived Last Name
            if(jeNames.hasNonNull("lived_last_name"))
            {
                rosettaPerson.Lived_Last_Name = jeNames.get("lived_last_name").asText();
            }

        }//End of Names Checks

        //Retrieve Email Addresses
        if(jePeople.hasNonNull("email"))
        {
            //Retrieve Email Node
            JsonNode jeEmailAddress = jePeople.get("email");

            //Check for Campus Email Address
            if(jeEmailAddress.hasNonNull("campus"))
            {
                rosettaPerson.Email_Address_Campus = jeEmailAddress.get("campus").asText();
            }

            //Check for Health Email Address
            if(jeEmailAddress.hasNonNull("health"))
            {
                rosettaPerson.Email_Address_Health = jeEmailAddress.get("health").asText();
            }

        }//End of Email Addresses

        //Retrieve Provisioning Statuses
        if(jePeople.hasNonNull("provisioning_status"))
        {
            //Retrieve Provisioning Statuses 
            JsonNode jeProvisioningStatus = jePeople.get("provisioning_status");

            //Retrieve Primary Provisioning Status
            if(jeProvisioningStatus.hasNonNull("primary"))
            {
                rosettaPerson.Provisioning_Status_Primary = jeProvisioningStatus.get("primary").asText();
            }

            //Retrieve Employee Provisioning Status
            if(jeProvisioningStatus.hasNonNull("employee"))
            {
                rosettaPerson.Provisioning_Status_Employee = jeProvisioningStatus.get("employee").asText();
            }

            //Retrieve Faculty Provisioning Status
            if(jeProvisioningStatus.hasNonNull("faculty"))
            {
                rosettaPerson.Provisioning_Status_Faculty = jeProvisioningStatus.get("faculty").asText();
            }

            //Retrieve Student Provisioning Status
            if(jeProvisioningStatus.hasNonNull("student"))
            {
                rosettaPerson.Provisioning_Status_Student = jeProvisioningStatus.get("student").asText();
            }


        }//End of Provisioning Status

        //Retrieve Affiliation
        if(jePeople.hasNonNull("affiliation") && jePeople.get("affiliation").isArray())
        {

            //Retreive Affiliations
            JsonNode jeAffiliation = jePeople.get("affiliation");

            //Loop Through Each Affiliation
            for(JsonNode jeAffil : jeAffiliation)
            {

                switch(jeAffil.asText())
                {

                    case "employee":
                        rosettaPerson.Affiliation_Employee = true;
                        break;
                    
                    case "faculty":
                        rosettaPerson.Affiliation_Faculty = true;
                        break;
                    
                    case "temporary_affiliate":
                        rosettaPerson.Affiliation_Temporary_Affiliate = true;
                        break;

                    case "student":
                        rosettaPerson.Affiliation_Student = true;
                        break;

                    case "student_applicant":
                        rosettaPerson.Affiliation_Student_Applicant = true;
                        break;

                    case "health_affiliate":
                        rosettaPerson.Affiliation_Health_Affiliate = true;
                        break;

                }//End of jeAffil Switch Statement

            }//End of Affiliation Enumerate Array
        
        }//End of Affiliations 

        //Retrieve Employment Status
        if(jePeople.hasNonNull("employment_status") && jePeople.get("employment_status").isArray())
        {
            //Retrieve Employment Statuses
            JsonNode jeEmploymentStatus = jePeople.get("employment_status");

            //Loop Through Each Employment Status
            for(JsonNode jeEmplStatus : jeEmploymentStatus)
            {
                switch(jeEmplStatus.asText())
                {

                    case "is_academic":
                        rosettaPerson.Employment_Is_Academic = true;
                        break;
                    
                    case "is_academic_senate":
                        rosettaPerson.Employment_Is_Academic_Senate = true;
                        break;
                    
                    case "is_academic_federation":
                        rosettaPerson.Employment_Is_Academic_Federation = true;
                        break;

                    case "is_faculty":
                        rosettaPerson.Employment_Is_Faculty = true;
                        break;

                    case "is_teaching_faculty":
                        rosettaPerson.Employment_Is_Teaching_Faculty = true;
                        break;

                    case "is_ladder_rank":
                        rosettaPerson.Employment_Is_Ladder_Rank = true;
                        break;

                    case "is_without_salary":
                        rosettaPerson.Employment_Is_Without_Salary = true;
                        break;

                    case "is_msp":
                        rosettaPerson.Employment_Is_MSP = true;
                        break;

                    case "is_ssp":
                        rosettaPerson.Employment_Is_SSP = true;
                        break;

                    case "is_manager":
                        rosettaPerson.Employment_Is_Manager = true;
                        break;

                    case "is_campus_employee":
                        rosettaPerson.Employment_Is_Campus_Employee = true;
                        break;

                    case "is_health_employee":
                        rosettaPerson.Employment_Is_Health_Employee = true;
                        break;

                }//End of jeEmplStatus Switch Statement

            }//End of Employment Status Enumerate Array

        }//End of Employment Statuses

        //Check for Employment Associations
        if(jePeople.hasNonNull("employee_association") && jePeople.get("employee_association").isArray())
        {
            //Retrieve Employment Associations Node
            JsonNode jeEmploymentAssociations = jePeople.get("employee_association");

            //Loop Through Each Employment Association
            for(JsonNode jeEmplAssociation : jeEmploymentAssociations)
            {
                rosettaPerson.lEmployeeAssociations.add(ParseRosettaEmployeeAssocJson(jeEmplAssociation));
            }

            //Update Employee Associations with IAM ID
            if(rosettaPerson.IAM_ID.isEmpty() == false && rosettaPerson.lEmployeeAssociations.size() > 0)
            {
                for(RosettaEmployeeAssociation rea : rosettaPerson.lEmployeeAssociations)
                {
                    rea.IAM_ID = rosettaPerson.IAM_ID;
                }
            }

        }//End of Employment Associations

        //Check for Student Associations
        if(jePeople.hasNonNull("student_association") && jePeople.get("student_association").isArray())
        {
            //Retrieve Student Associations Node
            JsonNode jeStudentAssociations = jePeople.get("student_association");

            for(JsonNode jeStdtAssociation : jeStudentAssociations)
            {
                rosettaPerson.lStudentAssociations.add(ParseRosettaStudentAssocShortJson(jeStdtAssociation));
            }

        }//End of Student Associations

        return rosettaPerson;
    }

    public List<RosettaPerson> GetPeopleBySearchTerm(PeopleSearchBy searchBy, String searchTerm)
    {
        //Var for Return List
        List<RosettaPerson> lRosettaPeople = new ArrayList<>();

        //Initiate Object Mapper to Parse Returned Json
        ObjectMapper joMapper = new ObjectMapper();

        //Var for Search Result Limit
        int nSrchRsltLimit = 100;

        //Var for Search Result Offset
        int nSrchRsltOffset = 0;

        //Var for Retrieve More Search Results
        boolean bRetrMoreSrchRslts = true;

        do
        {
            //Check OAuth Token
            if(CheckOAuthToken() == true)
            {
                //HttpClient for API Call to Rosetta API
                try(HttpClient raHttpClient  = HttpClient.newHttpClient())
                {
                    //Var for Accounts URL
                    String peopleURL =  Base_Url + "people?"+ searchBy.toString() + "=" + searchTerm + "&offset=" + Integer.toString(nSrchRsltOffset) + "&limit=" + Integer.toString(nSrchRsltLimit) + "&count=true";

                    //Build Request for People Lookup
                    HttpRequest peopleHttpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(peopleURL))
                            .header("Authorization","Bearer " + _OAuth_Token)
                            .GET()
                            .build();

                    //Send Accounts Request 
                    HttpResponse<String> peopleHttpResponse = raHttpClient.send(peopleHttpRequest, HttpResponse.BodyHandlers.ofString());

                    //Check Return Status Code
                    if(peopleHttpResponse.statusCode() == 200)
                    {

                        //Pull X-Total-Count and X-Response-Count Values
                        if(peopleHttpResponse.headers().firstValue("x-total-count").isPresent() &&
                           peopleHttpResponse.headers().firstValue("x-response-count").isPresent())
                        {
                            //Determine Header Values Counts
                            int nTotalCnt = peopleHttpResponse.headers().firstValue("x-total-count").map(Integer::parseInt).orElse(0);
                            int nRspnCnt = peopleHttpResponse.headers().firstValue("x-response-count").map(Integer::parseInt).orElse(0);

                            //Check Total and Reponse Counts are Not Empty
                            if(nTotalCnt > 0 && nRspnCnt > 0)
                            {
                                //Create Json Object of Accounts Json Data
                                JsonNode jnPeopleData = joMapper.readTree(peopleHttpResponse.body());

                                //Loop Through Accounts Information
                                for(JsonNode jnPerson : jnPeopleData)
                                {
                                    //Add Rosetta Person to Returned People List
                                    lRosettaPeople.add(ParseRosettaPersonJson(jnPerson));
                                }

                                //Increment Offset
                                nSrchRsltOffset += nSrchRsltLimit;

                                //Check Offset to Total Count
                                if(nSrchRsltOffset >= nTotalCnt)
                                {
                                    bRetrMoreSrchRslts = false;
                                }


                            }
                            else
                            {
                                bRetrMoreSrchRslts = false;
                            }//End of nTotalCnt and nRspnCnt Empty Checks
                            
                        }
                        else
                        {
                            bRetrMoreSrchRslts = false;
                        }//End of Return Header Counts Checks
                        
                    }
                    else
                    {
                        bRetrMoreSrchRslts = false;
                    }//End of Status Code Check

                }
                catch (Exception e) {
                    bRetrMoreSrchRslts = false;
                }//End of HttpClient

            }//End of CheckOAuthToken
        }
        while(bRetrMoreSrchRslts == true);

        return lRosettaPeople;
    }

    public List<RosettaEmployeeAssociation> GetEmployeeAssociationsBySearchTerm(EmployeeSearchBy searchBy, String searchTerm)
    {
        //Var for List to Return
        List<RosettaEmployeeAssociation> lEmployeeAssociations = new ArrayList<>();

        //Initiate Object Mapper to Parse Returned Json
        ObjectMapper joMapper = new ObjectMapper();

        //Var for Search Result Limit
        int nSrchRsltLimit = 200;

        //Var for Search Result Offset
        int nSrchRsltOffset = 0;

        //Var for Retrieve More Search Results
        boolean bRetrMoreSrchRslts = true;

        do
        {
            //Check OAuth Token
            if(CheckOAuthToken() == true)
            {

                //HttpClient for API Call to Rosetta API
                try(HttpClient raHttpClient  = HttpClient.newHttpClient())
                {
                    //Var for Employee Associations URL
                    String employeeURL =  Base_Url + "employee-association?"+ searchBy.toString() + "=" + searchTerm + "&offset=" + Integer.toString(nSrchRsltOffset) + "&limit=" + Integer.toString(nSrchRsltLimit) + "&count=true";

                    //Build Request for Employee Associations Lookup
                    HttpRequest employeeHttpRequest = HttpRequest.newBuilder()
                            .uri(URI.create(employeeURL))
                            .header("Authorization","Bearer " + _OAuth_Token)
                            .GET()
                            .build();

                    //Send Employee Associations Request 
                    HttpResponse<String> employeeHttpResponse = raHttpClient.send(employeeHttpRequest, HttpResponse.BodyHandlers.ofString());

                    //Check Return Status Code
                    if(employeeHttpResponse.statusCode() == 200)
                    {

                        //Pull X-Total-Count and X-Response-Count Values
                        if(employeeHttpResponse.headers().firstValue("x-total-count").isPresent() &&
                           employeeHttpResponse.headers().firstValue("x-response-count").isPresent())
                        {
                            //Determine Header Values Counts
                            int nTotalCnt = employeeHttpResponse.headers().firstValue("x-total-count").map(Integer::parseInt).orElse(0);
                            int nRspnCnt = employeeHttpResponse.headers().firstValue("x-response-count").map(Integer::parseInt).orElse(0);

                            //Check Total and Reponse Counts are Not Empty
                            if(nTotalCnt > 0 && nRspnCnt > 0)
                            {
                                //Create Json Object of Employee Associations Json Data
                                JsonNode jnEmployeeData = joMapper.readTree(employeeHttpResponse.body());

                                //Loop Through Employee Association Information
                                for(JsonNode jnEmployee : jnEmployeeData)
                                {
                                    //Add Rosetta Employee Association to Returned Employee List
                                    lEmployeeAssociations.add(ParseRosettaEmployeeAssocJson(jnEmployee));
                                }

                                //Increment Offset
                                nSrchRsltOffset += nSrchRsltLimit;

                                //Check Offset to Total Count
                                if(nSrchRsltOffset >= nTotalCnt)
                                {
                                    bRetrMoreSrchRslts = false;
                                }


                            }
                            else
                            {
                                bRetrMoreSrchRslts = false;
                            }//End of nTotalCnt and nRspnCnt Empty Checks
                            
                        }
                        else
                        {
                            bRetrMoreSrchRslts = false;
                        }//End of Return Header Counts Checks
                        
                    }
                    else
                    {
                        bRetrMoreSrchRslts = false;
                    }//End of Status Code Check

                }
                catch (Exception e) {
                    bRetrMoreSrchRslts = false;
                }//End of HttpClient

            }//End of CheckOAuthToken
        }
        while(bRetrMoreSrchRslts == true);

        return lEmployeeAssociations;
    }

    public List<RosettaDepartment> GetRosettaDepartments()
    {
        //Var for List to Return
        List<RosettaDepartment> lDepartments = new ArrayList<>();

         //Initiate Object Mapper to Parse Returned Json
        ObjectMapper joMapper = new ObjectMapper();

        //Var for Search Result Limit
        int nSrchRsltLimit = 3000;

        //Check OAuth Token
        if(CheckOAuthToken() == true)
        {

            //HttpClient for API Call to Rosetta API
            try(HttpClient raHttpClient  = HttpClient.newHttpClient())
            {
                //Var for Departments URL
                String departmentsURL =  Base_Url + "employee-association/departments?limit=" + Integer.toString(nSrchRsltLimit);

                //Build Request for Departments Lookup
                HttpRequest departmentsHttpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(departmentsURL))
                        .header("Authorization","Bearer " + _OAuth_Token)
                        .GET()
                        .build();

                //Send Departments Request 
                HttpResponse<String> departmentsHttpResponse = raHttpClient.send(departmentsHttpRequest, HttpResponse.BodyHandlers.ofString());

                //Check Return Status Code
                if(departmentsHttpResponse.statusCode() == 200)
                {

                    //Create Json Object of Department Json Data
                    JsonNode jnDepartmentsData = joMapper.readTree(departmentsHttpResponse.body());

                    //Loop Through Employee Association Information
                    for(JsonNode jnDepartment : jnDepartmentsData)
                    {
                        //Add Rosetta Department to Returned Department List
                        lDepartments.add(ParseRosettaDepartmentJson(jnDepartment));
                    }

                }

            }
            catch (Exception e) {
                System.out.println(e);
            }//End of HttpClient

        }//End of CheckOAuthToken
        
        return lDepartments;
    }
    
    
    
    public void ResourceStuffs()
    {

        // public static void main(String[] args) throws Exception {
        //
        //
        // //##########################################
        //     //Retreiving OAuth Token
        //     //##########################################
        
        //     //HttpClient for API Call to Rosetta API
        //     HttpClient raHttpClient  = HttpClient.newHttpClient();

        //     //Initiate Object Mapper to Parse Returned Json
        //     ObjectMapper joMapper = new ObjectMapper();

        //     //Build Request with Custom Header for OAuth Call
        //     HttpRequest raHttpRequest = HttpRequest.newBuilder()
        //                 .uri(URI.create(rosettaAPIInfo.token_url))
        //                 .header("client_id", rosettaAPIInfo.client_id)
        //                 .header("client_secret", rosettaAPIInfo.client_secret)
        //                 .header("grant_type","CLIENT_CREDENTIALS")
        //                 .POST(BodyPublishers.noBody())
        //                 .build();

        //     //Send Request via HTTP Client
        //     HttpResponse<String> raHttpResponse = raHttpClient.send(raHttpRequest, HttpResponse.BodyHandlers.ofString());

        //     //Create Json Object of Returned Json
        //     JsonNode jnOAuthToken = joMapper.readTree(raHttpResponse.body());

        //     //Load OAuth Access Token
        //     rosettaAPIInfo.oauth_token = jnOAuthToken.get("access_token").asText();

        // //Check on Returned OAuth Access Token
        //     if(rosettaAPIInfo.oauth_token.isEmpty() == false)
        //     {

        //         //########################################
        //         // Viewing Accounts Endpoint Information
        //         //########################################

        //         //Var for Accounts URL
        //         String accountsURL = rosettaAPIInfo.base_url + "accounts?iamid=" + rosettaAPIInfo.test_id;

        //         //Build Request for Accounts Lookup
        //         HttpRequest accntsHttpRequest = HttpRequest.newBuilder()
        //                     .uri(URI.create(accountsURL))
        //                     .header("Authorization","Bearer " + rosettaAPIInfo.oauth_token)
        //                     .GET()
        //                     .build();

        //         //Send Accounts Request 
        //         HttpResponse<String> accntsHttpResponse = raHttpClient.send(accntsHttpRequest, HttpResponse.BodyHandlers.ofString());

        //         //Create Json Object of Accounts Json Data
        //         JsonNode jnAccountsData = joMapper.readTree(accntsHttpResponse.body());

        //         //Loop Through Accounts Information
        //         for(JsonNode jnAccount : jnAccountsData)
        //         {

        //             if(jnAccount.get("AccountName").asText().equalsIgnoreCase("UCPath Position Entitlement") == true)
        //             {
        //                 String prettyJson = joMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jnAccount);
        //                 System.out.println(prettyJson);
        //             }

        //         }//End of jnAccountData For




        //     }//End of OAuth Access Token Empty Check

    }

}
