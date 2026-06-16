package edu.ucdavis.rosetta;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

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
