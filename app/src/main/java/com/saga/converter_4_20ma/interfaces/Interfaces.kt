package com.saga.converter_4_20ma.interfaces

import com.saga.converter_4_20ma.enumeration.FeedbackCodeEnum

class Interfaces {
    /**
     *
     */
    interface OnBluetoothCallBack {
        fun onBluetoothCallBackFunction(
            arg0: String?,
            arg1: ByteArray?
        )
    }

    /**
     *
     */
    interface OnProtocolCallBack {
        fun onProtocolCallBackFunction(
            arg0: String?,
            arg1: FeedbackCodeEnum?,
            arg2: Int
        )
    }

    /**
     *
     */
    interface OnPositiveButtonDialog {
        fun onPositiveListener(arg: Any?)
    }

    /**
     *
     */
    interface OnNeutralButtonDialog {
        fun onNeutralListener(arg0: String?)
    }

    /**
     *
     */
    interface OnNegativeButtonDialog {
        fun onNegativeListener()
    }

    /**
     *
     */
    interface OnClickAdapter {
        fun onClickAdapterLintener(arg0: Any?)
    }

    /**
     *
     */
    interface OnClickFloatingActionButton {
        fun onClickFloatingActionButtonLintener(arg0: Any?)
    }

    /**
     *
     */
    interface OnTimeoutCallBack {
        fun onTimeoutCallBackFunction(arg0: Int)
    }

    /**
     *
     */
    interface OnServerCallBack {
        fun onServerCallBackFunction(
            arg0: String?,
            arg1: FeedbackCodeEnum?,
            message: String?
        )
    }
}
