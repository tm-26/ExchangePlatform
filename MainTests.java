//package com.company;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    MethodClass mc = new MethodClass();
    Random r = new Random();

    @Test
    void Test_Security(){
        double testPrice = (double) r.nextInt(1000) + 1;
        long testSupply = r.nextInt(1000) + 1;

        Security testSecurity = new Security("Test name placeholder", "Test description placeholder" , testPrice, testSupply);

        assertEquals(testSecurity.getName(),"Test name placeholder");
        assertEquals(testSecurity.getDescription(),"Test description placeholder");
        assertEquals(testSecurity.getPrice(), testPrice);
        assertEquals(testSecurity.getSupply(),testSupply);

    }

    @Test
    void Test_Order(){
        double testPrice = (double) r.nextInt(1000) + 1;
        long testSupply = (long) r.nextInt(1000) + 1;
        Security testSecurity = new Security("Test name placeholder", "Test description placeholder" , testPrice, testSupply);
        LocalDateTime testTime = LocalDateTime.now();
        testPrice = (double) r.nextInt(1000) + 1;
        testSupply = (long) r.nextInt((int) testSupply) + 1;
        Order testOrder = new Order(testSecurity, testPrice, testSupply,"Test user placeholder", testTime);

        assertEquals(testOrder.getSecurity(),testSecurity);
        assertEquals(testOrder.getPrice(),testPrice);
        assertEquals(testOrder.getQuantity(),testSupply);
        assertEquals(testOrder.getUser(),"Test user placeholder");
        assertEquals(testOrder.getTime(),testTime);
    }

    @Test
    void Test_User(){
        boolean testType = r.nextBoolean();
        double testMoney = (double) r.nextInt(1000) + 1;
        ArrayList<Security> testSecurities = new ArrayList<>();
        double testPrice = (double) r.nextInt(1000) + 1;
        long testSupply = (long) r.nextInt(1000) + 1;
        Security testSecurity1 = new Security("Test name placeholder", "Test description placeholder" , testPrice, testSupply);
        testPrice = (double) r.nextInt(1000) + 1;
        testSupply = (long) r.nextInt(1000) + 1;
        Security testSecurity2 = new Security("Test name placeholder(1)", "Test description placeholder(1)" , testPrice, testSupply);
        testSecurities.add(testSecurity1);
        testSecurities.add(testSecurity2);

        User testUser =  new User("Test username placeholder", "Test password placeholder",testType, testMoney,testSecurities);

        assertEquals(testUser.getUsername(), "Test username placeholder");
        assertEquals(testUser.getPassword(), "Test password placeholder");
        assertEquals(testUser.getType(), testType);
        assertEquals(testUser.getMoney(),testMoney);
        assertEquals(testUser.getSecurities(),testSecurities);
    }

    //Testing methods in the class MethodClass

    @Test
    void Test_matchingEngine() throws IOException {
        /*So all possible outputs from the matchingEngine are:
          1) 0, When only some of the order matched.
          2) <0, When none of the order matched.
          3) 0>, When all of the order matches.
          All of these instances will be tested.
         */

        // First instance to be tested is 1), when the matching engine should return 0,
        // implying that only a part of the order has been matched.

        // First let's test this instance for a buy order.

        boolean isSellOrder = false;

        User testBuyer= new User("Test buyer placeholder", "Test password placeholder", r.nextBoolean(),  r.nextInt(1000) ,new ArrayList<>());
        Security testSecurity = new Security("Test name placeholder", "Test description placeholder", 10, 100);
        Order testBuyOrder = new Order(testSecurity, 100,10,testBuyer.getUsername(), LocalDateTime.now());
        ArrayList<Order> testBuyOrderList = new ArrayList<>();
        testBuyOrderList.add(testBuyOrder);

        User testSeller= new User("Test seller placeholder", "Test password placeholder", false,  r.nextInt(1000) ,new ArrayList<>());
        Order testSellOrder = new Order(testSecurity,11,5, testSeller.getUsername(), LocalDateTime.now());
        ArrayList<Order> testSellOrderList = new ArrayList<>();
        testSellOrderList.add(testSellOrder);

        ArrayList<User> testUserList = new ArrayList<>();
        testUserList.add(testBuyer);
        testUserList.add(testSeller);
        int testAns = mc.matchingEngine(isSellOrder, testBuyOrderList, testSellOrderList, testUserList);
        assertEquals(testAns, 0);

        // Now let's test this instance for a sell order.

        isSellOrder = true;

        Order testBuyOrder1 = new Order(testSecurity, 100,2,testBuyer.getUsername(), LocalDateTime.now());
        testBuyOrderList.add(testBuyOrder1);

        Order testSellOrder1 = new Order(testSecurity,11,5, testSeller.getUsername(), LocalDateTime.now());
        testSellOrderList.add(testSellOrder1);

        testAns = mc.matchingEngine(isSellOrder, testBuyOrderList, testSellOrderList, testUserList);
        assertEquals(testAns, 0);

        // Now testing instance 2), here the  matching engine should return -ve integer,
        // implying that none of the order is matched.

        // Testing instance for buy order.

        isSellOrder = false;

        Order testBuyOrder2 = new Order(testSecurity, 10,2,testBuyer.getUsername(), LocalDateTime.now());
        testBuyOrderList.add(testBuyOrder2);
        Order testSellOrder2 = new Order(testSecurity,100,5, testSeller.getUsername(), LocalDateTime.now());
        testSellOrderList.add(testSellOrder2);

        testAns = mc.matchingEngine(isSellOrder, testBuyOrderList, testSellOrderList, testUserList);
        assertTrue(testAns < 0);

        // Testing instance for sell order

        isSellOrder = true;

        testBuyOrder2 = new Order(testSecurity, 10,2,testBuyer.getUsername(), LocalDateTime.now());
        testBuyOrderList.add(testBuyOrder2);
        testSellOrder2 = new Order(testSecurity,100,5, testSeller.getUsername(), LocalDateTime.now());
        testSellOrderList.add(testSellOrder2);

        testAns = mc.matchingEngine(isSellOrder, testBuyOrderList, testSellOrderList, testUserList);
        assertTrue(testAns < 0);

        // Now testing instance 3), here the  matching engine should return +ve integer,
        // implying that all of the order found a match.

        // Testing instance for buy order

        isSellOrder = false;

        Order testBuyOrder3 = new Order(testSecurity, 100,2,testBuyer.getUsername(), LocalDateTime.now());
        testBuyOrderList.add(testBuyOrder3);
        Order testSellOrder3 = new Order(testSecurity,10,5, testSeller.getUsername(), LocalDateTime.now());
        testSellOrderList.add(testSellOrder3);

        testAns = mc.matchingEngine(isSellOrder, testBuyOrderList, testSellOrderList, testUserList);
        assertTrue(testAns > 0);

        isSellOrder = true;

        testBuyOrder3 = new Order(testSecurity, 100,5,testBuyer.getUsername(), LocalDateTime.now());
        testBuyOrderList.add(testBuyOrder3);
        testSellOrder3 = new Order(testSecurity,10,2, testSeller.getUsername(), LocalDateTime.now());
        testSellOrderList.add(testSellOrder3);

        testAns = mc.matchingEngine(isSellOrder, testBuyOrderList, testSellOrderList, testUserList);
        assertTrue(testAns > 0);

    }

    @Test
    void Test_removeOrder(){
        Security testSecurity = new Security("Test name placeholder", "Test description placeholder", r.nextInt(1000), r.nextInt(10000) + 1);
        User testUser= new User("Test buyer placeholder", "Test password placeholder", r.nextBoolean(),  r.nextInt(1000) ,new ArrayList<>());
        Order testOrderToBeRemoved1 = new Order(testSecurity, r.nextInt(1000),1,testUser.getUsername(), LocalDateTime.now());
        Order testOrderToBeRemoved2 = new Order(testSecurity, r.nextInt(1000),1,testUser.getUsername(), LocalDateTime.now());
        ArrayList<Order> testOrderList = new ArrayList<>();
        testOrderList.add(testOrderToBeRemoved1);
        testOrderList.add(testOrderToBeRemoved2);

        //First let's test this method by removing a buy order
        boolean isSellOrder = false;

        mc.removeOrder(isSellOrder,testOrderToBeRemoved1,testOrderList,testUser);
        assertEquals(1,testOrderList.size());

        //and then a sell order
        isSellOrder = true;

        mc.removeOrder(isSellOrder,testOrderToBeRemoved2,testOrderList,testUser);
        assertTrue(testOrderList.isEmpty());

        //Finally let's test that if removeOrder is given an order that is not there, the list is not changed.
        Order testOrderToBeRemoved3 = new Order(testSecurity, r.nextInt(1000),1,testUser.getUsername(), LocalDateTime.now());
        testOrderList.add(testOrderToBeRemoved3);
        ArrayList<Order> testOrderListCopy = testOrderList;

        mc.removeOrder(isSellOrder ,testOrderToBeRemoved1 /*Order not in testOrderList*/ , testOrderList, testUser);
        assertSame(testOrderList, testOrderListCopy);
        //Assert that no change is done to the list
    }

}
