package com.saga.converter_4_20ma.service.cmd;

import com.saga.converter_4_20ma.enumeration.OperationTypeEnum;

import java.util.ArrayList;

public abstract class CommandPackBt {

    // ---------------------
    // Estrutura do pacote de comunicacao Bluetooth

    public final static byte SIZE_BT_HEADER_PACK = 29;
    public final static byte idx_BT_LENGTH = 0x19;

}
