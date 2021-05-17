package com.saga.converter_4_20ma.enumeration

enum class OperationTypeEnum(i: Int) {

    //
    CMD_INVALID(0x00),
    CMD_SET_ACQUIRE_TIME(0x01),
    CMD_GET_ACQUIRE_TIME(0x02),
    CMD_SET_RANGE(0x03),
    CMD_GET_RANGE(0x04),
    CMD_SET_Q3(0x05),
    CMD_GET_Q3(0x06),
    CMD_PRE_VALID(0xFF);

    // -------------------
    private var intValue = 0

    /**
     *
     */
    open fun CmdArduinoEnum(value: Int) {
        intValue = value
    }

    /**
     *
     */
    open fun getValue(): Int {
        return intValue
    }

}