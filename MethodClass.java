//package com.company;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class MethodClass {
    Scanner input = new Scanner(System.in);
    int logIndex = 0;


    //Used to log events
    public void writeToLog(String text) throws IOException {
        File log = new File("log.txt");
        FileWriter fileWriter = new FileWriter(log, true);
        try (fileWriter; BufferedWriter write = new BufferedWriter(fileWriter)) {
            if (!log.exists()) {
                log.createNewFile();
                writeToLog("--------------------------------------------------------------------------------");
                writeToLog("No log.txt found, created a new one @" + LocalDateTime.now().toString());
                writeToLog("--------------------------------------------------------------------------------");
            }
            if (logIndex == 0) {
                write.write(text);
            } else {
                write.write(logIndex + ") " + text + "    (@" + LocalDateTime.now().toString() + ")");
            }
            write.write("\n");
            logIndex++;

        } catch (IOException ignored) {
        }
    }

    //Matching Engine
    public int matchingEngine(boolean sellOrder, ArrayList<Order> buyOrderList, ArrayList<Order> sellOrderList, ArrayList<User> userList) throws IOException {
        //Variable Deceleration
        int bestPriceIndex = -2;
        User theBuyer = null;
        if(!sellOrder){
            //Handle a buy order
            Order current = buyOrderList.get(buyOrderList.size() - 1);
            for(;;){
                double bestPrice = current.getPrice();
                boolean first = true;
                String currentName = current.getSecurity().getName();
                int counter = 0;
                //Finding cheapest and least recent order (since system is first come first served)
                for (Order i : sellOrderList) {
                    if (i.getSecurity().getName().equals(currentName) && ((i.getPrice() <= bestPrice && first) || (i.getPrice() < bestPrice && !first)) && i.getQuantity() != 0) {
                        first = false;
                        bestPrice = i.getPrice();
                        bestPriceIndex = counter;
                    }
                    counter++;
                }
                //Check if any sell order matches buy order
                if (bestPriceIndex == -2) {
                    //If not return a negative number
                    writeToLog("No match found for the buy order");
                    return -1;
                }

                if(bestPriceIndex == -1){
                    //If partial match return 0
                    return 0;
                }

                writeToLog("Order matched with " + sellOrderList.get(bestPriceIndex).getUser() + "'s sell order");
                //If match, first remove amount of sell order
                String seller = sellOrderList.get(bestPriceIndex).getUser();
                long amountLeft = sellOrderList.get(bestPriceIndex).getQuantity() - current.getQuantity();
                long amountBought;
                if(amountLeft <= 0){
                    //If sell order is now empty
                    amountBought = sellOrderList.get(bestPriceIndex).getQuantity();
                    sellOrderList.get(bestPriceIndex).setQuantity(0);
                    String currentSecurity = sellOrderList.get(bestPriceIndex).getSecurity().getName();
                    boolean currentFirst = true;
                    int currentCounter = 0;
                    for(Order i : sellOrderList){
                        if(i.getSecurity().getName().equals(currentSecurity) && i.getQuantity() == 0){
                           if(!currentFirst){
                               sellOrderList.remove(currentCounter);
                               break;
                           }
                           currentFirst = false;
                        }
                        currentCounter++;
                    }
                    //sellOrderList.remove(bestPriceIndex);
                }
                else{
                    //else just remove amount bought
                    amountBought = current.getQuantity();
                    sellOrderList.get(bestPriceIndex).setQuantity(amountLeft);
                }

                //Give money to seller
                for (User i : userList) {
                    if (seller.equals(i.getUsername())) {
                        i.setMoney(i.getMoney() + (bestPrice * amountBought));
                        break;
                    }
                }

                //Remove money from buy order
                current.setTotalPrice(current.getTotalPrice() - (bestPrice * amountBought));


                //Add security to user and give change
                String buyer = current.getUser();

                for(User i : userList){
                    if(buyer.equals(i.getUsername())){
                        Security currentSecurity = current.getSecurity();
                        i.getSecurities().add(new Security(currentSecurity.getName(), currentSecurity.getDescription(), currentSecurity.getPrice(), amountBought));
                        i.setSecurities(i.getSecurities());
                        theBuyer = i;
                        break;
                    }
                }

                current.setQuantity(current.getQuantity() - amountBought);
                if (current.getQuantity() <= 0) {
                    theBuyer.setMoney(theBuyer.getMoney() + current.getTotalPrice());
                    buyOrderList.remove(buyOrderList.size() - 1);
                    return 1;
                } else {
                    bestPriceIndex = -1;
                }
            }

        }
        else{
            //Handle a sell order
            Order current = sellOrderList.get(sellOrderList.size() - 1);
            for(;;){
                String currentName = current.getSecurity().getName();
                int counter = 0;
                //Finding least recent buy order (since system is first come first served)
                for(Order i : buyOrderList){
                    Security currentSecurity = i.getSecurity();
                    if(currentSecurity.getName().equals(currentName) && current.getPrice() <= i.getPrice()){
                        bestPriceIndex = counter;
                        break;
                    }
                    counter++;
                }
                //Check if any buy order matches sell order
                if(bestPriceIndex == -2){
                    //If not return a negative number
                    writeToLog("No match found for sell order");
                    return -1;
                }

                if(bestPriceIndex == -1){
                    //If partial match return 0
                    return 0;
                }

                writeToLog("Order matched with " + buyOrderList.get(bestPriceIndex).getUser() + "'s buy order");

                //If match, first remove amount of buy order
                String buyer;
                long amountLeft = buyOrderList.get(bestPriceIndex).getQuantity() - current.getQuantity();
                long amountSold;
                buyer = buyOrderList.get(bestPriceIndex).getUser();
                if(amountLeft <= 0){
                    //If buy order is empty, remove it
                    amountSold = sellOrderList.get(bestPriceIndex).getQuantity();
                    buyOrderList.remove(bestPriceIndex);
                }
                else{
                    //else remove amount sold
                    amountSold = current.getQuantity();
                    buyOrderList.get(bestPriceIndex).setQuantity(amountLeft);
                }

                //Pay seller
                String seller = current.getUser();
                for (User i : userList) {
                    if (seller.equals(i.getUsername())) {
                        i.setMoney(i.getMoney() + (current.getPrice() * amountSold));
                        break;
                    }
                }

                //Remove money from sell order
                current.setTotalPrice(current.getTotalPrice() - (current.getPrice() * amountSold));

                //Add security to buyer and give change
                for(User i: userList){
                    if(buyer.equals((i.getUsername()))){
                        Security currentSecurity = current.getSecurity();
                        i.getSecurities().add(new Security(currentSecurity.getName(), currentSecurity.getDescription(), currentSecurity.getPrice(), amountSold));
                        i.setSecurities(i.getSecurities());
                        theBuyer = i;
                        break;
                    }
                }
                current.setQuantity(current.getQuantity() - amountSold);
                if(current.getQuantity() <= 0){
                    theBuyer.setMoney(theBuyer.getMoney() + current.getTotalPrice());
                    sellOrderList.remove(buyOrderList.size() - 1);
                    return 1;
                }
                else{
                    bestPriceIndex = -1;
                }
            }
        }
    }

    //Flushes console
    public void flush(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //Remove Order
    public void removeOrder(boolean isSellOrder, Order currentOrder, ArrayList<Order> OrderList, User currentUser){
        //If buy order
        if(!isSellOrder){
            //Give money back to user
            currentUser.setMoney(currentUser.getMoney() + currentOrder.getTotalPrice());
        }
        // else if sell order
        else{
            //Give security back to user
            boolean ownSecurity = false;
            for(Security i : currentUser.getSecurities()){
                if(i.equals(currentOrder.getSecurity())){
                    i.setSupply(i.getSupply() + currentOrder.getQuantity());
                    ownSecurity = true;
                }
            }
            if(!ownSecurity){
                currentUser.getSecurities().add(currentOrder.getSecurity());
                currentUser.setSecurities(currentUser.getSecurities());
            }

        }
        //Remove order
        OrderList.remove(currentOrder);
    }

    public void removeBuyOrSellOrder(ArrayList<Order> sellOrderList, ArrayList<Order> buyOrderList, User currentUser) throws IOException {
        System.out.println("Select, by index, which order you would like to remove");
        int menuInput = getInt(0, sellOrderList.size() + buyOrderList.size());
        if (menuInput != 0) {
            if(menuInput <= sellOrderList.size()){
                System.out.println("Are you sure you want to remove the sell order " + sellOrderList.get(menuInput - 1).getSecurity().getName() + "?");
                removeUserOrder(sellOrderList, menuInput, sellOrderList, currentUser, true);
            }
            else{
                System.out.println("Are you sure you want to remove the buy order " + buyOrderList.get(menuInput - 1).getSecurity().getName() + "?");
                removeUserOrder(buyOrderList, menuInput, buyOrderList, currentUser, false);
            }
        }
    }

    public void removeUserOrder(ArrayList<Order> userOrders, int menuInput, ArrayList<Order> orderList, User currentUser, boolean isSellOrder) throws IOException {
        System.out.print("(Y/N):");
        String confirmation = getString(new String[]{"y", "n", "yes", "no"});
        if(confirmation.equals("y") || confirmation.equals("yes")) {
            writeToLog("Trader " + currentUser.getUsername() + " removed his/her order of security " + orderList.get(menuInput - 1).getSecurity().getName());
            //Remove sell order
            removeOrder(isSellOrder, userOrders.get(menuInput - 1), orderList, currentUser);
            System.out.println("Order successfully removed");
        }
    }

    //Asks user if he/she would like to view his/her securities
    public void askForPrintUserSecurities(User currentUser){
        System.out.println("Would you like to view your current security library");
        System.out.print("Y/N:");
        String confirmation = getString(new String[]{"y", "n", "yes", "no"});
        if(confirmation.equals("y") || confirmation.equals("yes")){
            printUserSecurities(currentUser.getSecurities());
            System.out.println("Press enter to continue");
            input.nextLine();
        }
    }

    //Prints current available securities
    public void printUserSecurities(ArrayList<Security> securitiesList){
        System.out.println("    Your current securities");
        System.out.format("%-15s%30s%30s%30s%30s", "Index", "Name", "Description", "Quantity", "Lister's Price\n");
        for(int i = 0; i < securitiesList.size(); i++){
            System.out.format("%-15s%30s%30s%30s%30s", i+1, securitiesList.get(i).getName(), securitiesList.get(i).getDescription(), securitiesList.get(i).getSupply(), "€" + securitiesList.get(i).getPrice() +"\n");
        }
        System.out.println();
    }

    //Prints Order
    public void printOrders(ArrayList<Order> OrderList){
        System.out.format("%-15s%30s%30s%30s%30s%30s", "Index", "Security Name", "Quantity", "Price of seller", "Seller", "Time Listed\n");
        for(int i = 0; i < OrderList.size(); i++){
            System.out.format("%-15s%30s%30s%30s%30s%30s", i+1, OrderList.get(i).getSecurity().getName(), OrderList.get(i).getQuantity(), "€" + OrderList.get(i).getPrice(), OrderList.get(i).getUser(), OrderList.get(i).getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "\n");
        }
        System.out.println();
    }

    //Overloading printOrders method, for those instances where the start index needs to be specified
    public void printOrders(ArrayList<Order> OrderList, int index){
        System.out.format("%-15s%30s%30s%30s%30s%30s", "Index", "Security Name", "Quantity", "Price of seller", "Seller", "Time Listed\n");
        for (Order order : OrderList) {
            System.out.format("%-15s%30s%30s%30s%30s%30s", index, order.getSecurity().getName(), order.getQuantity(), "€" + order.getPrice(), order.getUser(), order.getTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "\n");
            index++;
        }
    }

    //Prints Order Book
    public void printOrderBook(ArrayList<Order> buyOrderList, ArrayList<Order> sellOrderList){
        System.out.println("---- Sell Orders ----");
        printOrders(sellOrderList);
        System.out.println("---- Buy Orders ----");
        printOrders(buyOrderList);
        System.out.println();
    }

    //Adds funds
    public void addFunds(User currentUser) throws IOException {
        System.out.println("You currently have a balance of: €" + currentUser.getMoney());
        System.out.print("How much cash would you like to add to your account: €");
        double money = Math.round(getDouble(true) * 100.0) /100.0;
        currentUser.setMoney(currentUser.getMoney() + money);
        writeToLog("User " + currentUser.getUsername() + " added " + money + " to his/her balance");
    }

    //Validates char input
    public char getChar(char[] possibleOptions){
        for(;;){
            String entered = input.nextLine();
            if(entered == null || entered.length() != 1){
                char enteredChar = entered.charAt(0);
                if(Collections.singletonList(possibleOptions).contains(enteredChar)){
                    return enteredChar;
                }
                else{
                    System.out.println("Please enter a valid input");
                }
            }
            else{
                System.out.println("Please enter a valid input");
            }
        }
    }

    //Validates string input
    public String getString(){
        for(;;){
            String entered = input.nextLine();
            if(entered == null || entered.isEmpty()){
                System.out.println("Please enter a valid input");
            }
            else{
                return entered;
            }
        }
    }
    //Overloading getString method, for those instances where only some inputs are valid
    public String getString(String[] options){
        for(;;){
            String entered = input.nextLine();
            if(entered == null || !Arrays.asList(options).contains(entered.toLowerCase())){
                System.out.println("Please enter a valid input");
            }
            else{
                return entered.toLowerCase();
            }
        }
    }

    //Validates double input
    public double getDouble(boolean notNegative){
        boolean isDouble = false;
        for(;;){
            boolean correct = true;
            String entered = input.nextLine();
            try{
                double num = Double.parseDouble(entered);
                if(notNegative && num < 0){
                    System.out.println("Please enter a positive number");
                    correct = false;
                }
                if(correct){
                    return num;
                }
            }
            catch (NumberFormatException e){
                System.out.println("Please enter a valid number");
            }
        }
    }

    //Validates integer input
    public int getInt(boolean notNegative){
        for(;;){
            String entered = input.nextLine();
            try{
                boolean correct = true;
                int num = Integer.parseInt(entered);
                if(notNegative && num < 0){
                    System.out.println("Please enter a positive integer");
                    correct = false;
                }
                if(correct){
                    return num;
                }
            }
            catch (NumberFormatException e){
                System.out.println("Please enter a valid integer");
            }
        }
    }
    //Overloading getInt method, for those instances where only a range of inputs are valid
    public int getInt(int start, int end){
        for(;;){
            int entered;
            if(start >= 0){
                 entered = getInt(true);
            }
            else{
                entered = getInt(false);
            }

            if(entered < start || end < entered){
                System.out.println("Please enter a valid input");
            }
            else{
                return entered;
            }
        }
    }


    //Overloading getLong method, for those instances where only a range of inputs are valid
    public long getLong(boolean notNegative, long start, long end){
        for(;;){
            long entered = getLong(notNegative);
            if(entered < start || end < entered){
                System.out.println("Please enter a valid input");
            }
            else{
                return entered;
            }
        }
    }


    public long getLong(boolean notNegative){
        boolean isDouble = false;
        for(;;){
            String entered = input.nextLine();
            try{
                boolean correct = true;
                long num = Integer.parseInt(entered);
                if(notNegative && num < 0){
                    System.out.println("Please enter a positive integer");
                    correct = false;
                }
                if(correct){
                    return num;
                }
            }
            catch (NumberFormatException e){
                System.out.println("Please enter a valid integer");
            }
        }
    }

    //Automatically prints a menu
    public int menuInput(String[] options){

        for(int i = 0; i < options.length; i++){
            if(i == options.length - 1){
                System.out.println("0) " + options[i]);
            }
            else {
                System.out.println(i+1 + ") " + options[i]);
            }
        }
        for(;;){
            String entered = input.nextLine();
            for(int i = 0; i < options.length; i++){
                if(entered.equals(String.valueOf(i))){
                    return i;
                }
                else if(((entered.toLowerCase()).trim()).equals(options[i].toLowerCase())){
                    if(i+1 == options.length){
                        return 0;
                    }
                    return i+1;
                }
            }
            System.out.println("Invalid Input, please enter the option or the number of the option you wish to select");
        }
    }
}
