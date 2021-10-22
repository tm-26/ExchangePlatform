/*TODO
Requirements:
1) Create an Exchange platform operator account. ✓
2) Exchange platform operator must be able to approve of users, and unapproved user can not use the platform. ✓
3) Implement a matching engine. ✓
4) Allow for orders to be cancelled by the exchange platform operator, lister or trader. ✓
5) Create a log for every action. ✓

Require fixing:
1) printOrder function. ✓

Would be nice to have:
1) Whole platform saves after proper exit.

Polishing:
1) Beautify the table generation. ✓
2) Make sure passwords are hidden when they are being entered.
3) Get mc.flush to work as intended. ✓
4) Consider adding an About option.
5) Remove unneeded comments and prints. ✓
 */

//package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        //Really wanted to keep this part in but i'm not certain if it works with every console
        //Console console = System.console();

        MethodClass mc = new MethodClass();
        Scanner input = new Scanner(System.in);

        mc.writeToLog("----------------------------------------------------------------------------------------\n**** PLACEHOLDER executed and launched at " + LocalDateTime.now().toString() + " ****\n----------------------------------------------------------------------------------------");
        ArrayList<User> userList = new ArrayList<>();
        ArrayList<User> usersToBeApproved = new ArrayList<>();
        ArrayList<Order> buyOrderList = new ArrayList<>();
        ArrayList<Order> sellOrderList = new ArrayList<>();

        //Login Menu
        for (;;) {
            mc.flush();
            System.out.println("---- WELCOME TO PLACEHOLDER - A Stock Exchange Platform ----");
            int menuInput = mc.menuInput(new String[]{"Login", "Sign Up", "Exit"});
            if (menuInput == 1){

                //Log in menu
                boolean userFound = false;
                System.out.println("---- Logging In ----");
                System.out.print("Enter your username:");
                String username = mc.getString();
                System.out.print("Enter your password:");
                //String password = String.valueOf(console.readPassword());
                String password = mc.getString();
                User currentUser = new User("", "", false, 0,new ArrayList<Security>());
                for (User user : userList) {
                    if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                        System.out.println("Logged In!");
                        currentUser = user;
                        userFound =true;
                    }
                }

                if (!userFound){
                    if(username.equals("Operator") && password.equals("admin")) {
                        // Exchange Platform Operator menu
                        mc.writeToLog("Exchange Platform Operator logged in");
                        boolean exitTraderMenu = false;
                        while (!exitTraderMenu) {
                            mc.flush();
                            System.out.println("Hello Platform Operator, please select the functionality which you would you like to use.");
                            menuInput = mc.menuInput(new String[]{"View Order Book", "Approve of new users (" + usersToBeApproved.size() + " new users to be approved)", "Log Out", "Exit"});
                            mc.flush();
                            switch (menuInput) {
                                case 1:
                                    System.out.println("    Order Book");
                                    System.out.println("---- Sell Orders ----");
                                    mc.printOrders(sellOrderList);
                                    System.out.println("---- Buy Orders ----");
                                    mc.printOrders(buyOrderList, sellOrderList.size() + 1);
                                    menuInput = mc.menuInput(new String[]{"Remove an order", "Go back to menu"});
                                    if (menuInput == 1) {
                                        if(sellOrderList.isEmpty() && buyOrderList.isEmpty()){
                                            System.out.println("No order to remove");
                                            break;
                                        }
                                        System.out.println("Select, by index, which order you would like to remove");
                                        mc.removeBuyOrSellOrder(sellOrderList, buyOrderList, currentUser);

                                    }
                                    break;
                                case 2:
                                    System.out.format("%-15s%30s%30s", "Index", "Username", "Type\n");
                                    int counter = 1;
                                    for(User i: usersToBeApproved){
                                        System.out.format("%-15s%30s", counter, i.getUsername());
                                        if(i.getType()){
                                            System.out.format("%30s", "Lister\n");
                                        }
                                        else{
                                            System.out.format("%30s", "Trader\n");
                                        }
                                        counter++;
                                    }
                                    System.out.println();
                                    System.out.println("0) Exit");
                                    System.out.print("Select, by index, which user you would like to approve of: ");
                                    menuInput = mc.getInt(0, usersToBeApproved.size()) - 1;
                                    if(menuInput == -1){
                                        break;
                                    }
                                    System.out.println("Are you sure you want to approve " + usersToBeApproved.get(menuInput).getUsername());
                                    System.out.print("(Y/N): ");
                                    String confirmation = mc.getString(new String[]{"y", "n", "yes", "no"});
                                    if(confirmation.equals("y") || confirmation.equals("yes")){
                                        System.out.println("User" + usersToBeApproved.get(menuInput).getUsername() + " approved.");
                                        userList.add(usersToBeApproved.get(menuInput));
                                        mc.writeToLog("Exchange Platform Operator approved of user: " + usersToBeApproved.get(menuInput).getUsername() + ".");
                                        usersToBeApproved.remove(usersToBeApproved.get(menuInput));
                                    }
                                    break;
                                case 3:
                                    System.out.println("We hope to see you again soon");
                                    mc.writeToLog("Exchange Platform Operator logged out");
                                    exitTraderMenu = true;
                                    break;
                                case 0:
                                    System.out.println("We hope to see you again soon");
                                    mc.writeToLog("Exchange Platform Operator exited application\n----------------------------------------------------------------------------------------");
                                    System.exit(0);
                                    break;
                            }
                        }
                    }
                    else{
                        System.out.println("Invalid Credentials");
                        mc.writeToLog("Attempt login with credentials:");
                        mc.writeToLog("Username: " + username);
                        mc.writeToLog("Password" + password);
                        System.out.println("Press enter to continue");
                        input.nextLine();
                    }
                }
                else if(!currentUser.getType()){
                    mc.writeToLog("Trader " + username + " successfully logged in");
                    //Trader Menu
                    boolean exitTraderMenu = false;
                    while (!exitTraderMenu){
                        mc.flush();
                        System.out.println("Hello " + currentUser.getUsername() + ", please select the functionality which you would you like to use");
                        menuInput = mc.menuInput(new String[]{"Add funds", "View Order Book", "View your current securities", "Submit a buy order", "Submit a sell order", "View your current orders", "Log Out", "Exit"});
                        mc.flush();
                        switch (menuInput) {
                            case 1:
                                mc.addFunds(currentUser);
                                break;
                            case 2:
                                mc.writeToLog("User: " + username + " viewed the order book");
                                System.out.println("    Order Book");
                                mc.printOrderBook(buyOrderList, sellOrderList);
                                System.out.println("Press enter to continue");
                                input.nextLine();
                                break;
                            case 3:
                                mc.writeToLog("User: " + username + " viewed his/her securities");
                                mc.printUserSecurities(currentUser.getSecurities());
                                System.out.println("Press enter to continue");
                                input.nextLine();
                                break;
                            case 4:
                                System.out.println("Select, by index, which security you would like to put a buy order on:");
                                mc.printOrders(sellOrderList);
                                System.out.println("0) Exit");
                                int indexChoice = mc.getInt(0, sellOrderList.size()) - 1;
                                if(indexChoice == -1){
                                    continue;
                                }
                                System.out.print("Your Price: €");
                                double price = mc.getDouble(true);
                                System.out.println("How much of " + sellOrderList.get(indexChoice).getSecurity().getName() + " would you like to put a buy order on?");
                                long purchaseAmount = mc.getLong(true);
                                boolean orderComplete = false;
                                double total = price * purchaseAmount;
                                while(!orderComplete){
                                    System.out.println("Your current balance: €" + currentUser.getMoney());
                                    System.out.println("Amount of buy order: €" + total);
                                    if(total <= currentUser.getMoney()){
                                        System.out.println("Would you like to complete the transaction?");
                                        System.out.print("Y/N:");
                                        String confirmation = mc.getString(new String[]{"y", "n", "yes", "no"});
                                        if(confirmation.equals("y") || confirmation.equals("yes")){
                                            currentUser.setMoney(currentUser.getMoney() - total);
                                            System.out.println("Your buy order has been successfully added");
                                            //Add order
                                            Order newOrder = new Order(sellOrderList.get(indexChoice).getSecurity(), price, purchaseAmount, username, LocalDateTime.now());
                                            mc.writeToLog("Trader " + username + " added a new buy order for security " + sellOrderList.get(indexChoice).getSecurity().getName() + " with price " + price + " and quantity" + purchaseAmount);
                                            buyOrderList.add(newOrder);
                                            int result = mc.matchingEngine(false, buyOrderList, sellOrderList, userList);
                                            if(result == 0){
                                                System.out.println("Some of your buy order matched with a sell order(s), the matching engine will continue to try and complete your order");
                                                mc.askForPrintUserSecurities(currentUser);
                                            }
                                            else if(result < 0){
                                                System.out.println("No sell order matched your buy order, but the matching engine will continue to try and complete your order");
                                            }
                                            else{
                                                System.out.println("Your buy order found a match (or multiple matches) and fully went through!");
                                                mc.askForPrintUserSecurities(currentUser);
                                            }
                                        }
                                        orderComplete = true;
                                    }
                                    //If not enough money
                                    else{
                                        System.out.println("You don't have enough funds in your wallet, would you like to add more?");
                                        System.out.print("Y/N:");
                                        String confirmation = mc.getString(new String[]{"y", "n", "yes", "no"});
                                        if(confirmation.equals("y") || confirmation.equals("yes")){
                                            mc.addFunds(currentUser);
                                        }
                                        else{
                                            orderComplete = true;
                                        }
                                    }
                                }
                                break;
                            case 5:
                                System.out.println("Select, by index, which security you would like to make a sell order off:");
                                mc.printUserSecurities(currentUser.getSecurities());
                                System.out.println("0)Exit");
                                int securityChoice = mc.getInt(0, currentUser.getSecurities().size()) - 1;
                                if(securityChoice == -1){
                                    continue;
                                }
                                System.out.print("Selling Price: €");
                                double sellPrice = mc.getDouble(true);
                                System.out.println("How much of " + currentUser.getSecurities().get(securityChoice).getName() + " would you like to sell?");
                                long sellAmount = mc.getLong(true, 0, currentUser.getSecurities().get(securityChoice).getSupply());
                                System.out.println("Selling " + sellAmount + "of " + currentUser.getSecurities().get(securityChoice).getName());
                                System.out.print("Proceed (Y/N):");
                                String confirmation = mc.getString(new String[]{"y", "n", "yes", "no"});
                                if(confirmation.equals("y") || confirmation.equals("yes")){
                                    String name = currentUser.getSecurities().get(securityChoice).getName();
                                    currentUser.getSecurities().get(securityChoice).setSupply(currentUser.getSecurities().get(securityChoice).getSupply() - sellAmount);
                                    Order sellorder = new Order(currentUser.getSecurities().get(securityChoice), sellPrice, sellAmount, currentUser.getUsername(), LocalDateTime.now());
                                    mc.writeToLog("Trader " + username + " added a new sell order for security " + currentUser.getSecurities().get(securityChoice) + " with price " + sellPrice + " and quantity" + sellAmount);
                                    sellOrderList.add(sellorder);
                                    int counter = 0;
                                    //Check if an empty one exists
                                    for(Order i : sellOrderList){
                                        if(i.getSecurity().getName().equals(name) && i.getQuantity() == 0){
                                            sellOrderList.remove(counter);
                                            break;
                                        }
                                        counter++;
                                    }
                                    int result = mc.matchingEngine(true, buyOrderList, sellOrderList, userList);
                                    System.out.println("Sell order successfully put on the market");
                                    if(result == 0){
                                        System.out.println("Some of securities where sold, the matching engine will continue to try and complete your order");
                                    }
                                    else if(result < 0){
                                        System.out.println("No buy order matched your sell order, the matching engine will continue to try and complete your order");
                                    }
                                    else{
                                        System.out.println("Your sell order found a match (or multiple matches) and fully went through!");
                                        mc.askForPrintUserSecurities(currentUser);
                                    }
                                }
                                break;
                            case 6:
                                boolean noOrder= true;
                                System.out.println("---- Your Sell Orders ----");
                                ArrayList<Order> userSellOrders = new ArrayList<>();
                                for(Order i : sellOrderList){
                                    if(i.getUser().equals(username)){
                                        userSellOrders.add(i);
                                        noOrder= false;
                                    }
                                }
                                mc.printOrders(userSellOrders);
                                System.out.println("---- Your Buy Orders ----");
                                ArrayList<Order> userBuyOrders = new ArrayList<>();
                                for(Order i: buyOrderList){
                                    if(i.getUser().equals(username)){
                                        noOrder = false;
                                        userBuyOrders.add(i);
                                    }
                                }
                                mc.printOrders(userBuyOrders, userSellOrders.size() + 1);
                                System.out.println();
                                if(!noOrder){
                                    menuInput = mc.menuInput(new String[]{"Remove an order", "Go back to menu"});
                                    if (menuInput == 1) {
                                        System.out.println("Select, by index, which order you would like to remove");
                                        menuInput = mc.getInt(0, sellOrderList.size() + buyOrderList.size());
                                        if(menuInput != 0){
                                            int orderNum = 0;
                                            if(menuInput <= userSellOrders.size()){
                                                for(Order i : sellOrderList){
                                                    if(i.equals(userSellOrders.get(menuInput - 1))){
                                                        break;
                                                    }
                                                    orderNum++;
                                                }
                                                System.out.println("Are you sure you want to remove the sell order " + sellOrderList.get(orderNum).getSecurity().getName() + "?");
                                                System.out.print("(Y/N):");
                                                confirmation = mc.getString(new String[]{"y", "n", "yes", "no"});
                                                if(confirmation.equals("y") || confirmation.equals("yes")) {
                                                    mc.writeToLog("Trader " + currentUser.getUsername() + " removed his/her order of security " + sellOrderList.get(orderNum).getSecurity().getName());
                                                    //Remove sell order
                                                    mc.removeOrder(true, sellOrderList.get(orderNum), sellOrderList, currentUser);
                                                }
                                            }
                                            else{
                                                for(Order i : buyOrderList){
                                                    if(i.equals(userBuyOrders.get(menuInput - 1))){
                                                        break;
                                                    }
                                                    orderNum++;
                                                }
                                                System.out.println("Are you sure you want to remove the buy order " + buyOrderList.get(orderNum).getSecurity().getName() + "?");
                                                System.out.print("(Y/N):");
                                                confirmation = mc.getString(new String[]{"y", "n", "yes", "no"});
                                                if(confirmation.equals("y") || confirmation.equals("yes")) {
                                                    mc.writeToLog("Trader " + currentUser.getUsername() + " removed his/her order of security " + sellOrderList.get(orderNum).getSecurity().getName());
                                                    //Remove sell order
                                                    mc.removeOrder(false, buyOrderList.get(orderNum), buyOrderList, currentUser);
                                                }
                                            }
                                        }
                                    }
                                }
                                else{
                                    System.out.println("Press enter to continue");
                                    input.nextLine();
                                }
                                userBuyOrders.clear();
                                userSellOrders.clear();
                                break;
                            case 7:
                                System.out.println("We hope to see you again soon");
                                mc.writeToLog("User " + username + " logged out");
                                exitTraderMenu = true;
                                break;
                            case 0:
                                System.out.println("We hope to see you again soon");
                                mc.writeToLog("User " + username + " exited application\n----------------------------------------------------------------------------------------");
                                System.exit(0);
                                break;
                        }
                    }

                }
                else {
                    // Lister Menu
                    boolean exitListerMenu = false;
                    while (!exitListerMenu){
                        mc.flush();
                        System.out.println("Hello " + currentUser.getUsername() + ", please select the functionality which you would you like to use.");
                        menuInput = mc.menuInput(new String[]{"View Order Book", "List a new security", "View all your listings", "Log Out", "Exit"});
                        mc.flush();
                        switch (menuInput) {
                            case 1:
                                System.out.println("---- Current available securities ----");
                                mc.printOrderBook(buyOrderList, sellOrderList);
                                System.out.println("Press enter to continue");
                                input.nextLine();
                                mc.writeToLog("User: " + username + " viewed the order book");
                                break;
                            case 2:
                                mc.flush();
                                System.out.println("---- Listing a new security ----");
                                //N.B Different listing can be listed with the same name, in-fact they can be listed with all the same properties
                                System.out.print("Name of Security:");
                                String name = mc.getString();
                                System.out.print("Description of " + name + ":");
                                String description = mc.getString();
                                System.out.print("Price of " + name + ": €");
                                double price = Math.round(mc.getDouble(true) * 100.0) / 100.0;
                                System.out.print("Amount of " + name + ":");
                                long amount = mc.getLong(true);
                                Security currentSecurity = new Security(name, description, price, amount);
                                Order currentOrder = new Order(currentSecurity, price,amount,currentUser.getUsername(), LocalDateTime.now());
                                mc.writeToLog("User: " + username + " listed a new Security");
                                sellOrderList.add(currentOrder);
                                mc.writeToLog("Security " + name + " added, with price: " + price + " and quantity " + amount);
                                break;
                            case 3:
                                System.out.println("Viewing user's listing");
                                System.out.println("---- Your Listings ----");
                                boolean noOrder= true;
                                ArrayList<Order> userOrders = new ArrayList<>();
                                for(Order i : sellOrderList){
                                    if(i.getUser().equals(username)){
                                        userOrders.add(i);
                                        noOrder= false;
                                    }
                                }
                                mc.printOrders(userOrders);
                                System.out.println();
                                if(!noOrder){
                                    menuInput = mc.menuInput(new String[]{"Remove a listing", "Go back to menu"});
                                    if(menuInput == 1){
                                        System.out.println("Select, by index, which listing you would like to remove");
                                        menuInput = mc.getInt(0, userList.size() + 1);
                                        if(menuInput != 0){
                                            //Remove listing
                                            System.out.println("Are you sure you want to remove the " + sellOrderList.get(menuInput - 1).getSecurity().getName() + " listing?");
                                            System.out.print("Y/N:");
                                            String confirmation = mc.getString(new String[]{"y", "n", "yes", "no"});
                                            if(confirmation.equals("y") || confirmation.equals("yes")) {
                                                mc.writeToLog("Lister " + username + " removed his/her listing of security " + sellOrderList.get(menuInput - 1).getSecurity().getName());
                                                mc.removeOrder(true, sellOrderList.get(menuInput - 1), sellOrderList, currentUser);
                                                System.out.println("Listing successfully removed");
                                            }
                                        }
                                    }
                                }
                                else{
                                    System.out.println("Press enter to continue");
                                    input.nextLine();
                                }
                                break;
                            case 4:
                                System.out.println("We hope to see you again soon");
                                mc.writeToLog("User " + username + " logged out");
                                exitListerMenu = true;
                                break;
                            case 0:
                                System.out.println("We hope to see you again soon");
                                mc.writeToLog("User " + username + " exited application\n----------------------------------------------------------------------------------------");
                                System.exit(0);
                                break;
                        }
                    }
                }
            }else if (menuInput == 2) {

                //Sign in menu
                mc.flush();
                System.out.println("---- Signing Up ----");
                String username = null;
                boolean enteredUsername = false;
                while (!enteredUsername) {
                    System.out.print("Username:");
                    username = mc.getString();
                    enteredUsername = true;
                    //Admin check
                    if(username.equals("Operator")){
                        System.out.println("Username already taken");
                        enteredUsername = false;
                    }
                    //Check if username is already taken
                    for(User i : userList) {
                        if (i.getUsername().equals(username)) {
                            System.out.println("Username already taken");
                            enteredUsername = false;
                        }
                    }
                }

                boolean confirmedPassword = false;
                char[] tempPassword;
                String password = "";
                String confirmation;

                while (!confirmedPassword) {
                    System.out.print("Password:");


                    //Get input from console
                    for(;;){
                        //password = String.valueOf(console.readPassword());
                        password = mc.getString();
                        if(password.isEmpty()){
                            System.out.println("Please enter a valid input");
                        }
                        else{
                            break;
                        }
                    }

                    //password = mc.getString();

                    System.out.print("Confirm Password:");
                    for(;;){
                        //confirmation = String.valueOf(console.readPassword());
                        confirmation = mc.getString();
                        if(confirmation.isEmpty()){
                            System.out.println("Please enter a valid input");
                        }
                        else{
                            break;
                        }
                    }

                    //confirmation = mc.getString();
                    if (password.equals(confirmation)) {
                        confirmedPassword = true;
                    } else {
                        System.out.println("Passwords do not match");
                    }
                }
                boolean enteredType = false;
                boolean type = false;
                System.out.println("Would you like to sigh up as a:");
                while(!enteredType){
                    menuInput = mc.menuInput(new String[]{"Trader", "Lister", "Learn the difference"});
                    if (menuInput == 1) {
                        enteredType = true;
                    } else if (menuInput == 2) {
                        type = true;
                        enteredType = true;
                    } else if (menuInput == 0) {
                        System.out.println("A Trader can place buy or sell orders on securities");
                        System.out.println("A Lister can list his own securities for traders put buy/sell orders on");
                        System.out.println("Press enter to continue");
                        input.nextLine();
                    }
                }
                User newUser = new User(username, password, type, 0, new ArrayList<Security>());
                usersToBeApproved.add(newUser);

                System.out.print("User: " + username + " of type ");
                if (!type) {
                    System.out.println("trader successfully created.");
                    mc.writeToLog("User " + username + " of type trader created");
                } else {
                    System.out.println("lister successfully created.");
                    mc.writeToLog("User " + username + " of lister trader created");
                }
                System.out.println("If you attempt to login and the program does not permit it, you will need to wait for our team to approve of your account.");
                System.out.println("It is important that you remember your password and that you do not share it!");

                System.out.println("Press enter to continue");
                input.nextLine();
            } else if (menuInput == 0) {
                System.out.println("We hope to see you again soon");
                mc.writeToLog("Application exited\n----------------------------------------------------------------------------------------");
                System.exit(0);
            } else {
                System.out.println("Please enter a valid number");
            }
        }
    }
}
