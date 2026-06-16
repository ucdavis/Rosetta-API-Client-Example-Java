package edu.ucdavis.rosetta;


public class Main {
    public static void main(String[] args) {

        //Submitted Argument Check
        if(args.length > 0 && args[0].isEmpty() == false)
        {

            //Determine Action to Take
            switch(args[0].toString().trim().toLowerCase())
            {
                case "people-login":
                    System.out.println("Looking something up by login");
                    break;
                case "people-iam":
                    System.out.println("Looking something up by IAM ID");
                    break;
                case "people-employee":
                    System.out.println("Looking something up by Employee ID");
                    break;
                case "people-student":
                    System.out.println("Looking something up by Student ID");
                    break;
                case "people-department":
                    System.out.println("Looking something up by Department Code");
                    break;
                case "employee-iam":
                    System.out.println("employee lookup by IAM ID");
                    break;
                case "employee-department":
                    System.out.println("employee lookup by Department Code");
                    break;
                case "employee-division":
                    System.out.println("employee lookup by Division");
                    break;
                case "employee-organization":
                    System.out.println("employee lookup by Organization");
                    break;
                case "employee-subdivision":
                    System.out.println("employee lookup by Sub Division");
                    break;
                case "employee-subdivisionl4":
                    System.out.println("employee lookup by Sub Division L4");
                    break;
                case "departments":
                    System.out.println("show departments");
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