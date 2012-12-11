cmake_minimum_required (VERSION 2.6)
project(machine_learning)

find_package(Boost COMPONENTS iostreams REQUIRED)

include_directories(. ${Boost_INCLUDE_DIRS})

add_executable(lab1
./images_holder.cpp
./images_holder.h
./lab1.cpp
)
target_link_libraries(lab1 ${Boost_IOSTREAMS_LIBRARY})
