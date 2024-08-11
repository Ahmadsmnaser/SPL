#include "../include/Customer.h"
#include <algorithm>

Customer::Customer(int id, const string &name, int locationDistance, int maxOrders) : id(id), name(name), locationDistance(locationDistance), maxOrders(maxOrders), ordersId(), numofOrders(0), type() {}
Customer::Customer(const Customer &other) : id(other.id), name(other.name), locationDistance(other.locationDistance), maxOrders(other.maxOrders), ordersId(), numofOrders(other.numofOrders), type(other.type)
{
    size_t size1 = other.ordersId.size();
    for (size_t i = 0; i < size1; i = i + 1)
    {
        int insert = other.ordersId[i];
        this->ordersId.push_back(insert);
    }
}

Customer::~Customer() {}
const string &Customer::getName() const
{
    return name;
}

int Customer::getId() const
{
    return id;
}

int Customer::getCustomerDistance() const
{
    return locationDistance;
}

int Customer::getMaxOrders() const
{
    return maxOrders;
}

int Customer::getNumOrders() const
{
    return numofOrders;
}

bool Customer::canMakeOrder() const
{
    return maxOrders > numofOrders;
}

const vector<int> &Customer::getOrders() const
{
    return ordersId;
}

int Customer::addOrder(int orderId)
{

    if (canMakeOrder())
    {
        auto it = std::find(ordersId.begin(), ordersId.end(), orderId);
        if (it == ordersId.end())
        {
            ordersId.push_back(orderId);
            numofOrders++;
            return orderId;
        }
    }
    return -1;
}
void Customer::DecresenumOfOrder()
{
    numofOrders++;
}
void Customer::setType(CustomerType toset){type = toset;}
CustomerType Customer::getType(){return type;}
//--------------------------------------------------------------------------------------------
// SoldierCustomer

SoldierCustomer::SoldierCustomer(int id, const string &name, int locationDistance, int maxOrders) : Customer(id, name, locationDistance, maxOrders), customerType(1)
{
    this->setType(CustomerType::SoldierCustomer);
}
SoldierCustomer::SoldierCustomer(const SoldierCustomer &other) : Customer(other) , customerType(other.customerType)
{
    this->setType(CustomerType::SoldierCustomer);
}

SoldierCustomer::~SoldierCustomer() {}
SoldierCustomer *SoldierCustomer::clone() const
{
    return new SoldierCustomer(*this);
}

//--------------------------------------------------------------------------------------------
// CivilianCustomer
CivilianCustomer::CivilianCustomer(int id, const string &name, int locationDistance, int maxOrders) : Customer(id, name, locationDistance, maxOrders), customerType(2)
{
    this->setType(CustomerType::CivilianCustomer);
}
CivilianCustomer::CivilianCustomer(const CivilianCustomer &other): Customer(other) , customerType(other.customerType)
{
    this->setType(CustomerType::CivilianCustomer);
}

CivilianCustomer::~CivilianCustomer() {}
CivilianCustomer *CivilianCustomer::clone() const
{
    return new CivilianCustomer(*this);
}
