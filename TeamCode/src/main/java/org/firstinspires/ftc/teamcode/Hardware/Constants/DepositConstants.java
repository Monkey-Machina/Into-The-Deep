package org.firstinspires.ftc.teamcode.Hardware.Constants;

public class DepositConstants {

    // Claw Positions (degrees)
    public static final double
            clawClosedPos = 135.0,
            clawOpenPos = 245.0,
            clawEncLowerBound = 42.544,
            clawEncUpperBound = 319.2,
            clawEncPosTolerance = 10.00;

    // Arm Positions (degrees)
    public static final double
            armTransferPos = 295.0,
            armSpecIntakePos = 244.0,
            armSpecDepositPos = 147.0,
            armSampleDepositPos = 200.0,
            armSamplePreDeposit = 275.0,
            armEncLowerBound = 20.4 ,
            armEncUpperBound = 342.4364,
            armPositionTolerance = 11.00;


    // Wrist Positions (degrees)
    public static final double
            wristTransferPos = 86.00,
            wristSpecIntakePos = 122.00,
            wristSpecDepositPos = 245.00,
            wristSampleDepositPos = 180.00,
            wristSamplePreDeposit = 90.0,
            wristEncLowerBound = 21.2727,
            wristEncUpperBound = 344.0727,
            wristPositionTolerance = 9.00;


    // Deposit Slide PID Constants
    public static final double
            sp = 0.01,
            si = 0.1,
            sd = 0.0003,
            sf = 0.12,
            sp2 = 0.03,
            si2 = 0.3,
            sd2 = 0.00055,
            spa = 0.007,
            sia = 0.0,
            sda = 0.0004;
//            sp = 0.02,
//            si = 0.05,
//            sd = 0.00035,
//            sf = 0.13;

    // Deposit Slides Positions (CM)
    public static final double
            slideTransferPos = 8.75,
            slideSpecIntakePos = 11.525,
            slideSpecDepositPos = 42.58,
            slideSampleDepositPos = 81,
            slideMaxExtension = 82,
            slidePositionTolerance = 1.00;

}
