package org.firstinspires.ftc.teamcode.Hardware.Constants;

public class DepositConstants {

    // Claw Positions (degrees)
    public static final double
            clawClosedPos = 180.0,
            clawOpenPos = 275.0,
            clawEncLowerBound = 42.544,
            clawEncUpperBound = 319.2,
            clawEncPosTolerance = 10.00;

    // Arm Positions (degrees)
    public static final double
            armTransferPos = 277.0,
            armSpecIntakePos = 232.0,
            armSpecDepositPos = 122.0,
            armSampleDepositPos = 187.0,
            armEncLowerBound = 20.4 ,
            armEncUpperBound = 342.4364,
            armPositionTolerance = 5.00;


    // Wrist Positions (degrees)
    public static final double
            wristTransferPos = 107.00,
            wristSpecIntakePos = 147.00,
            wristSpecDepositPos = 272.00,
            wristSampleDepositPos = 192.00,
            wristEncLowerBound = 21.2727,
            wristEncUpperBound = 344.0727,
            wristPositionTolerance = 5.00;


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
            slideTransferPos = 10.00,
            slideSpecIntakePos = 7.00,
            slideSpecDepositPos = 55.00,
            slideSampleDepositPos = 80,
            slideMaxExtension = 82,
            slidePositionTolerance = 1.00;

}
