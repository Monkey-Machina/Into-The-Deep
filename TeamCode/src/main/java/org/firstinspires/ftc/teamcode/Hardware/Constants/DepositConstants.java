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
            armSpecIntakePos = 235.0,
            armSpecDepositPos = 137.0,
            armSampleDepositPos = 202.0,
            armSamplePreDeposit = 265.0,
            armEncLowerBound = 20.4 ,
            armEncUpperBound = 342.4364,
            armPositionTolerance = 10.00;


    // Wrist Positions (degrees)
    public static final double
            wristTransferPos = 117.00,
            wristSpecIntakePos = 152.00,
            wristSpecDepositPos = 275.00,
            wristSampleDepositPos = 202.00,
            wristSamplePreDeposit = 120.0,
            wristEncLowerBound = 21.2727,
            wristEncUpperBound = 344.0727,
            wristPositionTolerance = 9.00;


    // Deposit Slide PID Constants
    public static final double
            sp = 0.025,
            si = 0.0,
            sd = 0.0005,
            sf = 0.12;
//            sp = 0.02,
//            si = 0.05,
//            sd = 0.00035,
//            sf = 0.13;

    // Deposit Slides Positions (CM)
    public static final double
            slideTransferPos = 7.50,
            slideSpecIntakePos = 11.525,
            slideSpecDepositPos = 42.58,
            slideSampleDepositPos = 80,
            slideMaxExtension = 82,
            slidePositionTolerance = 1.00;

}
