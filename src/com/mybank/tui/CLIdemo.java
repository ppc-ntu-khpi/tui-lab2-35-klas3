package com.mybank.tui;

import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.OverDraftAmountException;
import com.mybank.domain.SavingsAccount;
import com.mybank.reporting.CustomerReport;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.jline.reader.*;
import org.jline.reader.impl.completer.*;
import org.jline.utils.*;
import org.fusesource.jansi.*;

/**
 * Sample application to show how jLine can be used.
 *
 * @author sandarenu
 *
 */
/**
 * Console client for 'Banking' example
 *
 * @author Alexander 'Taurus' Babich
 */
public class CLIdemo {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private final String filePath = "data/test.dat";
    
    private String[] commandsList;

    public void init() {
        commandsList = new String[]{"help", "customers", "customer", "report", "account", "deposit", "withdraw", "save", "exit"};
    }

    public void run() {
        AnsiConsole.systemInstall(); // needed to support ansi on Windows cmd
        printWelcomeMessage();
        LineReaderBuilder readerBuilder = LineReaderBuilder.builder();
        List<Completer> completors = new LinkedList<Completer>();

        completors.add(new StringsCompleter(commandsList));
        readerBuilder.completer(new ArgumentCompleter(completors));

        LineReader reader = readerBuilder.build();

        String line;
        PrintWriter out = new PrintWriter(System.out);

        while ((line = readLine(reader, "")) != null) {
            if (commandsList[0].equals(line)) {
                printHelp();
            } else if (commandsList[1].equals(line)) {
                AttributedStringBuilder a = new AttributedStringBuilder()
                        .append("\nThis is all of your ")
                        .append("customers", AttributedStyle.BOLD.foreground(AttributedStyle.RED))
                        .append(":");

                System.out.println(a.toAnsi());
                if (Bank.getNumberOfCustomers() > 0) {
                    System.out.println("\nLast name\tFirst Name\tBalance");
                    System.out.println("---------------------------------------");
                    for (int i = 0; i < Bank.getNumberOfCustomers(); i++) {
                        System.out.println(Bank.getCustomer(i).getLastName() + "\t\t" + Bank.getCustomer(i).getFirstName() + "\t\t$" + Bank.getCustomer(i).getAccount(0).getBalance());
                    }
                } else {
                    System.out.println(ANSI_RED+"Your bank has no customers!"+ANSI_RESET);
                }

            } else if (line.indexOf(commandsList[2]) != -1) {
                try {
                    int custNo = 0;
                    if (line.length() > 8) {
                        String strNum = line.split(" ")[1];
                        if (strNum != null) {
                            custNo = Integer.parseInt(strNum);
                        }
                    }                    
                    Customer cust = Bank.getCustomer(custNo);
                    String accType = cust.getAccount(0) instanceof CheckingAccount ? "Checkinh" : "Savings";
                    
                    AttributedStringBuilder a = new AttributedStringBuilder()
                            .append("\nThis is detailed information about customer #")
                            .append(Integer.toString(custNo), AttributedStyle.BOLD.foreground(AttributedStyle.RED))
                            .append("!");

                    System.out.println(a.toAnsi());
                    
                    System.out.println("\nLast name\tFirst Name\tAccount Type\tBalance");
                    System.out.println("-------------------------------------------------------");
                    System.out.println(cust.getLastName() + "\t\t" + cust.getFirstName() + "\t\t" + accType + "\t$" + cust.getAccount(0).getBalance());
                } catch (Exception e) {
                    System.out
                        .println(ANSI_RED + "ERROR! Wrong customer number!" + ANSI_RESET);
                }
            } else if(commandsList[3].equals(line)) {
                (new CustomerReport()).generateReport();
            } else if(line.indexOf(commandsList[4]) != -1) {
                Account account = handleCommandWithParams(line, 3);
                if(account != null) {
                    System.out.println((new StringBuilder()).append("$").append(account.getBalance()));
                }
            } else if (line.indexOf(commandsList[5]) != -1) {
                try {
                    Account account = handleCommandWithParams(line, 4);
                    String moneyCount = line.split(" ")[3];
                    if(account != null) {
                        account.deposit(Double.parseDouble(moneyCount));
                        System.out.println((new StringBuilder()).append("You've just deposit $").append(moneyCount).append(" on customer's account, current balance &").append(account.getBalance()));
                    } 
                } catch(Exception e) {
                    System.out.println("Invalid parameters for this command");
                }
            } else if (line.indexOf(commandsList[6]) != -1) {
                try {
                    Account account = handleCommandWithParams(line, 4);
                    String moneyCount = line.split(" ")[3];
                    if(account != null) {
                        account.withdraw(Double.parseDouble(moneyCount));
                        System.out.println((new StringBuilder()).append("You've just withdraw $").append(moneyCount).append(" from customer's account, current balance &").append(account.getBalance()));
                    }
                } catch(OverDraftAmountException e) {
                    System.out.println(e.getMessage());
                } catch(Exception e) {
                    System.out.println("Invalid parameters for this command");
                }
            } else if(line.indexOf(commandsList[7]) != -1) {
              saveChanges();  
            } else if (commandsList[8].equals(line)) {
                System.out.println("Exiting application");
                return;
            } else {
                System.out
                        .println(ANSI_RED + "Invalid command, For assistance press TAB or type \"help\" then hit ENTER." + ANSI_RESET);
            }
        }

        AnsiConsole.systemUninstall();
    }
    
    private Account handleCommandWithParams(String line, int requiredParamsCount) {
        try {
            String[] params = line.split(" ");

            if(params.length == requiredParamsCount) {
                Customer customer = Bank.getCustomer(Integer.parseInt(params[1]));
                Account account = getCustomerAccount(params[2], customer);

                if(account != null) {
                    return account;
                } else {
                    System.out.println("Customer don't have this account");
                }
            } else {
                System.out.println("Invalid parameters for this command");
            }
        } catch(IndexOutOfBoundsException e) {
            System.out.println("Customer with provided number is not found");
        } catch(Exception e) {
            System.out.println("Invalid parameters for this command");
        }
        
        return null;
    }
    
    private Account getCustomerAccount(String accountType, Customer customer) {
        for(int i = 0; i < customer.getNumberOfAccounts(); i++) {
            Account account = customer.getAccount(i);

            if("s".equals(accountType.toLowerCase()) && account instanceof SavingsAccount) {
                return account;
            } else if("c".equals(accountType.toLowerCase()) && account instanceof CheckingAccount) {
                return account;
            }
        }
        
        return null;
    }

    private void printWelcomeMessage() {
        System.out
                .println("\nWelcome to " + ANSI_GREEN + " MyBank Console Client App" + ANSI_RESET + "! \nFor assistance press TAB or type \"help\" then hit ENTER.");

    }

    private void printHelp() {
        System.out.println("help\t\t\t\t- Show help");
        System.out.println("customer\t\t\t- Show list of customers");
        System.out.println("customer \'index\'\t\t- Show customer details");
        System.out.println("report\t\t\t\t- Customers report");
        System.out.println("account \'index\' S/C\t\t- Show current balance on customer's account");
        System.out.println("deposit \'customer\' S/C \'sum\'\t- Deposit on customer's account");
        System.out.println("withdraw \'customer\' S/C \'sum\'\t- Withdaw from customer's account");
        System.out.println("save\t\t\t\t - save all changes");
        System.out.println("exit\t\t\t\t- Exit the app");

    }

    private String readLine(LineReader reader, String promtMessage) {
        try {
            String line = reader.readLine(promtMessage + ANSI_YELLOW + "\nbank> " + ANSI_RESET);
            return line.trim();
        } catch (UserInterruptException e) {
            // e.g. ^C
            return null;
        } catch (EndOfFileException e) {
            // e.g. ^D
            return null;
        }
    }

    private void saveChanges() {
        try {
            File file = new File(filePath);
            
            if(file.delete()) {
                if(file.createNewFile()) {
                    FileWriter writer = new FileWriter(file);
                    int numberOfCustomers = Bank.getNumberOfCustomers();
                    
                    writer.write(Integer.toString(numberOfCustomers));
                    writer.write("\n\n");
                    
                    for (int i = 0; i < numberOfCustomers; i++) {
                        Customer customer = Bank.getCustomer(i);
                        int numberOfAccounts = customer.getNumberOfAccounts();
                        
                        writer.write(
                            (new StringBuilder())
                            .append(customer.getFirstName())
                            .append("\t")
                            .append(customer.getLastName())
                            .append("\t")
                            .append(numberOfAccounts)
                            .toString()
                        );
                        writer.write("\n");
                        
                        for (int j = 0; j < numberOfAccounts; j++) {
                            Account account = customer.getAccount(j);
                            
                            writer.write(
                                (new StringBuilder())
                                .append(account instanceof CheckingAccount ? "C\t" : "S\t")
                                .append(account.getBalance())
                                .toString() 
                            );
                        }
                        
                        writer.write("\n");
                    }
                    
                    writer.close();
                } else {
                    System.out.println("Cannot overwrite file");
                }
            } else {
                System.out.println("Cannot overwrite file");
            }
        } catch(Exception e) {
            System.out.println("File not fount");
        }
    }
    
    public static void main(String[] args) {

        Bank.addCustomer("John", "Doe");
        Bank.addCustomer("Fox", "Mulder");
        Bank.getCustomer(0).addAccount(new CheckingAccount(2000));
        Bank.getCustomer(1).addAccount(new SavingsAccount(1000, 3));

        CLIdemo shell = new CLIdemo();
        shell.init();
        shell.run();
    }
}
