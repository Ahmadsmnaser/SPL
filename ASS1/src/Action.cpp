#include "../include/Action.h"
extern WareHouse *backup;
// Defult Constructors
//------------------------------------------------------------------------------
BaseAction::BaseAction() : errorMsg(""), status(ActionStatus::ERROR) ,type() {}
CustomerType BaseAction::convertTOCustomerType(const string &st)
{
    if (st == "SoldierCustomer")
    {
        return CustomerType::SoldierCustomer;
    }
    return CustomerType::CivilianCustomer;
}
string BaseAction::CustomerTypetoString(const CustomerType type) const
{
    if (type == CustomerType::CivilianCustomer)
    {
        return "CivilianCustomer";
    }
    if (type == CustomerType::SoldierCustomer)
    {
        return "SoldierCustomer";
    }
    return "";
}
SimulateStep::SimulateStep(int numOfSteps) : numOfSteps(numOfSteps)
{
    this->SetType(ActionType::simulatestep);
}
AddOrder::AddOrder(int id) : customerId(id)
{
    this->SetType(ActionType::addOrder);
}
AddCustomer::AddCustomer(string customerName, string customerType, int distance, int maxOrders) : customerName(customerName), customerType(convertTOCustomerType(customerType)), distance(distance), maxOrders(maxOrders)
{
    this->SetType(ActionType::addCustomer);
}
PrintOrderStatus::PrintOrderStatus(int id) : orderId(id)
{
    this->SetType(ActionType::printOrderStatus);
}
PrintCustomerStatus::PrintCustomerStatus(int customerId) : customerId(customerId)
{
    this->SetType(ActionType::printCustomerStatus);
}
PrintVolunteerStatus::PrintVolunteerStatus(int id) : VolunteerId(id)
{
    this->SetType(ActionType::printVolunteerStatus);
}
PrintActionsLog::PrintActionsLog()
{
    this->SetType(ActionType::printActionsLog);
}
Close::Close()
{
    this->SetType(ActionType::close);
}
BackupWareHouse::BackupWareHouse()
{
    this->SetType(ActionType::backupWareHouse);
}
RestoreWareHouse::RestoreWareHouse()
{
    this->SetType(ActionType::restoreWareHouse);
}
// Copy Constructors
//------------------------------------------------------------------------------
BaseAction::BaseAction(const BaseAction &other) : errorMsg(other.errorMsg), status(other.status) , type(other.type){}
SimulateStep::SimulateStep(const SimulateStep &other) : BaseAction(other), numOfSteps(other.numOfSteps)
{
    this->SetType(ActionType::simulatestep);
}
AddOrder::AddOrder(const AddOrder &other) : BaseAction(other), customerId(other.customerId)
{
    this->SetType(ActionType::addOrder);
}
AddCustomer::AddCustomer(const AddCustomer &other) : BaseAction(other), customerName(other.customerName), customerType(other.customerType), distance(other.distance), maxOrders(other.maxOrders)
{
    this->SetType(ActionType::addCustomer);
}
PrintOrderStatus::PrintOrderStatus(const PrintOrderStatus &other) : BaseAction(other), orderId(other.orderId)
{
    this->SetType(ActionType::printOrderStatus);
}
PrintCustomerStatus::PrintCustomerStatus(const PrintCustomerStatus &other) : BaseAction(other), customerId(other.customerId)
{
    this->SetType(ActionType::printCustomerStatus);
}
PrintVolunteerStatus::PrintVolunteerStatus(const PrintVolunteerStatus &other) : BaseAction(other), VolunteerId(other.VolunteerId)
{
    this->SetType(ActionType::printVolunteerStatus);
}
PrintActionsLog::PrintActionsLog(const PrintActionsLog &other) : BaseAction(other)
{
    this->SetType(ActionType::printActionsLog);
}
Close::Close(const Close &other) : BaseAction(other)
{
    this->SetType(ActionType::close);
}
BackupWareHouse::BackupWareHouse(const BackupWareHouse &other) : BaseAction(other)
{
    this->SetType(ActionType::backupWareHouse);
}
RestoreWareHouse::RestoreWareHouse(const RestoreWareHouse &other) : BaseAction(other)
{
    this->SetType(ActionType::restoreWareHouse);
}
// Getters Functions
//------------------------------------------------------------------------------
ActionStatus BaseAction::getStatus() const
{
    return status;
}
std::string BaseAction::getErrorMsg() const
{
    return errorMsg;
}
   ActionType BaseAction::getType() const{
    return type;
   }
    void BaseAction::SetType(ActionType toset){
        type = toset;
    }
// Change Status Function
//------------------------------------------------------------------------------
void BaseAction::complete()
{
    status = ActionStatus::COMPLETED;
}
void BaseAction::error(std::string errorMsg)
{
    this->errorMsg = "Error: " + errorMsg;
    status = ActionStatus::ERROR;
    std::cout << errorMsg << std::endl;
}
// Act Functions
//------------------------------------------------------------------------------
void SimulateStep::act(WareHouse &wareHouse)

{
    wareHouse.PerformSimulateStep(numOfSteps);
    SimulateStep::complete();
}
void AddOrder::act(WareHouse &wareHouse)
{

    try
    {
        Customer &customer = wareHouse.getCustomer(customerId);
        if (customer.canMakeOrder())
        {

            Order *newOrder = new Order(wareHouse.getorderCounter(), customerId, wareHouse.getCustomer(customerId).getCustomerDistance());
            customer.addOrder(newOrder->getId());
            wareHouse.addOrder(newOrder);
            AddOrder::complete();
        }
        else
        {
            AddOrder::error("Cannot place this order");
        }
    }
    catch (const std::runtime_error &ex)
    {
        AddOrder::error("Cannot place this order");
    }
}
void AddCustomer::act(WareHouse &wareHouse)
{ 
    if (customerType == CustomerType::SoldierCustomer)
    {
        Customer *temp = new SoldierCustomer(wareHouse.getCustomerCounter(), customerName, distance, maxOrders);
        wareHouse.add1CustomerCounter();
        wareHouse.addCustomer(temp);
    }
    else // CivilianCustomer
    {
        Customer *temp = new CivilianCustomer(wareHouse.getCustomerCounter(), customerName, distance, maxOrders);
        wareHouse.add1CustomerCounter();
        wareHouse.addCustomer(temp);
    }
    AddCustomer::complete();
}
void PrintOrderStatus::act(WareHouse &wareHouse)
{
    try
    {
        Order &order = wareHouse.getOrder(orderId);
        std::cout << "OrderID: " + std::to_string(order.getId()) << std::endl;
        std::cout << "OrderStatus: " + order.statustoString() << std::endl;
        std::cout << "CustomerID: " + std::to_string(order.getCustomerId()) << std::endl;
        if (order.getCollectorId() == -1)
        {
            std::cout << "CollectorID: None" << std::endl;
        }
        else
        {
            std::cout << "CollectorID: " + std::to_string(order.getCollectorId()) << std::endl;
        }
        if (order.getDriverId() == -1)
        {
            std::cout << "DriverID: None" << std::endl;
        }
        else
        {
            std::cout << "DriverID: " + std::to_string(order.getDriverId()) << std::endl;
        }
        PrintOrderStatus::complete();
    }
    catch (const std::runtime_error &ex)
    {
        PrintOrderStatus::error("Order doesn’t exist");
    }
}
void PrintCustomerStatus::act(WareHouse &wareHouse)
{
    try
    {
        Customer &thisCustomer = wareHouse.getCustomer(customerId);
        std::cout << "CustomerID : " + std::to_string(thisCustomer.getId()) << std::endl;
        vector<int> Orders = thisCustomer.getOrders();
        size_t size = Orders.size();
        for (size_t i = 0; i < size; i++)
        {
            int Orders_Id = Orders[i]; // Id of Orders
            Order &thisorder = wareHouse.getOrder(Orders_Id);
            std::cout << "OrderID : " + std::to_string(thisorder.getId()) << std::endl;
            std::cout << "OrderStatus :" + thisorder.statustoString() << std::endl;
        }
        int numOrdersLeft = thisCustomer.getMaxOrders() - thisCustomer.getNumOrders();
        std::cout << "numOrdersLeft: " + std::to_string(numOrdersLeft) << std::endl;
        PrintCustomerStatus::complete();
    }
    catch (const std::runtime_error &ex)
    {
        PrintCustomerStatus::error("Customer doesn’t exist");
    }
}
void PrintVolunteerStatus::act(WareHouse &wareHouse)
{
    try
    {
        Volunteer &thisVolunteer = wareHouse.getVolunteer(VolunteerId);

        thisVolunteer.volunteerStatus();
        PrintVolunteerStatus::complete();
    }
    catch (const std::runtime_error &ex)
    {
        PrintVolunteerStatus::error("Volunteer doesn’t exist");
    }
}
void PrintActionsLog::act(WareHouse &wareHouse)
{
    std::vector<BaseAction *> temp = wareHouse.getActions();
    for (BaseAction *log : temp)
    {
        std::cout << log->toString() << std::endl;
    }
    size_t size = temp.size();
    for (size_t i = 0; i < size; i++)
    {
        temp[i] = nullptr;
    }
    PrintActionsLog::complete();
}
void Close::act(WareHouse &wareHouse)
{
    wareHouse.close();
    Close::complete();
}
void BackupWareHouse::act(WareHouse &wareHouse)
{
 
   if (backup == nullptr)
    {
        backup = new WareHouse(wareHouse);
    }else{
        backup->operator=(wareHouse);
    }
    complete();

}
void RestoreWareHouse::act(WareHouse &wareHouse)
{
    if (backup == nullptr)
    {
        error("No backup available");
    }
    else
    {
     
       wareHouse.operator=(*backup);
       complete();
    }
}
// ToSting
//------------------------------------------------------------------------------
string SimulateStep::toString() const
{
    std::string log = "simulationStep " + std::to_string(numOfSteps);
    log += " COMPLETED";
    return log;
}
string AddOrder::toString() const
{
    std::string log = "order " + std::to_string(customerId);
    if (AddOrder::getStatus() == ActionStatus::ERROR)
    {
        log += " ERROR ";
    }
    else
    {
        log += " COMPLETED ";
    }
    return log;
}
string AddCustomer::toString() const
{
    std::string log = "customer " + customerName + CustomerTypetoString(customerType) + std::to_string(distance) + std::to_string(maxOrders);
    log += " COMPLETED";
    return log;
}
string PrintOrderStatus::toString() const
{
    std::string log = "orderStatus " + std::to_string(orderId);
    if (PrintOrderStatus::getStatus() == ActionStatus::ERROR)
    {
        log += " ERROR ";
    }
    else
    {
        log += " COMPLETED ";
    }
    return log;
}
string PrintCustomerStatus::toString() const
{
    std::string log = "CustomerStatus " + std::to_string(customerId);
    if (PrintCustomerStatus::getStatus() == ActionStatus::ERROR)
    {
        log += " ERROR ";
    }
    else
    {
        log += " COMPLETED ";
    }
    return log;
}
string PrintVolunteerStatus::toString() const
{
    std::string log = "VolunteerStatus " + std::to_string(VolunteerId);
    if (PrintVolunteerStatus::getStatus() == ActionStatus::ERROR)
    {
        log += " ERROR ";
    }
    else
    {
        log += " COMPLETED";
    }
    return log;
}
string PrintActionsLog::toString() const
{
    return "ActionLogs COMPLETED";
}
string Close::toString() const
{
    return "Close COMPLETED";
}
string BackupWareHouse::toString() const
{
    return "Backup COMPLETED";
}
string RestoreWareHouse::toString() const
{
    if (this->getStatus() == ActionStatus::ERROR)
    {
        return "Restore ERROR";
    }
    return "Restore COMPLETED";
}
// Clone
//------------------------------------------------------------------------------
SimulateStep *SimulateStep::clone() const
{
    return new SimulateStep(*this);
}
AddOrder *AddOrder::clone() const
{
    return new AddOrder(*this);
}
AddCustomer *AddCustomer::clone() const
{
    return new AddCustomer(*this);
}
PrintOrderStatus *PrintOrderStatus::clone() const
{
    return new PrintOrderStatus(*this);
}
PrintCustomerStatus *PrintCustomerStatus::clone() const
{
    return new PrintCustomerStatus(*this);
}
PrintVolunteerStatus *PrintVolunteerStatus::clone() const
{
    return new PrintVolunteerStatus(*this);
}
PrintActionsLog *PrintActionsLog::clone() const
{
    return new PrintActionsLog(*this);
}
Close *Close::clone() const
{
    return new Close(*this);
}
BackupWareHouse *BackupWareHouse::clone() const
{
    return new BackupWareHouse(*this);
}
RestoreWareHouse *RestoreWareHouse::clone() const
{
    return new RestoreWareHouse(*this);
}
// Destructors
//------------------------------------------------------------------------------
BaseAction::~BaseAction() {}
SimulateStep::~SimulateStep() {}
AddOrder::~AddOrder() {}
AddCustomer::~AddCustomer() {}
PrintOrderStatus::~PrintOrderStatus() {}
PrintCustomerStatus::~PrintCustomerStatus() {}
PrintVolunteerStatus::~PrintVolunteerStatus() {}
PrintActionsLog::~PrintActionsLog() {}
Close::~Close() {}
BackupWareHouse::~BackupWareHouse() {}
RestoreWareHouse::~RestoreWareHouse() {}
