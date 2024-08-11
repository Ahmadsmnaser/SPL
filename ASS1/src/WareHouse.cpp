#include "../include/Action.h"
WareHouse::WareHouse(const std::string &configFilePath)
    : isOpen(true), actionsLog(), volunteers(), pendingOrders(), inProcessOrders(), completedOrders(), customers(), customerCounter(0), volunteerCounter(0), orderCounter(-1), CollectorCounter(0), DriverCounter(0)
{
    std::ifstream inFile;
    inFile.open(configFilePath);
    if (!inFile)
    {
        std::cerr << "Unable to open file" << std::endl;
        exit(1);
    }

    std::string line;
    customerCounter = 0;
    volunteerCounter = 0;
    string costORvol;

    while (getline(inFile, line))
    {
        if (!(line.empty()) && line.at(0) != '#')
        {
            std::queue<std::string> capacities;
            splitString(line, capacities, ' ');

            // Check if there are enough values in the queue
            if (capacities.size() < 3)
            {
                std::cerr << "Not enough values in the configuration file for an entity." << std::endl;
                exit(1);
            }

            std::string costORvol = capacities.front();

            try
            {
                if (costORvol == "customer")
                {
                    capacities.pop();
                    std::string name = capacities.front();
                    capacities.pop();
                    std::string TypeCustomer = capacities.front();
                    capacities.pop();
                    int distance = std::stoi(capacities.front());
                    capacities.pop();
                    int maxOrders = std::stoi(capacities.front());

                    if (TypeCustomer == "civilian")
                    {
                        Customer *Customer = new CivilianCustomer(customerCounter, name, distance, maxOrders);
                        customers.push_back(Customer);

                        customerCounter = customerCounter + 1;
                    }
                    else
                    { // SoldierCustomer
                        Customer *Customer = new SoldierCustomer(customerCounter, name, distance, maxOrders);
                        customers.push_back(Customer);
                        customerCounter = customerCounter + 1;
                    }
                }
                else if (costORvol == "volunteer")
                {
                    // its a volunteer
                    if (capacities.size() < 3)
                    {
                        std::cerr << "Not enough values in the configuration file for a volunteer." << std::endl;
                        exit(1);
                    }
                    capacities.pop();
                    std::string name = capacities.front();
                    capacities.pop();
                    std::string TypeVolunteer = capacities.front();
                    capacities.pop();
                    if (TypeVolunteer == "collector" || TypeVolunteer == "limited_collector" ||
                        TypeVolunteer == "driver" || TypeVolunteer == "limited_driver")
                    {
                        if (TypeVolunteer == "collector")
                        {
                            int coolDown = std::stoi(capacities.front());
                            capacities.pop();
                            volunteers.push_back(new CollectorVolunteer(volunteerCounter, name, coolDown));
                            volunteerCounter = volunteerCounter + 1;
                            CollectorCounter++;
                        }
                        else if (TypeVolunteer == "limited_collector")
                        {
                            int coolDown = std::stoi(capacities.front());
                            capacities.pop();
                            int maxOrders = std::stoi(capacities.front());
                            capacities.pop();
                            volunteers.push_back(new LimitedCollectorVolunteer(volunteerCounter, name, coolDown, maxOrders));
                            volunteerCounter = volunteerCounter + 1;
                            CollectorCounter++;
                        }
                        else if (TypeVolunteer == "driver")
                        {
                            int maxDistance = std::stoi(capacities.front());
                            capacities.pop();
                            int distance_per_step = std::stoi(capacities.front());
                            capacities.pop();
                            volunteers.push_back(new DriverVolunteer(volunteerCounter, name, maxDistance, distance_per_step));
                            volunteerCounter = volunteerCounter + 1;
                            DriverCounter++;
                        }
                        else
                        {
                            int maxDistance = std::stoi(capacities.front());
                            capacities.pop();
                            int distance_per_step = std::stoi(capacities.front());
                            capacities.pop();
                            int maxOrders = std::stoi(capacities.front());
                            capacities.pop();
                            volunteers.push_back(new LimitedDriverVolunteer(volunteerCounter, name, maxDistance, distance_per_step, maxOrders));
                            volunteerCounter = volunteerCounter + 1;
                            DriverCounter++;
                        }
                    }
                    else
                    {
                        std::cerr << "Invalid volunteer type: " << TypeVolunteer << std::endl;
                        exit(1);
                    }
                }
                else
                {
                    std::cerr << "Invalid entity type: " << costORvol << std::endl;
                    exit(1);
                }
            }
            catch (const std::invalid_argument &e)
            {
                std::cerr << "Invalid argument: " << e.what() << std::endl;
                exit(1);
            }
        }
    }
    inFile.close();
}
WareHouse::WareHouse(const WareHouse &other) : isOpen(other.getisOpen()), actionsLog(), volunteers(), pendingOrders(), inProcessOrders(), completedOrders(), customers(), customerCounter(other.customerCounter), volunteerCounter(other.volunteerCounter), orderCounter(other.orderCounter), CollectorCounter(other.CollectorCounter), DriverCounter(other.DriverCounter)
{
    this->copy(other);
}
void WareHouse::clear()
{
    // Delete volunteers
    size_t size1 = volunteers.size();
    for (size_t i = 0; i < size1; i = i + 1)
    {
        delete (volunteers[i]);
    }
    volunteers.clear();

    // Delete BaseAction
    if (actionsLog.empty())
        return;
    size_t size2 = actionsLog.size();
    for (size_t i = 0; i < size2; i = i + 1)
    {
        delete (actionsLog[i]);
    }
    actionsLog.clear();

    // Delete pendingOrders
    size_t size3 = pendingOrders.size();
    for (size_t i = 0; i < size3; i = i + 1)
    {
        delete (pendingOrders[i]);
    }
    pendingOrders.clear();

    // Delete inProcessOrders
    size_t size4 = inProcessOrders.size();
    for (size_t i = 0; i < size4; i = i + 1)
    {
        delete (inProcessOrders[i]);
    }
    inProcessOrders.clear();

    // Delete completedOrders
    size_t size5 = completedOrders.size();
    for (size_t i = 0; i < size5; i = i + 1)
    {
        delete (completedOrders[i]);
    }
    completedOrders.clear();

    // Delete customers
    size_t size6 = customers.size();
    for (size_t i = 0; i < size6; i = i + 1)
    {
        delete (customers[i]);
    }
    customers.clear();
}
void WareHouse::copy(const WareHouse &other)
{
    if (this == &other)
    {
        return;
    }
    this->clear();
    this->isOpen = other.getisOpen();
    this->customerCounter = other.customerCounter;
    this->volunteerCounter = other.volunteerCounter;
    this->orderCounter = other.orderCounter;
    this->CollectorCounter = other.CollectorCounter;
    this->DriverCounter = other.DriverCounter;
    ActionType type;
    size_t size1 = other.volunteers.size();
    for (size_t i = 0; i < size1; i = i + 1)
    {
        if (other.volunteers[i]->getType() == VolunteerType::Collector)
        {
            CollectorVolunteer *insert = new CollectorVolunteer(*((CollectorVolunteer *)other.volunteers[i]));
            this->volunteers.push_back(insert);
            insert = nullptr;
            delete (insert);
        }
        if (other.volunteers[i]->getType() == VolunteerType::LimitedCollector)
        {
            LimitedCollectorVolunteer *insert = new LimitedCollectorVolunteer(*((LimitedCollectorVolunteer *)other.volunteers[i]));
            this->volunteers.push_back(insert);
            insert = nullptr;
            delete (insert);
        }
        if (other.volunteers[i]->getType() == VolunteerType::Driver)
        {
            DriverVolunteer *insert = new DriverVolunteer(*((DriverVolunteer *)other.volunteers[i]));
            this->volunteers.push_back(insert);
            insert = nullptr;
            delete (insert);
        }

        if (other.volunteers[i]->getType() == VolunteerType::LimitedDriver)
        {
            LimitedDriverVolunteer *insert = new LimitedDriverVolunteer(*((LimitedDriverVolunteer *)other.volunteers[i]));
            this->volunteers.push_back(insert);
            insert = nullptr;
            delete (insert);
        }
    }
    size_t size3 = other.pendingOrders.size();
    for (size_t i = 0; i < size3; i = i + 1)
    {
        Order *insert = new Order(*(other.pendingOrders[i]));
        this->pendingOrders.push_back(insert);
        insert = nullptr;
        delete (insert);
    }
    size_t size4 = other.inProcessOrders.size();
    for (size_t i = 0; i < size4; i = i + 1)
    {
        Order *insert = new Order(*(other.inProcessOrders[i]));
        this->inProcessOrders.push_back(insert);
        insert = nullptr;
        delete (insert);
    }
    size_t size5 = other.completedOrders.size();
    for (size_t i = 0; i < size5; i = i + 1)
    {
        Order *insert = new Order(*(other.completedOrders[i]));
        this->completedOrders.push_back(insert);
        insert = nullptr;
        delete (insert);
    }
    size_t size6 = other.customers.size();
    for (size_t i = 0; i < size6; i = i + 1)
    {
        if (other.customers[i]->getType() == CustomerType::CivilianCustomer){

        CivilianCustomer *insert = new CivilianCustomer(*((CivilianCustomer *)other.customers[i]));
        this->customers.push_back(insert);
        insert = nullptr;
        delete (insert);
        }
        if (other.customers[i]->getType() == CustomerType::SoldierCustomer){

        SoldierCustomer *insert = new SoldierCustomer(*((SoldierCustomer *)other.customers[i]));
        this->customers.push_back(insert);
        insert = nullptr;
        delete (insert);
        }
    }
    size_t size2 = other.actionsLog.size();
    for (size_t i = 0; i < size2; i = i + 1)
    {
        type = other.actionsLog[i]->getType();
        switch (type)
        {
        case ActionType::simulatestep:
        {
            SimulateStep *insert = new SimulateStep(*((SimulateStep *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::addOrder:
        {
            AddOrder *insert = new AddOrder(*((AddOrder *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::addCustomer:
        {
            AddCustomer *insert = new AddCustomer(*((AddCustomer *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::printOrderStatus:
        {
            PrintOrderStatus *insert = new PrintOrderStatus(*((PrintOrderStatus *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::printCustomerStatus:
        {
            PrintCustomerStatus *insert = new PrintCustomerStatus(*((PrintCustomerStatus *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::printVolunteerStatus:
        {
            PrintVolunteerStatus *insert = new PrintVolunteerStatus(*((PrintVolunteerStatus *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::printActionsLog:
        {
            PrintActionsLog *insert = new PrintActionsLog(*((PrintActionsLog *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::close:
        {
            Close *insert = new Close(*((Close *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::backupWareHouse:
        {
            BackupWareHouse *insert = new BackupWareHouse(*((BackupWareHouse *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        case ActionType::restoreWareHouse:
        {
            RestoreWareHouse *insert = new RestoreWareHouse(*((RestoreWareHouse *)other.actionsLog[i]));
            this->actionsLog.push_back(insert);
            insert = nullptr;
            delete (insert);
            break;
        }
        }
    }
}
WareHouse *WareHouse::Copy() const
{
    WareHouse *temp = new WareHouse(*this);
    return temp;
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
WareHouse::~WareHouse()
{
    this->clear();
}
// assignemt operator
WareHouse &WareHouse::operator=(const WareHouse &other)
{
    if (this == &other)
    {
        return *this;
    }
    this->clear();
    this->isOpen = other.getisOpen();
    this->customerCounter = other.customerCounter;
    this->volunteerCounter = other.volunteerCounter;
    this->orderCounter = other.orderCounter;
    this->CollectorCounter = other.CollectorCounter;
    this->DriverCounter = other.DriverCounter;
    this->copy(other);
    return *this;
}
// Move constructor
WareHouse::WareHouse(WareHouse &&other) : isOpen(other.isOpen), actionsLog(), volunteers(), pendingOrders(),
                                          inProcessOrders(), completedOrders(), customers(), customerCounter(other.customerCounter), volunteerCounter(other.volunteerCounter),
                                          orderCounter(other.orderCounter), CollectorCounter(other.CollectorCounter), DriverCounter(other.DriverCounter)
{
    size_t Sizevolunteers = other.volunteers.size();
    for (size_t i = 0; i < Sizevolunteers; i = i + 1)
    {
        volunteers.push_back(other.volunteers[i]);
        other.volunteers[i] = nullptr;
    }
    size_t sizePendingOrders = other.pendingOrders.size();
    for (size_t i = 0; i < sizePendingOrders; i = i + 1)
    {
        pendingOrders.push_back((other.pendingOrders[i]));
        other.pendingOrders[i] = nullptr;
    }
    size_t sizeBaseAction = other.actionsLog.size();
    for (size_t i = 0; i < sizeBaseAction; i = i + 1)
    {
        actionsLog.push_back(other.actionsLog[i]);
        other.actionsLog[i] = nullptr;
    }
    size_t sizecompletedOrders = other.completedOrders.size();
    for (size_t i = 0; i < sizecompletedOrders; i = i + 1)
    {
        completedOrders.push_back((other.completedOrders[i]));
        other.completedOrders[i] = nullptr;
    }
    size_t sizeinProcessOrders = other.inProcessOrders.size();
    for (size_t i = 0; i < sizeinProcessOrders; i = i + 1)
    {
        inProcessOrders.push_back((other.inProcessOrders[i]));
        other.inProcessOrders[i] = nullptr;
    }
    size_t sizecustomers = other.customers.size();
    for (size_t i = 0; i < sizecustomers; i = i + 1)
    {
        customers.push_back(other.customers[i]);
        other.customers[i] = nullptr;
    }
}
// Move Assignemt operator
WareHouse &WareHouse::operator=(WareHouse &&other)
{
    if (this == &other)
    {
        return *this;
    }
    this->clear();
    this->isOpen = other.getisOpen();
    this->isOpen = other.getisOpen();
    this->customerCounter = other.customerCounter;
    this->volunteerCounter = other.volunteerCounter;
    this->orderCounter = other.orderCounter;
    this->CollectorCounter = other.CollectorCounter;
    this->DriverCounter = other.DriverCounter;
    size_t Sizevolunteers = other.volunteers.size();
    for (size_t i = 0; i < Sizevolunteers; i = i + 1)
    {
        volunteers.push_back(other.volunteers[i]);
        other.volunteers[i] = nullptr;
    }
    size_t sizePendingOrders = other.pendingOrders.size();
    for (size_t i = 0; i < sizePendingOrders; i = i + 1)
    {
        pendingOrders.push_back((other.pendingOrders[i]));
        other.pendingOrders[i] = nullptr;
    }
    size_t sizeBaseAction = other.actionsLog.size();
    for (size_t i = 0; i < sizeBaseAction; i = i + 1)
    {
        actionsLog.push_back(other.actionsLog[i]);
        other.actionsLog[i] = nullptr;
    }
    size_t sizecompletedOrders = other.completedOrders.size();
    for (size_t i = 0; i < sizecompletedOrders; i = i + 1)
    {
        completedOrders.push_back((other.completedOrders[i]));
        other.completedOrders[i] = nullptr;
    }
    size_t sizeinProcessOrders = other.inProcessOrders.size();
    for (size_t i = 0; i < sizeinProcessOrders; i = i + 1)
    {
        inProcessOrders.push_back((other.inProcessOrders[i]));
        other.inProcessOrders[i] = nullptr;
    }
    size_t sizecustomers = other.customers.size();
    for (size_t i = 0; i < sizecustomers; i = i + 1)
    {
        customers.push_back(other.customers[i]);
        other.customers[i] = nullptr;
    }

    return *this;
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
int WareHouse::getorderCounter()
{
    orderCounter++;
    return orderCounter;
}
int WareHouse::getCustomerCounter()
{
    return customerCounter;
}
void WareHouse::add1CustomerCounter()
{
    customerCounter++;
}
void WareHouse::addCustomer(Customer *customer)
{
    customers.push_back(customer);
}
const bool WareHouse::getisOpen() const
{
    return isOpen;
}
//--------------------------------------------------------------------//
// this function splits a given string (s) on the basis of a given char ( c ) , and puts the sub-strings in a queue
void WareHouse::splitString(std::string s, std::queue<std::string> &queue, char c)
{
    std::string temp;
    for (char i : s)
    {
        if (i == c)
        {
            queue.push(temp);
            temp = "";
        }
        else
        {
            temp.push_back(i);
        }
    }
    queue.push(temp);
}
// PerformSimulateStep--------------------------------------------------------------------------------------
void WareHouse::PerformSimulateStep(int numOfSteps)
{
    for (int i = 0; i < numOfSteps; ++i)
    {
        processPendingOrders();

        for (auto volunteer : volunteers)
        {
            if (volunteer->isBusy())
            {
                volunteer->step();

                if (!volunteer->isBusy())
                {
                    volunteer->setrecentlyFinished(true);
                    volunteer->setActiveOrder(NO_ORDER);
                }
            }
        }

        processFinishedVolunteers();

    }
}
void WareHouse::processPendingOrders()
{
    processVolunteerOrders(CollectorCounter, OrderStatus::PENDING, OrderStatus::COLLECTING, CollectorCounter);
    processVolunteerOrders(DriverCounter, OrderStatus::COLLECTING, OrderStatus::DELIVERING, DriverCounter);
}
void WareHouse::processVolunteerOrders(int &counter, OrderStatus fromStatus, OrderStatus toStatus, int &volunteerCounter)
{
    if (counter != 0)
    {
        auto it = pendingOrders.begin();
        while (it != pendingOrders.end())
        {
            Order *order = *it;

         
            if (order->getStatus() == fromStatus && volunteerCounter != 0)
            {
                for (auto volunteer : volunteers)
                {
                    if (volunteer->canTakeOrder(*order))
                    {
                        volunteer->acceptOrder(*order);
                   
                        order->setStatus(toStatus);
                        volunteer->setActiveOrder(order->getId());
                        if (fromStatus == OrderStatus::PENDING)
                        {
                            order->setCollectorId(volunteer->getId());
                        }
                        else
                        {
                            order->setDriverId(volunteer->getId());
                        }

        
                        inProcessOrders.push_back(order);
                        it = pendingOrders.erase(it);

                        volunteerCounter--;

              
                        break;
                    }
                }
            }
            else
            {
                it++;
            }
        }
    }
}
void WareHouse::processFinishedVolunteers()
{
    auto it = volunteers.begin();
    while (it != volunteers.end())
    {
        Volunteer *volunteer = *it;

        if (volunteer->getrecentlyFinished())
        {
            volunteer->setrecentlyFinished(false);
            Order &completed = getOrder(volunteer->getCompletedOrderId());

            if (completed.getStatus() == OrderStatus::COLLECTING)
            {
                CollectorCounter++;
                pendingOrders.push_back(&completed);
                for (auto it = inProcessOrders.begin(); it != inProcessOrders.end(); ++it)
                {
                    if (*it == &completed)
                    {
                        inProcessOrders.erase(it);
                        break;
                    }
                }
            }
            else if (completed.getStatus() == OrderStatus::DELIVERING)
            {
                DriverCounter++;
                completed.setStatus(OrderStatus::COMPLETED);
                completedOrders.push_back(&completed);
                for (auto it = inProcessOrders.begin(); it != inProcessOrders.end(); ++it)
                {
                    if (*it == &completed)
                    {
                        inProcessOrders.erase(it);
                        break;
                    }
                }
            }
        }

        if (!volunteer->hasOrdersLeft() && !volunteer->isBusy())
        {
            if (dynamic_cast<LimitedCollectorVolunteer *>(volunteer))
            {
                CollectorCounter--;
            }
            else if (dynamic_cast<LimitedDriverVolunteer *>(volunteer))
            {
                DriverCounter--;
            }

            it = volunteers.erase(it);
            delete volunteer; 
        }
        else
        {
            it++;
        }
    }
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void WareHouse::start()
{
    std::cout << "WareHouse is now open" << std::endl;
    std::string line;
    this->isOpen = true;
    while (isOpen)
    {
        getline(std::cin, line);
        if (!line.empty())
        {
            std::queue<std::string> *input = new std::queue<std::string>();
            splitString(line, *input, ' ');

            if (input->empty())
            {
                std::cerr << "Error: Empty input." << std::endl;
                delete input;
                continue; // Skip processing the empty input
            }

            if (input->front() == "close")
            {
                input->pop();
                performClose();
            }
            else if (input->front() == "step")
            {
                input->pop();

                if (!input->empty())
                {
                    int nunmber_of_steps = std::stoi(input->front());
                    std::cout << "nunmber_of_steps: " << nunmber_of_steps << std::endl;
                    input->pop();
                    performSimulateStep1(nunmber_of_steps);
                }
            }
            else if (input->front() == "order")
            {
                input->pop();
                int idCustomer = std::stoi(input->front());
                input->pop();
                performAddOrder(idCustomer);
            }
            else if (input->front() == "orderStatus")
            {
                input->pop();
                int Orderid = std::stoi(input->front());
                input->pop();
                performPrintOrderStatus(Orderid);
            }
            else if (input->front() == "customerStatus")
            {
                input->pop();
                int customerId = std::stoi(input->front());
                input->pop();
                performPrintCustomerStatus(customerId);
            }
            else if (input->front() == "volunteerStatus")
            {
                input->pop();
                int Volunteer_id = std::stoi(input->front());
                input->pop();
                performPrintVolunteerStatus(Volunteer_id);
            }
            else if (input->front() == "log")
            {
                input->pop();
                performPrintActionsLog();
            }
            else if (input->front() == "backup")
            {
                input->pop();
                performBackupWareHouse();
            }
            else if (input->front() == "restore")
            {
                input->pop();
                performRestoreWareHouse();
            }
            else if (input->front() == "customer")
            {
                input->pop();

                if (input->size() >= 4) // Ensure there are enough elements in the input
                {
                    string name = input->front();
                    input->pop();
                    string type = input->front();
                    input->pop();
                    int distance, maxOrders;

                    try
                    {
                        distance = std::stoi(input->front());
                        input->pop();
                        maxOrders = std::stoi(input->front());
                        input->pop();

                        performAddCustomer(name, type, distance, maxOrders);
                    }
                    catch (const std::invalid_argument &e)
                    {
                        std::cerr << "Error: Invalid argument for distance or maxOrders." << std::endl;
                    }
                }
                else
                {
                    std::cerr << "Error: Insufficient arguments for 'customer' command." << std::endl;
                }
            }
            delete (input);
        }
    }
}
const std::vector<BaseAction *> &WareHouse::getActions() const
{
    return actionsLog;
}
Customer &WareHouse::getCustomer(int customerId) const
{
    for (const auto &customer : customers)
    {
        if (customer->getId() == customerId)
        {
            return *customer;
        }
    }
    // The case where the customer with the given ID is not found.
    throw std::runtime_error("Customer not found with ID: " + std::to_string(customerId));
}
Volunteer &WareHouse::getVolunteer(int volunteerId) const
{
    for (const auto &volunteer : volunteers)
    {
        if (volunteer->getId() == volunteerId)
        {
            return *volunteer;
        }
    }
    // The volunteer with the given ID is not found.
    throw std::runtime_error("Volunteer not found with ID: " + std::to_string(volunteerId));
}
Order &WareHouse::getOrder(int orderId) const
{
    for (const auto &order : pendingOrders)
    {
        if (order->getId() == orderId)
        {
            return *order;
        }
    }
    for (const auto &order : inProcessOrders)
    {
        if (order->getId() == orderId)
        {
            return *order;
        }
    }
    for (const auto &order : completedOrders)
    {
        if (order->getId() == orderId)
        {
            return *order;
        }
    }
    // The order with the given ID is not found.
    throw std::runtime_error("Order not found with ID: " + std::to_string(orderId));
}
void WareHouse::addOrder(Order *order)
{
    order->setStatus(OrderStatus::PENDING);
    WareHouse::pendingOrders.push_back(order);
}
void WareHouse::close()
{
    for (const Order *order : WareHouse::pendingOrders)
    {
        std::cout << "OrderID: " + std::to_string(order->getId()) + " CustomerID: " + std::to_string(order->getCustomerId()) + " OrderStatus: " + order->statustoString() << std::endl;
    }
    for (const Order *order : WareHouse::inProcessOrders)
    {
        std::cout << "OrderID: " + std::to_string(order->getId()) + " CustomerID: " + std::to_string(order->getCustomerId()) + " OrderStatus: " + order->statustoString() << std::endl;
    }

    for (const Order *order : WareHouse::completedOrders)
    {
        std::cout << "OrderID: " + std::to_string(order->getId()) + " CustomerID: " + std::to_string(order->getCustomerId()) + " OrderStatus: " + order->statustoString() << std::endl;
    }
    //  Set the warehouse status to closed
    isOpen = false;
    std::cout << "WareHouse is now close" << std::endl;
}
void WareHouse::open()
{
    isOpen = true;
    std::cout << "WareHouse is now Open" << std::endl;
}
// perform functions
void WareHouse::performSimulateStep1(int numOfSteps)
{
    SimulateStep *action = new SimulateStep(numOfSteps);
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}
void WareHouse::performAddOrder(int id)
{
    AddOrder *action = new AddOrder(id);
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}
void WareHouse::performAddCustomer(string customerName, string customerType, int distance, int maxOrders)
{
    AddCustomer *action = new AddCustomer(customerName, customerType, distance, maxOrders);
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}
void WareHouse::performPrintCustomerStatus(int customerId)
{
    PrintCustomerStatus *action = new PrintCustomerStatus(customerId);
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}
void WareHouse::performPrintOrderStatus(int id)
{
    PrintOrderStatus *action = new PrintOrderStatus(id);
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}
void WareHouse::performPrintVolunteerStatus(int id)
{
    PrintVolunteerStatus *action = new PrintVolunteerStatus(id);
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}
void WareHouse::performPrintActionsLog()
{
    PrintActionsLog *action = new PrintActionsLog();
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}
void WareHouse::performBackupWareHouse()
{
    BackupWareHouse *action = new BackupWareHouse();
    actionsLog.push_back(action);
    action->act(*this);
    action = nullptr;
    delete action;
}
void WareHouse::performRestoreWareHouse()
{
    RestoreWareHouse *action = new RestoreWareHouse();
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}
void WareHouse::performClose()
{
    Close *action = new Close();
    action->act(*this);
    actionsLog.push_back(action);
    action = nullptr;
    delete action;
}