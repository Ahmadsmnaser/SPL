#include "../include/Volunteer.h"
#define NO_ORDER -1
#define FALSE false
#include <string>
#include <vector>
#include <iostream>
#include "../include/Volunteer.h"
using std::string;
using std::vector;

// CONSTRUCTORS
Volunteer::Volunteer(int id, const string &name) : id(id), name(name), completedOrderId(NO_ORDER), activeOrderId(NO_ORDER), recentlyFinished(FALSE) , type(){}
CollectorVolunteer::CollectorVolunteer(int id, const string &name, int coolDown) : Volunteer::Volunteer(id, name), coolDown(coolDown), timeLeft(NO_ORDER) {
    this->setType(VolunteerType::Collector);
}
LimitedCollectorVolunteer::LimitedCollectorVolunteer(int id, const string &name, int coolDown, int maxOrders) : CollectorVolunteer(id, name, coolDown), maxOrders(maxOrders), ordersLeft(maxOrders) {
        this->setType(VolunteerType::LimitedCollector);
}
DriverVolunteer::DriverVolunteer(int id, const string &name, int maxDistance, int distancePerStep) : Volunteer(id, name), maxDistance(maxDistance), distancePerStep(distancePerStep), distanceLeft(NO_ORDER) {
        this->setType(VolunteerType::Driver);
}
LimitedDriverVolunteer::LimitedDriverVolunteer(int id, const string &name, int maxDistance, int distancePerStep, int maxOrders) : DriverVolunteer(id, name, maxDistance, distancePerStep), maxOrders(maxOrders), ordersLeft(maxOrders) {
        this->setType(VolunteerType::LimitedDriver);

}
Volunteer::~Volunteer(){}
// DESTRUCTORS
Volunteer::Volunteer(const Volunteer& other):id(other.id) , name(other.name) ,completedOrderId(other.completedOrderId), activeOrderId(other.activeOrderId), recentlyFinished(other.recentlyFinished) , type(other.type){}
CollectorVolunteer::CollectorVolunteer(const CollectorVolunteer& other): Volunteer(other) ,coolDown ( other.coolDown),timeLeft ( other.timeLeft){}
LimitedCollectorVolunteer::LimitedCollectorVolunteer(const LimitedCollectorVolunteer& other):CollectorVolunteer(other) , maxOrders(other.maxOrders) , ordersLeft(other.ordersLeft){}
DriverVolunteer::DriverVolunteer(const DriverVolunteer& other):Volunteer(other),maxDistance ( other.maxDistance),distancePerStep ( other.distancePerStep) , distanceLeft(other.distanceLeft){}
LimitedDriverVolunteer::LimitedDriverVolunteer(const LimitedDriverVolunteer& other): DriverVolunteer(other), maxOrders(other.maxOrders),ordersLeft(other.ordersLeft) {}

//-----------------------------------------------------------------------------------------------//
// GETTER
int Volunteer::getId() const
{
    return id;
}
const string &Volunteer::getName() const
{
    return name;
}
int Volunteer::getActiveOrderId() const
{
    return activeOrderId;
}
int Volunteer::getCompletedOrderId() const
{
    return Volunteer::completedOrderId;
}
int CollectorVolunteer::getCoolDown() const
{
    return CollectorVolunteer::coolDown;
}
int CollectorVolunteer::getTimeLeft() const
{
    return CollectorVolunteer::timeLeft;
}
int LimitedCollectorVolunteer::getMaxOrders() const
{
    return maxOrders;
}
int LimitedCollectorVolunteer::getNumOrdersLeft() const
{
    return ordersLeft;
}
int DriverVolunteer::getDistanceLeft() const
{
    return distanceLeft;
}
int DriverVolunteer::getMaxDistance() const
{
    return maxDistance;
}
int DriverVolunteer::getDistancePerStep() const
{
    return distancePerStep;
}
int LimitedDriverVolunteer::getMaxOrders() const
{
    return maxOrders;
}
int LimitedDriverVolunteer::getNumOrdersLeft() const
{
    return ordersLeft;
}
bool Volunteer::getrecentlyFinished() const
{
    return recentlyFinished;
}
void Volunteer::setrecentlyFinished(bool recentlyfinish)
{
    recentlyFinished = recentlyfinish;
}
void Volunteer::setActiveOrder(int id)
{
    activeOrderId = id;
}
void Volunteer::setType(VolunteerType toset){type = toset;}
VolunteerType Volunteer::getType(){return type;}
//-----------------------------------------------------------------------------------------------//
// Signal whether the volunteer is currently processing an order
bool Volunteer::isBusy() const
{
    if (Volunteer::getActiveOrderId() != NO_ORDER)
    {
        return true;
    }
    return false;
}
const string Volunteer::isBusyPrint()const{
    string str = "";
    if(isBusy() == true){
        str =  "true";

    }else{
        str = "false";
    }
    return str;
}
//-----------------------------------------------------------------------------------------------//
// ToSTRING
string CollectorVolunteer::toString() const
{
    return "Collector Volunteer - ID: " + std::to_string(getId()) +
           ", Name: " + getName() + " , isBusy: " + std::to_string(isBusy()) +
           ", CoolDown: " + std::to_string(coolDown) +
           ", Time Left: " + std::to_string(timeLeft);
}
string LimitedCollectorVolunteer::toString() const
{
    return "Limited Collector Volunteer - ID: " + std::to_string(getId()) +
           ", Name: " + getName() + " , isBusy: " + std::to_string(isBusy()) +
           ", CoolDown: " + std::to_string(getCoolDown()) +
           ", Time Left: " + std::to_string(getTimeLeft()) +
           ", Max Orders: " + std::to_string(maxOrders) +
           ", Orders Left: " + std::to_string(ordersLeft);
}
string DriverVolunteer::toString() const
{
    return "Driver Volunteer - ID: " + std::to_string(getId()) +
           ", Name: " + getName() + " , isBusy: " + std::to_string(isBusy()) +
           ", Max Distance: " + std::to_string(maxDistance) +
           ", Distance Per Step: " + std::to_string(distancePerStep) +
           ", Distance Left: " + std::to_string(distanceLeft);
}
string LimitedDriverVolunteer::toString() const
{
    return "Limited Driver Volunteer - ID: " + std::to_string(getId()) +
           ", Name: " + getName() + " , isBusy: " + std::to_string(isBusy()) +
           ", Max Distance: " + std::to_string(getMaxDistance()) +
           ", Distance Per Step: " + std::to_string(getDistancePerStep()) +
           ", Distance Left: " + std::to_string(getDistanceLeft()) +
           ", Max Orders: " + std::to_string(maxOrders) +
           ", Orders Left: " + std::to_string(ordersLeft);
}
//-----------------------------------------------------------------------------------------------//
void CollectorVolunteer::volunteerStatus()
{
    std::cout << "VolunteerID: " + std::to_string(getId()) << std::endl;
    std::cout << "isBusy: " + isBusyPrint() << std::endl;
    if (Volunteer::getActiveOrderId() != -1)
    {
        std::cout << "Orderid: " + std::to_string(getActiveOrderId()) << std::endl;
    }
    else
    {
        std::cout << "Orderid: None" << std::endl;
    }
    std::cout << "Timeleft: " + std::to_string(getTimeLeft()) << std::endl;
    std::cout << "ordersLeft: No Limit" << std::endl;
}
void LimitedCollectorVolunteer::volunteerStatus()  {
    std::cout << "VolunteerID: " + std::to_string(getId()) << std::endl;
    std::cout << "isBusy: " + isBusyPrint()  << std::endl;
    if (Volunteer::getActiveOrderId() != -1)
    {
        std::cout << "Orderid: " + std::to_string(getActiveOrderId()) << std::endl;
    }
    else
    {
        std::cout << "Orderid: None" << std::endl;
    }
    std::cout << "Timeleft: " + std::to_string(getTimeLeft()) << std::endl;
    std::cout << "ordersLeft: "  + std::to_string(getNumOrdersLeft())<< std::endl;
}
void DriverVolunteer::volunteerStatus()  {
    std::cout << "VolunteerID: " + std::to_string(getId()) << std::endl;
    std::cout << "isBusy: " + isBusyPrint()  << std::endl;
    if (Volunteer::getActiveOrderId() != -1)
    {
        std::cout << "Orderid: " + std::to_string(getActiveOrderId()) << std::endl;
    }
    else
    {
        std::cout << "Orderid: None" << std::endl;
    }
    std::cout << "DistaneLeft: " + std::to_string(getDistanceLeft()) << std::endl;
    std::cout << "ordersLeft: No Limit" << std::endl;
}
void LimitedDriverVolunteer::volunteerStatus()  {
    std::cout << "VolunteerID: " + std::to_string(getId()) << std::endl;
    std::cout << "isBusy: " + isBusyPrint()  << std::endl;
    if (Volunteer::getActiveOrderId() != -1)
    {
        std::cout << "Orderid: " + std::to_string(getActiveOrderId()) << std::endl;
    }
    else
    {
        std::cout << "Orderid: None" << std::endl;
    }
    std::cout << "DistaneLeft: " + std::to_string(getDistanceLeft()) << std::endl;
    std::cout << "ordersLeft: "  + std::to_string(getNumOrdersLeft())<< std::endl;
}

// CollectorVolunteer
CollectorVolunteer *CollectorVolunteer::clone() const
{
    return new CollectorVolunteer(*this);
}
void CollectorVolunteer::step()
{

    timeLeft--;
    if (CollectorVolunteer::getTimeLeft() == 0)
    {
        completedOrderId = activeOrderId;
        activeOrderId = NO_ORDER;
    }
    else
    {
    }
}
bool CollectorVolunteer::decreaseCoolDown()
{
    if (CollectorVolunteer::timeLeft == 0)
    {
        return true;
    }
    CollectorVolunteer::timeLeft = CollectorVolunteer::timeLeft - 1;

    return false;
} // Decrease timeLeft by 1,return true if timeLeft=0,false otherwise
bool CollectorVolunteer::hasOrdersLeft() const
{
    return true;
}
bool CollectorVolunteer::canTakeOrder(const Order &order) const
{
    if (Volunteer::isBusy() == true)
    {
        return false;
    }
    if (order.getStatus() != OrderStatus::PENDING)
    {
        return false;
    }
    return true;
}
void CollectorVolunteer::acceptOrder(const Order &order)
{
    Volunteer::activeOrderId = order.getId();
    timeLeft = getCoolDown();
}
void CollectorVolunteer::setTimeLeft(int time)
{
    timeLeft = time;
}
//-----------------------------------------------------------------------------------------------//
// LimitedCollectorVolunteer
LimitedCollectorVolunteer *LimitedCollectorVolunteer::clone() const
{
    return new LimitedCollectorVolunteer(*this);
}
bool LimitedCollectorVolunteer::hasOrdersLeft() const
{
    if (LimitedCollectorVolunteer::getNumOrdersLeft() > 0)
    {
        return true;
    }
    return false;
}
bool LimitedCollectorVolunteer::canTakeOrder(const Order &order) const
{
    if (LimitedCollectorVolunteer::hasOrdersLeft() == true && (Volunteer::isBusy() == false) && order.getStatus() == OrderStatus::PENDING)
    {
        return true;
    }

    return false;
}
void LimitedCollectorVolunteer::acceptOrder(const Order &order)
{
    CollectorVolunteer::acceptOrder(order);
    ordersLeft--;
}
//-----------------------------------------------------------------------------------------------//
// DriverVolunteer
DriverVolunteer *DriverVolunteer::clone() const
{
    return new DriverVolunteer(*this);
}

// Decrease distanceLeft by distancePerStep,return true if distanceLeft<=0,false otherwise
bool DriverVolunteer::decreaseDistanceLeft()
{
    if (DriverVolunteer::distancePerStep >= DriverVolunteer::distanceLeft)
    {
        DriverVolunteer::distanceLeft = 0;
        return true;
    }
    DriverVolunteer::distanceLeft -= DriverVolunteer::distancePerStep;
    return false;
}
bool DriverVolunteer::hasOrdersLeft() const
{
    return true;
}
bool DriverVolunteer::canTakeOrder(const Order &order) const
{
    // Signal if the volunteer is not busy and the order is within the maxDistance
    if ((Volunteer::isBusy() == false) && order.getOrderDistance() <= DriverVolunteer::maxDistance && order.getStatus() == OrderStatus::COLLECTING)
        return true;
 
    return false;
}
// Reset distanceLeft to maxDistance
void DriverVolunteer::acceptOrder(const Order &order)
{
    Volunteer::activeOrderId = order.getId();
    DriverVolunteer::distanceLeft = order.getOrderDistance();
}
void DriverVolunteer::setDistanceLeft(int Distance)
{
    if (Distance <= 0)
    {
        DriverVolunteer::distanceLeft = 0;
    }
    distanceLeft = Distance;
}
void DriverVolunteer::step()
{

    DriverVolunteer::setDistanceLeft(DriverVolunteer::getDistanceLeft() - DriverVolunteer::getDistancePerStep());
    if (DriverVolunteer::getDistanceLeft() <= 0)
    {
        Volunteer::completedOrderId = activeOrderId;
        Volunteer::activeOrderId = NO_ORDER;
    }
    else
    {
    }

} 

//-----------------------------------------------------------------------------------------------//
// LimitedDriverVolunteer
LimitedDriverVolunteer *LimitedDriverVolunteer::clone() const
{
    return new LimitedDriverVolunteer(*this);
}
bool LimitedDriverVolunteer::hasOrdersLeft() const
{
    if (LimitedDriverVolunteer::getNumOrdersLeft() > 0)
        return true;
    return false;
}
// Signal if the volunteer is not busy, the order is within the maxDistance and have orders left
bool LimitedDriverVolunteer::canTakeOrder(const Order &order) const
{
    if (LimitedDriverVolunteer::hasOrdersLeft() == true && (Volunteer::isBusy() == false) && order.getOrderDistance() <= DriverVolunteer::getMaxDistance() && order.getStatus() == OrderStatus::COLLECTING)
        return true;

    return false;
}
// Reset distanceLeft to maxDistance and decrease ordersLeft
void LimitedDriverVolunteer::acceptOrder(const Order &order)
{
    DriverVolunteer::acceptOrder(order);
    ordersLeft--;
}
