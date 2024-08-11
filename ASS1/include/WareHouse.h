#pragma once
#include <string>
#include <vector>
#include <fstream>
#include <iostream>
#include "queue"

#include "Order.h"
#include "Customer.h"
#include "Volunteer.h"

class BaseAction;
class Volunteer;

// Warehouse responsible for Volunteers, Customers Actions, and Orders.

class WareHouse
{

public:
    WareHouse(const string &configFilePath);
    ~WareHouse();
    WareHouse &operator=(const WareHouse &other);
    WareHouse(const WareHouse &other);
    WareHouse(WareHouse &&other);
    WareHouse &operator=(WareHouse &&other);
    void start();
    void addOrder(Order *order);
    void addAction(BaseAction *action);
    void PerformSimulateStep(int numOfSteps);
    Customer &getCustomer(int customerId) const;
    Volunteer &getVolunteer(int volunteerId) const;
    Order &getOrder(int orderId) const;
    const vector<BaseAction *> &getActions() const;
    int getorderCounter();
    int getCustomerCounter();
    void add1CustomerCounter();
    void addCustomer(Customer *customer);
    void close();
    void open();
    void clear();
    void processPendingOrders();
    void processFinishedVolunteers();
    void processVolunteerOrders(int &counter, OrderStatus fromStatus, OrderStatus toStatus, int &volunteerCounter);
    WareHouse *Copy() const;
    void copy(const WareHouse &other);
    const bool getisOpen() const;

private:
    bool isOpen;
    vector<BaseAction *> actionsLog;
    vector<Volunteer *> volunteers;
    vector<Order *> pendingOrders;
    vector<Order *> inProcessOrders;
    vector<Order *> completedOrders;
    vector<Customer *> customers;
    //int customerCounter;  // For assigning unique customer IDs
    //int volunteerCounter; // For assigning unique volunteer IDs
    //int orderCounter;     // For assigning uniue order IDs
    void performClose();
    // void performSimulateStep(int numOfSteps);
    void performSimulateStep1(int numOfSteps);
    void performAddOrder(int id);
    void performAddCustomer(string customerName, string customerType, int distance, int maxOrders);
    void performPrintOrderStatus(int id);
    void performPrintCustomerStatus(int customerId);
    void performPrintVolunteerStatus(int id);
    void performPrintActionsLog();
    void performBackupWareHouse();
    void performRestoreWareHouse();
    void splitString(std::string s, std::queue<std::string> &queue, char c);

protected:
    int customerCounter;  // For assigning unique customer IDs
    int volunteerCounter; // For assigning unique volunteer IDs
    int orderCounter;     // For assigning uniue order IDs
    int CollectorCounter;
    int DriverCounter;
};