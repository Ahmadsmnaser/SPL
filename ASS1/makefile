
CXX = g++
CXXFLAGS = -g -Wall -Weffc++ -std=c++11 -Iinclude
CPPFLAGS = -I ./include -MMD -MP

SRCS = $(wildcard ./src/*.cpp)
OBJS = $(patsubst ./src/%.cpp,./bin/%.o, $(SRCS))
DEPS := $(patsubst %.o,%.d, $(OBJS))

# default target
all: debug ./bin/warehouse

debug:
	@echo "SRCS = $(SRCS)"
	@echo "OBJS = $(OBJS)"

# build warehouse
./bin/warehouse: $(OBJS)
	@echo "Building ..."
	$(CXX) $(OBJS) -o $@
	@echo "Finished building"

# build cpp files
./bin/%.o: ./src/%.cpp
	$(CXX) $(CXXFLAGS) $(CPPFLAGS) -c $< -o $@

# clean build files
clean:
	@rm -f ./bin/*

-include $(DEPS)
