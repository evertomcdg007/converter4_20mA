package com.saga.converter_4_20ma.enumeration

enum class TimeOutEnum(i: Int) {

    RECEIVE_HEADER(1000),
    RECEIVE_PACK(30000),
    RECEIVE_ROUTE(3600000);

    private var intValue = 0

    open fun TimeOutEnum(value: Int) {
        intValue = value
    }

    open fun getValue(): Int {
        return intValue
    }
}