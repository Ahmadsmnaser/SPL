#pragma once
#include <string>
#include <vector>
#include <iostream>
#include "WareHouse.h"
using std::string;
using std::vector;

enum class ActionStatus
{
    COMPLETED,
    ERROR
};
enum class ActionType
{
    simulatestep ,addOrder ,addCustomer,printOrderStatus,printCustomerStatus,
    printVolunteerStatus,printActionsLog,close,backupWareHouse,restoreWareHouse
};

class BaseAction
{
public:
    BaseAction();
    BaseAction(const BaseAction &other);
    virtual ~BaseAction() = 0;
    ActionStatus getStatus() const;
    ActionType getType() const;
    void SetType(ActionType toset);
    virtual void act(WareHouse &wareHouse) = 0;
    virtual string toString() const = 0;
    virtual BaseAction *clone() const = 0;
    
    CustomerType convertTOCustomerType(const string &st);
    string CustomerTypetoString(const CustomerType type)const;


protected:
    void complete();
    void error(string errorMsg);
    string getErrorMsg() const;

private:
    string errorMsg;
    ActionStatus status;
    ActionType type;
};

class SimulateStep : public BaseAction
{

public:
    SimulateStep(int numOfSteps);
    SimulateStep(const SimulateStep &other);
    void act(WareHouse &wareHouse) override;
    std::string toString() const override;
    SimulateStep *clone() const override;
    ~SimulateStep() override;

private:
    const int numOfSteps;
};

class AddOrder : public BaseAction
{
public:
    AddOrder(int id);
    AddOrder(const AddOrder &other);
    void act(WareHouse &wareHouse) override;
    string toString() const override;
    AddOrder *clone() const override;
    ~AddOrder() override;

private:
    const int customerId;
};

class AddCustomer : public BaseAction
{
public:
    AddCustomer(string customerName, string customerType, int distance, int maxOrders);
    AddCustomer(const AddCustomer &other);
    void act(WareHouse &wareHouse) override;
    AddCustomer *clone() const override;
    string toString() const override;
    ~AddCustomer() override;

private:
    const string customerName;
    const CustomerType customerType;
    const int distance;
    const int maxOrders;
};

class PrintOrderStatus : public BaseAction
{
public:
    PrintOrderStatus(int id);
    PrintOrderStatus(const PrintOrderStatus &other);
    void act(WareHouse &wareHouse) override;
    PrintOrderStatus *clone() const override;
    string toString() const override;
    ~PrintOrderStatus() override;

private:
    const int orderId;
};

class PrintCustomerStatus : public BaseAction
{
public:
    PrintCustomerStatus(int customerId);
    PrintCustomerStatus(const PrintCustomerStatus &other);
    void act(WareHouse &wareHouse) override;
    PrintCustomerStatus *clone() const override;
    string toString() const override;
    ~PrintCustomerStatus() override;

private:
    const int customerId;
};

class PrintVolunteerStatus : public BaseAction
{
public:
    PrintVolunteerStatus(int id);
    PrintVolunteerStatus(const PrintVolunteerStatus &other);
    void act(WareHouse &wareHouse) override;
    PrintVolunteerStatus *clone() const override;
    string toString() const override;
    ~PrintVolunteerStatus() override;

private:
    const int VolunteerId;
};

class PrintActionsLog : public BaseAction
{
public:
    PrintActionsLog();
    PrintActionsLog(const PrintActionsLog &other);
    void act(WareHouse &wareHouse) override;
    PrintActionsLog *clone() const override;
    string toString() const override;
    ~PrintActionsLog() override;

private:
};

class Close : public BaseAction
{
public:
    Close();
    Close(const Close &other);
    void act(WareHouse &wareHouse) override;
    Close *clone() const override;
    string toString() const override;
    ~Close() override;

private:
};

class BackupWareHouse : public BaseAction
{
public:
    BackupWareHouse();
    BackupWareHouse(const BackupWareHouse &other);
    void act(WareHouse &wareHouse) override;
    BackupWareHouse *clone() const override;
    string toString() const override;
    ~BackupWareHouse() override;

private:
};

class RestoreWareHouse : public BaseAction
{
public:
    RestoreWareHouse();
    RestoreWareHouse(const RestoreWareHouse &other);
    void act(WareHouse &wareHouse) override;
    RestoreWareHouse *clone() const override;
    string toString() const override;
    ~RestoreWareHouse() override;

private:
};