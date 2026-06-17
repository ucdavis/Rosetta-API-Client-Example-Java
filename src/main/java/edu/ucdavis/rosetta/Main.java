package edu.ucdavis.rosetta;

import java.util.List;
import java.lang.reflect.Field;

public class Main {
    public static void main(String[] args) {

        //Submitted Argument Check
        if(args.length > 0 && args[0].isEmpty() == false)
        {

            //Determine Action to Take
            switch(args[0].toString().trim().toLowerCase())
            {
                case "people-login":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.loginid,args[1]);
                    break;
                case "people-iam":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.iamid,args[1]);
                    break;
                case "people-employee":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.employeeid,args[1]);
                    break;
                case "people-student":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.studentid,args[1]);
                    break;
                case "people-department":
                    PeopleSearching(RosettaAPIWorker.PeopleSearchBy.department,args[1]);
                    break;
                case "employee-iam":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.iamid,args[1]);
                    break;
                case "employee-department":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.departmentid,args[1]);
                    break;
                case "employee-division":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.divisionid,args[1]);
                    break;
                case "employee-organization":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.organizationid,args[1]);
                    break;
                case "employee-subdivision":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.subdivisionid,args[1]);
                    break;
                case "employee-subdivisionl4":
                    EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy.subdivisionl4id,args[1]);
                    break;
                case "departments":
                    ShowDepartments();
                    break;
                default:
                    ShowArgumentOptions();
                    break;

            }//end of Action Switch
        }
        else
        {
            ShowArgumentOptions();
        }

    }

    //###############################
    // People Searching
    //###############################

    static void PeopleSearching(RosettaAPIWorker.PeopleSearchBy peopleSearchBy,String searchTerm)
    {
        //Initiate Rosetta API Worker
        RosettaAPIWorker rosettaAPIWrkr = new RosettaAPIWorker();

        //Query People by Search Parameters
        List<RosettaPerson> lRosettaPeople = rosettaAPIWrkr.GetPeopleBySearchTerm(peopleSearchBy,searchTerm.trim());

        //Loop Through Returned UCD Peeps
        for (RosettaPerson rosettaPrsn : lRosettaPeople) 
        {
            //For Readability
            System.out.println(" ");
            System.out.println("=========== " + rosettaPrsn.DisplayName + " =============");
            System.out.println(" ");

            //Pull Rosetta Person Class
            Class<?> clazz = rosettaPrsn.getClass();

            //Loop Through Fields and Display Values
            for (Field field : clazz.getDeclaredFields()) 
            {

                //Set Accessability
                field.setAccessible(true);

                try
                {
                    System.out.println(field.getName() + " = " + field.get(rosettaPrsn));
                }
                catch(IllegalAccessException e)
                {
                    System.out.println("error occured");
                }
            
            }

            System.out.println(" ");
            
        }

    }

    //###############################
    // Employee-Associations
    //###############################

    static void EmployeeSearching(RosettaAPIWorker.EmployeeSearchBy employeeSearchBy,String searchTerm)
    {
        
    }

    //###############################
    // Show Rosetta Departments
    //###############################
    
    static void ShowDepartments()
    {
        
    }

    //###############################
    // Display Agrument Options
    //###############################
    static void ShowArgumentOptions()
    {
        System.out.println(" ");
        System.out.println("Argument Options:");
        System.out.println("=======================");
        System.out.println("people-login <userid>");
        System.out.println("people-iam <iamid>");
        System.out.println("people-employee <employeeid>");
        System.out.println("people-student <studentid>");
        System.out.println("people-department <departmentcode>");
        System.out.println("employee-iam <iamid>");
        System.out.println("employee-department <departmentcode>");
        System.out.println("employee-division <divisionid>");
        System.out.println("employee-organization <organizationid>");
        System.out.println("employee-subdivision <subdivisionid>");
        System.out.println("employee-subdivisionl4 <subdivisionl4id>");
        System.out.println("departments");
        System.out.println(" ");
        System.out.println(" ");
    }
}