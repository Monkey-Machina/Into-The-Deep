package org.firstinspires.ftc.teamcode.Hardware.Constants;

public class DepositConstants {

    // Claw Positions
    public static final double
            clawClosedPos = 0.035,
            clawOpenPos = 0.46;

    public static final double
            clawEncClosedPos = 311.00,
            clawEncOpenPos = 192.50,
            clawEncPosTolerance = 4.00;

    // Arm Positions
    public static final double
            armTransferPos = 0.932,
            armSpecIntakePos = 0.04,
            armSpecDepositPos = 0.89,
            armSampleDepositPos = 0.32;

    // Arm Enc Positions
    public static final double
            armEncTransferPos = 42.00,
            armEncSpecIntakePos = 330.50,
            armEncSpecDepositPos = 53.60,
            armEncSampleDepositPos = 240.70,
            armPositionTolerance = 10.00;

    // Wrist Positions, wrist positions all indicated in terms of distance rotated all the way backwards (IE: claw arm side of claw against deposit arm)
    public static final double
            wristTransferPos = 0.00,
            wristSpecIntakePos = 0.00,
            wristSpecDepositPos = 0.00,
            wristSampleDepositPos = 0.00,
            wristPositionRange = 0.00;

    // Wrist Enc Positions
    public static final double
            wristEncTransferPos = 0.00,
            wristEncSpecIntakePos = 0.00,
            wristEncSpecDepositPos = 0.00,
            wristEncSampleDepositPos = 0.00,
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
