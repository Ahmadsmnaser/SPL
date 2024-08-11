#include "../include/Order.h"
#define NO_VOLUNTEER -1


    //CONSTRUCTORS 
    Order::Order(int id, int customerId, int distance) : id(id) , customerId(customerId) , distance(distance) , status(OrderStatus::PENDING) , collectorId(NO_VOLUNTEER) , driverId(NO_VOLUNTEER), orderCounter(0){}
    Order::Order(const Order &other): id(other.id) , customerId(other.customerId) , distance(other.distance) , status(other.status) , collectorId(other.collectorId) , driverId(other.driverId), orderCounter(other.orderCounter){}
    Order::~Order(){}
    //GETTER
    int Order::getId() const{
        return id;
    }
    int Order::getCustomerId() const{
        return customerId;
    }
    int Order::getCollectorId() const{
        return collectorId;
    }
    int Order::getDriverId() const{
        return driverId;
    }
    OrderStatus Order::getStatus() const{
        return status;
    }
    int Order::getOrderDistance() const{
        return distance;
    }
    int Order::getNextOrderId() {
        orderCounter++;
        return orderCounter;
    }

    //SETTER
    void Order::setStatus(OrderStatus _status){
        status = _status;
    }
    void Order::setCollectorId(int _collectorId){
        collectorId = _collectorId;
    }
    void Order::setDriverId(int _driverId) {
        driverId = _driverId;
    }
    //toSTRING
    string Order::statustoString() const{
        if(Order::getStatus() == OrderStatus::PENDING ){
            return "Pending";
        }
        if(Order::getStatus() == OrderStatus::COLLECTING){
            return "Collecting";
        }
        if(Order::getStatus() == OrderStatus::DELIVERING){
            return "Delivering";
        }
        if(Order::getStatus() == OrderStatus::COMPLETED){
            return "Completed";
        }
        return "";
    }

    const string Order::toString() const{
    return "Order ID: " + std::to_string(getId()) + ", Customer ID: " + std::to_string(getCustomerId()) + ", Status: " + statustoString() + ".";
    }
    

