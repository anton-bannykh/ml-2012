cmake_minimum_required (VERSION 2.6)
project(machine_learning)

# Boost >= 1.48 needed
find_package(Boost COMPONENTS system timer iostreams REQUIRED)

include_directories(. ${Boost_INCLUDE_DIRS})

add_executable(lab1
./images_holder.cpp
./images_holder.h
./lab1.cpp
)
target_link_libraries(lab1 ${Boost_IOSTREAMS_LIBRARY} ${Boost_SYSTEM_LIBRARY} ${Boost_TIMER_LIBRARY})
