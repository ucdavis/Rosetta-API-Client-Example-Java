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

                    //Build Request for Accounts Lookup
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
