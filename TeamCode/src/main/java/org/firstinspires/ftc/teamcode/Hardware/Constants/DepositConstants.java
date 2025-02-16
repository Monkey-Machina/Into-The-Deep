package org.firstinspires.ftc.teamcode.Hardware.Constants;

public class DepositConstants {

    // Claw Positions
    public static final double
            clawClosedPos = 0.035,
            clawOpenPos = 0.46,
            clawEncLowerBound = 0.00,
            clawEncUpperBound = 0.00,
            clawEncPosTolerance = 4.00;

    // Arm Positions
    public static final double
            armTransferPos = 0.932,
            armSpecIntakePos = 0.04,
            armSpecDepositPos = 0.89,
            armSampleDepositPos = 0.32,
            armPositionTolerance = 10.00,
            armEncLowerBound = 0.00,
            armEncUpperBound = 0.00;


    // Wrist Positions
    public static final double
            wristTransferPos = 0.00,
            wristSpecIntakePos = 0.00,
            wristSpecDepositPos = 0.00,
            wristSampleDepositPos = 0.00,
            wristEncLowerBound = 0.00,
            wristEncUpperBound = 0.00,
            wristPositionTolerance = 0.00;


    // Deposit Slide PID Constants
    public static final double
            sp = 0.02,
            si = 0.05,
            sd = 0.00035,
            sf = 0.13;
    //Zeros for testing
//            sp = 0.0,
//            si = 0.0,
//            sd = 0.0,
//            sf = 0.0;

    // Deposit Slides Positions in cm
    public static final double
            slideTransferPos = 4.00,
            slidePreTransferPos = 20.00,
            slideSpecIntakePos = 0.00,
            slideSpecDepositReadyPos = 44.90,
            slideSpecClippedPos = 63.00,
            slideSampleDepositPos = 72,
            slideMaxExtension = 72,
            slidePositionTolerance = 1.00;

}
